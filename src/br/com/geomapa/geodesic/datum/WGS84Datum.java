/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.datum;

/**
 *
 * @author paulocanedo
 */
public class WGS84Datum extends CustomDatum {

    public WGS84Datum() {
        super(63781370, 6356752.314245, 298.257223563);
    }

    @Override
    public String toString() {
        return "WGS84";
    }


}
