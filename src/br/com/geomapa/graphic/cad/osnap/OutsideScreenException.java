/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.osnap;

/**
 *
 * @author paulocanedo
 */
public class OutsideScreenException extends Exception {

    public OutsideScreenException() {
    }

    public OutsideScreenException(String message) {
        super(message);
    }

    public OutsideScreenException(Throwable cause) {
        super(cause);
    }

    public OutsideScreenException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
