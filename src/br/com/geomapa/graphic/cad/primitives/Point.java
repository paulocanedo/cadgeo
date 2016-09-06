/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.primitives;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.main.Bus;
import br.com.geomapa.ui.panels.topographic.Projector;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

import static br.com.geomapa.graphic.cad.primitives.Circle.CIRCLE_RESOLUTION;
import static br.com.geomapa.graphic.cad.primitives.Circle.CIRCLE_ARRAY_VERTEX;
import br.com.geomapa.graphic.cad.spec.VisualObject;

/**
 *
 * @author paulocanedo
 */
public class Point extends AbstractVisualObject {

    private double[] arrayVertex = new double[CIRCLE_RESOLUTION * 4 + (2 * 4)];
    private double cx;
    private double cy;
    private double lastRadius = 0;

    public Point() {
        setVertex(0, 0);
    }

    public Point(double x, double y) {
        super(x, y);

        setVertex(x, y);
    }

    public Point(Point2D point) {
        super(point);

        setVertex(point.getX(), point.getY());
    }

    private void setVertex(double cx, double cy) {
        this.cx = cx;
        this.cy = cy;
        double radius = Bus.getScale();

        for (int i = 0; i < CIRCLE_RESOLUTION * 4; i += 4) {
            arrayVertex[i + 0] = CIRCLE_ARRAY_VERTEX[i + 0] * radius * 0.5 + cx;
            arrayVertex[i + 1] = CIRCLE_ARRAY_VERTEX[i + 1] * radius * 0.5 + cy;
            arrayVertex[i + 2] = CIRCLE_ARRAY_VERTEX[i + 2] * radius * 0.5 + cx;
            arrayVertex[i + 3] = CIRCLE_ARRAY_VERTEX[i + 3] * radius * 0.5 + cy;
        }


        if (this instanceof GeodesicPoint) {
            GeodesicPoint gpoint = (GeodesicPoint) this;
            if (gpoint.getType() == GeodesicPointType.M) {
                arrayVertex[CIRCLE_RESOLUTION * 4 + 0] = cx - radius;
                arrayVertex[CIRCLE_RESOLUTION * 4 + 1] = cy;
                arrayVertex[CIRCLE_RESOLUTION * 4 + 2] = cx + radius;
                arrayVertex[CIRCLE_RESOLUTION * 4 + 3] = cy;

                arrayVertex[CIRCLE_RESOLUTION * 4 + 4] = cx;
                arrayVertex[CIRCLE_RESOLUTION * 4 + 5] = cy - radius;
                arrayVertex[CIRCLE_RESOLUTION * 4 + 6] = cx;
                arrayVertex[CIRCLE_RESOLUTION * 4 + 7] = cy + radius;
                return;
            }
        }
        arrayVertex[CIRCLE_RESOLUTION * 4 + 0] = 0;
        arrayVertex[CIRCLE_RESOLUTION * 4 + 1] = 0;
        arrayVertex[CIRCLE_RESOLUTION * 4 + 2] = 0;
        arrayVertex[CIRCLE_RESOLUTION * 4 + 3] = 0;

        arrayVertex[CIRCLE_RESOLUTION * 4 + 4] = 0;
        arrayVertex[CIRCLE_RESOLUTION * 4 + 5] = 0;
        arrayVertex[CIRCLE_RESOLUTION * 4 + 6] = 0;
        arrayVertex[CIRCLE_RESOLUTION * 4 + 7] = 0;
    }

    @Override
    public boolean selection(int x, int y, Projector projector) {
        float radius = Bus.getScale() / 2;
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
        
        float radius = Bus.getScale() / 2;
        int[] project = projector.project(getX(), getY());
        int[] borderRadius = projector.project(getX() + radius, getY());
        int screenRadius = Math.abs(borderRadius[0] - project[0]);
 
        return Circle.intersectsRect(project[0], project[1], screenRadius, x1, y1, x2, y2);
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
        stream.println("POINT");
        stream.println("8");
        stream.println(getLayer().toString());
        stream.println("10");
        stream.println(getX());
        stream.println("20");
        stream.println(getY());
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f", 
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY()));
    }

    @Override
    public LineType getLineType() {
        return CONTINUOUS_LINE_TYPE;
    }
    
    @Override
    public String getVisualObjectName() {
        return "point";
    }

    @Override
    public double[] getArrayVertex() {
        if (lastRadius != Bus.getScale()) {
            this.lastRadius = Bus.getScale();
            setVertex(cx, cy);
        }
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
        setVertex(x, y);
    }

    @Override
    public boolean canRotate() {
        return false;
    }

    @Override
    public VisualObject copy() {
        Point other = new Point(getX(), getY());
        matchProperties(this, other);

        return other;
    }

    @Override
    public void refresh() {
        setVertex(cx, cy);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Point other = (Point) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.oid;
        return hash;
    }
    
}
