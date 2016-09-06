/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.export.Exporter;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.coordinate.Longitude;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.report.ReportGenerator;
import br.com.geomapa.ui.model.GeodesicGridTableModel;
import br.com.geomapa.util.unit.specs.AreaUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;
import br.com.geomapa.util.unit.impl.HectareUnit;
import br.com.geomapa.util.unit.impl.Meter;
import br.com.geomapa.util.unit.impl.SquareMeterUnit;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;

/**
 *
 * @author paulocanedo
 */
public class CalculationAreaPanel extends JPanel implements GeodesicPanel {

    private GeodesicGridTableModel model;
    private JTable table;
    private String headerHtml = "<html>Cálculo de área - %s; <b>Área:</b> %s; <b>Perímetro:</b> %s</html>";
    private JLabel headerLabel = new JLabel();
    private Polygonal polygonal = DataManagement.getMainPolygonal();
    private HashMap<String, Object> headerValues = new HashMap<String, Object>();

    public CalculationAreaPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        JPanel firstLine = new JPanel();
        firstLine.setLayout(new BoxLayout(firstLine, BoxLayout.LINE_AXIS));
        firstLine.setBorder(border);
        firstLine.add(headerLabel);
        firstLine.add(Box.createHorizontalGlue());

        JPanel secondLine = new JPanel(new GridLayout());
        model = new GeodesicGridTableModel(polygonal, GeodesicGridTableModel.columnNamesCalculationArea);
        table = new JTable(model);

        secondLine.add(new JScrollPane(table));
        secondLine.setBorder(border);

        add(firstLine);
        add(secondLine);
    }

    @Override
    public void refresh() {
        String name = polygonal.getName();

        try {
            headerLabel.setText(String.format(headerHtml, name, aUnit.toString(polygonal.area(), 4), dUnit.toString(polygonal.perimeter(), 2)));
        } catch (Exception ex) {
            headerLabel.setText(ex.getMessage());
        }
        model.fireTableDataChanged();
    }

    @Override
    public void export(Polygonal polygonal) throws IOException {
        Main main = Main.getInstance();
        ProjectMetadata projectMetadata = main.getProjectInfo();
        File areaCalcFolder = projectMetadata.getAreaCalcFolder();
        areaCalcFolder.mkdir();

        File file = new File(areaCalcFolder, polygonal.getName() + ".odt");
        export(polygonal, file);
    }

    public void export(Polygonal polygonal, File out) throws IOException {
        Polygonal oldPolygonal = this.polygonal;

        try {
            setPolygonal(polygonal);
            Main main = Main.getInstance();
            ProjectMetadata projectMetadata = main.getProjectInfo();

            headerValues.clear();
            double area = polygonal.area();
            headerValues.put("area_total_m2", sUnit.toString(area, 2));
            headerValues.put("area_total_ha", aUnit.toString(area, 4));
            headerValues.put("perimetro", dUnit.toString(polygonal.perimeter(), 2));//TODO obter meridiano central pela parcela ao inves do projeto
            headerValues.put("meridiano_central", new Longitude(Longitude.calcCentralMeridian(projectMetadata.getZonaUtm())).toMeridianCentralString());
            headerValues.put("info", projectMetadata);
            headerValues.put("parcela", polygonal.getMetadata());

            String odtTab = "<text:tab/>";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    sb.append(table.getValueAt(i, j)).append(odtTab);
                }
                sb.delete(sb.length() - odtTab.length(), sb.length());
                sb.append("<text:line-break/>");
            }
            headerValues.put("tabela_calculo", sb.toString());

            ReportGenerator reportGenerator = new ReportGenerator(Exporter.getStream(projectMetadata, Exporter.ExporterId.CALCULATION_AREA));
            reportGenerator.generate(out, headerValues);
        } finally {
            setPolygonal(oldPolygonal);
        }
    }

    @Override
    public void filter(String text) {
    }

    @Override
    public String action(String text) {
        return null;
    }

    @Override
    public Polygonal getPolygonal() {
        return polygonal;
    }

    @Override
    public void setPolygonal(Polygonal polygonal) {
        this.polygonal = polygonal;
        this.model.setPolygonal(polygonal);
    }
    private AreaUnit aUnit = new HectareUnit();
    private AreaUnit sUnit = new SquareMeterUnit();
    private DistanceUnit dUnit = new Meter();
    
    public static final String FILE_NAME = "calculo_area.odt";
}
