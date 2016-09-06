/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels.topographic;

import br.com.geomapa.controller.CadCommandController;
import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.CrossPointer;
import br.com.geomapa.graphic.RenderContext;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Bus;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.controller.ToolBarController;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.ui.panels.GeodesicPanel;
import br.com.geomapa.ui.panels.options.OptionsPanel;
import br.com.geomapa.util.ArraysUtil;
import br.com.geomapa.util.PGLUtil;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;

/**
 *
 * @author paulocanedo
 */
public class GLTopographicPanel implements GeodesicPanel, Projector {

    private final GLCanvas canvas;
    private final CrossPointer pointer;
    private Polygonal polygonal;
    private Cursor clearCursor;
    private double[] cornersView = {0, 0, 100, 100};
    /*------------------------------------------------------------------------*/
    private Collection<VisualObject> selectedObjects = new HashSet<VisualObject>();
    private Collection<VisualObject> noSelectedObjects = new HashSet<VisualObject>();
    private Point2D offsetSelectedObjects = new Point2D.Double(0, 0);
    private VisualObject tempVO;
    private VisualObject magneticVO;
    /*------------------------------------------------------------------------*/
    private double offsetX = 0.0;
    private double offsetY = 0.0;
    /*------------------------------------------------------------------------*/
    private GLTopographicMouseListener mouseListener;
    private String benchmarkText = "";

    public GLTopographicPanel(GLCapabilities caps) {
        canvas = new GLCanvas(caps);
        pointer = new CrossPointer(canvas);
        polygonal = DataManagement.getMainPolygonal();

        canvas.addGLEventListener(new GLTopographicListener(this));

        mouseListener = new GLTopographicMouseListener(this);
        canvas.addMouseMotionListener(mouseListener);
        canvas.addMouseWheelListener(mouseListener);
        canvas.addMouseListener(mouseListener);
        canvas.setCursor(createCursor());

        canvas.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                zoom(getCornerX1(), getCornerY1(), getCornerX2(), getCornerY2());
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="GeodesicPanel">
    @Override
    public void filter(String text) {
        if (text == null) {
            text = "";
        }
//        text = GeodesicPoint.getNameNoSeparators(text);
//        for (GeodesicPoint gp : DataManagement.getAllPoints()) {
//            boolean flag = gp.getNameNoSeparators().toLowerCase().contains(text);
//            gp.activeRealce(text.isEmpty() ? false : flag);
//        }
//        requestRepaint();
    }

    @Override
    public String action(String text) {
        GeodesicPoint gpoint = DataManagement.findPoint(text);
        if (gpoint != null) {
            zoomAt(gpoint.toPoint2D());
        }
        return null;
    }

    @Override
    public void refresh() {
        for (VisualObject vo : getVisualObjects()) {
            vo.refresh();
        }
        requestRepaint();
    }

    @Override
    public void setPolygonal(Polygonal polygonal) {
        this.polygonal = polygonal;

        selectedObjects.clear();
        noSelectedObjects.clear();
        noSelectedObjects.addAll(Arrays.asList(polygonal.getVisualObjects()));

        if (polygonal.isMain()) {
            noSelectedObjects.addAll(DataManagement.getAllPoints().toList());
        }

        RenderContext.getInstance().requestToRedraw();
    }
    // </editor-fold>   

    public void requestRepaint() {
        canvas.display();
    }

    public void resetView(GL2 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();
        activeMatrixUTM(gl);
    }

    public void activeMatrixUTM(GL2 gl) {
        gl.glOrtho(cornersView[0], cornersView[1], cornersView[2], cornersView[3], -1, 1);

        gl.glTranslated(offsetX, offsetY, 0);
    }

    public void pushMatrixScreen(GL2 gl) {
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, canvas.getWidth(), canvas.getHeight(), 0, -1, 1);
    }

    private void changeView(double left, double right, double bottom, double top) {
        double[] rectZoom = fixRectzoom(left, bottom, right, top);

        cornersView[0] = rectZoom[0];
        cornersView[1] = rectZoom[1];
        cornersView[2] = rectZoom[2];
        cornersView[3] = rectZoom[3];
    }

