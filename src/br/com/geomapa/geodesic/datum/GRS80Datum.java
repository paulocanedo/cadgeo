/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.geodesic.datum;

/**
 *
 * @author paulocanedo
 */
public class GRS80Datum extends CustomDatum {

    public GRS80Datum() {
        super(6378137, 6356752.31413, 298.257222101);
    }

    @Override
    public String toString() {
        return "GRS80";
    }

}
