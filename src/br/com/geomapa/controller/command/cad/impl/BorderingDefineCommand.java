/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.options.SchemeColors;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 *
 * @author paulocanedo
 */
public class BorderingDefineCommand extends AbstractCadCommand {

    protected static String[] messages = new String[]{
        "Selecione a parcela que deseja definir os confrontantes",
        "Informe o confrontante %s -> %s %s",
        "Não foi possível editar confrontantes em massa, '%s' não é um ponto apropriado",
        "O perímetro da parcela '%s' ainda não foi definido ou não existe"
    };
    private Polygonal polygonal;
    private Iterator<LineDivision> piterator;
    private boolean polygonalDefined = false;
    private Line lineAux = new Line();
    private String lastBorderName = "";
    private LineDivision currentLd;

    public BorderingDefineCommand() {
        super(messages[0]);
    }

    public BorderingDefineCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CommandException, CoordinateException, PolygonalException {
        if (polygonal == null) {
            MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();
            if (text.equals("0")) {
                text = "Planta Geral";
            }
            
            polygonal = mainPolygonal.containsPolygonalName(text) ? mainPolygonal.createOrGetPolygonal(text) : null;
            if (polygonal != null && polygonal.isClosed()) {
                polygonalDefined = true;
                piterator = polygonal.getLineDivisions().iterator();
                nextLineDivision();
            } else {
                super.message = String.format(messages[3], text);
            }
            return false;
        }

        if (!text.isEmpty() && polygonalDefined == true) {
            int indexOf = text.indexOf(">>");
            if (indexOf > 0) {
                String pointName = text.substring(indexOf + 2).trim();
                GeodesicPoint gpoint = searchPoint(pointName, polygonal);
                GeodesicPoint currentStartPoint = currentLd.getStartPoint();

                int indexOf1 = polygonal.indexOf(gpoint);
                int indexOf2 = polygonal.indexOf(currentStartPoint);

                if (gpoint != null && (indexOf1 == 0 || indexOf1 > indexOf2)) {
                    String borderName = text.substring(0, indexOf).trim();
                    while (!currentLd.getStartPoint().equals(gpoint) && piterator.hasNext()) {
                        currentLd.setBorderName(borderName);
                        nextLineDivision();
                    }

                    if (!piterator.hasNext()) {
                        currentLd.setBorderName(borderName);
                        execute();
                    }
                    return true;
                }
                super.message = String.format(messages[2], pointName);
                return false;
            } else if (text.equals("=")) {
                currentLd.setBorderName(lastBorderName);
            } else {
                currentLd.setBorderName(text);
                lastBorderName = text;
            }
        }
        nextLineDivision();
        return true;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (!polygonalDefined) {
            polygonal = MainPolygonal.findPolygonal(point.getX(), point.getY());
            if (polygonal != null) {
                polygonalDefined = true;

                piterator = polygonal.getLineDivisions().iterator();
                nextLineDivision();

                getDisplayPanel().setTempVO(lineAux);
                redraw();
                return true;
            } else {
                super.message = "Parcela não encontrada, " + messages[0];
            }
        }
        return false;
    }

    @Override
    public void execute() {
        getDisplayPanel().setTempVO(null);
        redraw();

        finish();
    }

    @Override
    public void undo() {
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Definir confrontantes";
    }

    @Override
    public VisualObject getVisualObject() {
        return lineAux;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new BorderingDefineCommand(displayPanel);
    }

    @Override
    public boolean canSelect() {
        return false;
    }

    @Override
    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException {
        super.setDisplayPanel(displayPanel);

        if (!getPolygonal().isMain()) {
            polygonal = displayPanel.getPolygonal();
            polygonalDefined = true;

            piterator = polygonal.getLineDivisions().iterator();
            nextLineDivision();
        }
    }

    private void nextLineDivision() {
        if (piterator.hasNext()) {
            currentLd = piterator.next();
            lineAux.setLocation(currentLd.getLocation());
            lineAux.setEndLocation(currentLd.getEndLocation());
            lineAux.setColor(SchemeColors.SELECTED);
            lineAux.setLineWidth(SchemeColors.SELECTED_AND_BOLD);

            super.canDraw = true;
            String borderName = (currentLd.getBorderName().isEmpty()) ? "" : ("(" + currentLd.getBorderName() + ")");
            super.message = String.format(messages[1], currentLd.getStartPoint(), currentLd.getEndPoint(), borderName);
        } else {
            execute();
        }
    }

    @Override
    public boolean acceptSpaceBar() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "define_confrontacao";
    }
}
