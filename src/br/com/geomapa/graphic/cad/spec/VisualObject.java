/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.spec;

import br.com.geomapa.geodesic.coordinate.Coordinate;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.ui.panels.topographic.Projector;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import javax.media.opengl.GL2;

/**
 *
 * @author paulocanedo
 */
public interface VisualObject {

    public Integer getOid();

    public void draw(GL2 gl, Color color);

    public void fill(GL2 gl, Color color);

    public double[] getArrayVertex();

    public Layer getLayer();

    public void setLayer(Layer layer);

    public double getX();

    public double getY();

    public Point2D getLocation();

    public void setLocation(Point2D point);

    public void setLocation(double x, double y);

    public void setLocation(Coordinate coord);

    public Color getColor();

    public String getColorAsString();

    public void setColor(Color color);

    public Color getFillColor();

    public void move(double offsetX, double offsetY);

    public boolean canMove();

    public VisualObject copy();

    public boolean canCopy();

    public void rotate(double theta);

    public boolean canRotate();

    public boolean selection(int selX, int selY, Projector projector);

    public boolean selectionFull(int selX1, int selY1, int selX2, int selY2, Projector projector);

    public boolean selectionPartial(int selX1, int selY1, int selX2, int selY2, Projector projector);

    public void writeToDxf(PrintStream stream) throws IOException;

    public void write(PrintStream stream) throws IOException;

    public String getVisualObjectName();

    public void setLineType(LineType lineType);

    public LineType getLineType();

    public float getLineWidth();

    public void setLineWidth(float lineWidth);

    public void refresh();

    public boolean isColorLayer();
    
    public boolean isLineTypeLayer();

}
