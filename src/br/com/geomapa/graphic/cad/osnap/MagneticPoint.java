/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.osnap;

import br.com.geomapa.controller.MagneticController.MagneticType;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.primitives.Circle;
import br.com.geomapa.graphic.cad.primitives.Rectangle;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.options.SchemeColors;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.util.PGLUtil;
import java.awt.Color;
import java.awt.geom.Point2D;
import javax.media.opengl.GL2;

/**
 *
 * @author paulocanedo
 */
public class MagneticPoint {

    private Point2D point;
    private GeodesicPoint gpoint;
    private MagneticType type;

    public MagneticPoint(Point2D point, MagneticType type) {
        this.point = point;
        this.type = type;
    }

    public MagneticPoint(GeodesicPoint gpoint, MagneticType type) {
        this.gpoint = gpoint;
        this.type = type;
    }

    public GeodesicPoint getGeodesicPoint() {
        return gpoint;
    }

    public Point2D getPoint() {
        if (gpoint != null) {
            return gpoint.getLocation();
        }
        return point;
    }

    public MagneticType getType() {
        return type;
    }

    public VisualObject getVisualObject(GLTopographicPanel displayPanel) {
        int[] coords = displayPanel.project(getPoint().getX(), getPoint().getY());

        if (getType() == MagneticType.END_POINT) {
            rectangle.setColor(SchemeColors.MAGNETIC_COLOR);
            rectangle.setLocation(coords[0] - MAGNETIC_SIZE, coords[1] - MAGNETIC_SIZE);
            rectangle.setEndLocation(coords[0] + MAGNETIC_SIZE, coords[1] + MAGNETIC_SIZE);
            return rectangle;
        } else if (getType() == MagneticType.GEODESIC_POINT) {
            circle.setColor(SchemeColors.MAGNETIC_COLOR);
            circle.setLocation(coords[0], coords[1]);
            circle.setRadius(MAGNETIC_SIZE);
            return circle;
        } else if (getType() == MagneticType.INTERSECTION_POINT) {
            crossVO.setColor(SchemeColors.MAGNETIC_COLOR);
            crossVO.setLocation(coords[0] - MAGNETIC_SIZE, coords[1] - MAGNETIC_SIZE);
            crossVO.setEndLocation(coords[0] + MAGNETIC_SIZE, coords[1] + MAGNETIC_SIZE);
            return crossVO;
        }
        return null;
    }
    private static Rectangle rectangle = new Rectangle(0, 0, 0, 0);
    private static Circle circle = new Circle(0, 0, 0);
    private static X crossVO = new X();
    private static final int MAGNETIC_SIZE = 6;

    private static class X extends Rectangle {

        public X() {
            super(0, 0, 0, 0);
        }

        @Override
        public void draw(GL2 gl, Color color) {
            PGLUtil.setGlColor(gl, color);
            PGLUtil.drawLine(gl, getX(), getY(), getEndX(), getEndY());
            PGLUtil.drawLine(gl, getX(), getEndY(), getEndX(), getY());
        }
    }
}
