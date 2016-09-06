/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package br.com.geomapa.graphic.cad.spec;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.coordinate.Coordinate;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.cad.linetype.ContinuousLineType;
import br.com.geomapa.graphic.cad.linetype.DashedLineType;
import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.ui.panels.topographic.Projector;
import br.com.geomapa.util.PGLUtil;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Locale;
import javax.media.opengl.GL2;

/**
 *
 * @author paulocanedo
 */
public abstract class AbstractVisualObject implements VisualObject {

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    protected static int selectionThreshold = 10;
    protected int oid;
    private static int oidControl = 10;
    private Color color;
    private Layer layer;
    protected Point2D location = new Double();
    public static final String PROP_COLOR = "color";
    public static final String PROP_LAYER = "layer";
    public static final String PROP_LOCATION = "location";

    public AbstractVisualObject() {
        this(0, 0);
    }

    public AbstractVisualObject(Point2D point) {
        this(point.getX(), point.getY());
    }

    public AbstractVisualObject(Layer layer, Point2D point) {
        this(layer, point.getX(), point.getY());
    }

    public AbstractVisualObject(double x, double y) {
        this(LayerController.getCurrentLayer(), LayerController.getCurrentLineType(), x, y);
    }

    public AbstractVisualObject(Layer layer, double x, double y) {
        this.layer = layer;
        this.location.setLocation(x, y);
        this.oid = oidControl++;
    }
    
    public AbstractVisualObject(Layer layer, LineType lineType, double x, double y) {
        this.layer = layer;
        this.lineType = lineType;
        this.location.setLocation(x, y);
        this.oid = oidControl++;
    }

    @Override
    public void move(double offsetX, double offsetY) {
        if (canMove()) {
            setLocation(getX() + offsetX, getY() + offsetY);
        }
    }

    @Override
    public VisualObject copy() {
        return null;
    }

    @Override
    public void rotate(double theta) {
        if (!canRotate()) {
            return;
        }

        double rad = Math.toRadians(theta);

        double[] arrayVertex = getArrayVertex();
        for (int i = 0; i < arrayVertex.length; i += 2) {
            double x = arrayVertex[i + 0];
            double y = arrayVertex[i + 1];

            arrayVertex[i + 0] = (x * Math.cos(rad) - y * Math.sin(rad));
            arrayVertex[i + 1] = (x * Math.sin(rad) + y * Math.cos(rad));
        }
    }

    @Override
    public void draw(GL2 gl, Color color) {
        PGLUtil.setGlColor(gl, color);
        double[] arrayVertex = getArrayVertex();
        for (int i = 0; i < arrayVertex.length; i += 4) {
            gl.glVertex2d(arrayVertex[i + 0], arrayVertex[i + 1]);
            gl.glVertex2d(arrayVertex[i + 2], arrayVertex[i + 3]);
        }

    }

    @Override
    public void fill(GL2 gl, Color color) {
    }

    @Override
    public Color getFillColor() {
        return Color.WHITE;
    }

