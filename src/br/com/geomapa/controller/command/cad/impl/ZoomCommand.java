/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CadCommandController;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.graphic.cad.primitives.FilledRectangle;
import br.com.geomapa.graphic.cad.primitives.Rectangle;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.options.SchemeColors;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.util.ArraysUtil;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;

/**
 *
 * @author paulocanedo
 */
public class ZoomCommand extends AbstractCadCommand {

    private Rectangle rect = new Rectangle(0, 0, 0, 0);
    private static final FilledRectangle rectView = new FilledRectangle(0, 0, 0, 0, SchemeColors.ZOOM_BORDER, SchemeColors.ZOOM_FILL);
    private int state = -1;
    private char aChar = ' ';
    private static final String[] messages = new String[]{"Informe o ponto inicial do zoom ou uma opção: (E)Estendido (P)Anterior"};
    private static Stack<Rectangle> previousStack = new Stack<Rectangle>();

    public ZoomCommand() {
        super(messages[0]);

        CadCommandController.storeCurrentCommand();
    }

    public ZoomCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);

        CadCommandController.storeCurrentCommand();
    }

    @Override
    public boolean transitToNextState(String text) throws CommandException {
        try {
            Point2D point = parsePoint(text);
            return nextState(point);
        } catch (CoordinateException ex) {
        }

        aChar = getCharacter(text, "Informe um ponto ou uma das opções: (E)Estendido (P)Anterior", 'E', 'P');
        execute();
        return true;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (aChar != ' ') {
            return false;
        }
        switch (state) {
            case -1:
                rect.setLocation(point);
                rectView.setLocation(point);

                this.message = "Informe o segundo ponto";
                this.canDraw = true;
                break;
            case 0: {
                rect.setEndLocation(point);

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
        if (state == 0) {
            Point2D location = rect.getLocation();

            double[] cz = displayPanel.fixRectzoom(location.getX(), location.getY(), point.getX(), point.getY());
            rectView.setLocation(cz[0], cz[2]);
            rectView.setEndLocation(cz[1], cz[3]);
        }
    }
    private static Collection<VisualObject> collection = new ArrayList<VisualObject>();

    @Override
    public void execute() {
        if (aChar == 'E') {
            collection.clear();

            collection.addAll(Arrays.asList(getPolygonal().getVisualObjects()));
            if (getPolygonal().isMain()) {
                collection.addAll(DataManagement.getAllPoints().toList());
            }
            double[] corners = ArraysUtil.collectCorners(collection);
            if (corners[0] == Double.MAX_VALUE || corners[1] == Double.MAX_VALUE || corners[2] == Double.MIN_VALUE || corners[3] == Double.MIN_VALUE) {
                displayPanel.zoom(0, 0, displayPanel.getComponent().getWidth(), displayPanel.getComponent().getHeight());
            } else {
                previousStack.add(new Rectangle(displayPanel.getCornerX1(), displayPanel.getCornerY1(), displayPanel.getCornerX2(), displayPanel.getCornerY2()));
                displayPanel.zoom(corners[0], corners[1], corners[2], corners[3]);

                double width1 = corners[2] - corners[0];
                double width2 = displayPanel.getCornerX2() - displayPanel.getCornerX1();
                displayPanel.offset((width2 - width1) / 2, 0);
                displayPanel.zoomInOut(0.99f);
            }
        } else if (aChar == 'P') {
            if (!previousStack.isEmpty()) {
                Rectangle pRect = previousStack.pop();
                Point2D location = pRect.getLocation();
                Point2D endLocation = pRect.getEndLocation();
                displayPanel.zoom(location.getX(), location.getY(), endLocation.getX(), endLocation.getY());
            }
        } else {
            Point2D location = rect.getLocation();
            Point2D endLocation = rect.getEndLocation();

            previousStack.add(new Rectangle(displayPanel.getCornerX1(), displayPanel.getCornerY1(), displayPanel.getCornerX2(), displayPanel.getCornerY2()));
            displayPanel.zoom(location.getX(), location.getY(), endLocation.getX(), endLocation.getY());
        }

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
        return "Zoom";
    }

    @Override
    public FilledRectangle getVisualObject() {
        return rectView;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new ZoomCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "zoom";
    }
}
