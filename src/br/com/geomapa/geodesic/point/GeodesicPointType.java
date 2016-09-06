/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.point;

/**
 *
 * @author paulocanedo
 */
public enum GeodesicPointType {

    M("Marco"),
    O("Offset"),
    V("Virtual"),
    P("Ponto"),
    X("Desconhecido");

    private GeodesicPointType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
    private String description;
}