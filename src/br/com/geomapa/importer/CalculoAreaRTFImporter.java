/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer;

import br.com.geomapa.geodesic.GeodesicEnum;
import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.datum.Datum;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import javax.swing.text.BadLocationException;

/**
 *
 * @author paulocanedo
 */
public class CalculoAreaRTFImporter extends RTFPointImporter {

    public CalculoAreaRTFImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum) throws IOException, BadLocationException {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);
    }

    @Override
    protected String[] parseRecord(InputStream stream) throws IOException {
        StringTokenizer lineToken = getLineToken();
        while (lineToken.hasMoreTokens()) {
            String nextToken = lineToken.nextToken();
            if (nextToken.matches("([\\w\\s-]+)\\s([\\w\\s-]+)\\s([\\d\\.,]+)\\s([\\d\\.,]+)\\s(.+)")) {
                return nextToken.split("\t");
            } else {
                continue;
            }
        }
        return null;
    }

}
