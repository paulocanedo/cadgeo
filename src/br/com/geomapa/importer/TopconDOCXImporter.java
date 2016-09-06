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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

/**
 *
 * @author paulocanedo
 */
public class TopconDOCXImporter extends PointImporter {

    private XWPFDocument document;
    private StringTokenizer tableCoordUTM;
    private StringTokenizer tableQuality;
    private Set controlPoints;

    public TopconDOCXImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum) throws IOException {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);

        this.document = new XWPFDocument(stream);
        for (Iterator<XWPFTable> it = document.getTablesIterator(); it.hasNext();) {
            XWPFTable table = it.next();

            String tableText = table.getText();
            StringTokenizer tokenizer = new StringTokenizer(tableText, "\n");

            String line = tokenizer.nextToken();
            if (line.equals("COORDENADAS UTM")) {
                tokenizer.nextToken(); //ignore table header
                tableCoordUTM = tokenizer;
            } else if (line.equals("PRECISÃO")) {
                tokenizer.nextToken(); //ignore table header
                tableQuality = tokenizer;
            } else if (line.equals("PONTOS DE CONTROLE")) {
                tokenizer.nextToken();

                controlPoints = new TreeSet();
                while (tokenizer.hasMoreTokens()) {
                    String l = tokenizer.nextToken();
                    StringTokenizer st = new StringTokenizer(l, "\t");
                    controlPoints.add(st.nextToken());
                }
            }
        }
    }

    @Override
    protected String[] parseRecord(InputStream stream) throws IOException {
        if (tableCoordUTM.hasMoreTokens() && tableQuality.hasMoreTokens()) {
            ArrayList<String> record = new ArrayList<String>();

            String[] lineRecord = tableCoordUTM.nextToken().split("\t");
            while (controlPoints.contains(lineRecord[0])) { //ignore control points
                lineRecord = tableCoordUTM.nextToken().split("\t");
            }

            for (String s : lineRecord) {
                record.add(s.trim());
            }

            lineRecord = tableQuality.nextToken().split("\t");
            record.add(lineRecord[4].trim()); //QX
            record.add(lineRecord[5].trim()); //QY
            record.add(lineRecord[6].trim()); //QZ

            return record.toArray(new String[0]);
        } else {
            return null;
        }
    }

}
