/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.geo;

import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.ui.panels.topographic.Projector;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public class GeodesicPointReference extends AbstractVisualObject {

    private GeodesicPoint geopoint;
    private int utmZoneOverrided = 0;

    public GeodesicPointReference(GeodesicPoint geopoint) {
        this.geopoint = geopoint;
    }

    public GeodesicPointReference(GeodesicPoint geopoint, int utmZoneOverrided) {
        this.geopoint = geopoint;
        this.utmZoneOverrided = utmZoneOverrided;
    }

    public GeodesicPoint getGeopoint() {
        return geopoint;
    }

    @Override
    public double[] getArrayVertex() {
        return geopoint.getArrayVertex();
    }

    @Override
    public double getX() {
        if (utmZoneOverrided == 0) {
            return geopoint.getX();
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getY() {
        if (utmZoneOverrided == 0) {
            return geopoint.getY();
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLocation(double x, double y) {
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public boolean canRotate() {
        return false;
    }

    @Override
    public Layer getLayer() {
        return geopoint.getLayer();
    }

    @Override
    public Point2D getLocation() {
        return geopoint.getLocation();
    }

    @Override
    public void refresh() {
        geopoint.refresh();
    }

    @Override
    public boolean selection(int x, int y, Projector projector) {
        return geopoint.selection(x, y, projector);
    }

    @Override
    public boolean selectionFull(int x1, int y1, int x2, int y2, Projector projector) {
        return geopoint.selectionFull(x1, y1, x2, y2, projector);
    }

    @Override
    public boolean selectionPartial(int x1, int y1, int x2, int y2, Projector projector) {
        return geopoint.selectionPartial(x1, y1, x2, y2, projector);
    }

    @Override
    public void writeToDxf(PrintStream stream) throws IOException {
        if (utmZoneOverrided == 0) {
            geopoint.writeToDxf(stream);
        }
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f %s %d", 
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(), geopoint.getName(), utmZoneOverrided));
    }

    @Override
    public String getVisualObjectName() {
        return "geodesic_point_reference";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeodesicPointReference other = (GeodesicPointReference) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.oid;
        return hash;
    }

}
