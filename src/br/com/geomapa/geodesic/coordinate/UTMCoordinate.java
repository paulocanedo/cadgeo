/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.coordinate;

import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.util.AngleValue;
import br.com.geomapa.geodesic.datum.Ellipsoid;
import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.geodesic.TransverseMercator;
import br.com.geomapa.geodesic.datum.GRS80Datum;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author paulocanedo
 */
public class UTMCoordinate implements Coordinate {

    private Ellipsoid ellipsoid;
    private int zone;
    private Hemisphere hemisphere;
    private double height;
    private TransverseMercator transverseMercator;

    public UTMCoordinate(Ellipsoid ellipsoid, int zone, Hemisphere hemisphere, double east, double north, double height) {
        this.ellipsoid = ellipsoid;
        this.zone = zone;
        this.hemisphere = hemisphere;
        this.point2d.setLocation(east, north);
        this.height = height;
    }

    public UTMCoordinate(Ellipsoid ellipsoid, int zone, Hemisphere hemisphere, double east, double north) {
        this(ellipsoid, zone, hemisphere, east, north, 0);
    }

    public UTMCoordinate(int zone, Hemisphere hemisphere, double east, double north, double height) {
        //TODO BUG aqui: não deve atribuir um datum automático
        this(new Ellipsoid(new GRS80Datum()), zone, hemisphere, east, north, height);
    }

    public UTMCoordinate(int zone, Hemisphere hemisphere, double east, double north) {
        this(new Ellipsoid(new GRS80Datum()), zone, hemisphere, east, north, 0);
    }

    public boolean isTransformSupported() {
        return ellipsoid != null;
    }

    @Override
    public CartesianCoordinate toCartesian() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UTMCoordinate toUTM() {
        return this;
    }

    @Override
    public GeographicCoordinate toGeodesic() {
        return getTransverseMercator().convertToGeographic(this);
    }

    @Override
    public AngleValue getMeridianConvergence() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private TransverseMercator getTransverseMercator() {
        if (ellipsoid == null) {
            throw new IllegalArgumentException("Não é possível realizar a conversão porque esta coordenada não possui um datum definido.");
        }
        if (this.transverseMercator == null) {
            Longitude centralMeridian = new Longitude(Longitude.calcCentralMeridian(zone));
            this.transverseMercator = new TransverseMercator(centralMeridian, ellipsoid);
        }
        return this.transverseMercator;
    }

    public double horizontalDistance(UTMCoordinate otherCoordinate) {
        return PolygonalUtils.horizontalDistance(this, otherCoordinate);
    }

    public AngleValue azimuth(UTMCoordinate otherCoordinate) {
        return PolygonalUtils.azimuth(this, otherCoordinate);
    }

    public UTMCoordinate projection(AngleValue azimuth, double distance) {
        double projectionEast, projectionNorth;

        projectionEast = getEast() + (distance * Math.sin(azimuth.toRadians()));
        projectionNorth = getNorth() + (distance * Math.cos(azimuth.toRadians()));

        return new UTMCoordinate(getZone(), getHemisphere(), projectionEast, projectionNorth);
    }

    public int getZone() {
        return zone;
    }

    public Hemisphere getHemisphere() {
        return hemisphere;
    }

    public double getEast() {
        return point2d.getX();
    }

    public double getNorth() {
        return point2d.getY();
    }

    public void setEllipsoidalHeight(double height) {
        this.height = height;
    }

    @Override
    public double getEllipsoidalHeight() {
        return height;
    }

    @Override
    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    public Point2D toPoint2D() {
        return point2d;
    }

    public static Point2D parsePoint2D(String text) throws CoordinateException {
        try {
            String[] split = text.split(",");
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);

            return new Point2D.Double(x, y);
        } catch (Throwable ex) {
            throw new CoordinateException("Coordenada inválida", ex);
        }
    }

    public static Point2D derivatePoint2D(Point2D point, String offset) throws CoordinateException {
        try {
            if (!offset.startsWith("@")) {
                return null;
            }
            offset = offset.substring(1);
            String[] split = offset.split(",");
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);

            return new Point2D.Double(point.getX() + x, point.getY() + y);
        } catch (Throwable ex) {
            throw new CoordinateException("Coordenada inválida", ex);
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "E: %.3fm N: %.3fm", getEast(), getNorth());
    }
    private Point2D point2d = new Point2D.Double();

    public static void main(String... args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 400, 250);
        frame.add(new JButton(new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTimeMillis = System.currentTimeMillis();
                for (int i = 0; i < 100000; i++) {
                    UTMCoordinate coord = new UTMCoordinate(new Ellipsoid(new GRS80Datum()), 23, Hemisphere.SOUTH, 210340, 9200134);
                    GeographicCoordinate toGeodesic = coord.toGeodesic();
                }

                System.out.println(System.currentTimeMillis() - currentTimeMillis);
            }
        }));
        frame.setVisible(true);
    }
}
