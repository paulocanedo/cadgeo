/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.graphic.cad.geo.AreaAndPerimeterTableModel;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class TableAreaPerimeterCommand extends AbstractCadCommand {

    private AreaAndPerimeterTableModel tableap = new AreaAndPerimeterTableModel();
    private static final String[] messages = new String[]{"Informe o ponto de inserção do quadro"};

    public TableAreaPerimeterCommand() {
        super(messages[0]);
        super.canDraw = true;
    }

    public TableAreaPerimeterCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);

        super.canDraw = true;
    }

    @Override
    public boolean transitToNextState(String text) throws CoordinateException, CommandException {
        Point2D point = parsePoint(text);
        return nextState(point);
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        tableap.setLocation(point);

        execute();
        return true;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        tableap.setLocation(point);
    }

    @Override
    public void execute() {
        getDisplayPanel().addToVisualObjects(tableap);

        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(tableap);
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Quadro de Áreas e perímetros";
    }

    @Override
    public AreaAndPerimeterTableModel getVisualObject() {
        return tableap;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new TableAreaPerimeterCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "tabela_area_perimetro";
    }

}
