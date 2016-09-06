/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.geo;

import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.geodesic.point.LineDivisionType;
import br.com.geomapa.geodesic.point.RoadType;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.util.AngleValue;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author paulocanedo
 */
public class LineDivision extends Line {

    private static Map<String, LineDivision> map = new HashMap<String, LineDivision>();
    private GeodesicPoint startpoint;
    private GeodesicPoint endpoint;
    private String borderName;
    private LineDivisionType type = LineDivisionType.LA1;
    public static final String PROP_START_POINT = "startpoint";
    public static final String PROP_END_POINT = "endpoint";
    public static final String PROP_BORDER_NAME = "borderName";
    public static final String PROP_TYPE = "type";

    private LineDivision(GeodesicPoint startPoint, GeodesicPoint endPoint) {
        super(startPoint.toPoint2D(), endPoint.toPoint2D());

        this.startpoint = startPoint;
        this.endpoint = endPoint;
    }

    public static LineDivision getInstance(GeodesicPoint startPoint, GeodesicPoint endPoint) {
        String key = startPoint.getNameNoSeparators() + "." + endPoint.getNameNoSeparators();
        LineDivision ld;
        if (!map.containsKey(key)) {
            ld = new LineDivision(startPoint, endPoint);
            map.put(key, ld);
        } else {
            ld = map.get(key);
        }
        ld.startpoint = startPoint;
        ld.endpoint = endPoint;
        return ld;
    }

    public static void removeInstance(LineDivision ld) {
        removeInstance(ld.getStartPoint(), ld.getEndPoint());
    }

    public static void removeInstance(GeodesicPoint onePoint, GeodesicPoint otherPoint) {
        String key = onePoint.getNameNoSeparators() + "." + otherPoint;

        map.remove(key);
    }

    public static boolean anyOccurrence(GeodesicPoint gpoint) {
        for (LineDivision ld : map.values()) {
            if (ld.getStartPoint().equals(gpoint) || ld.getEndPoint().equals(gpoint)) {
                return true;
            }
        }
        return false;
    }

    public static LineDivision findAnyOccurrence(GeodesicPoint gpoint) {
        for (LineDivision ld : map.values()) {
            if (ld.getStartPoint().equals(gpoint) || ld.getEndPoint().equals(gpoint)) {
                return ld;
            }
        }
        return null;
    }

    public static LineDivision[] findFrom(GeodesicPoint gpoint) {
        return find(gpoint, true);
    }

    public static LineDivision[] findTo(GeodesicPoint gpoint) {
        return find(gpoint, false);
    }

    private static LineDivision[] find(GeodesicPoint gpoint, boolean first) {
        Collection<LineDivision> collection = new ArrayList<LineDivision>();
        for (LineDivision ld : map.values()) {
            if (first && ld.getStartPoint().equals(gpoint)) {
                collection.add(ld);
            } else if (!first && ld.getEndPoint().equals(gpoint)) {
                collection.add(ld);
            }
        }
        return collection.toArray(new LineDivision[0]);
    }

    public static Collection<LineDivision> getAllLineDivisions() {
        return map.values();
    }

    @Override
    public Point2D getLocation() {
        UTMCoordinate coord = startpoint.getCoordinate().toUTM();
        Point2D point = super.getLocation();
        point.setLocation(coord.getEast(), coord.getNorth());
        return point;
    }

    @Override
    public Point2D getEndLocation() {
        UTMCoordinate coord = endpoint.getCoordinate().toUTM();
        Point2D point = super.getEndLocation();
        point.setLocation(coord.getEast(), coord.getNorth());
        return point;
    }

    public LineDivision reverseLineDivision() {
        return getInstance(endpoint, startpoint);
    }

    public Boolean isWaterCourseClockwiseDirection() {
        if (getType() != LineDivisionType.LN1) {
            return null;
        }
        return waterCourseDirection;
    }

    public void setWaterCourseClockwiseDirection(boolean clockwise) {
        this.waterCourseDirection = clockwise;
    }

    public String getNomeEstrada() {
        if (getType() != LineDivisionType.LA4) {
            return null;
        }

        if (getRoadType() != RoadType.FAIXA_DE_DOMINIO) {
            return "Estrada Vicinal";
        }
        return "Faixa de domínio - " + idFaixaDeDominio;
    }

