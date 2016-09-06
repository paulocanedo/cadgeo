/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.primitives;

import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.geodesic.VariableControl;
import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.graphic.cad.text.FontLoader;
import br.com.geomapa.graphic.cad.text.FontMetrics;
import br.com.geomapa.main.Bus;
import br.com.geomapa.ui.panels.topographic.Projector;
import br.com.geomapa.util.AngleValue;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.FloatBuffer;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paulocanedo
 */
public class VisualText extends QuadrilateralVisualObject {

    private static Map<Character, FloatBuffer> charVertex;

    static {
        try {
            charVertex = FontLoader.reloadCharVertex();
        } catch (Throwable ex) {
            Logger.getLogger(VisualText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private float height = 1.5f;
    private double rotation = 0d;
    private String text;
    private double[] arrayVertex = new double[0];

    public VisualText() {
    }

    public VisualText(double x, double y, String text) {
        super(x, y);

        this.text = text;
        setVertex(x, y, 1.5f, 0, VariableControl.replaceText(text));
    }

    public VisualText(Point2D point, String text) {
        super(point.getX(), point.getY());

        this.text = text;
        setVertex(point.getX(), point.getY(), 1.5f, 0, VariableControl.replaceText(text));
    }

    public VisualText(double x, double y, float height, String text) {
        super(x, y);

        this.text = text;
        setVertex(x, y, height, 0, VariableControl.replaceText(text));
    }

    protected void setArrayVertex(double x, double y, float height, double rotation, String text) {
        setVertex(x, y, height, rotation, text);
    }

    private void setVertex(double x, double y, float height, double rotation, String text) {
        double radAng = Math.toRadians(getRotation());
        
        double dx = getScaledHeight() * Math.sin(radAng);
        double dy = getScaledHeight() * Math.cos(radAng);
        double endX = getScaledWidth() * Math.cos(Math.toRadians(getRotation()));
        double endY = getScaledWidth() * Math.sin(Math.toRadians(getRotation()));
        
        getPoint0().setLocation(x, y);
        getPoint1().setLocation(x - dx, y + dy);
        getPoint2().setLocation(x + endX - dx, y + endY + dy);
        getPoint3().setLocation(x + endX, y + endY);
        
        this.height = height;
        this.rotation = rotation;

        float scaledHeight = getScaledHeight();
        double rot = Math.toRadians(getRotation());

        int size = 0;
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                continue;
            }

            FloatBuffer get = charVertex.get(c);
            if (get == null) {
                size += 0;
            } else {
                size += charVertex.get(c).capacity();
            }
        }
        arrayVertex = new double[size];

        int idx = 0;
        float space = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                continue;
            }

            FloatBuffer buffer = charVertex.get(c);
            if (buffer == null) {
                space += 10;
                continue;
            }
            buffer.rewind();

            Float x1 = null, y1 = null, x2 = null, y2 = null;
            for (int j = 0; j < buffer.capacity(); j++) {
                boolean isX = j % 2 == 0;
                float value = buffer.get() + (isX ? (i * 10 + space) : 0);

                if (isX) {
                    if (x1 == null) {
                        x1 = value;
                    } else {
                        x2 = value;
                    }
                } else {
                    if (y1 == null) {
                        y1 = value;
                    } else {
                        y2 = value;
                    }
                }

                if (x1 != null && y1 != null && x2 != null && y2 != null) {
                    x1 = x1 * scaledHeight / 10;
                    y1 = y1 * scaledHeight / 10;
                    x2 = x2 * scaledHeight / 10;
                    y2 = y2 * scaledHeight / 10;

                    float rx1 = (float) (x1 * Math.cos(rot) - y1 * Math.sin(rot));
                    float ry1 = (float) (x1 * Math.sin(rot) + y1 * Math.cos(rot));
                    float rx2 = (float) (x2 * Math.cos(rot) - y2 * Math.sin(rot));
                    float ry2 = (float) (x2 * Math.sin(rot) + y2 * Math.cos(rot));

                    x1 = rx1 + (float) getX();
                    y1 = ry1 + (float) getY();
                    x2 = rx2 + (float) getX();
                    y2 = ry2 + (float) getY();

                    arrayVertex[idx++] = (x1);
                    arrayVertex[idx++] = (y1);
                    arrayVertex[idx++] = (x2);
                    arrayVertex[idx++] = (y2);

                    x1 = y1 = x2 = y2 = null;
                }
            }
        }
    }

    public String getText() {
        return VariableControl.replaceText(text);
    }
    
    public String getTextOriginal() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        setArrayVertex(getX(), getY(), getHeight(), getRotation(), getText());
    }

    public float getHeight() {
        return height;
    }

    public float getScaledHeight() {
        return Bus.getScale() * getHeight();
    }

    public void setHeight(float height) {
        setArrayVertex(getX(), getY(), height, getRotation(), getText());
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        setArrayVertex(getX(), getY(), getHeight(), rotation, getText());
    }

    public void applyRotationAndPosition(Point2D from, Point2D to) {
        applyRotationAndPosition(from, to, this);
    }

    public static void applyRotationAndPosition(Point2D from, Point2D to, VisualText vtext) {
        AngleValue azimuth = PolygonalUtils.azimuth(from.getX(), from.getY(), to.getX(), to.getY());
        double dazimuth = azimuth.toDegreeDecimal();
        double distance = PolygonalUtils.horizontalDistance(from.getX(), from.getY(), to.getX(), to.getY());
        String text = vtext.getText();

        float scaledWidth = FontMetrics.getScaledStringWidth(text);
        double dist = (distance - scaledWidth) / 2;

        double[] projection;
        if (dazimuth > 180) {
            vtext.setRotation(270 - dazimuth);
            projection = PolygonalUtils.projection(from.getX(), from.getY(), dist + scaledWidth, dazimuth);
        } else {
            vtext.setRotation(90 - dazimuth);
            projection = PolygonalUtils.projection(from.getX(), from.getY(), dist, dazimuth);
        }

        projection = PolygonalUtils.projection(projection[0], projection[1], (dazimuth > 180 ? 2 : 1) * vtext.getScaledHeight(), dazimuth - 90);
        vtext.setLocation(projection[0], projection[1]);
    }

    public float getWidth() {
        String resultText = getText();
        return (resultText == null) ? 0f : FontMetrics.getStringWidth(getText()) * getHeight();
    }

    public float getScaledWidth() {
        return Bus.getScale() * getWidth();
    }

    @Override
    public LineType getLineType() {
        return CONTINUOUS_LINE_TYPE;
    }
    
    @Override
    public void writeToDxf(PrintStream stream) {
        if (getText().isEmpty()) {
            return;
        }

        stream.println("0");
        stream.println("TEXT");
        stream.println("8");
        stream.println(getLayer().toString());
        stream.println("10");
        stream.println(getX());
        stream.println("20");
        stream.println(getY());
        stream.println("40");
        stream.println(getScaledHeight());
        stream.println("50");
        stream.println(getRotation());
        stream.println("1");
        stream.println(getText());
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f \"%s\" %.1f %.1f", 
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(), text, getHeight(), getRotation()));
    }

    @Override
    public String getVisualObjectName() {
        return "text";
    }

    @Override
    public double[] getArrayVertex() {
        return arrayVertex;
    }

    @Override
    public double getX() {
        return getPoint0().getX();
    }

    @Override
    public double getY() {
        return getPoint0().getY();
    }

    @Override
    public void setLocation(double x, double y) {
        setArrayVertex(x, y, getHeight(), getRotation(), getText());
    }

    @Override
    public void refresh() {
        setVertex(getX(), getY(), getHeight(), getRotation(), getText());
    }

    @Override
    public void rotate(double theta) {
        double currentRotation = getRotation();

        double rot = Math.toRadians(theta);
        double x = getX();
        double y = getY();

        double nx = (x * Math.cos(rot) - y * Math.sin(rot));
        double ny = (x * Math.sin(rot) + y * Math.cos(rot));

        setLocation(nx, ny);
        setRotation(currentRotation + theta);
    }

    @Override
    public VisualObject copy() {
        VisualText other = new VisualText(getX(), getY(), getHeight(), getText());
        matchProperties(this, other);

        return other;
    }

    @Override
    public boolean selection(int x, int y, Projector projector) {
        double[] xs = new double[] {getPoint0().getX(), getPoint1().getX(), getPoint2().getX(), getPoint3().getX()};
        double[] ys = new double[] {getPoint0().getY(), getPoint1().getY(), getPoint2().getY(), getPoint3().getY()};
        
        double[] coord = projector.unProject(x, y);
        
        return Polygonal.isInsidePolygonal(coord[0], coord[1], xs, ys);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VisualText other = (VisualText) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.oid;
        return hash;
    }
    
}
