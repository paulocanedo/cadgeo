/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.geodesic.coordinate;

import br.com.geomapa.util.AngleValue;
import br.com.geomapa.geodesic.datum.Ellipsoid;

/**
 *
 * @author paulocanedo
 */
public interface Coordinate {

    public Ellipsoid getEllipsoid();

    public CartesianCoordinate toCartesian();

    public UTMCoordinate toUTM();

    public GeographicCoordinate toGeodesic();

    public double getEllipsoidalHeight();

    public AngleValue getMeridianConvergence();

}
