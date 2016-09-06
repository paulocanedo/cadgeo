/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class LineCommand extends AbstractCadCommand {

    private List<Line> list = new ArrayList<Line>();
    private Line currentLine = new Line(0, 0, 0, 0);
    private int state = 0;
    private static final String[] messages = new String[]{
        "Informe o ponto inicial",
        "Informe o próximo ponto"
    };

    public LineCommand() {
        super(messages[0]);
    }

    public LineCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    private void insertLine() {
        if (list.isEmpty()) {
            stackUndo.add(this);
        }
        Point2D endLocation = currentLine.getEndLocation();
        list.add(currentLine);
        getDisplayPanel().addToVisualObjects(currentLine);

        currentLine = new Line();
        currentLine.setLocation(endLocation);
        currentLine.setEndLocation(endLocation);
        state = 1;
    }

    private void internalUndo() {
        if (!list.isEmpty()) {
            Line line = list.remove(list.size() - 1);
            getDisplayPanel().removeFromVisualObjects(line);
            currentLine = line;
        }
    }

    @Override
    public boolean transitToNextState(String text) throws CoordinateException {
        if (text.isEmpty()) {
            execute();
            return true;
        } else if (text.equalsIgnoreCase("u")) {
            internalUndo();
            return true;
        }

        if (state == 1) {
            try {
                Double value = getPositiveDouble(text, messages[1]);

                Point2D location = currentLine.getLocation();
                double azimuth = PolygonalUtils.azimuth(location, currentLine.getEndLocation()).toDegreeDecimal();

                double[] projection = PolygonalUtils.projection(location.getX(), location.getY(), value, azimuth);
                currentLine.setEndLocation(projection[0], projection[1]);

                insertLine();
                return true;
            } catch (Exception ex) {
            }
        }
        Point2D point = parsePoint(text);
        return nextState(point);
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        switch (state) {
            case 0:
                currentLine.setLocation(point);
                currentLine.setEndLocation(point);

                this.message = messages[1];
                this.canDraw = true;

                getDisplayPanel().setTempVO(currentLine);
                state++;
                return true;
            case 1: {
                currentLine.setEndLocation(point);

                insertLine();
                return true;
            }
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        currentLine.setEndLocation(point);
    }

    @Override
    public void execute() {
        finish();
    }

    @Override
    public void canceled() {
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
        return "Linha";
    }

    @Override
    public boolean canUseMagnetic() {
        return true;
    }

    @Override
    public Line getVisualObject() {
        return currentLine;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new LineCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "linha";
    }
}
