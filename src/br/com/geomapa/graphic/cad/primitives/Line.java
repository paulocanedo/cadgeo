/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.primitives;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.graphic.cad.spec.AbstractDoublePointVisualObject;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.CustomLayer;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.Projector;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 *
 * @author paulocanedo
 */
public class Line extends AbstractDoublePointVisualObject {

    private double[] arrayVertex = new double[]{0, 0, 0, 0};
    public static final Line LINE_SAMPLE = new Line();

    static {
        LINE_SAMPLE.setLayer(new CustomLayer("", ""));
    }

    public Line() {
    }

    public Line(Point2D startPoint, Point2D endPoint) {
        super(startPoint, endPoint);

        setArrayVertex(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
    }

    public Line(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);

        setArrayVertex(x1, y1, x2, y2);
    }

    protected final void setArrayVertex(double x1, double y1, double x2, double y2) {
        arrayVertex[0] = x1;
        arrayVertex[1] = y1;
        arrayVertex[2] = x2;
        arrayVertex[3] = y2;
    }

    public Point2D intersection(Line otherLine) {
        return intersection(getLocation(), getEndLocation(), otherLine.getLocation(), otherLine.getEndLocation());
    }

    public Point2D[] intersection(Circle circle) {
        return intersectionWithCircle(getLocation(), getEndLocation(), circle.getLocation(), circle.getRadius());
    }

    public static Point2D[] intersectionWithCircle(Point2D k, Point2D l, Point2D cl, double radius) {
        double r = radius;
        double m = (l.getY() - k.getY()) / (l.getX() - k.getX());
        double c = k.getY() - m * k.getX();

        double eq2a = 1 + pow(m, 2);
        double eq2b = 2 * m * (c - cl.getY()) - 2 * cl.getX();
        double eq2c = pow(cl.getX(), 2) + pow(c - cl.getY(), 2) - pow(r, 2);

        double delta = (pow(eq2b, 2) - 4 * eq2a * eq2c);
        if (delta < 0) {
            return null;
        }

        double sqrt_delta = sqrt(delta);

        double x1 = (-eq2b + sqrt_delta) / (2 * eq2a);
        double x2 = (-eq2b - sqrt_delta) / (2 * eq2a);

        double y1 = m * x1 + c;
        double y2 = m * x2 + c;

        Point2D p1 = new Point2D.Double(x1, y1);
        Point2D p2 = delta == 0 ? p1 : new Point2D.Double(x2, y2);

        return new Point2D[]{p1, p2};
    }

    public static void main(String... args) {
        Point2D p1 = new Point2D.Double(1, 6);
        Point2D p2 = new Point2D.Double(3, 1);
        Point2D cl = new Point2D.Double(2, 3);

        System.out.println(intersectionWithCircle(p1, p2, cl, 5));
    }

    public static Point2D intersection(Point2D k, Point2D l, Point2D m, Point2D n) {
        double A1 = l.getY() - k.getY();
        double B1 = k.getX() - l.getX();
        double C1 = A1 * k.getX() + B1 * k.getY();

        double A2 = n.getY() - m.getY();
        double B2 = m.getX() - n.getX();
        double C2 = A2 * m.getX() + B2 * m.getY();

        double det = A1 * B2 - A2 * B1;
        if (det == 0.0) {
            return null;
        }

        double x = (B2 * C1 - B1 * C2) / det;
        double y = (A1 * C2 - A2 * C1) / det;

        return new Point2D.Double(x, y);
    }

    public boolean isParallel(Line otherLine) {
        return intersection(otherLine) == null;
    }

    public boolean linesIntersect(Line otherLine) {
        return Line2D.linesIntersect(getX(), getY(), getEndX(), getEndY(), otherLine.getX(), otherLine.getY(), otherLine.getEndX(), otherLine.getEndY());
    }

    public Line offset(double distance, boolean positive) {
        return offset(this, distance, positive);
    }

    public static Line offset(Line sourceLine, double distance, boolean positive) {
        double azimuth = PolygonalUtils.azimuth(sourceLine.getX(), sourceLine.getY(), sourceLine.getEndX(), sourceLine.getEndY()).toDegreeDecimal();
        double rotate = 90;
        if (!positive) {
            rotate = -rotate;
        }
        azimuth += rotate;

        double[] projection1 = PolygonalUtils.projection(sourceLine.getX(), sourceLine.getY(), distance, azimuth);
        double[] projection2 = PolygonalUtils.projection(sourceLine.getEndX(), sourceLine.getEndY(), distance, azimuth);

        return new Line(new Point2D.Double(projection1[0], projection1[1]), new Point2D.Double(projection2[0], projection2[1]));
    }

    public static List<Line> offset(List<Line> sourceLines, double distance, boolean positive) {
        ArrayList<Line> list = new ArrayList<Line>();
        for (Line line : sourceLines) {
            list.add(line.offset(distance, positive));
        }

        for (int i = 0; i < list.size() - 1; i++) {
            Line l1 = list.get(i);
            Line l2 = list.get(i + 1);
            joinLines(l1, l2);
        }

        return list;
    }

    public static void joinLines(Line l1, Line l2) {
        Point2D intersection = l1.intersection(l2);
        l1.setEndLocation(intersection);
        l2.setLocation(intersection);
    }

    @Override
    public boolean selection(int x, int y, Projector projector) {
        int[] project1 = projector.project(getX(), getY());
        int[] project2 = projector.project(getEndX(), getEndY());

        return Line2D.ptSegDist(project1[0], project1[1], project2[0], project2[1], x, y) <= selectionThreshold;
    }

    @Override
    public boolean selectionPartial(int x1, int y1, int x2, int y2, Projector projector) {
        if (selectionFull(x1, y1, x2, y2, projector)) {
            return true;
        }

        int[] project1 = projector.project(getX(), getY());
        int[] project2 = projector.project(getEndX(), getEndY());
        int lx1 = project1[0], ly1 = project1[1], lx2 = project2[0], ly2 = project2[1];

        return Line2D.linesIntersect(x1, y1, x1, y2, lx1, ly1, lx2, ly2)
                || Line2D.linesIntersect(x1, y2, x2, y2, lx1, ly1, lx2, ly2)
                || Line2D.linesIntersect(x2, y2, x2, y1, lx1, ly1, lx2, ly2)
                || Line2D.linesIntersect(x2, y1, x1, y1, lx1, ly1, lx2, ly2);
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
        stream.println(getEndX());
        stream.println("21");
        stream.println(getEndY());
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f %.3f %.3f",
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(), getEndX(), getEndY()));
    }

    @Override
    public String getVisualObjectName() {
        return "line";
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
        return getArrayVertex()[2];
    }

    @Override
    public double getEndY() {
        return getArrayVertex()[3];
    }

    @Override
    public void setLocation(double x, double y) {
        arrayVertex[0] = x;
        arrayVertex[1] = y;
    }

    @Override
    public void setEndLocation(double endX, double endY) {
        arrayVertex[2] = endX;
        arrayVertex[3] = endY;
    }

    @Override
    public VisualObject copy() {
        Line other = new Line(getX(), getY(), getEndX(), getEndY());
        matchProperties(this, other);

        return other;
    }

    @Override
    public void refresh() {
        double x1 = getX(), y1 = getY(), x2 = getEndX(), y2 = getEndY();

        setArrayVertex(x1, y1, x2, y2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Line other = (Line) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.oid;
        return hash;
    }
}
