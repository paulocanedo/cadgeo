/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.geo;

import br.com.geomapa.graphic.cad.compound.AbstractVisualObjectCompound;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.primitives.LineArrow;
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
public class GeodesicPointLabelCoord extends AbstractVisualObjectCompound implements VisualObjectReferenced<GeodesicPoint> {

    private GeodesicPoint gpoint;
    private VisualText nText;
    private VisualText eText;
    private Point2D offset = new Point2D.Double(50, 50);

    public GeodesicPointLabelCoord(GeodesicPoint point) {
        this.gpoint = point;

        nText = new VisualText(0, 0, "") {

            @Override
            protected void setArrayVertex(double x, double y, float height, double rotation, String text) {
                super.setArrayVertex(gpoint.getX() + offset.getX(), gpoint.getY() + offset.getY(), height, rotation, text);
            }

            @Override
            public String getText() {
                return String.format("N: %.3fm", gpoint.getCoordinate().toUTM().getNorth());
            }

            @Override
            public void refresh() {
                setArrayVertex(0, 0, getHeight(), getRotation(), getText());
            }
        };
        eText = new VisualText(0, 0, "") {

            @Override
            protected void setArrayVertex(double x, double y, float height, double rotation, String text) {
                super.setArrayVertex(gpoint.getX() + offset.getX(), (nText.getScaledHeight() * 1.5) + gpoint.getY() + offset.getY(), height, rotation, text);
            }

            @Override
            public String getText() {
                return String.format("E: %.3fm", gpoint.getCoordinate().toUTM().getEast());
            }

            @Override
            public void refresh() {
                setArrayVertex(0, 0, getHeight(), getRotation(), getText());
            }
        };

        delegate.add(eText);
        delegate.add(nText);
        final Line underlined = new Line() {

            @Override
            public double[] getArrayVertex() {
                return new double[]{gpoint.getX() + offset.getX(), gpoint.getY() + offset.getY() - (nText.getScaledHeight() * 1.15),
                            gpoint.getX() + offset.getX() + nText.getScaledWidth(), gpoint.getY() + offset.getY() - (nText.getScaledHeight() * 1.15),};
            }
        };

        final LineArrow arrowLine = new LineArrow() {

            private Point2D point = new Point2D.Double();

            @Override
            public double getX() {
                return getPoint().getX();
            }

            @Override
            public double getY() {
                return getPoint().getY();
            }

            @Override
            public double getEndX() {
                return gpoint.getX();
            }

            @Override
            public double getEndY() {
                return gpoint.getY();
            }

            private Point2D getPoint() {
                double x = underlined.getX(), y = underlined.getY();

                if (underlined.getX() < gpoint.getX()) {
                    x = underlined.getEndX();
                    y = underlined.getEndY();
                }
                point.setLocation(x, y);
                return point;
            }
        };
        delegate.add(underlined);
        delegate.add(arrowLine);
    }

    @Override
    public GeodesicPoint referencedObject() {
        return gpoint;
    }

    @Override
    public String getVisualObjectName() {
        return "geodesic_point_label_coord";
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f %.3f %.3f %s",
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(), offset.getX(), offset.getY(), gpoint.getNameNoSeparators()));
    }

    @Override
    public void writeToDxf(PrintStream stream) throws IOException {
        refresh();
        super.writeToDxf(stream);
    }

    @Override
    public void move(double offsetX, double offsetY) {
        offset.setLocation(offset.getX() + offsetX, offset.getY() + offsetY);
    }

    @Override
    public void setOffset(double x, double y) {
        this.offset.setLocation(x, y);

        refresh();
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
        final GeodesicPointLabelCoord other = (GeodesicPointLabelCoord) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.oid;
        return hash;
    }
}
