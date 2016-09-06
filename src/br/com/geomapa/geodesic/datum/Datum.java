/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.datum;

/**
 *
 * @author paulocanedo
 */
public interface Datum {

    public double getSemiMajorAxis();

    public double getSemiMinorAxis();

    public double getFlattening();

    public double getInverseFlattening();

}
