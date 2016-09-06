/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.spec;

import br.com.geomapa.geodesic.coordinate.Coordinate;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.ui.panels.topographic.Projector;
import java.awt.geom.Point2D;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public abstract class AbstractDoublePointVisualObject extends AbstractVisualObject implements DoublePointVisualObject {

    protected Point2D endLocation = new Point2D.Double();

    public AbstractDoublePointVisualObject() {
        this(0, 0, 0, 0);
    }

    public AbstractDoublePointVisualObject(Point2D startPoint, Point2D endPoint) {
        super(startPoint);

        this.endLocation.setLocation(endPoint);
    }

    public AbstractDoublePointVisualObject(double x1, double y1, double x2, double y2) {
        super(x1, y1);

        this.endLocation.setLocation(x2, y2);
    }

    public AbstractDoublePointVisualObject(Layer layer, Point2D startPoint, Point2D endPoint) {
        super(layer, startPoint);

        this.endLocation.setLocation(endPoint);
    }

    @Override
    public Point2D getEndLocation() {
        endLocation.setLocation(getEndX(), getEndY());
        return endLocation;
    }

    @Override
    public void setEndLocation(Coordinate endCoord) {
        setEndLocation(endCoord.toUTM().toPoint2D());
    }

    @Override
    public void setEndLocation(Point2D endLocation) {
        setEndLocation(endLocation.getX(), endLocation.getY());
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s(%.3f,%.3f)(%.3f,%.3f)", getClass().getSimpleName(), getX(), getY(), getEndX(), getEndY());
    }

    @Override
    public void move(double offsetX, double offsetY) {
        if (canMove()) {
            double endX = getEndX();
            double endY = getEndY();

            super.move(offsetX, offsetY);
            this.setEndLocation(endX + offsetX, endY + offsetY);
        }
    }

    @Override
    public boolean selectionFull(int x1, int y1, int x2, int y2, Projector projector) {
        int[] project1 = projector.project(getX(), getY());
        int[] project2 = projector.project(getEndX(), getEndY());
        
        return testSelectionFull(project1[0], project1[1], project2[0], project2[1], x1, y1, x2, y2);
    }
    
    public static boolean testSelectionFull(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        return coverPoint(x1, y1, x3, y3, x4, y4) && coverPoint(x2, y2, x3, y3, x4, y4);
    }
}
