/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.geo;

import java.util.Locale;
import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.util.AngleValue;
import br.com.geomapa.geodesic.coordinate.Coordinate;
import br.com.geomapa.geodesic.coordinate.GeographicCoordinate;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.geodesic.point.MetaDataPoint;
import br.com.geomapa.graphic.cad.primitives.ImmutablePoint;
import br.com.geomapa.graphic.cad.primitives.Polyline;
import br.com.geomapa.main.Bus;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.PrintStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.abs;

/**
 *
 * @author paulocanedo
 */
public final class GeodesicPoint extends ImmutablePoint implements Comparable<GeodesicPoint> {

    private static final Pattern numericPattern = Pattern.compile("\\d+");
    private PropertyChangeSupport propertyChangeSupport;
    private MetaDataPoint metaData;
    public final static String charsSeparator = "\u2212|\u002d";
    private boolean satGeo = false;
    private boolean favorite = false;
    /*
     * Observable attributes
     */
    private String name;
    private Coordinate coordinate;

    {
        propertyChangeSupport = new PropertyChangeSupport(this);
        addPropertyChangeListener(new DefaultListener());
    }
    /*
     * Properties names
     */
    public static final String PROP_NAME = "name";
    public static final String PROP_COORDINATE = "coordinate";

    public GeodesicPoint(Coordinate coordinate, String name) {
        setName(name);
        if (coordinate != null) {
            setCoordinate(coordinate);
        }
    }

    public GeodesicPoint(Coordinate coordinate, String name, MetaDataPoint metaData) {
        setName(name);
        setMetaData(metaData);
        if (coordinate != null) {
            setCoordinate(coordinate);
        }
    }

    public String getName() {
        return name;
    }

    public String getNameForceSeparators() {
        String nameNoSeparators = getNameNoSeparators(getName());
        String nameWithSeparators = nameNoSeparators;

        try {
            nameWithSeparators = String.format("%s-%s-%s", nameNoSeparators.substring(0, 3), nameNoSeparators.charAt(3), nameNoSeparators.substring(4));
        } catch (Exception ex) {
        }

        return nameWithSeparators;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = getNameNoSeparators(name);
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);

