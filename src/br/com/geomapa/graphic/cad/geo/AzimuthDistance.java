/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.geo;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.graphic.cad.text.FontMetrics;
import br.com.geomapa.main.Bus;
import br.com.geomapa.util.AngleValue;
import br.com.geomapa.util.unit.impl.AzimuthUnit;
import br.com.geomapa.util.unit.impl.Meter;
import br.com.geomapa.util.unit.specs.DirectionUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public class AzimuthDistance extends VisualText {

    private double rotation = 0;
    private String text = "";
    private GeodesicPoint from;
    private GeodesicPoint to;
    private static DirectionUnit dUnit = new AzimuthUnit();
    private static DistanceUnit distUnit = new Meter();

    public AzimuthDistance(GeodesicPoint from, GeodesicPoint to) {
        this.from = from;
        this.to = to;

        setLayer(LayerController.find("INFORMACOES_CARTOGRAFICAS"));
        
        updateLabels();
    }

    private void updateLabels() {
        float heightText = 1.5f;
        AngleValue azimuth = from.azimuth(to);
        double dazimuth = azimuth.toDegreeDecimal();
        double distance = from.horizontalDistance(to);

        if (dazimuth > 180) {
            text = String.format("%s <- %s", dUnit.toString(azimuth, 0), distUnit.toString(distance, 2));
        } else {
            text = String.format("%s -> %s", dUnit.toString(azimuth, 0), distUnit.toString(distance, 2));
        }
        float scaledWidth = FontMetrics.getScaledStringWidth(text);
        double dist = (distance - scaledWidth) / 2;

        double[] projection;
        if (dazimuth > 180) {
            rotation = (270 - dazimuth);
            projection = PolygonalUtils.projection(from.getX(), from.getY(), dist + scaledWidth, dazimuth);
        } else {
            rotation = (90 - dazimuth);
            projection = PolygonalUtils.projection(from.getX(), from.getY(), dist, dazimuth);
        }

        if (scaledWidth * 1.2 > distance) {
            text = "";
        }

        projection = PolygonalUtils.projection(projection[0], projection[1], (dazimuth > 180 ? 2 : 1) * heightText * Bus.getScale(), dazimuth - 90);
        
        setArrayVertex(projection[0], projection[1], heightText, rotation, text);
    }
    
    @Override
    public String getText() {
        return text;
    }
    
    @Override
    public double getRotation() {
        return rotation;
    }

    @Override
    public String getVisualObjectName() {
        return "azimuth_distance";
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f %s %s", 
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(), getFrom(), getTo()));
    }

    public GeodesicPoint getFrom() {
        return from;
    }

    public final void setFrom(GeodesicPoint from) {
        this.from = from;
    }

    public GeodesicPoint getTo() {
        return to;
    }

    public final void setTo(GeodesicPoint to) {
        this.to = to;
    }
    
    @Override
    public void refresh() {
        updateLabels();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AzimuthDistance other = (AzimuthDistance) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
    
}
