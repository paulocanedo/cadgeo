/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.geo;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.graphic.cad.spec.VisualObjectReferenced;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public class GeodesicPointText extends VisualText implements VisualObjectReferenced<GeodesicPoint> {

    private final GeodesicPoint geoPoint;
    private final Point2D offset = new Point2D.Double(10, 10);

    public GeodesicPointText(GeodesicPoint gpoint) {
        this.geoPoint = gpoint;
        
        setLocation(geoPoint.getLocation());
        GeodesicPointType type = geoPoint.getType();
        
        setHeight(1.5f);
        setLayer(LayerController.find("Vertices_Tipo_" + type.name()));
        setArrayVertex(getX(), getY(), getHeight(), getRotation(), getText());
    }

    @Override
    public final String getText() {
        return geoPoint.getNameNoSeparators();
    }

    @Override
    protected final void setArrayVertex(double x, double y, float height, double rotation, String text) {
        super.setArrayVertex(geoPoint.getX() + offset.getX(), geoPoint.getY() + offset.getY(), height, rotation, text);
    }
    
    @Override
    public void move(double offsetX, double offsetY) {
        double x = offset.getX();
        double y = offset.getY();

        offset.setLocation(x + offsetX, y + offsetY);
        
        setArrayVertex(0, 0, getHeight(), getRotation(), getText());
    }

    @Override
    public void setOffset(double x, double y) {
        this.offset.setLocation(x, y);
        
        setArrayVertex(0, 0, getHeight(), getRotation(), getText());
    }
    
    @Override
    public GeodesicPoint referencedObject() {
        return geoPoint;
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f %.3f %.3f %s %.1f", 
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(), offset.getX(), offset.getY(), referencedObject().toString(), getRotation()));
    }

    @Override
    public String getVisualObjectName() {
        return "geopoint_text";
    }

    @Override
    public void refresh() {
        setArrayVertex(0, 0, getHeight(), getRotation(), getText());
    }

    @Override
    public void rotate(double theta) {
        double rot = Math.toRadians(theta);
        double x = geoPoint.getX() + offset.getX();
        double y = geoPoint.getY() + offset.getY();

        double nx = (x * Math.cos(rot) - y * Math.sin(rot));
        double ny = (x * Math.sin(rot) + y * Math.cos(rot));
        
        offset.setLocation(nx - geoPoint.getX(), ny - geoPoint.getY());
        super.rotate(theta);
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeodesicPointText other = (GeodesicPointText) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.oid;
        return hash;
    }
    
}
