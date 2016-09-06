/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class LineDivisionCommand extends AbstractCadCommand {

    private List<LineDivision> list = new ArrayList<LineDivision>();
    private LineDivision currentLineDivision;
    private Line line = new Line(0, 0, 0, 0);
    private GeodesicPoint from;
    private GeodesicPoint to;
    private static final String[] messages = new String[]{
        "Informe o ponto inicial", 
        "Informe o próximo ponto"
    };

    public LineDivisionCommand() {
        super(messages[0]);
    }

    public LineDivisionCommand(GLTopographicPanel displayPanel) throws CommandException {
        super(displayPanel, messages[0]);

        checkIfPolygonalIsMain();
    }

    private void insertLine() {
        if (list.isEmpty()) {
            stackUndo.add(this);
        }
        GeodesicPoint endpoint = to;
        currentLineDivision = LineDivision.getInstance(from, to);
        currentLineDivision.setLayer(LayerController.getCurrentLayer());
        list.add(currentLineDivision);
        getDisplayPanel().addToVisualObjects(currentLineDivision);

        from = endpoint;
        to = null;
        line.setLocation(endpoint.getLocation());
        line.setEndLocation(endpoint.getLocation());
        super.message = messages[1];
    }

    private void internalUndo() {
        if (!list.isEmpty()) {
            LineDivision ld = list.remove(list.size() - 1);
            getDisplayPanel().removeFromVisualObjects(ld);

            from = ld.getStartPoint();
            to = null;
            line.setLocation(ld.getLocation());
            line.setEndLocation(ld.getEndLocation());
        }
    }
    
    private void setStartPoint(GeodesicPoint gpoint) {
        this.from = gpoint;
        this.line.setLocation(gpoint.getLocation());
        super.message = messages[1];
        super.canDraw = true;
    }

    @Override
    public boolean transitToNextState(String text) throws PolygonalException {
        if (text.isEmpty()) {
            execute();
            return true;
        } else if (text.equalsIgnoreCase("u")) {
            internalUndo();
            return true;
        }

        GeodesicPoint findPoint = searchPoint(text);
        if (findPoint == null) {
            throw new PolygonalException(String.format("O ponto %s não foi encontrado.", text));
        }

        if (from == null) {
            setStartPoint(findPoint);
        } else {
            to = findPoint;
            insertLine();
        }
        return true;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        for (GeodesicPoint gpoint : DataManagement.getAllPoints()) {
            Point2D location = gpoint.getLocation();

            if (PolygonalUtils.horizontalDistance(location.getX(), location.getY(), point.getX(), point.getY()) < 0.1) {
                if (from == null) {
                    setStartPoint(gpoint);
                } else {
                    to = gpoint;
                    insertLine();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        line.setEndLocation(point);
    }

    @Override
    public void execute() {
        finish();
    }

    @Override
    public void undo() {
        if (!list.isEmpty()) {
            getDisplayPanel().removeFromVisualObjects(list.toArray(new VisualObject[0]));
        }
    }

    @Override
    public boolean isUndoable() {
        return !list.isEmpty();
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Linha de divisão";
    }

    @Override
    public Line getVisualObject() {
        return line;
    }

    @Override
    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException {
        super.setDisplayPanel(displayPanel);
        checkIfPolygonalIsMain();
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new LineDivisionCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "linha_divisao";
    }
}
