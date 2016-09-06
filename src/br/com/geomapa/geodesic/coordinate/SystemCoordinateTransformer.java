/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.coordinate;

/**
 *
 * @author paulocanedo
 */
public class SystemCoordinateTransformer {

    private Class<? extends Coordinate> systemCoordinate;
    public final static SystemCoordinateTransformer UTM_TRANSFORMER = new SystemCoordinateTransformer(UTMCoordinate.class);
    public final static SystemCoordinateTransformer GEOGRAPHIC_TRANSFORMER = new SystemCoordinateTransformer(GeographicCoordinate.class);

    private SystemCoordinateTransformer(Class<? extends Coordinate> systemCoordinate) {
        this.systemCoordinate = systemCoordinate;
    }

    public Object getX(Coordinate coord) {
        if (systemCoordinate.equals(GeographicCoordinate.class)) {
            return coord.toGeodesic().getLongitude();
        } else if (systemCoordinate.equals(UTMCoordinate.class)) {
            return coord.toUTM().getEast();
        }
        return null;
    }

    public Object getY(Coordinate coord) {
        if (systemCoordinate.equals(GeographicCoordinate.class)) {
            return coord.toGeodesic().getLatitude();
        } else if (systemCoordinate.equals(UTMCoordinate.class)) {
            return coord.toUTM().getNorth();
        }
        return null;
    }
}
