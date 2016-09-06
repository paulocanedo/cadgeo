/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class PointImporterListHandler implements PointImporterHandle {

    private List<GeodesicPoint> points = new ArrayList<GeodesicPoint>();

    @Override
    public void handlePoint(GeodesicPoint point) {
        points.add(point);
    }

    public List<GeodesicPoint> getPoints() {
        return points;
    }

    @Override
    public void startImport() {
    }

    @Override
    public void endImport() {
    }

}
