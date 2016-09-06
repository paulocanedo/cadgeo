/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels.topographic;

import br.com.geomapa.controller.CadCommandController;
import br.com.geomapa.controller.MagneticController;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.graphic.CrossPointer;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.osnap.MagneticPoint;
import br.com.geomapa.graphic.cad.osnap.MagneticFinder;
import br.com.geomapa.graphic.cad.primitives.FilledRectangle;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.util.PGLUtil;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Locale;
import javax.swing.SwingUtilities;

/**
 *
 * @author paulocanedo
 */
public class GLTopographicMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {

    private GLTopographicPanel parent;
    private Point2D realCoordAux = new Point2D.Double();

    public GLTopographicMouseListener(GLTopographicPanel parent) {
        this.parent = parent;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        CadCommand command = CadCommandController.getCommand();
        parent.setTempVO(null);
        boolean canSelect = command == null || command.canSelect();

        if (canSelect) {
            if (lastMousePressed == null) {
                VisualObject singleSelection = parent.singleSelection(e.getPoint());
                if (singleSelection != null) {
                    if (e.isShiftDown()) {
                        parent.removeSelectedObject(singleSelection);
                    } else {
                        parent.addSelectedObject(singleSelection);
                    }
                } else {
                    lastMousePressed = e.getPoint();
                }
            } else {
                Collection<VisualObject> selection = parent.selection(lastMousePressed, e.getPoint());
                if (e.isShiftDown()) {
                    parent.removeSelectedObject(selection.toArray(new VisualObject[0]));
                } else {
                    parent.addSelectedObject(selection.toArray(new VisualObject[0]));
                }
                lastMousePressed = null;
            }
        }

        if (command != null) {
            MagneticPoint find = MagneticController.find(parent, e.getPoint());

            Point2D realCoord = find == null || canSelect ? convertToRealCoord(e.getPoint()) : find.getPoint();
            CadCommandController.nextState(realCoord);
        } else {
            canSelect = true;
        }

        parent.requestRepaint();
    }

    private Point2D convertToRealCoord(Point p) {
        double[] currentMouseWorld = parent.unProject(p.x, p.y);

        realCoordAux.setLocation(currentMouseWorld[0], currentMouseWorld[1]);
        return realCoordAux;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lastMouseDragged = null;
    }
    private FilledRectangle selectionRectangle = new FilledRectangle(0, 0, 0, 0, Color.WHITE, Color.WHITE);

    @Override
    public void mouseMoved(MouseEvent e) {
        parent.setTempVO(null);
        parent.setMagneticVO(null);

        Point2D realCoord = convertToRealCoord(e.getPoint());
        CadCommand command = CadCommandController.getCommand();
        boolean canSelect = false;

        if (command != null) {
            canSelect = command.canSelect();

            MagneticPoint find = MagneticController.find(parent, e.getPoint());
            parent.setMagneticVO(find == null || canSelect ? null : find.getVisualObject(parent));
            VisualObject visualObject = command.getVisualObject();
            if (command.canDraw()) {
                command.setCurrentPosition(realCoord);
                parent.setTempVO(visualObject);
            }
        } else {
            canSelect = true;
        }

        if (canSelect) {
            if (lastMousePressed != null) {
                double[] coords = parent.unProject(lastMousePressed.x, lastMousePressed.y);
                selectionRectangle.setLocation(coords[0], coords[1]);
                selectionRectangle.setEndLocation(realCoord);
                selectionRectangle.setFillColor(PGLUtil.getSelectionColor(lastMousePressed, e.getPoint()));
                selectionRectangle.setLineType(PGLUtil.isLeftToRight(lastMousePressed, e.getPoint()) ? AbstractVisualObject.CONTINUOUS_LINE_TYPE : AbstractVisualObject.DASHED_LINE_TYPE);
                parent.setTempVO(selectionRectangle);
            }
        }

        CrossPointer pointer = parent.getPointer();
        pointer.setCenterLocation(e.getPoint());
        setGeodesicPointName(e.getPoint());

        this.currentCoord = String.format(Locale.ENGLISH, "E: %.3f, N: %.3f", realCoord.getX(), realCoord.getY());
        parent.requestRepaint();
    }

    private void setGeodesicPointName(Point point) {
        MagneticPoint endPoint = MagneticFinder.getInstance(parent).getGeoPoint(point);
        this.currentGeoPoint = endPoint == null ? null : endPoint.getGeodesicPoint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float factor = 1.1f;
        parent.zoomInOut(e.getPoint(), e.getWheelRotation() < 0 ? factor : 1 / factor);

        parent.setMagneticVO(null);
        parent.requestRepaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        CrossPointer pointer = parent.getPointer();
        parent.setMagneticVO(null);

        pointer.setCenterLocation(e.getPoint());
        if ((SwingUtilities.isRightMouseButton(e) || (SwingUtilities.isMiddleMouseButton(e)))) {
            if (lastMouseDragged != null) {
                Point point = e.getPoint();

                double[] realPoint1 = parent.unProject(lastMouseDragged.x, lastMouseDragged.y);
                double[] realPoint2 = parent.unProject(point.x, point.y);

                parent.offset(realPoint2[0] - realPoint1[0], realPoint2[1] - realPoint1[1]);
            }
        }

        lastMouseDragged = e.getPoint();
        parent.requestRepaint();
    }
    
    public void setLastMousePressed(Point lastMousePressed) {
        this.lastMousePressed = lastMousePressed;
    }
    private Point lastMouseDragged;
    private Point lastMousePressed;
    private String currentCoord = "0,0";
    private GeodesicPoint currentGeoPoint;

    public String getCurrentCoord() {
        return currentCoord;
    }

    public GeodesicPoint getCurrentGeoPoint() {
        return currentGeoPoint;
    }
}
