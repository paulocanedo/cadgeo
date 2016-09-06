/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.util;

/**
 *
 * @author paulocanedo
 */
public class InvalidAngleValue extends RuntimeException {

    public InvalidAngleValue(Throwable cause) {
        super(cause);
    }

    public InvalidAngleValue(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAngleValue(String message) {
        super(message);
    }

    public InvalidAngleValue() {
    }

}
