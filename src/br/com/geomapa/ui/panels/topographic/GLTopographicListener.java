/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels.topographic;

import br.com.geomapa.controller.command.cad.impl.ZoomCommand;
import br.com.geomapa.graphic.CrossPointer;
import br.com.geomapa.ui.panels.options.OptionsPanel;
import br.com.geomapa.ui.panels.options.SchemeColors;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 *
 * @author paulocanedo
 */
public class GLTopographicListener implements GLEventListener {

    private GLTopographicPanel parent;
    private boolean firstInit = true;

    public GLTopographicListener(GLTopographicPanel parent) {
        this.parent = parent;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = (GL2) drawable.getGL();

        gl.setSwapInterval(0);

        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        gl.glShadeModel(GL2.GL_FLAT);				// Enable Smooth Shading
        float[] rgb = SchemeColors.BACKGROUND.getRGBComponents(null);
        gl.glClearColor(rgb[0], rgb[1], rgb[2], 1f);			// Black Background
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glEnable(GL.GL_DEPTH_TEST);				// Enables Depth Testing

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnable(GL2.GL_LINE_STIPPLE);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        CrossPointer pointer = parent.getPointer();

        pointer.width = drawable.getWidth();
        pointer.height = drawable.getHeight();

        parent.resetView(gl);
        long startTime = System.nanoTime();

        parent.drawAllVisualObjects(gl);

        if (pointer != null) {
            pointer.draw(gl);
        }

        if (!parent.getPolygonal().isInitializedZoom()) {
            new ZoomCommand(parent).nextState("E");
            parent.getPolygonal().setInitializedZoom(true);
        }

        gl.glFlush();

        if (OptionsPanel.isBenchmarkGL()) {
            parent.benchmark(startTime, gl);
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }
}
//case SELECT:
//    if (mousePressed != null) {
//        parent.activeMatrixScreen(gl);
//        gl.glPushAttrib(GL2.GL_LINE_STIPPLE);
//        if (!PGUtil.isLeftToRight(mousePressed, centerLocation)) {
//            gl.glLineStipple(1, (short) 0x00FF); /* dashed */
//        }
//
//        PGLUtil.setGlColor(gl, parent.getSelectionColor(mousePressed, centerLocation));
//        PGLUtil.fillRect(gl, mousePressed, centerLocation);
//
//        gl.glDisable(GL.GL_BLEND);
//        PGLUtil.setGlColor(gl, Color.WHITE);
//        PGLUtil.drawRect(gl, mousePressed, centerLocation);
//        gl.glEnable(GL.GL_BLEND);
//
//        gl.glPopAttrib();
//        gl.glPopMatrix();
//    }
//    break;