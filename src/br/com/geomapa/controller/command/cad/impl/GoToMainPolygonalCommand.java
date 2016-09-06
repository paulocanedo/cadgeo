/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class GoToMainPolygonalCommand extends AbstractCadCommand {

    public GoToMainPolygonalCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, "");
    }

    public GoToMainPolygonalCommand() {
        super("");
    }

    @Override
    public boolean transitToNextState(String text) throws Throwable {
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) throws Throwable {
        return false;
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public final void execute() {
        getDisplayPanel().setPolygonal(DataManagement.getMainPolygonal());
        redraw();

        finish();
    }

    @Override
    public void undo() {
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new GoToMainPolygonalCommand(displayPanel);
    }

    @Override
    public String toString() {
        return "Mapa da Planta Geral";
    }

    @Override
    public boolean hasToBeExecuted() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "planta_geral";
    }
}
