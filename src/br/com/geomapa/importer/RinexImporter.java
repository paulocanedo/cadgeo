/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer;

import br.com.geomapa.geodesic.GeodesicEnum;
import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.datum.Datum;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author paulocanedo
 */
public class RinexImporter extends PlainTextPointImporter {

    private BufferedReader reader = getReader();

    public RinexImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum) {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);
    }

    @Override
    protected String[] parseRecord(InputStream stream) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().endsWith("MARKER NAME")) {
                return new String[]{line.substring(0, 60).trim()};
            }
        }
        return null;
    }

}
