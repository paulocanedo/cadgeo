/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.util.mlist.MacroList;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author paulocanedo
 */
public class StatisticsPanel extends JPanel implements GeodesicPanel {

    private JEditorPane editorPane = new JEditorPane();

    public StatisticsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(editorPane);
        add(scrollPane);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });
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
        MacroList<GeodesicPoint> allPoints = DataManagement.getAllPoints();
        int totalM = 0, totalP = 0, totalO = 0, totalV = 0, totalOther = 0;
        for (GeodesicPoint gp : allPoints) {
            if (gp.getType() == GeodesicPointType.M) {
                totalM++;
            } else if (gp.getType() == GeodesicPointType.O) {
                totalO++;
            } else if (gp.getType() == GeodesicPointType.P) {
                totalP++;
            } else if (gp.getType() == GeodesicPointType.V) {
                totalV++;
            } else if (gp.getType() == GeodesicPointType.X) {
                totalOther++;
            }
        }

        editorPane.setText(String.format("<html>"
                + "<p><b>Ponto marco: </b>%d</p>"
                + "<p><b>Pontos offset: </b>%d</p>"
                + "<p><b>Pontos tipo P: </b>%d</p>"
                + "<p><b>Pontos virtual: </b>%d</p>"
                + "<p><b>Outros: </b>%d</p>"
                + "<p><b>Total Pontos: </b>%d</p>"
                + "<p><b>Parcelas: </b>%d</p>"
                + "</html>", totalM, totalO, totalP, totalV, totalOther, allPoints.size(), DataManagement.getMainPolygonal().getChildrenSize()));
    }

    @Override
    public void setPolygonal(Polygonal polygonal) {
    }

    @Override
    public Polygonal getPolygonal() {
        return null;
    }

    @Override
    public void export(Polygonal polygonal) throws IOException {
    }
}
