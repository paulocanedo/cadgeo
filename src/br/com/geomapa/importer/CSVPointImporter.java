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
public class CSVPointImporter extends PlainTextPointImporter {

    private String fieldDelimiter = ",";

    public CSVPointImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum) {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);
    }
    
    public CSVPointImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum, String charset) {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum, charset);
    }

    public CSVPointImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, String fieldDelimiter, int utmZone, Hemisphere hemisphere, Datum datum) {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);
        this.fieldDelimiter = fieldDelimiter;
    }

    public CSVPointImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, String fieldDelimiter, String charset, int utmZone, Hemisphere hemisphere, Datum datum) {
        super(handle, stream, sourceOrder, charset, utmZone, hemisphere, datum);
        this.fieldDelimiter = fieldDelimiter;
    }

    @Override
    protected String[] parseRecord(InputStream stream) throws IOException {
        String line = getReader().readLine();
        if (line == null) {
            return null;
        } else {
            line = line.replace(",,", ", , ");
            return line.split(fieldDelimiter);
        }
    }
}
