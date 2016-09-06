/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.util.UserInterfaceUtil;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class ExportCommand extends AbstractCadCommand {

    public ExportCommand(GLTopographicPanel displayPanel, String message) {
        super(displayPanel, "");
    }

    public ExportCommand() {
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
    public void execute() {
        UserInterfaceUtil.showDialog(UserInterfaceUtil.EXPORT, getDisplayPanel().getPolygonal());

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
    public boolean acceptAnyChar() {
        return false;
    }

    @Override
    public boolean acceptSpaceBar() {
        return false;
    }

    @Override
    public boolean canSelect() {
        return false;
    }

    @Override
    public boolean canUseMagnetic() {
        return false;
    }

    @Override
    public boolean hasToBeExecuted() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new ExportCommand(displayPanel, "");
    }

    @Override
    public String toString() {
        return "Exportar";
    }

    @Override
    public String getCommandName() {
        return "exportar";
    }
}
