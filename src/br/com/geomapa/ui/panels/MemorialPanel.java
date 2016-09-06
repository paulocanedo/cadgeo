/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.export.Exporter;
import br.com.geomapa.export.TechnicalReport;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.VariableControl;
import br.com.geomapa.geodesic.coordinate.Longitude;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.geodesic.memorial.Memorial;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.report.ReportGenerator;
import br.com.geomapa.ui.panels.options.ResponsavelTecnico;
import br.com.geomapa.util.unit.impl.AzimuthUnit;
import br.com.geomapa.util.unit.impl.HectareUnit;
import br.com.geomapa.util.unit.impl.Meter;
import br.com.geomapa.util.unit.specs.AreaUnit;
import br.com.geomapa.util.unit.specs.DirectionUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

/**
 *
 * @author paulocanedo
 */
public class MemorialPanel extends JPanel implements GeodesicPanel {

    private JEditorPane editorPane;
    private JCheckBox aspasMarcoCheckBox;
    private JCheckBox pontoNegritoCheckBox;
    private JCheckBox intermediarioNegritoCheckBox;
    private JCheckBox coordNegritoCheckBox;
    private Polygonal polygonal = DataManagement.getMainPolygonal();
    private HashMap<String, Object> headerValues = new HashMap<String, Object>();
    private DecimalFormat decimalFormat = new DecimalFormat("0.000");

    public MemorialPanel() {
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        JPanel firstLine = new JPanel();
        firstLine.setLayout(new BoxLayout(firstLine, BoxLayout.LINE_AXIS));
        firstLine.setBorder(border);

        MemorialConfigItemListener listener = new MemorialConfigItemListener();
        aspasMarcoCheckBox = new JCheckBox("Ponto entre aspas", true);
        aspasMarcoCheckBox.addItemListener(listener);
        aspasMarcoCheckBox.setToolTipText("Adiciona uma aspa simples antes e depois de cada marco");

        pontoNegritoCheckBox = new JCheckBox("Ponto em destaque", true);
        pontoNegritoCheckBox.addItemListener(listener);
        pontoNegritoCheckBox.setToolTipText("Aplica o estilo de negrito nos pontos deste memorial");

        intermediarioNegritoCheckBox = new JCheckBox("Destacar pontos intermediários", true);
        intermediarioNegritoCheckBox.addItemListener(listener);
        intermediarioNegritoCheckBox.setToolTipText("Aplica o estilo de negrito também aos pontos intermediários de mesmo limite dos anteriores");

        coordNegritoCheckBox = new JCheckBox("Coordenadas em destaque", true);
        coordNegritoCheckBox.addItemListener(listener);
        coordNegritoCheckBox.setToolTipText("Aplica o estilo de negrito para as coordenadas dos pontos");

        firstLine.add(aspasMarcoCheckBox);
        firstLine.add(pontoNegritoCheckBox);
        firstLine.add(coordNegritoCheckBox);
        firstLine.add(intermediarioNegritoCheckBox);
        firstLine.add(Box.createHorizontalGlue());

        JPanel secondLine = new JPanel(new GridLayout());
        secondLine.add(new JScrollPane(editorPane));
        secondLine.setBorder(border);

        add(firstLine);
        add(secondLine);
    }

    @Override
    public void filter(String text) {
    }

    @Override
    public String action(String text) {
        return null;
    }

    @Override
    public void refresh() {
        if (!polygonal.isClosed() || !polygonal.isBorderingFilled()) {
            editorPane.setText("A poligonal não está fechada ou os limites e confrontações não foram definidos");
            return;
        }
        Main main = Main.getInstance();
        ProjectMetadata projectInfo = main.getProjectInfo();

        editorPane.setText(String.format("<html><div align='justify'>&nbsp;&nbsp;&nbsp;&nbsp;%s</div></html>", getINCRAMemorialText(polygonal, projectInfo)));
    }

    private String getINCRAMemorialText(Polygonal polygonal, ProjectMetadata projectMetadata) {
        Memorial memorial = new Memorial(polygonal, projectMetadata);
        memorial.setAspasMarco(aspasMarcoCheckBox.isSelected());
        memorial.setPontoNegrito(pontoNegritoCheckBox.isSelected());
        memorial.setIntermediarioNegrito(intermediarioNegritoCheckBox.isSelected());
        memorial.setCoordNegrito(coordNegritoCheckBox.isSelected());

        return memorial.toString();
    }

    private String getINCRAMemorialTextForOdt(Polygonal polygonal, ProjectMetadata projectMetadata) {
        String memorialText = getINCRAMemorialText(polygonal, projectMetadata);

        memorialText = memorialText.replaceAll(Pattern.quote("<b>"), "<text:span text:style-name=\"T1\">");
        memorialText = memorialText.replaceAll(Pattern.quote("</b>"), "</text:span>");
        return memorialText;
    }

    @Override
    public Polygonal getPolygonal() {
        return polygonal;
    }

    @Override
    public void setPolygonal(Polygonal polygonal) {
        this.polygonal = polygonal;
    }

    @Override
    public void export(Polygonal polygonal) throws IOException {
        Main instance = Main.getInstance();
        ProjectMetadata projectMetada = instance.getProjectInfo();

        save(polygonal, projectMetada);
    }

