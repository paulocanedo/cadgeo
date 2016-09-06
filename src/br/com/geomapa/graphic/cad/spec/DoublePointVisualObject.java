/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.spec;

import br.com.geomapa.geodesic.coordinate.Coordinate;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public interface DoublePointVisualObject extends VisualObject {

    public double getEndX();

    public double getEndY();

    public Point2D getEndLocation();

    public void setEndLocation(Point2D endPoint);

    public void setEndLocation(Coordinate endCoord);

    public void setEndLocation(double endX, double endY);
}
