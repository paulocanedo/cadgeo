/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.primitives;

import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Bus;
import java.awt.geom.Point2D;
import java.io.PrintStream;

/**
 *
 * @author paulocanedo
 */
public class LineArrow extends Line {
    
    private static final float ARROW_SIZE = 1.5f;
    private static final int ANGLE_APERTURE = 15;
    private double[] anewVertex = new double[12];

    public LineArrow(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);
    }

    public LineArrow(Point2D startPoint, Point2D endPoint) {
        super(startPoint, endPoint);
    }

    public LineArrow() {
    }
    
    @Override
    public VisualObject copy() {
        LineArrow other = new LineArrow(getX(), getY(), getEndX(), getEndY());
        matchProperties(this, other);

        return other;
    }

    @Override
    public double[] getArrayVertex() {
        double[] arrayVertex = super.getArrayVertex();
        System.arraycopy(arrayVertex, 0, anewVertex, 0, arrayVertex.length);
        
        double x = arrayVertex[0], y = arrayVertex[1], endx = arrayVertex[2], endy = arrayVertex[3];
        double azimuth = PolygonalUtils.azimuth(endx, endy, x, y).toDegreeDecimal();
        double size = ARROW_SIZE * Bus.getScale();
        
        double[] coords = PolygonalUtils.projection(endx, endy, size, azimuth + ANGLE_APERTURE);
        anewVertex[4] = endx;
        anewVertex[5] = endy;
        anewVertex[6] = coords[0];
        anewVertex[7] = coords[1];
        
        coords = PolygonalUtils.projection(endx, endy, size, azimuth - ANGLE_APERTURE);
        anewVertex[8] = endx;
        anewVertex[9] = endy;
        anewVertex[10] = coords[0];
        anewVertex[11] = coords[1];
        
        return anewVertex;
    }

    @Override
    public String getVisualObjectName() {
        return "line_arrow";
    }

    @Override
    public void writeToDxf(PrintStream stream) {
        super.writeToDxf(stream);
        
        stream.println("0");
        stream.println("LINE");
        stream.println("8");
        stream.println(getLayer().toString());
        stream.println("10");
        stream.println(getEndX());
        stream.println("20");
        stream.println(getEndY());
        stream.println("11");
        stream.println(anewVertex[6]);
        stream.println("21");
        stream.println(anewVertex[7]);
        
        stream.println("0");
        stream.println("LINE");
        stream.println("8");
        stream.println(getLayer().toString());
        stream.println("10");
        stream.println(getEndX());
        stream.println("20");
        stream.println(getEndY());
        stream.println("11");
        stream.println(anewVertex[10]);
        stream.println("21");
        stream.println(anewVertex[11]);
    }
    
}
