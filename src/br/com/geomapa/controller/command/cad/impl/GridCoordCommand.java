/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.cad.geo.GridCoordinates;
import br.com.geomapa.graphic.cad.primitives.Rectangle;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.options.SchemeColors;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class GridCoordCommand extends AbstractCadCommand {

    private List<VisualObject> list;
    private Point2D startPoint = new Point2D.Double();
    private Point2D endPoint = new Point2D.Double();
    private double gridspace;
    private Rectangle rect = new Rectangle(0, 0, 0, 0);
    private int state = -1;
    private static final String[] messages = new String[]{"Informe o espaçamento entre as coordenadas da grade"};

    {
        rect.setLayer(LayerController.SELECTION_LAYER);
        rect.setLineWidth(SchemeColors.SELECTED_AND_BOLD);
    }

    public GridCoordCommand() {
        super(messages[0]);
    }

    public GridCoordCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CoordinateException {
        if (state == -1) {
            gridspace = getPositiveDouble(text, "Você deve informar um valor positivo para o espaçamento: %s");
            super.message = "Informe o ponto inicial da grade";
            state++;
            return true;
        }
        Point2D point = parsePoint(text);
        return nextState(point);
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        switch (state) {
            case -1: {
                return false;
            }
            case 0:
                rect.setLocation(point);
                startPoint = point;

                this.message = "Informe o ponto final da grade";
                this.canDraw = true;
                break;
            case 1: {
                endPoint = point;
                GridCoordinates gridCoordinates = new GridCoordinates(startPoint, endPoint, gridspace);
                this.list = gridCoordinates.createLabels();

                execute();
                break;
            }
            default:
                throw new AssertionError();
        }

        state++;
        return true;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        if (state == 1) {
            rect.setEndLocation(point);
        }
    }

    @Override
    public void execute() {
        Layer layer = LayerController.find("Grid");
        for (VisualObject vo : list) {
            vo.setLayer(layer);
        }
        getDisplayPanel().addToVisualObjects(list.toArray(new VisualObject[0]));
        redraw();

        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(list.toArray(new VisualObject[0]));
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Grade de coordenadas";
    }

    @Override
    public VisualObject getVisualObject() {
        return rect;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new GridCoordCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "grade_coordenada";
    }
}