    @Override
    public void refresh() {
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public boolean canCopy() {
        return canMove();
    }

    @Override
    public boolean canRotate() {
        return true;
    }

    @Override
    public boolean isColorLayer() {
        return color == null;
    }

    @Override
    public boolean isLineTypeLayer() {
        return lineType == null;
    }
    
    /**
     * Get the value of location
     *
     * @return the value of location
     */
    @Override
    public Point2D getLocation() {
        location.setLocation(getX(), getY());
        return location;
    }

    @Override
    public void setLocation(Coordinate coord) {
        setLocation(coord.toUTM().toPoint2D());
    }

    @Override
    public void setLocation(Point2D location) {
        setLocation(location.getX(), location.getY());
    }

    @Override
    public boolean selection(int x, int y, Projector projector) {
        int[] project = projector.project(getX(), getY());
        int rx1 = project[0] - selectionThreshold / 2, ry1 = project[1] - selectionThreshold / 2,
                rx2 = project[0] + selectionThreshold / 2, ry2 = project[1] + selectionThreshold / 2;

        return coverPoint(x, y, rx1, ry1, rx2, ry2);
    }

    @Override
    public boolean selectionFull(int x1, int y1, int x2, int y2, Projector projector) {
        int[] project = projector.project(getX(), getY());

        return coverPoint(project[0], project[1], x1, y1, x2, y2);
    }

    @Override
    public boolean selectionPartial(int x1, int y1, int x2, int y2, Projector projector) {
        return selectionFull(x1, y1, x2, y2, projector);
    }

    public static boolean coverPoint(double x, double y, double x1, double y1, double x2, double y2) {
        double lx = Math.min(x1, x2);
        double ly = Math.min(y1, y2);

        double gx = Math.max(x1, x2);
        double gy = Math.max(y1, y2);

        return lx <= x && ly <= y && x <= gx && y <= gy;
    }

    /**
     * Get the value of layer
     *
     * @return the value of layer
     */
    @Override
    public Layer getLayer() {
        return layer;
    }

    /**
     * Set the value of layer
     *
     * @param layer new value of layer
     */
    @Override
    public void setLayer(Layer layer) {
        if (layer == null) {
            return;
        }
        Layer oldLayer = this.layer;
        this.layer = layer;
        propertyChangeSupport.firePropertyChange(PROP_LAYER, oldLayer, layer);
    }

    /**
     * Get the value of color
     *
     * @return the value of color
     */
    @Override
    public Color getColor() {
        if (isColorLayer()) {
            return layer == null ? LayerController.DEFAULT_LAYER.getColor() : layer.getColor();
        }
        return color;
    }

    @Override
    public String getColorAsString() {
        if (isColorLayer()) {
            return "BY_LAYER";
        }
        if (color.equals(Color.BLUE)) {
            return "BLUE";
        } else if (color.equals(Color.GREEN)) {
            return "GREEN";
        } else if (color.equals(Color.RED)) {
            return "RED";
        } else if (color.equals(Color.YELLOW)) {
            return "YELLOW";
        } else if (color.equals(Color.GRAY)) {
            return "GRAY";
        } else if (color.equals(Color.DARK_GRAY)) {
            return "DARK_GRAY";
        } else if (color.equals(Color.CYAN)) {
            return "CYAN";
        } else if (color.equals(Color.MAGENTA)) {
            return "MAGENTA";
        }

        return "BY_LAYER";
    }

    /**
     * Set the value of color
     *
     * @param color new value of color
     */
    @Override
    public void setColor(Color color) {
        Color oldColor = this.color;
        this.color = color;
        propertyChangeSupport.firePropertyChange(PROP_COLOR, oldColor, color);
    }
    public static final LineType CONTINUOUS_LINE_TYPE = new ContinuousLineType();
    public static final LineType DASHED_LINE_TYPE = new DashedLineType();
    private LineType lineType;
    private float lineWidth = 1f;

    @Override
    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    @Override
    public LineType getLineType() {
        return lineType == null ? getLayer().getLineType() : lineType;
    }

    public String getLineTypeAsString() {
        return getLineType().toString();
    }

    @Override
    public float getLineWidth() {
        return lineWidth;
    }

    @Override
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s(%.3f,%.3f)", getClass().getSimpleName(), getX(), getY());
    }

    @Override
    public Integer getOid() {
        return oid;
    }

    public static void matchProperties(VisualObject referencedObject, VisualObject vo) {
        VisualText rvt = (referencedObject instanceof VisualText) ? (VisualText) referencedObject : null;
        vo.setColor(referencedObject.isColorLayer() ? null : referencedObject.getColor());
        vo.setLayer(referencedObject.getLayer());
        if (vo instanceof VisualText && rvt != null) {
            VisualText vt = (VisualText) vo;
            vt.setHeight(rvt.getHeight());
            vt.setRotation(rvt.getRotation());
        }
    }
}
//    @Override
//    public void draw(GL2 gl, Color color, Point2D offset) {
//        if (!canMove()) {
//            draw(gl, color);
//            return;
//        }
//
//        PGLUtil.setGlColor(gl, color);
//        double[] arrayVertex = getArrayVertex();
//        for (int i = 0; i < arrayVertex.length; i += 4) {
//            gl.glVertex2d(arrayVertex[i + 0] + offset.getX(), arrayVertex[i + 1] + offset.getY());
//            gl.glVertex2d(arrayVertex[i + 2] + offset.getX(), arrayVertex[i + 3] + offset.getY());
//        }
//
//    }
//
//    @Override
//    public void draw(GL2 gl, Color color, Point2D base, double rotation) {
//        if (!canRotate()) {
//            draw(gl, color);
//            return;
//        }
//
//        PGLUtil.setGlColor(gl, color);
//        double[] arrayVertex = getArrayVertex();
//        double rad = Math.toRadians(rotation);
//        for (int i = 0; i < arrayVertex.length; i += 4) {
//            double x1 = arrayVertex[i + 0] - base.getX();
//            double y1 = arrayVertex[i + 1] - base.getY();
//            double x2 = arrayVertex[i + 2] - base.getX();
//            double y2 = arrayVertex[i + 3] - base.getY();
//            double nx1 = (x1 * Math.cos(rad) - y1 * Math.sin(rad));
//            double ny1 = (x1 * Math.sin(rad) + y1 * Math.cos(rad));
//            double nx2 = (x2 * Math.cos(rad) - y2 * Math.sin(rad));
//            double ny2 = (x2 * Math.sin(rad) + y2 * Math.cos(rad));
//
//            gl.glVertex2d(nx1 + base.getX(), ny1 + base.getY());
//            gl.glVertex2d(nx2 + base.getX(), ny2 + base.getY());
//        }
//
//    }