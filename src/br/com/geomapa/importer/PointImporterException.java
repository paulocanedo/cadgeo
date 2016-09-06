/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer;

/**
 *
 * @author paulocanedo
 */
public class PointImporterException extends RuntimeException {

    public PointImporterException(Throwable cause) {
        super(cause);
    }

    public PointImporterException(String message, Throwable cause) {
        super(message, cause);
    }

    public PointImporterException(String message) {
        super(message);
    }

    public PointImporterException() {
    }
}
