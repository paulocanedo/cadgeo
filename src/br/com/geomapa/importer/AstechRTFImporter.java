/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer;

import br.com.geomapa.geodesic.GeodesicEnum;
import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.datum.Datum;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;

/**
 *
 * @author paulocanedo
 */
public class AstechRTFImporter extends RTFPointImporter {

    public AstechRTFImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum) throws IOException, BadLocationException {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);

        //skip the header file
        StringTokenizer lineToken = getLineToken();
        for (int i = 0; i < 11; i++) {
            lineToken.nextToken();
        }
    }

    @Override
    protected String[] parseRecord(InputStream stream) throws IOException {
        StringTokenizer lineToken = getLineToken();
        while (lineToken.hasMoreTokens()) {
            String line = lineToken.nextToken().trim();
            if (line.toLowerCase().startsWith("fator")) {
                return null;
            }

            String name = line.substring(12, 33).trim();
            String east = line.substring(45, 61).trim();
            String qx = line.substring(61, 69).trim();

            line = lineToken.nextToken();
            String north = line.substring(45, 61).trim();
            String qy = line.substring(61, 69).trim();

            line = lineToken.nextToken();
            String height = line.substring(45, 61).trim();
            String qz = line.substring(61, 69).trim();

            return new String[]{name, east, north, height, qx, qy, qz};
        }
        return null;
    }

}
