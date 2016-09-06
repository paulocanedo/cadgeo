/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.rinex;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 *
 * @author paulocanedo
 */
public class RinexUtil {

    public final static String lineSeparator = "\n";
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yy MM dddd kk mm ss");

    public static void writeFile(File out, StringBuilder header, StringBuilder content) throws FileNotFoundException, IOException {
        FileOutputStream stream = new FileOutputStream(out);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "ASCII"));

            writer.append(header);
            writer.append(content);
            writer.flush();
        } finally {
            stream.close();
        }
    }

    public static Date parseRinexDate(String date) {
        if (date == null) {
            return null;
        }
        String toParse = null;
        try {
            date = date.trim();
            int lastIndexOf = date.lastIndexOf(".");
            if (lastIndexOf > 0) {
                date = date.substring(0, lastIndexOf);
            }

            toParse = date.replaceAll(" +", " ");
            return sdf.parse(toParse);
        } catch (ParseException ex) {
        }
        return null;
    }

    public static String catchLineHeader(String header, String key) {
        StringTokenizer st = new StringTokenizer(header, "\n\r");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.length() > 60 && token.substring(60).trim().equals(key)) {
                return token;
            }
        }
        return null;
    }

    public static RinexFile findRinexFile(File folder, String markerName) {
        if (folder == null) {
            throw new NullPointerException("Folder cannot be null");
        }
        if (!folder.exists()) {
            return null;
        }
        File[] listFiles = folder.listFiles(RinexFileFilter.FILE_FILTER);
        for (File f : listFiles) {
            RinexFile rinexFile = new RinexFile(f);

            try {
                markerName = markerName.replaceAll(GeodesicPoint.charsSeparator, "");
                String otherMarkerName = rinexFile.getHeader().getMarkerName();
                if (otherMarkerName != null) {
                    otherMarkerName = otherMarkerName.replaceAll(GeodesicPoint.charsSeparator, "");
                }

                if (markerName.equals(otherMarkerName)) {
                    return rinexFile;
                }
            } catch (Exception ex) {
            }
        }
        return null;
    }
}
