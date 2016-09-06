/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic;

import java.awt.geom.Point2D;
import br.com.geomapa.util.AngleValue;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.graphic.cad.geo.GeodesicPointReference;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

/**
 *
 * @author paulocanedo
 */
public final class PolygonalUtils {

    private PolygonalUtils() {
    }

    public static double horizontalDistance(double x1, double y1, double x2, double y2) {
        double distance = sqrt(pow((x2 - x1), 2) + pow((y2 - y1), 2));
        if (distance < 0) {
            distance = -distance;
        }
        return distance;
    }

    public static double horizontalDistance(Point2D point1, Point2D point2) {
        return horizontalDistance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    public static double horizontalDistance(UTMCoordinate aCoord, UTMCoordinate otherCoord) {
        double x1 = aCoord.getEast();
        double x2 = otherCoord.getEast();

        double y1 = aCoord.getNorth();
        double y2 = otherCoord.getNorth();

        return horizontalDistance(x1, y1, x2, y2);
    }

    public static AngleValue azimuth(UTMCoordinate aCoord, UTMCoordinate otherCoord) {
        double e1 = aCoord.getEast();
        double e2 = otherCoord.getEast();

        double n1 = aCoord.getNorth();
        double n2 = otherCoord.getNorth();
        return azimuth(e1, n1, e2, n2);
    }

    public static AngleValue azimuth(Point2D p1, Point2D p2) {
        return azimuth(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public static AngleValue azimuth(double x1, double y1, double x2, double y2) {
        double rumo = Math.toDegrees(Math.atan((x2 - x1) / (y2 - y1)));
        if (rumo < 0) {
            rumo = -rumo;
        }
        double azimuth = 0;

        if (x1 < x2 && y1 < y2) { //primeiro quadrante
            azimuth = rumo;
        } else if (x1 < x2 && y1 > y2) { //segundo quadrante
            azimuth = 180 - rumo;
        } else if (x1 > x2 && y1 > y2) { //terceiro quadrante
            azimuth = 180 + rumo;
        } else if (x1 > x2 && y1 < y2) {
            azimuth = 360 - rumo;
        }
        return new AngleValue(azimuth) {

            @Override
            public String toString() {
                return toString("dd" + AngleValue.UNICODE_DEGREE + "mm'ss\"", 0);
            }
        };
    }

    public static double area(UTMCoordinate... coords) {
        if (coords.length < 3) {
            throw new IllegalArgumentException("Precisa-se de pelo menos três pontos para calcular a área.");
        }
        double area = 0;

        UTMCoordinate coord;
        UTMCoordinate nextCoord;
        for (int i = 0; i < coords.length - 1; i++) {
            coord = coords[i];
            nextCoord = coords[i + 1];
            area += coord.getEast() * nextCoord.getNorth() - nextCoord.getEast() * coord.getNorth();
        }
        coord = coords[coords.length - 1];
        nextCoord = coords[0];
        area += (coord.getEast() * nextCoord.getNorth() - nextCoord.getEast() * coord.getNorth());
        if (area < 0) {
            area = -area;
        }

        return area / 2;
    }

    public static Point2D centroid(UTMCoordinate... coords) {
        if (coords.length < 3) {
            throw new IllegalArgumentException("Precisa-se de pelo menos três pontos para calcular o centro de massa.");
        }
        double x = 0, y = 0;
        double area = area(coords);

        UTMCoordinate coord;
        UTMCoordinate nextCoord;
        for (int i = 0; i < coords.length - 1; i++) {
            coord = coords[i];
            nextCoord = coords[i + 1];

            x += (coord.getEast() + nextCoord.getEast()) * (coord.getEast() * nextCoord.getNorth() - nextCoord.getEast() * coord.getNorth());
            y += (coord.getNorth() + nextCoord.getNorth()) * (coord.getEast() * nextCoord.getNorth() - nextCoord.getEast() * coord.getNorth());
        }
        return new Point2D.Double(Math.abs(1 / (6.0 * area) * x), Math.abs(1 / (6.0 * area) * y));
    }

    public static double perimeter(UTMCoordinate... coords) {
        if (coords.length < 3) {
            throw new IllegalArgumentException("Precisa-se de pelo menos três pontos para calcular o perímetro.");
        }
        double perimeter = 0;

        for (int i = 0; i < coords.length - 1; i++) {
            perimeter += (coords[i].horizontalDistance(coords[i + 1]));
        }

        return perimeter + (coords[coords.length - 1].horizontalDistance(coords[0]));
    }

    public static double area(GeodesicPoint... points) {
        return area(listUTMCoordinates(points).toArray(new UTMCoordinate[0]));
    }

    public static double perimeter(GeodesicPoint... points) {
        return perimeter(listUTMCoordinates(points).toArray(new UTMCoordinate[0]));
    }

    /**
     * 
     * @param x
     * @param y
     * @param distance
     * @param azimuth in degree
     * @return array[x,y]
     */
    public static double[] projection(double x, double y, double distance, double azimuth) {
        double newx = x + (distance * Math.sin(Math.toRadians(azimuth)));
        double newy = y + (distance * Math.cos(Math.toRadians(azimuth)));

        return new double[]{newx, newy};
    }

    public static List<UTMCoordinate> listUTMCoordinates(GeodesicPoint... points) {
        List<UTMCoordinate> list = new ArrayList<UTMCoordinate>();
        for (GeodesicPoint point : points) {
            list.add(point.getCoordinate().toUTM());
        }
        return list;
    }

    public static int countOccurrences(Polygonal polygonal, GeodesicPointType type) {
        int total = 0;
        for (VisualObject vo : polygonal.getVisualObjects()) {
            GeodesicPoint gp = null;
            if (vo instanceof GeodesicPoint) {
                gp = (GeodesicPoint) vo;
            } else if (vo instanceof GeodesicPointReference) {
                gp = ((GeodesicPointReference) vo).getGeopoint();
            }

            if (gp == null) {
                continue;
            }

            if (gp.getType() == type) {
                total++;
            }
        }
        return total;
    }
}