    public void save(Polygonal polygonal, ProjectMetadata projectMetada) throws IOException {
        DistanceUnit dUnit = new Meter();
        AreaUnit aUnit = new HectareUnit();
        ResponsavelTecnico rt = projectMetada.getResponsavelTecnico();

        headerValues.clear();
        headerValues.put("area", aUnit.toString(polygonal.area(), 4));
        headerValues.put("perimetro", dUnit.toString(polygonal.perimeter(), 2));
        headerValues.put("DESCRICAO_PERIMETRO", getINCRAMemorialTextForOdt(polygonal, projectMetada));
        headerValues.put("tabela_memorial", getTerraLegalMemorialTextForOdt(polygonal));
        headerValues.put("info", projectMetada);
        headerValues.put("parcela", polygonal.getMetadata());
        headerValues.put("art_numero", projectMetada.getArtNumero());
        headerValues.put("rt_nome", rt == null ? "" : rt.getNome());
        headerValues.put("rt_crea", rt == null ? "" : rt.getCodigoCrea());
        headerValues.put("rt_codigo_credenciamento", rt == null ? "" : rt.getCodigoIncra());

        ReportGenerator reportMemoIncraGenerator = new ReportGenerator(Exporter.getStream(projectMetada, Exporter.ExporterId.INCRA_MEMO));
        ReportGenerator reportTerraLegalIncraGenerator = new ReportGenerator(Exporter.getStream(projectMetada, Exporter.ExporterId.TERRA_LEGAL_MEMO));

        File memoFolder = projectMetada.getMemoFolder();
        memoFolder.mkdir();

        File incraMemoFolder = new File(memoFolder, "incra");
        incraMemoFolder.mkdir();
        File terraLegalMemoFolder = new File(memoFolder, "terra_legal");
        terraLegalMemoFolder.mkdir();

        File incraFile = new File(incraMemoFolder, polygonal.getName() + ".odt");
        File terraLegalFile = new File(terraLegalMemoFolder, polygonal.getName() + ".odt");
        reportMemoIncraGenerator.generate(incraFile, headerValues);
        reportTerraLegalIncraGenerator.generate(terraLegalFile, headerValues);

        TechnicalReport.export(polygonal);
    }

    private String getTerraLegalMemorialTextForOdt(Polygonal polygonal) throws IOException {
        if (polygonal == null || !polygonal.isClosed()) {
            return "";
        }
        int zonaUtm = VariableControl.getZonaUtm();

        StringBuilder sb = new StringBuilder();
        sb.append("<table:table table:name=\"descricao_perimetro\" table:style-name=\"descricao_5f_perimetro\">"); //start table

        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/br/com/geomapa/geodesic/memorial/terra_legal_header_odt.xml"), "utf-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        DistanceUnit dUnit = new Meter();
        DirectionUnit aUnit = new AzimuthUnit();
        LinkedList<LineDivision> linedivisions = polygonal.getLineDivisions();
        for (LineDivision ld : linedivisions) {
            GeodesicPoint startPoint = ld.getStartPoint();
            GeodesicPoint endPoint = ld.getEndPoint();
            UTMCoordinate coordinate = endPoint.getCoordinate(zonaUtm).toUTM();
            Longitude mc = Longitude.getCentralMeridian(coordinate.toUTM().getZone());

            sb.append("<table:table-row>");
            sb.append("<table:table-cell table:style-name=\"descricao_5f_perimetro.F2\" office:value-type=\"string\">");
            sb.append(String.format("<text:p text:style-name=\"Table_20_Contents\">%s</text:p>", startPoint));
            sb.append("</table:table-cell>");
            sb.append("<table:table-cell table:style-name=\"descricao_5f_perimetro.F2\" office:value-type=\"string\">");
            sb.append(String.format("<text:p text:style-name=\"Table_20_Contents\">%s</text:p>", endPoint));
            sb.append("</table:table-cell>");
            sb.append("<table:table-cell table:style-name=\"descricao_5f_perimetro.F2\" office:value-type=\"string\">");
            sb.append(String.format("<text:p text:style-name=\"Table_20_Contents\">%s</text:p>", dUnit.toString(startPoint.horizontalDistance(endPoint), 2)));
            sb.append("</table:table-cell>");
            sb.append("<table:table-cell table:style-name=\"descricao_5f_perimetro.F2\" office:value-type=\"string\">");
            sb.append(String.format("<text:p text:style-name=\"Table_20_Contents\">%s</text:p>", aUnit.toString(startPoint.azimuth(endPoint), 0)));
            sb.append("</table:table-cell>");
            sb.append("<table:table-cell table:style-name=\"descricao_5f_perimetro.F2\" office:value-type=\"string\">");
            sb.append(String.format("<text:p text:style-name=\"Table_20_Contents\">%s</text:p>", mc.toMeridianCentralString()));
            sb.append("</table:table-cell>");
            sb.append("<table:table-cell table:style-name=\"descricao_5f_perimetro.F2\" office:value-type=\"string\">");
            sb.append(String.format("<text:p text:style-name=\"Table_20_Contents\">%s</text:p>", decimalFormat.format(coordinate.getEast())));
            sb.append("</table:table-cell>");
            sb.append("<table:table-cell table:style-name=\"descricao_5f_perimetro.F2\" office:value-type=\"string\">");
            sb.append(String.format("<text:p text:style-name=\"Table_20_Contents\">%s</text:p>", decimalFormat.format(coordinate.getNorth())));
            sb.append("</table:table-cell>");
            sb.append("<table:table-cell table:style-name=\"descricao_5f_perimetro.H3\" office:value-type=\"string\">");
            sb.append(String.format("<text:p text:style-name=\"Table_20_Contents\">%s</text:p>", VariableControl.getProprietarioFromId(ld.getBorderName())));
            sb.append("</table:table-cell>");
            sb.append("</table:table-row>");
        }

        sb.append("</table:table>"); //end table

        return sb.toString();
    }

    private class MemorialConfigItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            refresh();
        }
    }
    public static final String FILE_NAME_TERRA_LEGAL = "memorial_terra_legal.odt";
    public static final String FILE_NAME_INCRA = "memorial_incra.odt";
}
