/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.primitives;

import br.com.geomapa.graphic.cad.spec.AbstractDoublePointVisualObject;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.ui.panels.topographic.Projector;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public abstract class QuadrilateralVisualObject extends AbstractVisualObject {

    private Point2D point0 = new Point2D.Double(0, 0);
    private Point2D point1 = new Point2D.Double(0, 0);
    private Point2D point2 = new Point2D.Double(0, 0);
    private Point2D point3 = new Point2D.Double(0, 0);

    public QuadrilateralVisualObject() {
    }

    public QuadrilateralVisualObject(double x, double y) {
        super(x, y);
    }

    public Point2D getPoint0() {
        return point0;
    }

    public void setPoint0(Point2D point0) {
        this.point0 = point0;
    }

    public Point2D getPoint1() {
        return point1;
    }

    public void setPoint1(Point2D point1) {
        this.point1 = point1;
    }

    public Point2D getPoint2() {
        return point2;
    }

    public void setPoint2(Point2D point2) {
        this.point2 = point2;
    }

    public Point2D getPoint3() {
        return point3;
    }

    public void setPoint3(Point2D point3) {
        this.point3 = point3;
    }

    @Override
    public double getX() {
        return getPoint0().getX();
    }

    @Override
    public double getY() {
        return getPoint0().getY();
    }

    @Override
    public boolean selectionFull(int x1, int y1, int x2, int y2, Projector projector) {
        int[] project1 = projector.project(getX(), getY());
        int[] project2 = projector.project(getPoint2().getX(), getPoint2().getY());
        int lx1 = project1[0], ly1 = project1[1], lx2 = project2[0], ly2 = project2[1];

        return AbstractDoublePointVisualObject.testSelectionFull(lx1, ly1, lx2, ly2, x1, y1, x2, y2);
    }

    @Override
    public boolean selectionPartial(int x1, int y1, int x2, int y2, Projector projector) {
        if (selectionFull(x1, y1, x2, y2, projector)) {
            return true;
        }

        int[] project0 = projector.project(getPoint0().getX(), getPoint0().getY());
        int[] project1 = projector.project(getPoint1().getX(), getPoint1().getY());
        int[] project2 = projector.project(getPoint2().getX(), getPoint2().getY());
        int[] project3 = projector.project(getPoint3().getX(), getPoint3().getY());

        double px1 = project0[0], py1 = project0[1],
                px2 = project1[0], py2 = project1[1],
                px3 = project2[0], py3 = project2[1],
                px4 = project3[0], py4 = project3[1];

        double rx1 = x1, ry1 = y1, rx2 = x1, ry2 = y2, rx3 = x2, ry3 = y2, rx4 = x2, ry4 = y1;

        if (Line2D.linesIntersect(px1, py1, px2, py2, rx1, ry1, rx2, ry2)) {
            return true;
        }
        if (Line2D.linesIntersect(px1, py1, px2, py2, rx2, ry2, rx3, ry3)) {
            return true;
        }
        if (Line2D.linesIntersect(px1, py1, px2, py2, rx3, ry3, rx4, ry4)) {
            return true;
        }
        if (Line2D.linesIntersect(px1, py1, px2, py2, rx4, ry4, rx1, ry1)) {
            return true;
        }

        if (Line2D.linesIntersect(px2, py2, px3, py3, rx1, ry1, rx2, ry2)) {
            return true;
        }
        if (Line2D.linesIntersect(px2, py2, px3, py3, rx2, ry2, rx3, ry3)) {
            return true;
        }
        if (Line2D.linesIntersect(px2, py2, px3, py3, rx3, ry3, rx4, ry4)) {
            return true;
        }
        if (Line2D.linesIntersect(px2, py2, px3, py3, rx4, ry4, rx1, ry1)) {
            return true;
        }

        if (Line2D.linesIntersect(px3, py3, px4, py4, rx1, ry1, rx2, ry2)) {
            return true;
        }
        if (Line2D.linesIntersect(px3, py3, px4, py4, rx2, ry2, rx3, ry3)) {
            return true;
        }
        if (Line2D.linesIntersect(px3, py3, px4, py4, rx3, ry3, rx4, ry4)) {
            return true;
        }
        if (Line2D.linesIntersect(px3, py3, px4, py4, rx4, ry4, rx1, ry1)) {
            return true;
        }

        if (Line2D.linesIntersect(px4, py4, px1, py1, rx1, ry1, rx2, ry2)) {
            return true;
        }
        if (Line2D.linesIntersect(px4, py4, px1, py1, rx2, ry2, rx3, ry3)) {
            return true;
        }
        if (Line2D.linesIntersect(px4, py4, px1, py1, rx3, ry3, rx4, ry4)) {
            return true;
        }
        if (Line2D.linesIntersect(px4, py4, px1, py1, rx4, ry4, rx1, ry1)) {
            return true;
        }

        return false;
    }
}
