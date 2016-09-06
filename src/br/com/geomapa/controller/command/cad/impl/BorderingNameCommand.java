/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class BorderingNameCommand extends AbstractCadCommand {

    private VisualText vtext;
    private Line line = new Line(0, 0, 0, 0);
    private GeodesicPoint from;
    private GeodesicPoint to;
    private static final String[] messages = new String[]{"Informe o primeiro ponto da confrontação"};
    private static float BORDER_NAME_SIZE = 2.0f;

    public BorderingNameCommand() {
        super(messages[0]);
    }

    public BorderingNameCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws PolygonalException {
        GeodesicPoint findPoint = searchPoint(text);
        if (findPoint == null) {
            throw new PolygonalException(String.format("O ponto %s não foi encontrado.", text));
        }

        if (from == null) {
            setStartPoint(findPoint);
        } else {
            to = findPoint;
            execute();
        }
        return true;
    }

    private void setStartPoint(GeodesicPoint gpoint) {
        this.from = gpoint;
        this.line.setLocation(gpoint.getLocation());
        super.message = "Informe o ponto de confrontação adjacente";
        super.canDraw = true;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        for (GeodesicPoint gpoint : DataManagement.getAllPoints()) {
            Point2D location = gpoint.getLocation();

            if (PolygonalUtils.horizontalDistance(location.getX(), location.getY(), point.getX(), point.getY()) < 0.1) {
                if (from == null) {
                    setStartPoint(gpoint);
                } else {
                    to = gpoint;
                    execute();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        if (from != null) {
            line.setEndLocation(point);
        }
    }

    @Override
    public void execute() {
        String text = String.format("$CONFRONTANTE[%s,%s]", from.getNameNoSeparators(), to.getNameNoSeparators());
        vtext = new VisualText(from.getLocation(), text);
        vtext.setHeight(BORDER_NAME_SIZE);
        vtext.setLayer(LayerController.find("CONFRONTANTES"));
        vtext.applyRotationAndPosition(from.getLocation(), to.getLocation());
        getDisplayPanel().addToVisualObjects(vtext);

        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(vtext);
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Inserir Confrontante Texto";
    }

    @Override
    public Line getVisualObject() {
        return line;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new BorderingNameCommand(displayPanel);
    }

    @Override
    public boolean canUseMagnetic() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "texto_confrontante";
    }
}
