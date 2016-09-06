/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.pcgeocad;

/**
 *
 * @author paulocanedo
 */
public class VisualObjectParserException extends Exception {

    /**
     * Creates a new instance of <code>VisualObjectParserException</code> without detail message.
     */
    public VisualObjectParserException() {
    }

    /**
     * Constructs an instance of <code>VisualObjectParserException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public VisualObjectParserException(String msg) {
        super(msg);
    }

    public VisualObjectParserException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
