/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.primitives;

import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class ImmutablePoint extends Point {

    public ImmutablePoint(Point2D point) {
        super(point);
    }

    public ImmutablePoint(double x, double y) {
        super(x, y);
    }

    public ImmutablePoint() {
    }
    
    @Override
    public boolean canMove() {
        return false;
    }
    
    @Override
    public String getVisualObjectName() {
        return "immutable_point";
    }
    
}
