/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.geodesic.coordinate;

import br.com.geomapa.util.AngleValue;
import br.com.geomapa.geodesic.datum.Ellipsoid;
import br.com.geomapa.geodesic.coordinate.GeographicCoordinate;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;

/**
 *
 * @author paulocanedo
 */
public class CartesianCoordinate implements Coordinate {

    public Ellipsoid getEllipsoid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CartesianCoordinate toCartesian() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public UTMCoordinate toUTM() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GeographicCoordinate toGeodesic() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getEllipsoidalHeight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AngleValue getMeridianConvergence() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    

}
