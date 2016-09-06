/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic;

import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.graphic.cad.spec.DoublePointVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Bus;
import br.com.geomapa.ui.panels.options.SchemeColors;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.util.PGLUtil;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 *
 * @author paulocanedo
 */
public class RenderContext {

    private Point2D basePointRotate = new Point2D.Double();
    private double angleRotation = 0;
    private Point2D offset = new Point2D.Double();
    //------------------
    private boolean requestToRedraw = true;
    private int displayList = -1;
    private boolean selectionRender = false;
    private static RenderContext selectedInstance;
    private static RenderContext instance;
    //------------------
    private static final Set<Point2D> vertexGrip = new HashSet<Point2D>();

    private RenderContext() {
    }

    public static RenderContext getInstance() {
        return getInstance(false);
    }

    public static RenderContext getInstance(boolean selected) {
        if (selectedInstance == null) {
            selectedInstance = new RenderContext();
            selectedInstance.selectionRender = true;
        }

        if (instance == null) {
            instance = new RenderContext();
        }
        return selected ? selectedInstance : instance;
    }

    public void setVisualObjects(GL2 gl, VisualObject... visualObjects) {
        if (requestToRedraw = false) {
            return;
        }

        if (displayList != -1) {
            gl.glDeleteLists(displayList, 1);
        }

        vertexGrip.clear();
        gl.glEnable(GL2.GL_LINE_STIPPLE);
        displayList = gl.glGenLists(1);
        gl.glNewList(displayList, GL2.GL_COMPILE_AND_EXECUTE);
        gl.glPushAttrib(GL2.GL_LINE_BIT);
        for (VisualObject vo : visualObjects) {
            if (!vo.getLayer().isVisible()) {
                continue;
            }

            LineType lineType = vo.getLineType();
            if (lineType.isContinuous() || vo instanceof VisualText) {
                gl.glDisable(GL2.GL_LINE_STIPPLE);
            } else {
                gl.glEnable(GL2.GL_LINE_STIPPLE);
                gl.glLineStipple(1, lineType.getStipple());
            }

            gl.glBegin(GL2.GL_LINES);
            vo.draw(gl, selectionRender ? SchemeColors.SELECTED : vo.getColor());
            gl.glEnd();

            if (selectionRender) {
                vertexGrip.add(vo.getLocation());
                if (vo instanceof DoublePointVisualObject) {
                    vertexGrip.add(((DoublePointVisualObject) vo).getEndLocation());
                }
            }
        }
        gl.glPopAttrib();
        gl.glEndList();

        requestToRedraw = false;
    }

    public static void render(GL2 gl) {
        if (instance == null || selectedInstance == null) {
            return;
        }
        gl.glEnable(GL.GL_BLEND);

        if (instance.displayList != -1) {
            gl.glCallList(instance.displayList);
        }

        if (selectedInstance.displayList != -1) {
            renderSelected(gl);
        }
    }

    private static void renderSelected(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslated(selectedInstance.offset.getX(), selectedInstance.offset.getY(), 0);
        gl.glTranslated(selectedInstance.basePointRotate.getX(), selectedInstance.basePointRotate.getY(), 0);
        gl.glRotated(selectedInstance.angleRotation, 0, 0, 1);
        gl.glTranslated(-selectedInstance.basePointRotate.getX(), -selectedInstance.basePointRotate.getY(), 0);

        gl.glCallList(selectedInstance.displayList);

        GLTopographicPanel displayPanel = Bus.getDisplayPanel();
        displayPanel.pushMatrixScreen(gl);

        PGLUtil.setGlColor(gl, Color.BLUE);
        for (Point2D p : vertexGrip) {
            int[] project = displayPanel.project(p.getX(), p.getY());
            PGLUtil.fillQuad(gl, project[0], project[1], 12);
        }

        gl.glPopMatrix();
    }

    public void offset(double x, double y) {
        this.offset.setLocation(x, y);
    }

    public void angleRotation(double value) {
        this.angleRotation = value;
    }

    public void basePointRotation(double x, double y) {
        this.basePointRotate.setLocation(x, y);
    }

    public void requestToRedraw() {
        requestToRedraw = true;
    }

    public boolean needToRedraw() {
        return requestToRedraw;
    }
}
