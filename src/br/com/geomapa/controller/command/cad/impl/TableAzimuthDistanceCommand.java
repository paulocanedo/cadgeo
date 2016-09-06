/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.graphic.cad.geo.AzimuthAndDistanceTableModel;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class TableAzimuthDistanceCommand extends AbstractCadCommand {

    private AzimuthAndDistanceTableModel tableAzdt;
    private static final String[] messages = new String[]{"Informe o ponto de inserção do quadro"};

    public TableAzimuthDistanceCommand() {
        super(messages[0]);
        super.canDraw = true;
    }

    public TableAzimuthDistanceCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);

        this.tableAzdt = new AzimuthAndDistanceTableModel(getPolygonal());
        super.canDraw = true;
    }

    @Override
    public boolean transitToNextState(String text) throws CoordinateException, CommandException {
        Point2D point = parsePoint(text);
        return nextState(point);
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        tableAzdt.setLocation(point);

        execute();
        return true;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        tableAzdt.setLocation(point);
    }

    @Override
    public void execute() {
        getDisplayPanel().addToVisualObjects(tableAzdt);

        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(tableAzdt);
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Quadro de Azimute e distância";
    }

    @Override
    public AzimuthAndDistanceTableModel getVisualObject() {
        return tableAzdt;
    }

    @Override
    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException {
        super.setDisplayPanel(displayPanel);
        
        this.tableAzdt = new AzimuthAndDistanceTableModel(getPolygonal());
        super.canDraw = true;
    }
    
    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new TableAzimuthDistanceCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "tabela_azimute_distancia";
    }
}
