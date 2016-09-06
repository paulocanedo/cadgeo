/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.GeodesicPointLabelCoord;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.main.Bus;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class LabelCoordCommand extends AbstractCadCommand {

    private GeodesicPointLabelCoord geoPointLabelCoord;
    private Line line = new Line(0, 0, 0, 0);
    private GeodesicPoint geoPoint;
    private static final String[] messages = new String[]{"Informe o ponto de captura das coordenadas"};
    private boolean noOffset = false;

    public LabelCoordCommand() {
        super(messages[0]);
    }

    public LabelCoordCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws PolygonalException, CoordinateException {
        if (geoPoint == null) {
            if (text.isEmpty()) {
                geoPoint = getPolygonal().referencePoint();
                setGeoPoint(geoPoint);
                return geoPoint != null;
            }

            GeodesicPoint findPoint = searchPoint(text);
            if (findPoint == null) {
                throw new PolygonalException(String.format("O ponto %s não foi encontrado.", text));
            }
            setGeoPoint(findPoint);
        } else {
            if (text.isEmpty()) {
                noOffset = true;
                execute();
                return true;
            }
            Point2D point = parsePoint(text);
            return nextState(point);
        }
        return true;
    }

    private void setGeoPoint(GeodesicPoint gpoint) {
        if (gpoint != null) {
            this.geoPoint = gpoint;
            this.line.setLocation(gpoint.getLocation());
            super.message = "Informe o ponto de inserção do texto";
            super.canDraw = true;
        }
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (geoPoint != null) {
            line.setEndLocation(point.getX(), point.getY());
            execute();
            return true;
        }

        for (GeodesicPoint gpoint : DataManagement.getAllPoints()) {
            Point2D location = gpoint.getLocation();

            if (PolygonalUtils.horizontalDistance(location.getX(), location.getY(), point.getX(), point.getY()) < 0.1) {
                setGeoPoint(gpoint);
                return true;
            }
        }
        return false;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        if (geoPoint != null) {
            line.setEndLocation(point);
        }
    }

    @Override
    public void execute() {
        geoPointLabelCoord = new GeodesicPointLabelCoord(geoPoint);

        if (!noOffset) {
            Point2D location = geoPoint.getLocation();
            Point2D endlocation = line.getEndLocation();
            geoPointLabelCoord.setOffset(endlocation.getX() - location.getX(), endlocation.getY() - location.getY());
        } else {
            float scale = Bus.getScale();
            geoPointLabelCoord.setOffset(10 * scale, 10 * scale);
        }
        getDisplayPanel().addToVisualObjects(geoPointLabelCoord);

        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(geoPointLabelCoord);
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Etiqueta de Coordenadas";
    }

    @Override
    public Line getVisualObject() {
        return line;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new LabelCoordCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "etiqueta_coordenada";
    }
}
