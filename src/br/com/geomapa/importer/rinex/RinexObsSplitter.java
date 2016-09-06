/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.rinex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public class RinexObsSplitter {

    private final static List<String> rinexHeaderList = new ArrayList<String>();
    private final static Calendar calendarControl = Calendar.getInstance();
    private final static SimpleDateFormat sdfTimeStamp = new SimpleDateFormat("yyyy-MM-dd kk:mm");
    private final static SimpleDateFormat sdfTimeObs = new SimpleDateFormat("yyyy MM dd kk mm ss.S");
    private final static StringBuilder basicHeader = new StringBuilder();
    private String mainHeader;

    public RinexObsSplitter() {
        rinexHeaderList.add("RINEX VERSION / TYPE");
        rinexHeaderList.add("PGM / RUN BY / DATE");
        rinexHeaderList.add("COMMENT");
        rinexHeaderList.add("MARKER NAME");
        rinexHeaderList.add("MARKER NUMBER");
        rinexHeaderList.add("OBSERVER / AGENCY");
        rinexHeaderList.add("REC # / TYPE / VERS");
        rinexHeaderList.add("ANT # / TYPE");
        rinexHeaderList.add("APPROX POSITION XYZ");
        rinexHeaderList.add("ANTENNA: DELTA H/E/N");
        rinexHeaderList.add("WAVELENGTH FACT L1/2");
        rinexHeaderList.add("# / TYPES OF OBSERV");
        rinexHeaderList.add("INTERVAL");
        rinexHeaderList.add("TIME OF FIRST OBS");
        rinexHeaderList.add("TIME OF LAST OBS");
        rinexHeaderList.add("RCV CLOCK OFFS APPL");
        rinexHeaderList.add("LEAP SECONDS");
        rinexHeaderList.add("# OF SATELLITES");
        rinexHeaderList.add("PRN / # OF OBS");
        rinexHeaderList.add("END OF HEADER");
    }

    public void split(File obsInput, File dirOut) throws IOException {
        float rinexVersion = 99f;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(obsInput)));

        StringBuilder header = new StringBuilder();
        StringBuilder pointContent = new StringBuilder();
        boolean flagContent = false;

        String stringStartTime = null;
        String stringEndTime = null;
        String line;
        while ((line = reader.readLine()) != null) {
            if (rinexVersion == 99f) {
                rinexVersion = Float.parseFloat(line.substring(0, 9));
                if (rinexVersion < 2f || rinexVersion > 2.11f) {
                    throw new IOException("Versão de arquivo RINEX não suportada: " + rinexVersion);
                }
            }

            String key = line.length() > 60 ? line.substring(60) : null;

            if (rinexHeaderList.contains(key)) {
                if (flagContent) {//changed from content to header: time to flush data
                    writeFile(obsInput, dirOut, stringStartTime, stringEndTime, header, pointContent);

                    header.delete(0, header.length());
                    pointContent.delete(0, pointContent.length());
                    stringStartTime = null;
                    stringEndTime = null;
                }

                flagContent = false;
                if (!key.equals(rinexHeaderList.get(13)) && !key.equals(rinexHeaderList.get(14))
                        && !key.equals(rinexHeaderList.get(0)) && !key.equals(rinexHeaderList.get(1))
                        && !key.equals(rinexHeaderList.get(2)) && !key.equals(rinexHeaderList.get(5))
                        && !key.equals(rinexHeaderList.get(17)) && !key.equals(rinexHeaderList.get(18))) {
                    header.append(line).append(RinexUtil.lineSeparator);
                }
            } else {
                if (mainHeader == null) {
                    mainHeader = header.toString();
                }

                if (line.matches(" (\\d| )+\\.\\d{7}.*")) {
                    stringStartTime = min(stringStartTime, line.substring(0, 26));
                    stringEndTime = max(stringEndTime, line.substring(0, 26));
                }
                flagContent = true;
                pointContent.append(line).append(RinexUtil.lineSeparator);
            }
        }
        writeFile(obsInput, dirOut, stringStartTime, stringEndTime, header, pointContent);
    }

    private void writeFile(File input, File dirOut, String startTime, String endTime, StringBuilder header, StringBuilder pointContent) throws FileNotFoundException, IOException {
        calendarControl.setTime(RinexUtil.parseRinexDate(startTime));

        String timeEndObs = formatTimeObservation(endTime);
        Date dateEnd = parseTimeObservation(endTime);
        header.insert(0, timeEndObs + rinexHeaderList.get(14) + RinexUtil.lineSeparator);

        String timeStartObs = formatTimeObservation(startTime);
        Date dateStart = parseTimeObservation(startTime);
        header.insert(0, timeStartObs + rinexHeaderList.get(13) + RinexUtil.lineSeparator);

        String typesObs = RinexUtil.catchLineHeader(mainHeader, rinexHeaderList.get(11));
        header.insert(0, typesObs + RinexUtil.lineSeparator);

        if (RinexUtil.catchLineHeader(header.toString(), rinexHeaderList.get(7)) == null) {
            String ant_type = RinexUtil.catchLineHeader(mainHeader, rinexHeaderList.get(7));
            header.insert(0, ant_type + RinexUtil.lineSeparator);
        }

        if (RinexUtil.catchLineHeader(header.toString(), rinexHeaderList.get(6)) == null) {
            String rec_type_vers = RinexUtil.catchLineHeader(mainHeader, rinexHeaderList.get(6));
            header.insert(0, rec_type_vers + RinexUtil.lineSeparator);
        }

        header.insert(0, basicHeader());

        if (RinexUtil.catchLineHeader(header.toString(), rinexHeaderList.get(19)) == null) {
            String endOfHeader = String.format("%60s%s", "", rinexHeaderList.get(19));
            header.append(endOfHeader).append(RinexUtil.lineSeparator);
        }

        String year = String.valueOf(calendarControl.get(Calendar.YEAR));
        String year2digits;
        if (year.length() == 4) {
            year2digits = year.substring(2, 4);
        } else {
            year2digits = year;
        }

        String s = RinexUtil.catchLineHeader(header.toString(), rinexHeaderList.get(3));
        if(s == null) {
            System.out.println("erro: " + pointContent);
            return;
        }
        String markerName = s.substring(0, 60).trim();
        RinexUtil.writeFile(new File(dirOut, String.format("%s.%so", markerName, year2digits)), header, pointContent);

        String path = input.getAbsolutePath();
        String navInput = path.substring(0, path.lastIndexOf(".") + 3) + "n";

        InputStream stream = new FileInputStream(navInput);
        File file = new File(dirOut, String.format("%s.%sn", markerName, year2digits));
        new RinexNavSplitter().split(stream, dateStart, dateEnd, file);
    }

    public static StringBuilder basicHeader() {
        if (basicHeader.length() > 0) {
            return basicHeader;
        }

        String rinexVersion = String.format(Locale.US, "%9.2f%11s%-20s%-20s%s", 2.11, "", "OBSERVATION DATA", "G", rinexHeaderList.get(0));
        basicHeader.append(rinexVersion).append(RinexUtil.lineSeparator);

        String pgm_runBy_date = String.format("%-20s%-20s%-20s%s", "GeoMapa 1.0", "", sdfTimeStamp.format(new Date()), rinexHeaderList.get(1));
        basicHeader.append(pgm_runBy_date).append(RinexUtil.lineSeparator);

        String observer_agency = String.format("%60s%s", "", rinexHeaderList.get(5));
        basicHeader.append(observer_agency).append(RinexUtil.lineSeparator);

        return basicHeader;
    }

    private Date parseTimeObservation(String timeObs) {
        String[] time = timeObs.trim().split(" +");

        int year = Integer.parseInt(time[0]);
        String result = String.format("%s %s %s %s %s %s", year > 80 ? ("19" + year) : ("20" + year), time[1], time[2], time[3], time[4], time[5]);
        try {
            return sdfTimeObs.parse(result);
        } catch (ParseException ex) {
            System.out.println(timeObs);
        }
        return null;
    }

    private String formatTimeObservation(String timeObs) {
        String[] time = timeObs.trim().split(" +");

        int year = Integer.parseInt(time[0]);
        String result = String.format("  %4s  %4s  %4s  %4s  %4s  %11s %16s", year > 80 ? ("19" + year) : ("20" + year), time[1], time[2], time[3], time[4], time[5], "");
        return result;
    }

    private String max(String s1, String s2) {
        if (s1 == null && s2 != null) {
            return s2;
        } else if (s1 != null && s2 == null) {
            return s1;
        } else if (s1 == null && s2 == null) {
            return null;
        }

        if (s1.compareTo(s2) > 0) {
            return s1;
        }
        return s2;
    }

    private String min(String s1, String s2) {
        if (s1 == null && s2 != null) {
            return s2;
        } else if (s1 != null && s2 == null) {
            return s1;
        } else if (s1 == null && s2 == null) {
            return null;
        }

        if (s1.compareTo(s2) < 0) {
            return s1;
        }
        return s2;
    }

    public static void main(String... args) throws FileNotFoundException, IOException {
        RinexObsSplitter rinexSplitter = new RinexObsSplitter();

        File file = new File("/home/paulocanedo/Desktop/rinex_sample/BA7M1250.10o");
        rinexSplitter.split(file, new File("/home/paulocanedo/Desktop/rinex_sample/dirOut"));
    }
}
