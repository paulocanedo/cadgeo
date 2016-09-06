/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.primitives;

import br.com.geomapa.graphic.Layer;
import br.com.geomapa.util.PGLUtil;
import java.awt.Color;
import java.awt.geom.Point2D;
import javax.media.opengl.GL2;

/**
 *
 * @author paulocanedo
 */
public class FilledRectangle extends Rectangle {

    private Color fillColor = Color.WHITE;

    public FilledRectangle(Layer layer, Point2D startPoint, Point2D endPoint) {
        super(layer, startPoint, endPoint);
    }

    public FilledRectangle(double x1, double y1, double x2, double y2, Color color, Color fillColor) {
        super(x1, y1, x2, y2);
        setColor(color);
        setFillColor(fillColor);
    }

    public FilledRectangle(Point2D startPoint, Point2D endPoint) {
        super(startPoint, endPoint);
    }

    @Override
    public void draw(GL2 gl, Color color) {
        super.draw(gl, color);
    }
    
    @Override
    public void fill(GL2 gl, Color color) {
        PGLUtil.setGlColor(gl, fillColor);
        PGLUtil.fillRect(gl, getX(), getY(), getEndX(), getEndY());
    }

    public Color getBorderColor() {
        return fillColor;
    }

    public final void setFillColor(Color borderColor) {
        this.fillColor = borderColor;
    }
}
