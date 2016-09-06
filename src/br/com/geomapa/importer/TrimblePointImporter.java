/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer;

import br.com.geomapa.geodesic.GeodesicEnum;
import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.datum.Datum;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author paulocanedo
 */
public class TrimblePointImporter extends PlainTextPointImporter {

    private static final Properties trimbleProperties = new Properties();
    private Pattern tagPattern = Pattern.compile(".+<.+>(.+)</.+>");
    public static final GeodesicEnum[] trimbleSequence = {GeodesicEnum.NAME,
        GeodesicEnum.EAST, GeodesicEnum.NORTH, GeodesicEnum.ELIPSOIDAL_HEIGHT,
        GeodesicEnum.QUALITY_X, GeodesicEnum.QUALITY_Y, GeodesicEnum.QUALITY_Z};

    static {
        try {
            InputStream resourceAsStream = TrimblePointImporter.class.getResourceAsStream("/br/com/geografico/resources/properties/trimble_report.properties");
            trimbleProperties.load(resourceAsStream);
        } catch (IOException ex) {
            Logger.getLogger(TrimblePointImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TrimblePointImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum) {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);
    }

    @Override
    protected String[] parseRecord(InputStream stream) throws IOException {
        BufferedReader reader = getReader();

        boolean flag = false;
        String line;
        String name = null, east = null, north = null, height = null, qx = null, qy = null, qz;
        while ((line = reader.readLine()) != null) {
            if (line.contains(trimbleProperties.getProperty("point"))) {
                reader.readLine();
                name = readValueFromTag(reader.readLine());
                flag = true;
            } else if (line.contains(trimbleProperties.getProperty("east")) && flag) {
                east = readValueFromTag(reader.readLine()).replace(",", ".").replace(" m", "");
            } else if (line.contains(trimbleProperties.getProperty("north")) && flag) {
                north = readValueFromTag(reader.readLine()).replace(",", ".").replace(" m", "");
            } else if (line.contains(trimbleProperties.getProperty("height")) && flag) {
                height = readValueFromTag(reader.readLine()).replace(",", ".").replace(" m", "");
            } else if (line.contains(trimbleProperties.getProperty("qx")) && flag) {
                qx = readValueFromTag(reader.readLine()).replace(",", ".").replace(" m", "");
            } else if (line.contains(trimbleProperties.getProperty("qy")) && flag) {
                qy = readValueFromTag(reader.readLine()).replace(",", ".").replace(" m", "");
            } else if (line.contains(trimbleProperties.getProperty("qz")) && flag) {
                qz = readValueFromTag(reader.readLine()).replace(",", ".").replace(" m", "");
                return new String[]{name, east, north, height, qx, qy, qz};
            }
        }

        return null;
    }

    private String readValueFromTag(String valueWithTag) {
        Matcher matcher = tagPattern.matcher(valueWithTag);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("not possible to read from tag");
    }

    public static class TrimbleResultsFileFilter implements FilenameFilter {

        private String prefix;

        public TrimbleResultsFileFilter(File sampleFile) {
            this.prefix = getPrefix(sampleFile);
        }

        private String getPrefix(File file) {
            StringBuilder sb = new StringBuilder(file.getName());
            sb.delete(sb.lastIndexOf("."), sb.length());
            sb.delete(sb.lastIndexOf(".") + 1, sb.length());
            return sb.toString();
        }

        public boolean accept(File dir, String name) {
            return (name.toLowerCase().startsWith(prefix.toLowerCase()));
        }
    }
}
