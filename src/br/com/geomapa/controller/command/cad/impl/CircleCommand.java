/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.primitives.Circle;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class CircleCommand extends AbstractCadCommand {

    private Circle circle = new Circle(0, 0, 0);
    private int state = -1;
    private static final String[] messages = new String[] {"Informe o ponto central do círculo"};

    public CircleCommand() {
        super(messages[0]);
    }
    
    public CircleCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CoordinateException {
        if (state == -1) {
            Point2D point = parsePoint(text);
            return nextState(point);
        } else if(state == 0) {
            Double value = getPositiveDouble(text, "Valor do raio deve ser maior que zero: %s");
            circle.setRadius(value);
            state++;
            
            execute();
            return true;
        }
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        switch (state) {
            case -1:
                circle.setLocation(point);
                this.message = "Informe o raio do círculo";
                this.canDraw = true;
                break;
            case 0: {
                double radius = PolygonalUtils.horizontalDistance(circle.getX(), circle.getY(), point.getX(), point.getY());
                circle.setRadius(Math.abs(radius));

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
                double radius = PolygonalUtils.horizontalDistance(circle.getX(), circle.getY(), point.getX(), point.getY());
                circle.setRadius(Math.abs(radius));
                break;
            }
        }
    }

    @Override
    public void execute() {
        getDisplayPanel().addToVisualObjects(circle);

        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(circle);
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Círculo";
    }

    @Override
    public Circle getVisualObject() {
        return circle;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) {
        return new CircleCommand(displayPanel);
    }

    @Override
    public boolean canUseMagnetic() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "circulo";
    }
    
}
