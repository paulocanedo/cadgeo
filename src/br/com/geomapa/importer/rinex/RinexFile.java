/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.rinex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paulocanedo
 */
public class RinexFile implements Comparable<RinexFile> {

    private File file;
    private RinexHeader rinexHeader = new RinexHeader();
    private final String nlWin = "\r\n", nlOSX = "\r", nlUnix = "\n";

    public RinexFile(File file) {
        this.file = file;
    }

    public RinexHeader getHeader() throws FileNotFoundException, IOException {
        String firstObs = null;
        String lastObs = null;

        String header = getHeader(file).toString();

        if (firstObs == null) {
            firstObs = RinexUtil.catchLineHeader(header, "TIME OF FIRST OBS");
        }
        if (lastObs == null) {
            lastObs = RinexUtil.catchLineHeader(header, "TIME OF LAST OBS");
        }

        String markerName = RinexUtil.catchLineHeader(header, "MARKER NAME").substring(0, 60).trim();
        Date first = RinexUtil.parseRinexDate(firstObs);
        Date last = RinexUtil.parseRinexDate(lastObs);
        String coord = RinexUtil.catchLineHeader(header, "APPROX POSITION XYZ").substring(0, 60).trim();

        rinexHeader.setMarkerName(markerName);
        rinexHeader.setFirstObs(first);
        rinexHeader.setLastObs(last);
        rinexHeader.setCoord(coord);

        return rinexHeader;
    }

    public StringBuilder getHeader(File file) throws IOException {
        FileReader freader = new FileReader(file);
        BufferedReader reader = new BufferedReader(freader);
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String key = line.substring(60).trim();

                if ("END OF HEADER".equals(key)) {
                    break;
                }
                sb.append(line).append(RinexUtil.lineSeparator);
            }
            return sb;
        } finally {
            reader.close();
            freader.close();
        }
    }

    public File getFile() {
        return file;
    }

    public File getNavigationFile() {
        return getNavigationFile(getFile());
    }

    public File getNavigationFile(File obsFile) {
        String name = obsFile.getName();
        int lastIndex = Math.max(name.lastIndexOf("o"), name.lastIndexOf("O"));

        return new File(obsFile.getParent(), name.substring(0, lastIndex) + "n");
    }

    private boolean hasLineSeparator(String text, String lineSeparator) {
        return text.indexOf(lineSeparator) >= 0;
    }

    public boolean renameTo(String name) throws IOException {
        if (!file.exists()) {
            return false;
        }

        File dest = new File(getFile().getParent(), name + getExtension(getFile()));
        File oldFile = file;
        File oldNavFile = getNavigationFile();

        File navigationFile = getNavigationFile();
        File destNavFile = getNavigationFile(dest);

        RandomAccessFile afile = null;
        try {
            File parentFile = dest.getParentFile();
            if (parentFile == null) {
                throw new IOException("Parent file does not exist");
            }

            String newMarkerName = new RinexFile(dest).toString();
            if (dest.exists() || RinexUtil.findRinexFile(parentFile, newMarkerName) != null) {
                throw new IOException(String.format("Ponto RINEX de destino %s já existe", newMarkerName));
            }

            if (!file.renameTo(dest)) {
                return false;
            }

            if (navigationFile.exists() && !destNavFile.exists()) {
                navigationFile.renameTo(destNavFile);
            }

            String sheader = getHeader(dest).toString();

            String lineSeparatorDetect;
            if (hasLineSeparator(sheader, nlWin)) {
                lineSeparatorDetect = nlWin;
            } else if (hasLineSeparator(sheader, nlOSX)) {
                lineSeparatorDetect = nlOSX;
            } else {
                lineSeparatorDetect = nlUnix;
            }
            String[] split = sheader.split(lineSeparatorDetect);

            int count = -1;
            int countBytes = 0;

            for (String s : split) {
                if (s.substring(60).trim().equals("MARKER NAME")) {
                    break;
                }
                count++;
                countBytes += s.length() + (lineSeparatorDetect.equals(nlWin) ? 2 : 1);
            }

            afile = new RandomAccessFile(dest, "rw");
            afile.seek(countBytes);
            afile.write(String.format("%-60s", newMarkerName).getBytes());
            afile.close();

            oldFile.delete();
            oldNavFile.delete();
            return true;
        } catch (IOException ex) {
            dest.renameTo(oldFile);
            destNavFile.renameTo(oldNavFile);

            throw ex;
        } finally {
            if (afile != null) {
                afile.close();
            }
        }
    }

    @Override
    public String toString() {
        return getSimpleName(file);
    }

    private static String getSimpleName(File rinexFile) {
        String name = rinexFile.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf > 0) {
            return name.substring(0, lastIndexOf);
        } else {
            return name;
        }
    }

    private static String getExtension(File rinexFile) {
        String name = rinexFile.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf > 0) {
            return name.substring(lastIndexOf);
        } else {
            return null;
        }
    }

    public static void main(String... args) {
//        System.out.println(RinexFile.getExtension(new File("/home/paulocanedo/Desktop/rinex_sample/dirOut/AVUM0579.10o")));
        RinexFile rinexFile = new RinexFile(new File("/home/paulocanedo/Desktop/rinex_sample/dirOut/AVUM0579.10o"));
        try {
            boolean renameTo = rinexFile.renameTo("ABCMA004");
//            boolean renameTo = rinexFile.renameTo(new File("/home/paulocanedo/Desktop/rinex_sample/dirOut/ABCMA004.10o"));
            System.out.println(renameTo);
        } catch (IOException ex) {
            Logger.getLogger(RinexFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int compareTo(RinexFile o) {
        return getFile().compareTo(o.getFile());
    }
}
