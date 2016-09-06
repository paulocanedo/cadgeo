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

/**
 *
 * @author paulocanedo
 */
public class XMLPointImporter extends PointImporter {

    public XMLPointImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum) {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);
    }

    @Override
    protected String[] parseRecord(InputStream stream) throws IOException {
        return null;
    }
}
