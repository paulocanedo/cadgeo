/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class BorderingSeparatorCommand extends AbstractCadCommand {

    private List<Line> lines = new ArrayList<Line>();
    private double size = 20;
    private static final String[] messages = new String[]{"Informe o tamanho da linha de separação"};

    public BorderingSeparatorCommand() {
        super(messages[0]);
    }

    public BorderingSeparatorCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws PolygonalException {
        size = getPositiveDouble(text, "O tamanho da linha de separação deve ser maior do que zero");
        execute();
        return true;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        return false;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
    }

    @Override
    public void execute() {
        String currentBorder = null;
        LinkedList<LineDivision> lineDivisions = getPolygonal().getLineDivisions();
        for (LineDivision ld : lineDivisions) {
            if (currentBorder != null && !currentBorder.equals(ld.getBorderName())) {
                double projectAzimuth = 0;
                for (LineDivision ld1 : LineDivision.getAllLineDivisions()) {
                    if (ld.getStartPoint().equals(ld1.getStartPoint()) && !getPolygonal().contains(ld1)) {
                        projectAzimuth = ld1.azimuth().toDegreeDecimal();
                    }
                }
                Point2D location = ld.getLocation();
                double[] projection = PolygonalUtils.projection(location.getX(), location.getY(), size, projectAzimuth);

                Line line = new Line(location.getX(), location.getY(), projection[0], projection[1]);
                line.setLayer(LayerController.find("Confrontantes"));
                line.setLineType(AbstractVisualObject.DASHED_LINE_TYPE);
                lines.add(line);
            }
            currentBorder = ld.getBorderName();
        }

        if (!lineDivisions.isEmpty() && currentBorder != null && !currentBorder.equals(lineDivisions.getFirst().getBorderName())) {
            Point2D location = lineDivisions.getFirst().getLocation();
            double[] projection = PolygonalUtils.projection(location.getX(), location.getY(), size, 0);

            Line line = new Line(location.getX(), location.getY(), projection[0], projection[1]);
            line.setLayer(LayerController.find("Confrontantes"));
            line.setLineType(AbstractVisualObject.DASHED_LINE_TYPE);
            lines.add(line);
        }
        getDisplayPanel().addToVisualObjects(lines.toArray(new VisualObject[0]));

        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(lines.toArray(new VisualObject[0]));
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Inserir separador de Confrontante";
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new BorderingSeparatorCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "separador_confrontacao";
    }
}
