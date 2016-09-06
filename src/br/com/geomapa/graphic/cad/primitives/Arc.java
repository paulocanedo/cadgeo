/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.primitives;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 *
 * @author paulocanedo
 */
public class Arc extends AbstractVisualObject {

    private double cx;
    private double cy;
    private double radius;
    private double startAngle;
    private double endAngle;
    private double[] arrayVertex;

    public Arc(double radius, double startAngle, double endAngle, Point2D point) {
        super(point);

        setVertex(point.getX(), point.getY(), radius, startAngle, endAngle);
    }

    public Arc(double radius, double startAngle, double endAngle, double x, double y) {
        super(x, y);

        setVertex(x, y, radius, startAngle, endAngle);
    }

    private void setVertex(double cx, double cy, double radius, double startAngle, double endAngle) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.startAngle = startAngle;
        this.endAngle = endAngle;

        List<Point2D> list = new ArrayList<Point2D>();

        double amount = 12;
        double startAngleRadians = Math.toRadians(startAngle);
        double endAngleRadians = Math.toRadians(endAngle);

        double angle = startAngle;
        for (int i = 0; i <= amount; i++) {
            double x = 0.5 + Math.cos(angle);
            double y = 0.5 + Math.sin(angle);

            x *= radius;
            y *= radius;

            x += cx - radius / 2.0;
            y += cy - radius / 2.0;

            list.add(new Point2D.Double(x, y));
            angle += (endAngleRadians - startAngleRadians) / amount;
        }
        Polyline polyline = new Polyline();
        for (Point2D p : list) {
            polyline.addVertex(p.getX(), p.getY());
        }
        this.arrayVertex = polyline.getArrayVertex();
    }

    public static void drawArc(GL2 gl, double cx, double cy, double radius, double startAngle, double endAngle) {
        gl.glPushMatrix();
        gl.glTranslated(cx - radius / 2.0, cy - radius / 2.0, 0);
        gl.glScaled(radius, radius, 1);

        gl.glBegin(GL.GL_LINE_STRIP);
        double amount = 12;
        double startAngleRadians = Math.toRadians(startAngle);
        double endAngleRadians = Math.toRadians(endAngle);

        double angle = startAngle;
        for (int i = 0; i <= amount; i++) {
            gl.glVertex2d(0.5 + (Math.cos(angle)), 0.5 + (Math.sin(angle)));

            angle += (endAngleRadians - startAngleRadians) / amount;
        }
        gl.glEnd();

        gl.glPopMatrix();
    }

    @Override
    public void writeToDxf(PrintStream stream) {
        stream.println("0");
        stream.println("ARC");
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
        stream.println("50");
        stream.println(getStartAngle());
        stream.println("51");
        stream.println(getEndAngle());
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f %.3f %.1f %.1f",
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(), getRadius(), getStartAngle(), getEndAngle()));
    }

    @Override
    public String getVisualObjectName() {
        return "arc";
    }

    public double getEndAngle() {
        return endAngle;
    }

    public void setEndAngle(double endAngle) {
        this.endAngle = endAngle;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(double startAngle) {
        this.startAngle = startAngle;
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
        setVertex(x, y, getRadius(), getStartAngle(), getEndAngle());
    }

    @Override
    public VisualObject copy() {
        Arc other = new Arc(radius, startAngle, endAngle, getX(), getY());
        matchProperties(this, other);

        return other;
    }

    @Override
    public void refresh() {
        setVertex(getX(), getY(), getRadius(), getStartAngle(), getEndAngle());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Arc other = (Arc) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.oid;
        return hash;
    }
}
