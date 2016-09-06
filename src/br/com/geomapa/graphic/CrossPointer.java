/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic;

import br.com.geomapa.controller.CadCommandController;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.util.PGLUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 *
 * @author paulocanedo
 */
public class CrossPointer {

    private Component parent;
    private Color color = Color.WHITE;
    private Point centerLocation = new Point();
    private Point ph1 = new Point(), ph2 = new Point(), pv1 = new Point(), pv2 = new Point();
    private int crossSize = 4;
    public int width;
    public int height;

    public CrossPointer(Component parent) {
        this.parent = parent;
    }

    public void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, width, height, 0, -1, 1);

        ph1.setLocation(0, centerLocation.getY());
        ph2.setLocation(parent.getWidth(), centerLocation.getY());

        pv1.setLocation(centerLocation.getX(), 0);
        pv2.setLocation(centerLocation.getX(), parent.getHeight());

        gl.glDisable(GL.GL_BLEND);
        gl.glLineWidth(1f);

        CadCommand command = CadCommandController.getCommand();
        PGLUtil.setGlColor(gl, color);
        if (command != null && (command.canSelect() || command.singleSelect())) {
            PGLUtil.drawRect(gl, centerLocation.getX() - crossSize, centerLocation.getY() - crossSize, centerLocation.getX() + crossSize, centerLocation.getY() + crossSize);
        } else {
            PGLUtil.drawLine(gl, ph1, ph2);
            PGLUtil.drawLine(gl, pv1, pv2);
        }

        gl.glEnable(GL.GL_BLEND);

        gl.glPopMatrix();
    }

    public Point getCenterLocation() {
        return centerLocation;
    }

    public void setCenterLocation(Point centerLocation) {
        this.centerLocation = centerLocation;
    }

    public int getCrossSize() {
        return crossSize;
    }

    public void setCrossSize(int crossSize) {
        this.crossSize = crossSize;
    }
}
