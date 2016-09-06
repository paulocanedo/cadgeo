/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.rinex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public class RinexNavSplitter {

    private final static String lineSeparator = "\n";
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yy M d k m s.S");
    private final static SimpleDateFormat sdfTimeStamp = new SimpleDateFormat("yyyy-MM-dd kk:mm");
    private final static StringBuilder basicHeader = new StringBuilder();
    private final static List<String> rinexHeaderList = new ArrayList<String>();

    public RinexNavSplitter() {
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

    public void split(InputStream stream, Date greaterThan, Date lessThan, File fileout) throws IOException {
        float rinexVersion = 99f;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        boolean flag = false;

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (rinexVersion == 99f) {
                rinexVersion = Float.parseFloat(line.substring(0, 9));
                if (rinexVersion < 2f || rinexVersion > 2.11f) {
                    throw new IOException("Versão de arquivo RINEX não suportada: " + rinexVersion);
                }
            }

            String key = line.length() > 60 ? line.substring(60) : null;

            if (flag) {
                String time = line.substring(3, 22);
                Date date = parseDate(time);
                String navdata = readNextNav(reader);
                if (date.compareTo(greaterThan) >= 0 && date.compareTo(lessThan) < 0) {

                    sb.append(line).append(lineSeparator);
                    sb.append(navdata);
                }
            }
            if ("END OF HEADER".equals(key)) {
                flag = true;
            }
        }
        if (sb.length() > 0) {
            RinexUtil.writeFile(fileout, basicHeader(), sb);
        }
    }

    public String readNextNav(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            sb.append(reader.readLine()).append(lineSeparator);
        }
        return sb.toString();
    }

    private Date parseDate(String time) {
        String[] split = time.split(" +");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s).append(" ");
        }

        try {
            return sdf.parse(sb.toString());
        } catch (ParseException ex) {
        }
        return null;
    }

    public static StringBuilder basicHeader() {
        if (basicHeader.length() > 0) {
            return basicHeader;
        }

        String rinexVersion = String.format(Locale.US, "%9.2f%11s%-20s%-20s%s", 2.10, "", "NAVIGATION DATA", "G", rinexHeaderList.get(0));
        basicHeader.append(rinexVersion).append(lineSeparator);

        String pgm_runBy_date = String.format("%-20s%-20s%-20s%s", "GeoMapa 1.0", "", sdfTimeStamp.format(new Date()), rinexHeaderList.get(1));
        basicHeader.append(pgm_runBy_date).append(lineSeparator);

        String observer_agency = String.format("%60s%s", "", rinexHeaderList.get(5));
        basicHeader.append(observer_agency).append(lineSeparator);

        return basicHeader;
    }
}
