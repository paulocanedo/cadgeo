/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.compound;

import br.com.geomapa.graphic.cad.spec.AbstractDoublePointVisualObject;
import br.com.geomapa.graphic.cad.spec.DoublePointVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.options.SchemeColors;
import br.com.geomapa.ui.panels.topographic.Projector;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.media.opengl.GL2;

/**
 *
 * @author paulocanedo
 */
public abstract class AbstractVisualObjectCompound extends AbstractDoublePointVisualObject implements VisualObjectCompound {

    protected List<VisualObject> delegate = new ArrayList<VisualObject>();

    @Override
    public double[] getArrayVertex() {
        return new double[0];
    }

    @Override
    public double getX() {
        if (delegate.isEmpty()) {
            return 0;
        }

        double x = Double.MAX_VALUE;
        for (Iterator<VisualObject> it = iterator(); it.hasNext();) {
            VisualObject vo = it.next();

            x = Math.min(x, vo.getX());
        }
        return x;
    }

    @Override
    public double getY() {
        if (delegate.isEmpty()) {
            return 0;
        }

        double y = Double.MAX_VALUE;
        for (Iterator<VisualObject> it = iterator(); it.hasNext();) {
            VisualObject vo = it.next();

            y = Math.min(y, vo.getY());
        }
        return y;
    }

    @Override
    public void setLocation(double x, double y) {
        double currentX = getX();
        double currentY = getY();

        move(x - currentX, y - currentY);
    }

    @Override
    public double getEndX() {
        if (delegate.isEmpty()) {
            return 0;
        }

        double x = 0;
        for (Iterator<VisualObject> it = iterator(); it.hasNext();) {
            VisualObject vo = it.next();

            x = Math.max(x, vo.getX());
            if (vo instanceof DoublePointVisualObject) {
                DoublePointVisualObject dvo = (DoublePointVisualObject) vo;
                x = Math.max(x, dvo.getEndX());
            }
        }
        return x;
    }

    @Override
    public double getEndY() {
        if (delegate.isEmpty()) {
            return 0;
        }

        double y = 0;
        for (Iterator<VisualObject> it = iterator(); it.hasNext();) {
            VisualObject vo = it.next();

            y = Math.max(y, vo.getY());
            if (vo instanceof DoublePointVisualObject) {
                DoublePointVisualObject dvo = (DoublePointVisualObject) vo;
                y = Math.max(y, dvo.getEndY());
            }
        }
        return y;
    }

    @Override
    public void setEndLocation(double endX, double endY) {
    }

    @Override
    public boolean selection(int x, int y, Projector projector) {
        for (Iterator<VisualObject> it = iterator(); it.hasNext();) {
            VisualObject vo = it.next();
            if (vo.selection(x, y, projector)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean selectionFull(int x1, int y1, int x2, int y2, Projector projector) {
        for (Iterator<VisualObject> it = iterator(); it.hasNext();) {
            VisualObject vo = it.next();
            if (vo.selectionFull(x1, y1, x2, y2, projector)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean selectionPartial(int x1, int y1, int x2, int y2, Projector projector) {
        for (Iterator<VisualObject> it = iterator(); it.hasNext();) {
            VisualObject vo = it.next();
            if (vo.selectionPartial(x1, y1, x2, y2, projector)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(GL2 gl, Color color) {
        for (Iterator<VisualObject> it = iterator(); it.hasNext();) {
            VisualObject vo = it.next();
            vo.draw(gl, color.equals(SchemeColors.SELECTED) ? color : vo.getColor());
        }
    }

    @Override
    public void writeToDxf(PrintStream stream) throws IOException {
        for (Iterator<VisualObject> it = iterator(); it.hasNext();) {
            VisualObject vo = it.next();
            vo.writeToDxf(stream);
        }
    }

    @Override
    public void refresh() {
        for (Iterator<VisualObject> it = iterator(); it.hasNext();) {
            VisualObject vo = it.next();
            vo.refresh();
        }
    }

    @Override
    public void move(double offsetX, double offsetY) {
        for (VisualObject vo : delegate) {
            vo.move(offsetX, offsetY);
        }
    }

    @Override
    public Iterator<VisualObject> iterator() {
        return delegate.iterator();
    }
}
