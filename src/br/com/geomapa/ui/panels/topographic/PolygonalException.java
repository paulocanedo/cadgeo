/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels.topographic;

/**
 *
 * @author paulocanedo
 */
public class PolygonalException extends Exception {

    public PolygonalException(Throwable thrwbl) {
        super(thrwbl);
    }

    public PolygonalException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public PolygonalException(String string) {
        super(string);
    }
    
}