    public boolean isInsideView(Point2D point) {
        double x = point.getX();
        double y = point.getY();
        return AbstractVisualObject.coverPoint(x, y, getCornerX1(), getCornerY1(), getCornerX2(), getCornerY2());
    }

    public double getCornerX1() {
        return cornersView[0] - offsetX;
    }

    public double getCornerX2() {
        return cornersView[1] - offsetX;
    }

    public double getCornerY1() {
        return cornersView[2] - offsetY;
    }

    public double getCornerY2() {
        return cornersView[3] - offsetY;
    }

    public void benchmark(long startTime, GL2 gl) {
        double timeSpent = (System.nanoTime() - startTime) / (double) 1E9;
        if (timeSpent == 0) {
            timeSpent = 1;
        }
        benchmarkText = String.format("%.0f FPS", 1 / timeSpent);
    }

    private Cursor createCursor() {
        if (clearCursor == null) {
            int sizeCursor = 2;
            Image image = new BufferedImage(sizeCursor, sizeCursor, BufferedImage.TYPE_INT_ARGB);
            clearCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(sizeCursor / 2, sizeCursor / 2), "clearCursor");
        }

        return clearCursor;
    }

    private void drawPolygonalObjects(GL2 gl) {
        RenderContext render = RenderContext.getInstance();
        RenderContext renderSelected = RenderContext.getInstance(true);

        if (render.needToRedraw()) {
            render.setVisualObjects(gl, noSelectedObjects.toArray(new VisualObject[0]));
        }

        if (renderSelected.needToRedraw()) {
            renderSelected.setVisualObjects(gl, selectedObjects.toArray(new VisualObject[0]));
        }

        RenderContext.render(gl);

        if (tempVO != null) {
            gl.glPushAttrib(GL2.GL_LINE_BIT);

            gl.glLineWidth(tempVO.getLineWidth());
            gl.glBegin(GL2.GL_LINES);
            tempVO.draw(gl, tempVO.getColor());
            gl.glEnd();

            tempVO.fill(gl, tempVO.getFillColor());
            gl.glPopAttrib();
        }
    }

    public void drawAllVisualObjects(GL2 gl) {
        drawPolygonalObjects(gl);

        if (magneticVO != null) {
            pushMatrixScreen(gl);

            gl.glBegin(GL2.GL_LINES);
            magneticVO.draw(gl, magneticVO.getColor());
            gl.glEnd();
            gl.glPopMatrix();
        }

        GeodesicPoint currentGeoPoint = mouseListener.getCurrentGeoPoint();
        String pointNameText = null;
        String rightText = mouseListener.getCurrentCoord();
        String statusText = CadCommandController.getCurrentMessageStatus();
        String errorText = CadCommandController.ERROR_MESSAGE;

        Rectangle2D boundsRightText = benchmarkTextRenderer.getBounds(rightText);
        Rectangle2D boundsBenchmarkText = benchmarkTextRenderer.getBounds(benchmarkText);
        Rectangle2D boundsPointNameText = null;

        gl.glColor4f(0.2f, 0.3f, 0.8f, 0.94f);
        gl.glLoadIdentity();
        gl.glOrthof(0, canvas.getWidth(), 0, canvas.getHeight(), 0, 1);
        PGLUtil.fillRect(gl, 0, 0, canvas.getWidth(), 14);

        int gPointX = 0, gPointY = 0;
        if (currentGeoPoint != null) {
            pointNameText = currentGeoPoint.getNameNoSeparators();
            boundsPointNameText = benchmarkTextRenderer.getBounds(pointNameText);

            int[] project = project(currentGeoPoint.getX(), currentGeoPoint.getY());
            gPointX = project[0] + 10;
            gPointY = canvas.getHeight() - project[1] + 10;
        }

        if (boundsPointNameText != null) {
            PGLUtil.fillRect(gl, gPointX - 2, gPointY - 2, gPointX + boundsPointNameText.getWidth() + 2, gPointY + 12);
        }

        benchmarkTextRenderer.beginRendering(canvas.getWidth(), canvas.getHeight());
        benchmarkTextRenderer.setColor(1f, 1f, 1f, 1f);
        benchmarkTextRenderer.draw(statusText, 2, 2);
        if (pointNameText != null) {
            benchmarkTextRenderer.draw(pointNameText, gPointX, gPointY);
        }
        if (OptionsPanel.isBenchmarkGL()) {
            benchmarkTextRenderer.draw(benchmarkText, canvas.getWidth() - (int) boundsBenchmarkText.getWidth() - 2, 16);
        }

        benchmarkTextRenderer.draw(rightText, canvas.getWidth() - (int) boundsRightText.getWidth() - 2, 2);
        if (errorText != null && !errorText.isEmpty()) {
            benchmarkTextRenderer.setColor(1f, 0.75f, 0.8f, 1f);
            benchmarkTextRenderer.draw(errorText, 2, 2);
        }
        benchmarkTextRenderer.endRendering();
    }

    public void zoomAt(Point2D point) {
        double value = 2.0;
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        double x1 = point.getX() - w / value, y1 = point.getY() - h / value, x2 = point.getX() + w / value, y2 = point.getY() + h / value;
        zoom(x1, y1, x2, y2);

        requestRepaint();
    }

    public void zoomInOut(float factorZoom) {
        zoomInOut(new Point(canvas.getWidth() / 2, canvas.getHeight() / 2), factorZoom);
    }

    public void zoomInOut(Point point, float factorZoom) {
        double currentWidth = getCornerX2() - getCornerX1();
        double currentHeight = getCornerY2() - getCornerY1();
        double[] coords = unProject(point.x, point.y);

        double x1 = getCornerX1() + (currentWidth / factorZoom);
        double y1 = getCornerY1() + (currentHeight / factorZoom);
        double x2 = getCornerX2() - (currentWidth / factorZoom);
        double y2 = getCornerY2() - (currentHeight / factorZoom);
        zoom(x1, y1, x2, y2);

        double[] newcoords = unProject(point.x, point.y);
        offset(newcoords[0] - coords[0], newcoords[1] - coords[1]);
    }

    public void zoom(double x1_, double y1_, double x2_, double y2_) {
        changeView(x1_, x2_, y1_, y2_);

        clearOffset();
    }

    public final double[] fixRectzoom(double x1_, double y1_, double x2_, double y2_) {
        double x1 = Math.min(x1_, x2_);
        double x2 = Math.max(x1_, x2_);
        double y1 = Math.min(y1_, y2_);
        double y2 = Math.max(y1_, y2_);
        double c = x2 - x1;
        double d = y2 - y1;

        double a = canvas.getWidth();
        double b = canvas.getHeight();

        double newC = a * d / b;
        double newD = c * b / a;

        if (c < newC) {
            c = newC;
        } else {
            d = newD;
        }

        return new double[]{x1, x1 + c, y1, y1 + d};
    }

    public void offset(double dx, double dy) {
        this.offsetX += dx;
        this.offsetY += dy;
    }

    public void clearOffset() {
        this.offsetX = this.offsetY = 0;
    }

    @Override
    public int[] project(double x, double y) {
        double w1 = getCornerX2() - getCornerX1();
        double w2 = canvas.getWidth();
        double h1 = getCornerY2() - getCornerY1();
        double h2 = canvas.getHeight();

        int px = (int) (w2 / w1 * (x - getCornerX1()));
        int py = (int) (h2 / h1 * (y - getCornerY1()));

        return new int[]{px, canvas.getHeight() - py};
    }

    @Override
    public double[] unProject(int x, int y) {
        y = canvas.getHeight() - y;
        double w1 = getCornerX2() - getCornerX1();
        double w2 = canvas.getWidth();
        double h1 = getCornerY2() - getCornerY1();
        double h2 = canvas.getHeight();

        double px = getCornerX1() + (w1 / w2 * x);
        double py = getCornerY1() + (h1 / h2 * y);

        return new double[]{px, py};
    }

    public VisualObject singleSelection(Point2D point) {
        int[] project = project(point.getX(), point.getY());

        return singleSelection(project[0], project[1]);
    }

    public VisualObject singleSelection(int x, int y) {
        Collection<VisualObject> collection = new ArrayList<VisualObject>();
        collection.addAll(Arrays.asList(getVisualObjects()));
        if (getPolygonal().isMain()) {
            collection.addAll(DataManagement.getAllPoints().toList());
        }

        List<VisualObject> selection = new ArrayList<VisualObject>();
        for (VisualObject vo : collection) {
            if (vo.selection(x, y, this)) {
                selection.add(vo);
//                return vo;
            }
        }

        if (selection.isEmpty()) {
            return null;
        } else {
            double[] coords = unProject(x, y);
            VisualObject mostClose = selection.get(0);
            double minorDistance = Double.MAX_VALUE;
            for (VisualObject vo : selection) {
                double horizontalDistance;
                if (vo instanceof Line) {
                    Line l = (Line) vo;
                    horizontalDistance = Line2D.ptLineDist(l.getX(), l.getY(), l.getEndX(), l.getEndY(), coords[0], coords[1]);
                } else {
                    horizontalDistance = PolygonalUtils.horizontalDistance(coords[0], coords[1], vo.getX(), vo.getY());
                }

                if (horizontalDistance < minorDistance) {
                    minorDistance = horizontalDistance;
                    mostClose = vo;
                }
            }
            return mostClose;
        }
    }

    public VisualObject singleSelection(Point point) {
        return singleSelection(point.x, point.y);
    }

    public Collection<VisualObject> selection(Point point1, Point point2) {
        boolean positive = point2.x > point1.x;
        Collection<VisualObject> selection = new HashSet<VisualObject>();

        Collection<VisualObject> collection = new ArrayList<VisualObject>();
        collection.addAll(Arrays.asList(getVisualObjects()));
        if (getPolygonal().isMain()) {
            collection.addAll(DataManagement.getAllPoints().toList());
        }

        for (VisualObject vo : collection) {
            if (positive && vo.selectionFull(point1.x, point1.y, point2.x, point2.y, this)) {
                selection.add(vo);
            } else if (positive == false && vo.selectionPartial(point1.x, point1.y, point2.x, point2.y, this)) {
                selection.add(vo);
            }
        }

        return selection;
    }
    private TextRenderer benchmarkTextRenderer = new TextRenderer(Font.decode(Font.MONOSPACED).deriveFont(14f), true, true);

    @Override
    public Polygonal getPolygonal() {
        return polygonal;
    }

    public Component getComponent() {
        return canvas;
    }

    public CrossPointer getPointer() {
        return pointer;
    }

    public void addToVisualObjects(VisualObject... vos) {
        for (VisualObject vo : vos) {
            if (polygonal.addToVisualObjects(vo)) {
                noSelectedObjects.add(vo);
            }
        }
        RenderContext.getInstance().requestToRedraw();
    }

    public void removeFromVisualObjects(VisualObject... vos) {
        for (VisualObject vo : vos) {
            if (polygonal.removeFromVisualObjects(vo)) {
                selectedObjects.remove(vo);
                noSelectedObjects.remove(vo);
            }
        }
        RenderContext.getInstance().requestToRedraw();
    }

    public void addSelectedObject(VisualObject... selectedObjs) {
        for (VisualObject selected : selectedObjs) {
            noSelectedObjects.remove(selected);
            selectedObjects.add(selected);
        }
        RenderContext.getInstance().requestToRedraw();
        RenderContext.getInstance(true).requestToRedraw();
        selectionChanged();
    }

    public void removeSelectedObject(VisualObject... selectedObjs) {
        for (VisualObject selected : selectedObjs) {
            selectedObjects.remove(selected);
            noSelectedObjects.add(selected);
        }
        RenderContext.getInstance().requestToRedraw();
        RenderContext.getInstance(true).requestToRedraw();
        selectionChanged();
    }

    public VisualObject[] getVisualObjects() {
        return polygonal.getVisualObjects();
    }

    public VisualObject[] getSelectedObjects() {
        return selectedObjects.toArray(new VisualObject[0]);
    }

    public VisualObject[] getNoSelectedObjects() {
        return noSelectedObjects.toArray(new VisualObject[0]);
    }

    public void clearSelection() {
        noSelectedObjects.addAll(selectedObjects);
        selectedObjects.clear();
        mouseListener.setLastMousePressed(null);

        RenderContext.getInstance().requestToRedraw();
        RenderContext.getInstance(true).requestToRedraw();
        selectionChanged();
    }

    public void selectionChanged() {
        ToolBarController.selectionChanged(selectedObjects.toArray(new VisualObject[0]));
    }

    public void setTempVO(VisualObject tempVO) {
        this.tempVO = tempVO;
    }

    public void setMagneticVO(VisualObject magneticVO) {
        this.magneticVO = magneticVO;
    }

    public void setOffsetSelectedObjects(double ox, double oy) {
        this.offsetSelectedObjects.setLocation(ox, oy);
    }

    public Point2D getOffsetSelectedObjects() {
        return offsetSelectedObjects;
    }

    @Override
    public void export(Polygonal polygonal) throws IOException {
        Main main = Main.getInstance();
        ProjectMetadata projectInfo = main.getProjectInfo();
        File mapFolder = projectInfo.getMapFolder();

        mapFolder.mkdir();
        String name = polygonal.getName();

        File out = new File(mapFolder, name + ".dxf");
        export(polygonal, out);
    }

    public void export(Polygonal polygonal, File out) throws IOException {
        Bus.setCurrentPolygonal(polygonal);
        double[] corners = ArraysUtil.collectCorners(polygonal.getVisualObjects());
        PrintStream dxfFile = new PrintStream(out, "windows-1252");

        dxfFile.println("0");
        dxfFile.println("SECTION");
        dxfFile.println("2");
        dxfFile.println("HEADER");
        dxfFile.println("9");
        dxfFile.println("$ACADVER");
        dxfFile.println("1");
        dxfFile.println("AC1009");
        dxfFile.println("9");
        dxfFile.println("$EXTMIN");
        dxfFile.println("10");
        dxfFile.println(corners[0]);
        dxfFile.println("20");
        dxfFile.println(corners[1]);
        dxfFile.println("30");
        dxfFile.println("0.0");
        dxfFile.println("9");
        dxfFile.println("$EXTMAX");
        dxfFile.println("10");
        dxfFile.println(corners[2]);
        dxfFile.println("20");
        dxfFile.println(corners[3]);
        dxfFile.println("30");
        dxfFile.println("0.0");
        dxfFile.println("9");
        dxfFile.println("$PROJECTNAME");
        dxfFile.println("1");
        dxfFile.println(Main.getInstance().getProjectInfo() + " - " + polygonal.getName());
        dxfFile.println("9");
        dxfFile.println("$PDSIZE");
        dxfFile.println("40");
        dxfFile.println((double) Bus.getScale());
        dxfFile.println("9");
        dxfFile.println("$PDMODE");
        dxfFile.println("70");
        dxfFile.println("34");
        dxfFile.println("0");
        dxfFile.println("ENDSEC");

        dxfFile.println("0");
        dxfFile.println("SECTION");
        dxfFile.println("2");
        dxfFile.println("TABLES");
        dxfFile.println("0");
        dxfFile.println("TABLE");
        dxfFile.println("2");
        dxfFile.println("VPORT");
        dxfFile.println("70");
        dxfFile.println("1");
        dxfFile.println("0");
        dxfFile.println("VPORT");
        dxfFile.println("2");
        dxfFile.println("*ACTIVE");
        dxfFile.println("70");
        dxfFile.println("0");
        dxfFile.println("10");
        dxfFile.println("0.0");
        dxfFile.println("20");
        dxfFile.println("0.0");
        dxfFile.println("11");
        dxfFile.println("1.0");
        dxfFile.println("21");
        dxfFile.println("1.0");
        dxfFile.println("12");
        dxfFile.println((corners[0] + corners[2]) / 2);
        dxfFile.println("22");
        dxfFile.println((corners[1] + corners[3]) / 2);
        dxfFile.println("13");
        dxfFile.println("0.0");
        dxfFile.println("23");
        dxfFile.println("0.0");
        dxfFile.println("14");
        dxfFile.println("10.0");
        dxfFile.println("24");
        dxfFile.println("10.0");
        dxfFile.println("15");
        dxfFile.println("10.0");
        dxfFile.println("25");
        dxfFile.println("10.0");
        dxfFile.println("16");
        dxfFile.println("0.0");
        dxfFile.println("26");
        dxfFile.println("0.0");
        dxfFile.println("36");
        dxfFile.println("1.0");
        dxfFile.println("17");
        dxfFile.println("0.0");
        dxfFile.println("27");
        dxfFile.println("0.0");
        dxfFile.println("37");
        dxfFile.println("0.0");
        dxfFile.println("40");
        dxfFile.println(corners[3] - corners[1]);
        dxfFile.println("41");
        dxfFile.println(2.5);
        dxfFile.println("42");
        dxfFile.println("50.0");
        dxfFile.println("43");
        dxfFile.println("0.0");
        dxfFile.println("44");
        dxfFile.println("0.0");
        dxfFile.println("50");
        dxfFile.println("0.0");
        dxfFile.println("51");
        dxfFile.println("0.0");
        dxfFile.println("71");
        dxfFile.println("0");
        dxfFile.println("72");
        dxfFile.println("1000");
        dxfFile.println("73");
        dxfFile.println("1");
        dxfFile.println("74");
        dxfFile.println("3");
        dxfFile.println("75");
        dxfFile.println("0");
        dxfFile.println("76");
        dxfFile.println("0");
        dxfFile.println("77");
        dxfFile.println("0");
        dxfFile.println("78");
        dxfFile.println("0");
        dxfFile.println("0");
        dxfFile.println("ENDTAB");

        dxfFile.println("0");
        dxfFile.println("TABLE");
        dxfFile.println("2");
        dxfFile.println("LTYPE");
        dxfFile.println("70");
        dxfFile.println("3");
        dxfFile.println("0");
        dxfFile.println("LTYPE");
        dxfFile.println("2");
        dxfFile.println("CONTINUOUS");
        dxfFile.println("70");
        dxfFile.println("0");
        dxfFile.println("3");
        dxfFile.println("Linha Solida");
        dxfFile.println("72");
        dxfFile.println("65");
        dxfFile.println("73");
        dxfFile.println("0");
        dxfFile.println("40");
        dxfFile.println("0.0");

        dxfFile.println("0");
        dxfFile.println("LTYPE");
        dxfFile.println("2");
        dxfFile.println("DASHED");
        dxfFile.println("70");
        dxfFile.println("0");
        dxfFile.println("3");
        dxfFile.println("Tracejado __ __ __ __ __ __ __");
        dxfFile.println("72");
        dxfFile.println("65");
        dxfFile.println("73");
        dxfFile.println("2");
        dxfFile.println("40");
        dxfFile.println("19.0");
        dxfFile.println("49");
        dxfFile.println("13.0");
        dxfFile.println("49");
        dxfFile.println("-6.35");
        dxfFile.println("0");
        dxfFile.println("ENDTAB");

        LayerController.writeDxf(dxfFile);

        dxfFile.println("0");
        dxfFile.println("ENDSEC");

        dxfFile.println("0");
        dxfFile.println("SECTION");
        dxfFile.println("2");
        dxfFile.println("ENTITIES");

        for (VisualObject vo : polygonal.getVisualObjects()) {
            vo.writeToDxf(dxfFile);
        }

        if (polygonal.isMain()) {
            MainPolygonal mainPolygonal = (MainPolygonal) polygonal;
            for (Polygonal p : mainPolygonal.values()) {
                p.writeToDxf(dxfFile);
            }

            for (GeodesicPoint point : DataManagement.getAllPoints()) {
                point.writeToDxf(dxfFile);
            }
        }
        polygonal.writeToDxf(dxfFile);
        Bus.setCurrentPolygonal(null);

        dxfFile.println("0");
        dxfFile.println("ENDSEC");
        dxfFile.println("0");
        dxfFile.println("EOF");

        dxfFile.flush();
        dxfFile.close();
    }
}
