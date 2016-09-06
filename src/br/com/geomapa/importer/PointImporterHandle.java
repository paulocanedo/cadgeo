/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;

/**
 *
 * @author paulocanedo
 */
public interface PointImporterHandle {

    public void startImport();

    public void handlePoint(GeodesicPoint point);

    public void endImport();
}
