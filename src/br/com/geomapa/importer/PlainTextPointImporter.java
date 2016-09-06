/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.importer;

import br.com.geomapa.geodesic.GeodesicEnum;
import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.datum.Datum;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paulocanedo
 */
public abstract class PlainTextPointImporter extends PointImporter {

    private BufferedReader reader;

    public PlainTextPointImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum) {
        this(handle, stream, sourceOrder, utmZone, hemisphere, datum, "utf-8");
    }
    
    public PlainTextPointImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum, String charset) {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);

        try {
            this.reader = new BufferedReader(new InputStreamReader(stream, charset));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PlainTextPointImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PlainTextPointImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, String charset, int utmZone, Hemisphere hemisphere, Datum datum) {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);
        
        try {
            this.reader = new BufferedReader(new InputStreamReader(stream, charset));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PlainTextPointImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected BufferedReader getReader() {
        return reader;
    }

}
