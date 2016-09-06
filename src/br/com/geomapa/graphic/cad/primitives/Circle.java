/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.primitives;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.Projector;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public class Circle extends AbstractVisualObject {

    public final static int CIRCLE_RESOLUTION = 24;
    public final static double[] CIRCLE_ARRAY_VERTEX = new double[CIRCLE_RESOLUTION * 4];

    static {
        Double lastX = null, lastY = null;

        double amount = Math.PI * 2 / CIRCLE_RESOLUTION;
        double angle = 0;
        int j = 0;
        for (int i = 0; i < CIRCLE_RESOLUTION; i++) {
            double x = ((Math.cos(angle)));
            double y = ((Math.sin(angle)));

            if (lastX != null && lastY != null) {
                CIRCLE_ARRAY_VERTEX[j++] = (x);
                CIRCLE_ARRAY_VERTEX[j++] = (y);
                CIRCLE_ARRAY_VERTEX[j++] = (lastX);
                CIRCLE_ARRAY_VERTEX[j++] = (lastY);
            }

            angle += amount;
            lastX = x;
            lastY = y;
        }
        CIRCLE_ARRAY_VERTEX[j++] = (lastX);
        CIRCLE_ARRAY_VERTEX[j++] = (lastY);
        CIRCLE_ARRAY_VERTEX[j++] = (Math.cos(0));
        CIRCLE_ARRAY_VERTEX[j++] = (Math.sin(0));
    }
    private double[] arrayVertex = new double[CIRCLE_RESOLUTION * 4];
    private double cx;
    private double cy;
    private double radius;

    public Circle() {
        setVertex(0, 0, 0);
    }

    public Circle(double x, double y, double radius) {
        super(x, y);
        this.radius = radius;

        setVertex(x, y, radius);
    }

    public Circle(Point2D point, double radius) {
        super(point);

        this.radius = radius;
        setVertex(point.getX(), point.getY(), radius);
    }

    private void setVertex(double cx, double cy, double radius) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;

        for (int i = 0; i < CIRCLE_RESOLUTION * 4; i += 4) {
            arrayVertex[i] = CIRCLE_ARRAY_VERTEX[i] * radius + cx;
            arrayVertex[i + 1] = CIRCLE_ARRAY_VERTEX[i + 1] * radius + cy;
            arrayVertex[i + 2] = CIRCLE_ARRAY_VERTEX[i + 2] * radius + cx;
            arrayVertex[i + 3] = CIRCLE_ARRAY_VERTEX[i + 3] * radius + cy;
        }
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        setVertex(cx, cy, radius);
    }

    @Override
    public boolean selection(int x, int y, Projector projector) {
        int[] project = projector.project(getX(), getY());
        int[] borderRadius = projector.project(getX() + radius, getY());
        int screenRadius = Math.abs(borderRadius[0] - project[0]);

        double d = PolygonalUtils.horizontalDistance(project[0], project[1], x, y);

        return (screenRadius < d + selectionThreshold) && (screenRadius > d - selectionThreshold);
    }

    @Override
    public boolean selectionPartial(int x1, int y1, int x2, int y2, Projector projector) {
        if(selectionFull(x1, y1, x2, y2, projector)) {
            return true;
        }
        
        int[] project = projector.project(getX(), getY());
        int[] borderRadius = projector.project(getX() + getRadius(), getY());
        int screenRadius = Math.abs(borderRadius[0] - project[0]);
        
        return intersectsRect(project[0], project[1], screenRadius, x1, y1, x2, y2);
    }
    
    public static boolean intersectsRect(double cx, double cy, double radius, double x1, double y1, double x2, double y2) {
        if(x1 > x2) {
            double aux = x1;
            x1 = x2;
            x2 = aux;
        }
        if(y1 > y2) {
            double aux = y1;
            y1 = y2;
            y2 = aux;
        }
        
        if((x1 > cx - radius && x1 < cx + radius) && (x2 > cx - radius && x2 < cx + radius) &&
                (y1 > cy - radius && y1 < cy + radius) && (y2 > cy - radius && y2 < cy + radius)) {
            return false;
        }
        
        double closestX = clamp(cx, x1, x2);
        double closestY = clamp(cy, y1, y2);

        double distanceX = cx - closestX;
        double distanceY = cy - closestY;

        double distanceSquared = distanceSquared(distanceX, distanceY);
        return distanceSquared < (radius);
    }
    
    public static double distanceSquared(double x1, double y1) {
        return Math.sqrt(x1*x1 + y1*y1);
    }
    
    public static double clamp(double value, double min, double max) {
        if(value > max) {
            return max;
        } else if(value < min) {
            return min;
        }
        return value;
    }
    
    @Override
    public void writeToDxf(PrintStream stream) {
        stream.println("0");
        stream.println("CIRCLE");
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
        stream.println("40");
        stream.println(getRadius());
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f %.3f", 
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(), getRadius()));
    }

    @Override
    public String getVisualObjectName() {
        return "circle";
    }

    @Override
    public double[] getArrayVertex() {
        return arrayVertex;
    }

    @Override
    public double getX() {
        return cx;
    }

    @Override
    public double getY() {
        return cy;
    }

    @Override
    public void setLocation(double x, double y) {
        setVertex(x, y, this.radius);
    }

    @Override
    public VisualObject copy() {
        Circle other = new Circle(getX(), getY(), getRadius());
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
        final Circle other = (Circle) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.oid;
        return hash;
    }
    
}