    public void setIdFaixaDeDominio(String idFaixaDeDominio) {
        this.idFaixaDeDominio = idFaixaDeDominio;
    }

    public RoadType getRoadType() {
        return roadType;
    }

    public void setRoadType(RoadType roadType) {
        this.roadType = roadType;
    }
    private String idFaixaDeDominio = "";
    private boolean waterCourseDirection = true;
    private RoadType roadType;

    /**
     * Get the value of startpoint
     *
     * @return the value of startpoint
     */
    public GeodesicPoint getStartPoint() {
        return startpoint;
    }

    /**
     * Set the value of startpoint
     *
     * @param startpoint new value of endpoint1
     */
    public void setStartPoint(GeodesicPoint startpoint) {
        GeodesicPoint oldStartPoint = this.startpoint;
        String oldkey = oldStartPoint.getNameNoSeparators() + "." + getEndPoint().getNameNoSeparators();
        this.startpoint = startpoint;

        String key = getStartPoint().getNameNoSeparators() + "." + getEndPoint().getNameNoSeparators();
        map.remove(oldkey);
        map.put(key, this);
        propertyChangeSupport.firePropertyChange(PROP_START_POINT, oldStartPoint, startpoint);
    }

    /**
     * Get the value of endpoint
     *
     * @return the value of endpoint
     */
    public GeodesicPoint getEndPoint() {
        return endpoint;
    }

    /**
     * Set the value of endpoint
     *
     * @param endpoint new value of endpoint
     */
    public void setEndpoint(GeodesicPoint endpoint) {
        GeodesicPoint oldEndpoint2 = this.endpoint;
        String oldkey = getStartPoint().getNameNoSeparators() + "." + oldEndpoint2.getNameNoSeparators();
        this.endpoint = endpoint;

        String key = getStartPoint().getNameNoSeparators() + "." + getEndPoint().getNameNoSeparators();
        map.remove(oldkey);
        map.put(key, this);
        propertyChangeSupport.firePropertyChange(PROP_END_POINT, oldEndpoint2, endpoint);
    }

    /**
     * Get the value of borderName
     *
     * @return the value of borderName
     */
    public String getBorderName() {
        if (getType() == LineDivisionType.LA4) {
            return getNomeEstrada();
        }
        return borderName;
    }

    /**
     * Set the value of borderName
     *
     * @param borderName new value of borderName
     */
    public void setBorderName(String borderName) {
        if (borderName == null || borderName.toLowerCase().equals("null")) {
            borderName = "";
        }
        String oldBorderName = this.borderName;
        this.borderName = borderName;
        propertyChangeSupport.firePropertyChange(PROP_BORDER_NAME, oldBorderName, borderName);
    }

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public LineDivisionType getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type new value of type
     */
    public void setType(LineDivisionType type) {
        LineDivisionType oldType = this.type;
        this.type = type;
        propertyChangeSupport.firePropertyChange(PROP_TYPE, oldType, type);
    }

    public AngleValue azimuth() {
        return startpoint.azimuth(endpoint);
    }

    public double distance() {
        return startpoint.horizontalDistance(endpoint);
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public String getVisualObjectName() {
        return "lined";
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f %s %s \"%s\" %s %s %s \"%s\"",
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(),
                getStartPoint().getNameNoSeparators(), getEndPoint().getNameNoSeparators(),
                getBorderName(), getType() == null ? "null" : getType().name(), isWaterCourseClockwiseDirection(), getRoadType(), getNomeEstrada()));
    }

    public boolean equalsIgnoreWay(LineDivision obj) {
        if (obj == null) {
            return false;
        }

        if (equals(obj)) {
            return true;
        }

        if (getStartPoint().equals(obj.getEndPoint()) && getEndPoint().equals(obj.getStartPoint())) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", getStartPoint(), getEndPoint());
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public void move(double offsetX, double offsetY) {
    }

    @Override
    public void refresh() {
        double x1 = getStartPoint().getX();
        double y1 = getStartPoint().getY();
        double x2 = getEndPoint().getX();
        double y2 = getEndPoint().getY();

        setArrayVertex(x1, y1, x2, y2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LineDivision other = (LineDivision) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.oid;
        return hash;
    }
}
