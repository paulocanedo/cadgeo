/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.primitives;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.spec.AbstractDoublePointVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.Projector;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public class Rectangle extends AbstractDoublePointVisualObject {
    
    private LineType lineType = CONTINUOUS_LINE_TYPE;

    @Override
    public LineType getLineType() {
        return lineType;
    }

    @Override
    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }
    
    private double[] arrayVertex = new double[]{0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,};

    public Rectangle(Point2D startPoint, Point2D endPoint) {
        super(startPoint, endPoint);

        setVertex(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
    }

    public Rectangle(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);

        setVertex(x1, y1, x2, y2);
    }

    public Rectangle(Layer layer, Point2D startPoint, Point2D endPoint) {
        super(layer, startPoint, endPoint);

        setVertex(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
    }

    private void setVertex(double x1, double y1, double x2, double y2) {
        setLocation(x1, y1);
        setEndLocation(x2, y2);
    }

    public static Rectangle createRect(double x1, double y1, double width, double height) {
        return new Rectangle(x1, y1, x1 + width, y1 + height);
    }

    public double getWidth() {
        return Math.abs(getX() - getEndX());
    }

    public double getHeight() {
        return Math.abs(getY() - getEndY());
    }

    public Rectangle copy(double offsetX, double offsetY) {
        return new Rectangle(getX() + offsetX, getY() + offsetY, getEndX() + offsetX, getEndY() + offsetY);
    }

    @Override
    public void writeToDxf(PrintStream stream) {
        stream.println("0");
        stream.println("LINE");
        if (!isColorLayer()) {
            stream.println("62");
            stream.println(LayerController.getDxfColor(getColor()));
        }
        if (getLineType() != null) {
            stream.println("6");
            stream.println(getLineType().getDxfName());
        }
        stream.println("8");
        stream.println(getLayer().toString());
        stream.println("10");
        stream.println(getX());
        stream.println("20");
        stream.println(getY());
        stream.println("11");
        stream.println(getX());
        stream.println("21");
        stream.println(getEndY());

        stream.println("0");
        stream.println("LINE");
        if (!isColorLayer()) {
            stream.println("62");
            stream.println(LayerController.getDxfColor(getColor()));
        }
        if (getLineType() != null) {
            stream.println("6");
            stream.println(getLineType().getDxfName());
        }
        stream.println("8");
        stream.println(getLayer().toString());
        stream.println("10");
        stream.println(getX());
        stream.println("20");
        stream.println(getEndY());
        stream.println("11");
        stream.println(getEndX());
        stream.println("21");
        stream.println(getEndY());

        stream.println("0");
        stream.println("LINE");
        if (!isColorLayer()) {
            stream.println("62");
            stream.println(LayerController.getDxfColor(getColor()));
        }
        if (getLineType() != null) {
            stream.println("6");
            stream.println(getLineType().getDxfName());
        }
        stream.println("8");
        stream.println(getLayer().toString());
        stream.println("10");
        stream.println(getEndX());
        stream.println("20");
        stream.println(getEndY());
        stream.println("11");
        stream.println(getEndX());
        stream.println("21");
        stream.println(getY());

        stream.println("0");
        stream.println("LINE");
        if (!isColorLayer()) {
            stream.println("62");
            stream.println(LayerController.getDxfColor(getColor()));
        }
        if (getLineType() != null) {
            stream.println("6");
            stream.println(getLineType().getDxfName());
        }
        stream.println("8");
        stream.println(getLayer().toString());
        stream.println("10");
        stream.println(getEndX());
        stream.println("20");
        stream.println(getY());
        stream.println("11");
        stream.println(getX());
        stream.println("21");
        stream.println(getY());
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f %.3f %.3f", 
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(), getEndX(), getEndY()));
    }

    @Override
    public String getVisualObjectName() {
        return "rect";
    }

    @Override
    public boolean selection(int x, int y, Projector projector) {
        int[] project1 = projector.project(getX(), getY());
        int[] project2 = projector.project(getEndX(), getEndY());
        int x1 = project1[0], y1 = project1[1], x2 = project2[0], y2 = project2[1];

        return (Line2D.ptSegDist(x1, y1, x1, y2, x, y) <= selectionThreshold)
                || (Line2D.ptSegDist(x1, y2, x2, y2, x, y) <= selectionThreshold)
                || (Line2D.ptSegDist(x2, y2, x2, y1, x, y) <= selectionThreshold)
                || (Line2D.ptSegDist(x2, y1, x1, y1, x, y) <= selectionThreshold);
    }

    @Override
    public boolean selectionPartial(int x1, int y1, int x2, int y2, Projector projector) {
        int[] project1 = projector.project(getX(), getY());
        int[] project2 = projector.project(getEndX(), getEndY());
        int lx1 = project1[0], ly1 = project1[1], lx2 = project2[0], ly2 = project2[1];
        
        return testSelectionPartial(x1, y1, x2, y2, lx1, ly1, lx2, ly2);
    }
    
    public static boolean testSelectionPartial(double x1, double y1, double x2, double y2, double startX, double startY, double endX, double endY) {
        if (testSelectionFull(startX, startY, endX, endY, x1, y1, x2, y2)) {
            return true;
        }
        double x3, y3, x4, y4;

        x3 = startX;
        y3 = startY;
        x4 = startX;
        y4 = endY;
        if (testSelect(x1, y1, x2, y2, x3, y3, x4, y4)) {
            return true;
        }

        x3 = startX;
        y3 = endY;
        x4 = endX;
        y4 = endY;
        if (testSelect(x1, y1, x2, y2, x3, y3, x4, y4)) {
            return true;
        }

        x3 = endX;
        y3 = endY;
        x4 = endX;
        y4 = startY;
        if (testSelect(x1, y1, x2, y2, x3, y3, x4, y4)) {
            return true;
        }

        x3 = endX;
        y3 = startY;
        x4 = startX;
        y4 = startY;
        if (testSelect(x1, y1, x2, y2, x3, y3, x4, y4)) {
            return true;
        }
        return false;
    }

    public static boolean testSelect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        return Line2D.linesIntersect(x1, y1, x1, y2, x3, y3, x4, y4)
                || Line2D.linesIntersect(x1, y2, x2, y2, x3, y3, x4, y4)
                || Line2D.linesIntersect(x2, y2, x2, y1, x3, y3, x4, y4)
                || Line2D.linesIntersect(x2, y1, x1, y1, x3, y3, x4, y4);
    }

    @Override
    public double[] getArrayVertex() {
        return arrayVertex;
    }

    @Override
    public double getX() {
        return getArrayVertex()[0];
    }

    @Override
    public double getY() {
        return getArrayVertex()[1];
    }

    @Override
    public double getEndX() {
        return getArrayVertex()[6];
    }

    @Override
    public double getEndY() {
        return getArrayVertex()[7];
    }

    @Override
    public void setLocation(double x1, double y1) {
        arrayVertex[0] = x1;
        arrayVertex[1] = y1;
        arrayVertex[2] = x1;
        arrayVertex[4] = x1;
        arrayVertex[11] = y1;
        arrayVertex[13] = y1;
        arrayVertex[14] = x1;
        arrayVertex[15] = y1;
    }

    @Override
    public void setEndLocation(double x2, double y2) {
        arrayVertex[3] = y2;
        arrayVertex[5] = y2;
        arrayVertex[6] = x2;
        arrayVertex[7] = y2;
        arrayVertex[8] = x2;
        arrayVertex[9] = y2;
        arrayVertex[10] = x2;
        arrayVertex[12] = x2;
    }

    @Override
    public VisualObject copy() {
        Rectangle other = new Rectangle(getX(), getY(), getEndX(), getEndY());
        matchProperties(this, other);
        
        return other;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rectangle other = (Rectangle) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.oid;
        return hash;
    }
    
}
