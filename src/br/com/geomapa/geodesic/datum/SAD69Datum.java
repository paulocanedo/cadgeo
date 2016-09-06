/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.geodesic.datum;

/**
 *
 * @author paulocanedo
 */
public class SAD69Datum extends CustomDatum {

    public SAD69Datum() {
        super(6378160.0, 6356774.72, 298.25);
    }

    @Override
    public String toString() {
        return "SAD69";
    }

}
