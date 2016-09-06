/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.graphic.cad.primitives.Rectangle;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class RectangleCommand extends AbstractCadCommand {

    private Rectangle rect = new Rectangle(0, 0, 0, 0);
    private int state = -1;
    private static final String[] messages = new String[] {"Informe o ponto inicial"};

    public RectangleCommand() {
        super(messages[0]);
    }
    
    public RectangleCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CoordinateException {
        if (state == 0) {
            Point2D point = UTMCoordinate.derivatePoint2D(rect.getLocation(), text);
            if (point != null) {
                return nextState(point);
            }
        }
        Point2D point = parsePoint(text);
        return nextState(point);
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        switch (state) {
            case -1:
                rect.setLocation(point);
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
        switch (state) {
            case 0: {
                rect.setEndLocation(point);
                break;
            }
        }
    }

    @Override
    public void execute() {
        getDisplayPanel().addToVisualObjects(rect);

        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(rect);
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Retângulo";
    }

    @Override
    public Rectangle getVisualObject() {
        return rect;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new RectangleCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "retangulo";
    }
}
