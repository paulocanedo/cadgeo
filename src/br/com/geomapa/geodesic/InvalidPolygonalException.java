/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.geodesic;

/**
 *
 * @author paulocanedo
 */
public class InvalidPolygonalException extends RuntimeException {

    public InvalidPolygonalException(Throwable cause) {
        super(cause);
    }

    public InvalidPolygonalException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPolygonalException(String message) {
        super(message);
    }

    public InvalidPolygonalException() {
    }

}