        Layer layer = LayerController.getLayerByPointType(getType());
        setLayer(layer);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        Coordinate oldCoordinate = this.coordinate;
        this.coordinate = coordinate;
        propertyChangeSupport.firePropertyChange(PROP_COORDINATE, oldCoordinate, coordinate);
    }

    public void setEast(double east) {
        UTMCoordinate oldCoordinate = (UTMCoordinate) this.coordinate;
        this.coordinate = new UTMCoordinate(oldCoordinate.getZone(), oldCoordinate.getHemisphere(), east, oldCoordinate.getNorth(), oldCoordinate.getEllipsoidalHeight());
        propertyChangeSupport.firePropertyChange(PROP_COORDINATE, oldCoordinate, coordinate);
    }

    public void setNorth(double north) {
        UTMCoordinate oldCoordinate = (UTMCoordinate) this.coordinate;
        this.coordinate = new UTMCoordinate(oldCoordinate.getZone(), oldCoordinate.getHemisphere(), oldCoordinate.getEast(), north, oldCoordinate.getEllipsoidalHeight());
        propertyChangeSupport.firePropertyChange(PROP_COORDINATE, oldCoordinate, coordinate);
    }

    public MetaDataPoint getMetaData() {
        if (metaData == null) {
            metaData = new MetaDataPoint(null, 10, 10, 10);
        }
        return metaData;
    }

    public void setMetaData(MetaDataPoint metaData) {
        this.metaData = metaData;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isSatGeo() {
        return satGeo;
    }

    public void setSatGeo(boolean satGeo) {
        this.satGeo = satGeo;
    }

    public double horizontalDistance(GeodesicPoint otherPoint) {
        return getCoordinate().toUTM().horizontalDistance(otherPoint.getCoordinate().toUTM());
    }

    public AngleValue azimuth(GeodesicPoint otherPoint) {
        return getCoordinate().toUTM().azimuth(otherPoint.getCoordinate().toUTM());
    }

    public Point2D toPoint2D() {
        return getCoordinate().toUTM().toPoint2D();
    }

    @Override
    public String getVisualObjectName() {
        return "geodesic_point";
    }

    public GeodesicPointType getType() {
        if (getName() == null) {
            return GeodesicPointType.X;
        }
        return getType(getNameNoSeparators());
    }

    public static GeodesicPointType getType(String value) {
        if (value.length() < 4) {
            return GeodesicPointType.X;
        }
        char charAt = value.charAt(3);
        if (charAt == 'M') {
            return GeodesicPointType.M;
        } else if (charAt == 'O') {
            return GeodesicPointType.O;
        } else if (charAt == 'V') {
            return GeodesicPointType.V;
        } else if (charAt == 'P') {
            return GeodesicPointType.P;
        } else {
            return GeodesicPointType.X;
        }
    }

    public String getNameNoSeparators() {
        return getName();
    }

    public static String getNameNoSeparators(String name) {
        return name.replaceAll(charsSeparator, "");
    }

    public Integer getNumericValue() {
        return getNumericValue(getName());
    }

    public static Integer getNumericValue(String name) {
        Matcher matcher = numericPattern.matcher(name);
        String value = "0";
        while (matcher.find()) {
            value = (matcher.group());
        }
        return Integer.parseInt(value);
    }

    public static boolean containsIgnoreCase(String pointName1, String pointName2) {
        if (pointName1 == null || pointName1.isEmpty() || pointName2 == null || pointName2.isEmpty()) {
            return false;
        }
        return getNameNoSeparators(pointName1.toLowerCase()).contains(pointName2.toLowerCase());
    }

    @Override
    public double[] getArrayVertex() {
        double[] avertex = super.getArrayVertex();
        if (isFavorite()) {
            double[] newavertex = new double[20];
            double scale = Bus.getScale();

            double c1 = scale * abs(cos(2 * PI / 5));
            double c2 = scale * abs(cos(PI / 5));
            double s1 = scale * abs(sin(2 * PI / 5));
            double s2 = scale * abs(sin(4 * PI / 5));

            double x1 = getX(), y1 = getY() + scale;
            double x2 = getX() - s1, y2 = getY() + c1;
            double x3 = getX() - s2, y3 = getY() - c2;
            double x4 = getX() + s2, y4 = getY() - c2;
            double x5 = getX() + s1, y5 = getY() + c1;

            newavertex[0] = x1;
            newavertex[1] = y1;
            newavertex[2] = x3;
            newavertex[3] = y3;

            newavertex[4] = x3;
            newavertex[5] = y3;
            newavertex[6] = x5;
            newavertex[7] = y5;

            newavertex[8] = x5;
            newavertex[9] = y5;
            newavertex[10] = x2;
            newavertex[11] = y2;

            newavertex[12] = x2;
            newavertex[13] = y2;
            newavertex[14] = x4;
            newavertex[15] = y4;

            newavertex[16] = x4;
            newavertex[17] = y4;
            newavertex[18] = x1;
            newavertex[19] = y1;

            return newavertex;
        } else if (isSatGeo()) {
            int length = avertex.length;
            double[] newavertex = new double[length + 12];
            System.arraycopy(avertex, 0, newavertex, 0, avertex.length);
            double[] triangleVertexesForSat = getTriangleVertexesForSat();
            double x1 = triangleVertexesForSat[0], y1 = triangleVertexesForSat[1];
            double x2 = triangleVertexesForSat[2], y2 = triangleVertexesForSat[3];
            double x3 = triangleVertexesForSat[4], y3 = triangleVertexesForSat[5];

            newavertex[length + 0] = x1;
            newavertex[length + 1] = y1;
            newavertex[length + 2] = x2;
            newavertex[length + 3] = y2;

            newavertex[length + 4] = x2;
            newavertex[length + 5] = y2;
            newavertex[length + 6] = x3;
            newavertex[length + 7] = y3;

            newavertex[length + 8] = x3;
            newavertex[length + 9] = y3;
            newavertex[length + 10] = x1;
            newavertex[length + 11] = y1;

            return newavertex;
        }
        return avertex;
    }

    private double[] getTriangleVertexesForSat() {
        double scale1 = 2 * Bus.getScale();
        double scale2 = scale1 / 1.5;
        double x1 = getX(), y1 = getY() + scale1;
        double x2 = getX() - scale1, y2 = getY() - scale2;
        double x3 = getX() + scale1, y3 = getY() - scale2;
        return new double[]{x1, y1, x2, y2, x3, y3};
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeodesicPoint other = (GeodesicPoint) obj;
        if (this.oid != other.oid) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public int compareTo(GeodesicPoint o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(listener);
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void write(PrintStream stream) throws IOException {
    }

    @Override
    public void writeToDxf(PrintStream stream) {
        super.writeToDxf(stream);

        if (getType() == GeodesicPointType.M) {
            stream.println("0");
            stream.println("CIRCLE");
            stream.println("8");
            stream.println(getLayer().toString());
            stream.println("10");
            stream.println(getX());
            stream.println("20");
            stream.println(getY());
            stream.println("40");
            stream.println(Bus.getScale());
        }

        if (isSatGeo()) {
            Polyline polyline = new Polyline();
            double[] triangleVertexesForSat = getTriangleVertexesForSat();
            polyline.addVertex(triangleVertexesForSat[0], triangleVertexesForSat[1]);
            polyline.addVertex(triangleVertexesForSat[2], triangleVertexesForSat[3]);
            polyline.addVertex(triangleVertexesForSat[4], triangleVertexesForSat[5]);
            polyline.close();
            polyline.setLayer(LayerController.getLayerByPointType(getType()));
            polyline.writeToDxf(stream);
        }
    }

    public String toSerializeString() {
        return String.format(Locale.US, "%s,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%s", getName(),
                getX(), getY(), getCoordinate().getEllipsoidalHeight(),
                metaData == null ? " " : metaData.getQx(), metaData == null ? " " : metaData.getQy(), metaData == null ? " " : metaData.getQz(),
                metaData == null ? " " : metaData.getMeasurementMethod() == null ? " " : metaData.getMeasurementMethod().name());
    }

    private class DefaultListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(PROP_COORDINATE)) {
                setLocation(coordinate);
            }
        }
    }

    public Coordinate getCoordinate(int zone) {
        if (zone > 0 && (zone != getCoordinate().toUTM().getZone())) {
            GeographicCoordinate geographic = getCoordinate().toGeodesic();
            return geographic.toUTM(zone);
        }
        return getCoordinate();
    }
}
