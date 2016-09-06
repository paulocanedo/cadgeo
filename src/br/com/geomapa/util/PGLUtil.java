/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util;

import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.ui.panels.options.SchemeColors;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 *
 * @author paulocanedo
 */
public final class PGLUtil {

    public static void fillRect(GL2 gl, Point p1, Point p2) {
        fillRect(gl, p1.x, p1.y, p2.x, p2.y);
    }

    public static void fillRect(GL2 gl, double x1, double y1, double x2, double y2) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2d(x1, y1);
        gl.glVertex2d(x2, y1);
        gl.glVertex2d(x2, y2);
        gl.glVertex2d(x1, y2);
        gl.glEnd();
    }

    public static void fillQuad(GL2 gl, double cx, double cy, double size) {
        gl.glBegin(GL2.GL_QUADS);

        double halfWidth = size / 2;
        gl.glVertex2d(cx - halfWidth, cy - halfWidth);
        gl.glVertex2d(cx - halfWidth, cy + halfWidth);
        gl.glVertex2d(cx + halfWidth, cy + halfWidth);
        gl.glVertex2d(cx + halfWidth, cy - halfWidth);
        gl.glEnd();
    }

    public static void drawRect(GL2 gl, Point2D p1, Point2D p2) {
        drawRect(gl, p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public static void drawRect(GL2 gl, double x1, double y1, double x2, double y2) {
        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex2d(x1, y1);
        gl.glVertex2d(x2, y1);
        gl.glVertex2d(x2, y2);
        gl.glVertex2d(x1, y2);
        gl.glEnd();
    }

    public static void drawLine(GL2 gl, Point p1, Point p2) {
        drawLine(gl, p1.x, p1.y, p2.x, p2.y);
    }

    public static void drawLine(GL2 gl, double x1, double y1, double x2, double y2) {
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2d(x1, y1);
        gl.glVertex2d(x2, y2);
        gl.glEnd();
    }

    public static void drawCircle(GL2 gl, double cx, double cy, double radius) {
        gl.glBegin(GL.GL_LINE_LOOP);
        double amount = Math.PI * 2 / Math.max(12, radius);
        for (double angle = 0; angle < Math.PI * 2; angle += amount) {
            gl.glVertex2d(cx + (Math.cos(angle) * radius), cy + (Math.sin(angle) * radius));
        }
        gl.glEnd();
    }

    public static void drawPolygonal(GL2 gl, Polygonal polygonal) {
//        gl.glPushAttrib(GL2.GL_LINE_STIPPLE);
//        gl.glPushAttrib(GL2.GL_LINE_WIDTH);
//        
//        gl.glLineStipple(10, (short)0x00ff);
//        gl.glLineWidth(6f);
        gl.glBegin(GL2.GL_LINE_STRIP);
        LinkedList<LineDivision> lineDivisions = polygonal.getLineDivisions();
        for (LineDivision ld : lineDivisions) {
            UTMCoordinate coord = ld.getStartPoint().getCoordinate().toUTM();
            gl.glVertex2d(coord.getEast(), coord.getNorth());
        }

        if (!lineDivisions.isEmpty()) {
            UTMCoordinate coord = lineDivisions.getLast().getEndPoint().getCoordinate().toUTM();
            gl.glVertex2d(coord.getEast(), coord.getNorth());
        }


        gl.glEnd();
//        gl.glPopAttrib();
//        gl.glPopAttrib();
    }

//    public static void fillPolygonal(GL2 gl, Polygonal polygonal) {
//        GLU glu = new GLU();
//        glu.gluBeginPolygon(null);
//        glu.gluTessVertex(null, doubles, i, glu);
//        
//        gl.glBegin(GL2.GL_POLYGON);
//        
//        for(GeodesicPoint point : polygonal.toListGeoPoints()) {
//            UTMCoordinate coordinate = point.getCoordinate().toUTM();
//            gl.glVertex2d(coordinate.getEast(), coordinate.getNorth());
//        }
//        
//        gl.glEnd();
//    }
    public static void setGlColor(GL2 gl, Color color) {
        float[] rgb = color.getRGBComponents(null);
        float[] rgba = new float[]{rgb[0], rgb[1], rgb[2], color.getAlpha() / 255f};
        gl.glColor4fv(rgba, 0);
    }

    public static boolean isLeftToRight(Point p1, Point p2) {
        return p1.x < p2.x;
    }

    public static Color getSelectionColor(Point p1, Point p2) {
        if (isLeftToRight(p1, p2)) {
            return SchemeColors.POSITIVE_COLOR_SELECTION;
        } else {
            return SchemeColors.NEGATIVE_COLOR_SELECTION;
        }
    }
}
