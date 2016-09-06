/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.primitives;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.Projector;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

/**
 *
 * @author paulocanedo
 */
public class Polyline extends AbstractVisualObject {

    private double[] arrayVertex = new double[0];
    private Double bx, by;
    private boolean closed = false;

    public Polyline() {
    }

    @Override
    public double[] getArrayVertex() {
        return arrayVertex;
    }

    @Override
    public double getX() {
        return arrayVertex[0];
    }

    @Override
    public double getY() {
        return arrayVertex[1];
    }

    @Override
    public void setLocation(double x, double y) {
        arrayVertex[0] = x;
        arrayVertex[1] = y;
    }

    private void addFirstVertex(double x, double y) {
        this.bx = x;
        this.by = y;
    }

    private void addSecondVertex(double x, double y) {
        arrayVertex = new double[]{bx, by, x, y};
    }

    public final void addVertex(double x, double y) {
        if (arrayVertex.length == 0) {
            if (bx == null || by == null) {
                addFirstVertex(x, y);
            } else {
                addSecondVertex(x, y);
            }
            return;
        }

        double[] tempArrayVertex = new double[arrayVertex.length + 4];
        System.arraycopy(arrayVertex, 0, tempArrayVertex, 0, arrayVertex.length);

        tempArrayVertex[arrayVertex.length + 0] = arrayVertex[arrayVertex.length - 2];
        tempArrayVertex[arrayVertex.length + 1] = arrayVertex[arrayVertex.length - 1];
        tempArrayVertex[arrayVertex.length + 2] = x;
        tempArrayVertex[arrayVertex.length + 3] = y;

        this.arrayVertex = tempArrayVertex;
    }

    public final double[] getVertex(int index) {
        if (index == getVertexSize() - 1) {
            int lenght = arrayVertex.length;
            return new double[]{arrayVertex[lenght - 2], arrayVertex[lenght - 1]};
        }
        return new double[]{arrayVertex[index * 4], arrayVertex[index * 4 + 1]};
    }

    public final void setVertex(int index, double x, double y) {
        int vertexSize = getVertexSize();
        if (index >= vertexSize) {
            throw new IndexOutOfBoundsException(String.format("%d index position is greatter or equals than %d vertex size", index, vertexSize));
        }

        if (index < 0) {
            throw new IndexOutOfBoundsException(String.format("Invalid index value: %d", index));
        }

        if (index > 0) {
            arrayVertex[4 * index - 2] = x;
            arrayVertex[4 * index - 1] = y;
        }
        arrayVertex[4 * index + 0] = x;
        arrayVertex[4 * index + 1] = y;
    }

    public final int getVertexSize() {
        int length = arrayVertex.length;
        if (length < 4) {
            return 0;
        } else if (length == 4) {
            return 2;
        } else {
            return (arrayVertex.length / 4) + 1;
        }
    }

    public final boolean close() {
        int vertexSize = getVertexSize();
        if (vertexSize > 2) {
            int length = arrayVertex.length;
            double fx = arrayVertex[0], fy = arrayVertex[1];
            double lx = arrayVertex[length - 2], ly = arrayVertex[length - 1];
            if (fx != lx || fy != ly) {
                addVertex(lx, ly);
            }
            closed = true;
        }
        return closed;
    }

    public final boolean isClosed() {
        return closed;
    }

    @Override
    public void writeToDxf(PrintStream stream) {
        stream.println("0");
        stream.println("POLYLINE");
        if (!isColorLayer()) {
            stream.println("62");
            stream.println(LayerController.getDxfColor(getColor()));
        }
        if (getLineType() != null) {
            stream.println("6");
            stream.println(getLineType().getDxfName());
        }
        stream.println("8");
        stream.println(getLayer());
        stream.println("10");
        stream.println("0");
        stream.println("20");
        stream.println("0");
        stream.println("30");
        stream.println("0");
        stream.println("70");
        stream.println(closed ? "1" : "0");
        stream.println("62");
        stream.println("7");
        stream.println("66");
        stream.println("1");
        double[] vertex = null;
        for (int i = 0; i < getVertexSize(); i++) {
            vertex = getVertex(i);
            stream.println("0");
            stream.println("VERTEX");
            stream.println("8");
            stream.println(getLayer());
            stream.println("62");
            stream.println("7");
            stream.println("10");
            stream.println(vertex[0]);
            stream.println("20");
            stream.println(vertex[1]);
        }
        stream.println("0");
        stream.println("SEQEND");
    }

    @Override
    public void write(PrintStream stream) throws IOException {
    }

    @Override
    public String getVisualObjectName() {
        return "polyline";
    }

    @Override
    public VisualObject copy() {
        Polyline other = new Polyline();
        other.arrayVertex = getArrayVertex();

        matchProperties(this, other);
        return other;
    }

    @Override
    public boolean selectionFull(int x1, int y1, int x2, int y2, Projector projector) {
        for (Iterator<Point2D> iterator = vertexIterator(); iterator.hasNext();) {
            Point2D point = iterator.next();
            int[] project = projector.project(point.getX(), point.getY());
            int x = project[0], y= project[1];

            if (!coverPoint(x, y, x1, y1, x2, y2)) {
                return false;
            }
        }
        return true;
    }

    public Iterator<Point2D> vertexIterator() {
        return new VertexIterator();
    }

    private class VertexIterator implements Iterator<Point2D> {

        private int currentIndex = -1;

        @Override
        public boolean hasNext() {
            return currentIndex + 1 < getVertexSize();
        }

        @Override
        public Point2D next() {
            double[] vertex = getVertex(++currentIndex);
            return new Point2D.Double(vertex[0], vertex[1]);
        }

        @Override
        public void remove() {
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Polyline other = (Polyline) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + this.oid;
        return hash;
    }
    
}
