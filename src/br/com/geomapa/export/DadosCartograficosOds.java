/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.export;

import br.com.geomapa.util.AngleValue;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.point.PointStoredData;
import br.com.geomapa.geodesic.PolygonalUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author paulocanedo
 */
public class DadosCartograficosOds {

    private File templateFile;
    private String startRowId;
    private String endRowId;
    private String defaultCharset = "UTF-8";
    private static final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.ENGLISH);
    private static final DecimalFormat decimalFormat2c = new DecimalFormat("0.00", dfs);
    private static final DecimalFormat decimalFormat3c = new DecimalFormat("0.000", dfs);
    private static final DecimalFormat decimalFormat4c = new DecimalFormat("0.0000", dfs);
    private SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd __ MMMM __ yyyy", new Locale("pt", "br"));

    public DadosCartograficosOds(File templateFile) throws IOException {
        this.templateFile = templateFile;

        Properties prop = new Properties();
        prop.load(getClass().getResourceAsStream("/br/com/geomapa/resources/properties/row_template.properties"));
        startRowId = prop.getProperty("start_row");
        endRowId = prop.getProperty("end_row");
    }

    public DadosCartograficosOds(InputStream stream) throws IOException {
        File tempFile = File.createTempFile("pc9_", ".ods");
        FileOutputStream fos = new FileOutputStream(tempFile);

        int readed;
        byte[] buffer = new byte[4096];
        while ((readed = stream.read(buffer)) > 0) {
            fos.write(buffer, 0, readed);
        }
        fos.close();

        this.templateFile = tempFile;

        Properties prop = new Properties();
        prop.load(getClass().getResourceAsStream("/br/com/geomapa/resources/properties/row_template.properties"));
        startRowId = prop.getProperty("start_row");
        endRowId = prop.getProperty("end_row");
    }

    public void generate(File outputFile, Map<String, String> header, List<GeodesicPoint> points) throws IOException {
        GeodesicPoint[] aPoints = points.toArray(new GeodesicPoint[0]);
        double perimeter = PolygonalUtils.perimeter(aPoints);
        double area = PolygonalUtils.area(aPoints) / 10000; //area em hectares

        ZipFile zipfile = new ZipFile(templateFile);

        Enumeration<? extends ZipEntry> entries = zipfile.entries();
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            InputStream in = zipfile.getInputStream(entry);

            out.putNextEntry(new ZipEntry(entry.getName()));

            if (entry.getName().equals("content.xml")) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, defaultCharset));

                StringBuilder strBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.toLowerCase().contains("dados ")) {
                        System.out.println(line);
                    }
                    line = line.replace("$NOME_DA_PLANILHA", header.get("$NOME_DA_PLANILHA"));
                    line = line.replace("$IMOVEL", header.get("$IMOVEL"));
                    line = line.replace("$PROPRIETARIO", header.get("$PROPRIETARIO"));
                    line = line.replace("$MATRICULA", header.get("$MATRICULA"));
                    line = line.replace("$SNCR_IMOVEL", header.get("$SNCR_IMOVEL"));
                    line = line.replace("$COMARCA", header.get("$COMARCA"));
                    line = line.replace("$CPF_PROPRIETARIO", header.get("$CPF_PROPRIETARIO"));
                    line = line.replace("$CIRCUNSCRICAO", header.get("$CIRCUNSCRICAO"));
                    line = line.replace("$AREA", decimalFormat4c.format(area));
                    line = line.replace("$PERIMETRO", decimalFormat2c.format(perimeter));
                    line = line.replace("$MUNICIPIO_UF", header.get("$MUNICIPIO_UF"));
                    line = line.replace("$DATUM", header.get("$DATUM"));
                    line = line.replace("$DATA_AGORA", fullDateFormat.format(new Date()).replace("__", "de"));

                    strBuilder.append(line).append("\n");
                }

                int startRowTemplate = -1;
                int endRowTemplate = 0;
                String lastPart = null;
                while ((startRowTemplate = strBuilder.indexOf(startRowId, startRowTemplate + 1)) > 0) {
                    String firstPart = strBuilder.substring(endRowTemplate, startRowTemplate);
                    endRowTemplate = (strBuilder.indexOf(endRowId, startRowTemplate)) + endRowId.length();
                    lastPart = strBuilder.substring(endRowTemplate);

                    out.write(firstPart.getBytes(defaultCharset));
                    String rowTemplate = strBuilder.substring(startRowTemplate, endRowTemplate);
                    rowTemplate = rowTemplate.replace(startRowId, "").replace(endRowId, "");

                    int sequence = 0;
                    for (int i = 0; i < points.size(); i++) {
                        String noValue = "99";
                        GeodesicPoint point = points.get(i);
                        GeodesicPoint nextPoint = null;

                        if (i == points.size() - 1) {
                            nextPoint = points.get(0);
                        } else {
                            nextPoint = points.get(i + 1);
                        }

                        double qx = point.getMetaData().getQx();
                        double qy = point.getMetaData().getQy();
                        double qz = point.getMetaData().getQz();
                        double elipsoidalHeight = point.getCoordinate().getEllipsoidalHeight();
                        String newRow = rowTemplate;

                        newRow = newRow.replace("$SEQ_VERTICE", String.valueOf(++sequence));
                        newRow = newRow.replace("$VERTICE", normalizePointName(point.getName()));
                        newRow = newRow.replace("$COORD_ESTE", decimalFormat3c.format(point.getCoordinate().toUTM().getEast()));
                        newRow = newRow.replace("$RMS_ESTE", qx != Float.NEGATIVE_INFINITY ? decimalFormat4c.format(qx) : noValue);
                        newRow = newRow.replace("$COORD_NORTE", decimalFormat3c.format(point.getCoordinate().toUTM().getNorth()));
                        newRow = newRow.replace("$RMS_NORTE", qy != Float.NEGATIVE_INFINITY ? decimalFormat4c.format(qy) : noValue);
                        newRow = newRow.replace("$ALTITUDE", elipsoidalHeight != Double.NEGATIVE_INFINITY ? decimalFormat3c.format(elipsoidalHeight) : noValue);
                        newRow = newRow.replace("$RMS_ALT", qz != Float.NEGATIVE_INFINITY ? decimalFormat4c.format(qz) : noValue);

                        File rinex = point.getMetaData().getRinex();
                        newRow = newRow.replace("$ARQUIVO_RINEX", rinex != null ? rinex.getName() : "");
                        newRow = newRow.replace("$NOME_CONFRONTANTE", "");
                        newRow = newRow.replace("$MARGEM_RIO", "");
                        newRow = newRow.replace("$DIRECAO_RIO", "");

                        if (nextPoint != null) {
                            newRow = newRow.replace("$PROXIMO_VERTICE", normalizePointName(nextPoint.getName()));
                            newRow = newRow.replace("$DISTANCIA", decimalFormat2c.format(point.horizontalDistance(nextPoint)));
                            newRow = newRow.replace("$AZIMUTE", point.azimuth(nextPoint).toString("dd" + AngleValue.UNICODE_DEGREE + "mm\'ss\"", 0));
                            newRow = newRow.replace("$MC", "");
                        }
                        out.write(newRow.getBytes(defaultCharset));
                    }
                }
                out.write(lastPart.getBytes(defaultCharset));
            } else {
                byte[] buffer = new byte[1024];
                int readed = 0;
                while ((readed = in.read(buffer)) > 0) {
                    out.write(buffer, 0, readed);
                }
            }

            out.closeEntry();
        }
        out.close();
    }

    private String normalizePointName(String name) {
        if (name.length() == 10 || name.length() < 5) {
            return name;
        }

        StringBuilder sb = new StringBuilder(name);
        sb.insert(3, "-");
        sb.insert(5, "-");
        return sb.toString();
    }
}
