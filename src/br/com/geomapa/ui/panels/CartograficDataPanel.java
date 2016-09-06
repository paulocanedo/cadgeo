/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.export.Exporter;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.VariableControl;
import br.com.geomapa.geodesic.coordinate.Longitude;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.geodesic.point.MetaDataPoint;
import br.com.geomapa.importer.rinex.RinexUtil;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.report.ReportGenerator;
import br.com.geomapa.report.TableToTxt;
import br.com.geomapa.ui.model.GeodesicGridTableModel;
import br.com.geomapa.util.unit.impl.HectareUnit;
import br.com.geomapa.util.unit.impl.Meter;
import br.com.geomapa.util.unit.specs.AreaUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author paulocanedo
 */
public class CartograficDataPanel extends JPanel implements GeodesicPanel {

    private JTable table;
    private String headerHtml = "Dados Cartográficos";
    private JLabel headerLabel = new JLabel();
    private JComboBox estiloCB;
    private static final String terraLegalString = "Terra Legal";
    private static final String incraString = "INCRA";
    private GeodesicGridTableModel incraModel;
    private GeodesicGridTableModel terraLegalModel;
    private Polygonal polygonal = DataManagement.getMainPolygonal();

    public CartograficDataPanel() {
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

        estiloCB = new JComboBox(new String[]{terraLegalString, incraString});
        estiloCB.setMaximumSize(new Dimension(300, 25));
        estiloCB.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                refresh();
            }
        });
        firstLine.add(estiloCB);

        JPanel secondLine = new JPanel(new GridLayout());
        terraLegalModel = new GeodesicGridTableModel(polygonal, GeodesicGridTableModel.columnNamesTerraLegalDadosCartograficos);
        incraModel = new GeodesicGridTableModel(polygonal, GeodesicGridTableModel.columnNamesIncraDadosCartograficos);
        table = new JTable(terraLegalModel);

        secondLine.add(new JScrollPane(table));
        secondLine.setBorder(border);

        add(firstLine);
        add(secondLine);
    }

    @Override
    public void refresh() {
        headerLabel.setText(String.format("<html>%s - <b>%s</b></html>", headerHtml, polygonal.getName()));
        String selectedItem = (String) estiloCB.getSelectedItem();
        if (terraLegalString.equals(selectedItem)) {
            table.setModel(terraLegalModel);
        } else if (incraString.equals(selectedItem)) {
            table.setModel(incraModel);
        }

        AbstractTableModel model = (AbstractTableModel) table.getModel();
        model.fireTableDataChanged();
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
        this.incraModel.setPolygonal(polygonal);
        this.terraLegalModel.setPolygonal(polygonal);
    }

    @Override
    public void export(Polygonal polygonal) throws IOException {
        Polygonal oldPolygonal = this.polygonal;

        try {
            setPolygonal(polygonal);
            Main main = Main.getInstance();
            ProjectMetadata projectInfo = main.getProjectInfo();
            File cartFolder = projectInfo.getCartographicDataFolder();
            cartFolder.mkdir();

            File terraLegalFolder = new File(cartFolder, "terra_legal");
            terraLegalFolder.mkdir();

            File incraFolder = new File(cartFolder, "incra");
            incraFolder.mkdir();

            File terraLegalFile = new File(terraLegalFolder, polygonal.getName() + ".txt");
            File terraLegalValidacaoFile = new File(terraLegalFolder, polygonal.getName() + "_validacao.ods");
            File incraFile = new File(incraFolder, polygonal.getName() + ".ods");
            FileOutputStream out = new FileOutputStream(terraLegalFile);
            FileOutputStream outLimit = new FileOutputStream(terraLegalFile.getAbsolutePath().replace(".txt", "") + "_limites.txt");
            TableToTxt.exportToTxt(terraLegalModel, out);

            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            List<LineDivision> linedivisions = polygonal.getLineDivisions();
            for (int i = linedivisions.size(); i > 0; i--) {
                LineDivision ld = linedivisions.get(i - 1);
                GeodesicPoint point = ld.getStartPoint();
                GeodesicPoint endPoint = ld.getEndPoint();
                MetaDataPoint metaData = point.getMetaData();
                UTMCoordinate coord = point.getCoordinate().toUTM();
                File rinexFolder = Main.getInstance().getProjectInfo().getRinexFolder();

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("seq_vertice", i);
                map.put("vertice", point.getNameForceSeparators());
                map.put("vante", endPoint.getNameForceSeparators());
                map.put("coord_este", String.format("%.3f", coord.getEast()));
                map.put("rms_este", String.format("%.3f", metaData.getQx()));
                map.put("coord_norte", String.format("%.3f", coord.getNorth()));
                map.put("rms_norte", String.format("%.3f", metaData.getQy()));
                map.put("altitude", String.format("%.3f", coord.getEllipsoidalHeight()));
                map.put("rms_alt", String.format("%.3f", metaData.getQz()));
                map.put("metodo", metaData.getMeasurementMethod() == null ? "" : metaData.getMeasurementMethod().name());
                map.put("sncr_confrontante", VariableControl.getSNCRFromId(ld.getBorderName()));
                map.put("tipo_limite", ld.getType() == null ? "" : ld.getType().name());
                map.put("arquivo_rinex", RinexUtil.findRinexFile(rinexFolder, point.getName()));
                map.put("nome_confrontante", ld.getBorderName() == null ? "" : ld.getBorderName());
                map.put("margem_rio", "");
                map.put("direcao_rio", "");

                list.add(map);
            }

            for (int i = list.size() - 1; i >= 0; i--) {
                Map<String, Object> map = list.get(i);
                outLimit.write(String.format("%s\t%s\t%s\t%s\n",
                        map.get("vertice"),
                        map.get("vante"),
                        map.get("tipo_limite"),
                        map.get("nome_confrontante")).getBytes());
            }
            outLimit.close();

            ReportGenerator incraDcGenerator = new ReportGenerator(Exporter.getStream(projectInfo, Exporter.ExporterId.INCRA_CARTOGRAPHIC_DATA));
            incraDcGenerator.generate(incraFile, readMapConfig(polygonal), list);

            ReportGenerator validacaoDcGenerator = new ReportGenerator(Exporter.getStream(projectInfo, Exporter.ExporterId.TERRA_LEGAL_CARTOGRAPHIC_DATA));
            validacaoDcGenerator.generate(terraLegalValidacaoFile, readMapConfig(polygonal), list);
        } finally {
            setPolygonal(oldPolygonal);
        }
    }

    private Map<String, Object> readMapConfig(Polygonal polygonal) {
        Main main = Main.getInstance();
        ProjectMetadata projectInfo = main.getProjectInfo();
        Map<String, Object> mapConfig = new HashMap<String, Object>();
        double area = polygonal.area();
        double perimeter = polygonal.perimeter();
        AreaUnit aUnit = new HectareUnit();
        DistanceUnit dUnit = new Meter();

        mapConfig.put("nomePlanilha", "Planilha INCRA");
        mapConfig.put("meridiano_central", Longitude.calcCentralMeridian(projectInfo.getZonaUtm()));
        mapConfig.put("info", projectInfo);
        mapConfig.put("parcela", polygonal.getMetadata());
        mapConfig.put("area", aUnit.toString(area, 4));
        mapConfig.put("area_ha", aUnit.getValue(area));
        mapConfig.put("perimetro", dUnit.toString(perimeter, 2));
        mapConfig.put("perimetro_m", dUnit.getValue(perimeter));

        return mapConfig;
    }
    public static final String FILE_NAME_INCRA = "incra_dados_cartograficos.ods";
    public static final String FILE_NAME_TERRA_LEGAL = "terra_legal_validar.ods";
}
