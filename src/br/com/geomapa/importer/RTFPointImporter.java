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
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.rtf.RTFEditorKit;

/**
 *
 * @author paulocanedo
 */
public abstract class RTFPointImporter extends PointImporter {

    private JEditorPane documentReader = new JEditorPane();
    private StringTokenizer lineToken;

    public RTFPointImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum) throws IOException, BadLocationException {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);

        RTFEditorKit rtf = new RTFEditorKit();
        documentReader.setEditorKit(rtf);
        rtf.read(stream, documentReader.getDocument(), 0);

        int lenght = documentReader.getDocument().getLength();
        lineToken = new StringTokenizer(documentReader.getText(0, lenght), "\n");
    }

    protected StringTokenizer getLineToken() {
        return lineToken;
    }

}
