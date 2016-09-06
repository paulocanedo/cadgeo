/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.geo;

import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Bus;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class GridCoordinates {

    private List<VisualObject> delegate = new ArrayList<VisualObject>();

    public GridCoordinates(Point2D coord1, Point2D coord2, double gridSpace) {
        this.coord1 = coord1;
        this.coord2 = coord2;
        this.gridSpace = gridSpace;
    }

    private boolean canSetInCoord1(Point2D point) {
        double distX = Math.abs(point.getX() - coord2.getX());
        double distY = Math.abs(point.getY() - coord2.getY());

        return (distX > gridSpace && distY > gridSpace);
    }

    private boolean canSetInCoord2(Point2D point) {
        double distX = Math.abs(point.getX() - coord1.getX());
        double distY = Math.abs(point.getY() - coord1.getY());

        return (distX > gridSpace && distY > gridSpace);
    }

    public double getFirstX() {
        double firstX = Double.MAX_VALUE;
        firstX = Math.min(firstX, coord1.getX());
        firstX = Math.min(firstX, coord2.getX());
        return firstX;
    }

    public double getLastX() {
        double lastX = 0;
        lastX = Math.max(lastX, coord1.getX());
        lastX = Math.max(lastX, coord2.getX());
        return lastX;
    }

    public double getFirstY() {
        double firstY = Double.MAX_VALUE;
        firstY = Math.min(firstY, coord1.getY());
        firstY = Math.min(firstY, coord2.getY());
        return firstY;
    }

    public double getLastY() {
        double lastY = 0;
        lastY = Math.max(lastY, coord1.getY());
        lastY = Math.max(lastY, coord2.getY());
        return lastY;
    }

    private static long roundSuperior(double value, int amount) {
        return ((long) (value / amount) + 1) * amount;
    }

    private static long round(double value, int amount) {
        return ((long) (value / amount)) * amount;
    }

    public List<VisualObject> createLabels() {
        if (!canSetInCoord1(coord1) || !canSetInCoord2(coord2) || gridSpace <= 0) {
            throw new RuntimeException("Valores inválidos para grade");
        }

        final int amount = 100;
        long currentX = roundSuperior(getFirstX(), amount);
        long currentY = roundSuperior(getFirstY(), amount);

        long maxX = round(getLastX(), amount);
        long maxY = round(getLastY(), amount);

        delegate.clear();
        do {
            delegate.addAll(Arrays.asList(createHorizontalLabel(currentX)));
        } while (currentX < maxX && (currentX += gridSpace) < maxX);

        do {
            delegate.addAll(Arrays.asList(createVerticalLabel(currentY)));
        } while (currentY < maxY && (currentY += gridSpace) < maxY);
        return delegate;
    }

    private VisualObject[] createVerticalLabel(double y) {
        VisualText vtext = new VisualText(getFirstX() + Bus.getScale() * 3, y + 1 * Bus.getScale(), 2, String.format("%.0f", y));
        Line line = new Line(getFirstX(), y, getFirstX() + Bus.getScale() * 4 + vtext.getScaledWidth(), y);
        return new VisualObject[]{vtext, line};
    }

    private VisualObject[] createHorizontalLabel(double x) {
        VisualText vtext = new VisualText(x + 3 * Bus.getScale(), getFirstY() + Bus.getScale() * 3, 2, String.format("%.0f", x));
        vtext.setRotation(90);
        Line line = new Line(x, getFirstY(), x, getFirstY() + Bus.getScale() * 4 + vtext.getScaledWidth());
        return new VisualObject[]{vtext, line};
    }
    private Point2D coord1;
    private Point2D coord2;
    private double gridSpace;
}
