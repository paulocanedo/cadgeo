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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author paulocanedo
 */
public class AutotopoImporter extends PointImporter {

    private Workbook workBook;
    private Sheet sheet;
    private int rowPosition = 11;

    public AutotopoImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, boolean msxml, int utmZone, Hemisphere hemisphere, Datum datum) throws IOException {
        super(handle, stream, sourceOrder, utmZone, hemisphere, datum);

        if (msxml) {
            this.workBook = new HSSFWorkbook(stream);
        } else {
            this.workBook = new XSSFWorkbook(stream);
        }

        this.sheet = workBook.getSheetAt(0);
    }

    @Override
    protected String[] parseRecord(InputStream stream) throws IOException {
        if (rowPosition < sheet.getPhysicalNumberOfRows()) {
            Row row = sheet.getRow(rowPosition++);
            if (row != null) {
                Cell cell;

                cell = row.getCell(0);
                if (cell != null && cell.getStringCellValue().trim().equals("") == false) {
                    String name = cell.toString();
                    String nextPoint = row.getCell(1).toString();
                    String north = row.getCell(2).toString();
                    String east = row.getCell(3).toString();
                    String azimuth = row.getCell(4).toString();
                    String distance = row.getCell(5).toString();
                    String factorK = row.getCell(6).toString();
                    String latitude = row.getCell(7).toString();
                    String longitude = row.getCell(8).toString();

                    return new String[]{name, nextPoint, north, east, azimuth, distance, factorK, latitude, longitude};
                }
            }
        }
        return null;
    }

}
