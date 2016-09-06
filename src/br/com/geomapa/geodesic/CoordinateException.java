/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic;

/**
 *
 * @author paulocanedo
 */
public class CoordinateException extends Exception {

    public CoordinateException() {
    }

    public CoordinateException(String message) {
        super(message);
    }

    public CoordinateException(Throwable cause) {
        super(cause);
    }

    public CoordinateException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
