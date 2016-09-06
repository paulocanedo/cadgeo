/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.geodesic;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public class InvalidGeodesicPointException extends Exception {

    private GeodesicPoint geoPoint;

    public InvalidGeodesicPointException(GeodesicPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public GeodesicPoint getGeoPoint() {
        return geoPoint;
    }

    @Override
    public String getMessage() {
        String name;
        if(geoPoint.getName() == null || geoPoint.getName().length() == 0) {
            name = "Sem nome";
        } else {
            name = geoPoint.getName();
        }
        return String.format(Locale.ENGLISH, "Ponto inválido: %s", name);
    }

}
