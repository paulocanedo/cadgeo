/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class UndoCommand extends AbstractCadCommand {

    public UndoCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, "");
    }

    public UndoCommand() {
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
        if (!stackUndo.isEmpty()) {
            CadCommand undoCadCommand = stackUndo.pop();
            this.message = undoCadCommand.toString();
            undoCadCommand.undo();

            redraw();

        }
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
    public String toString() {
        return "Desfazer";
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new UndoCommand(displayPanel);
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public boolean hasToBeExecuted() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "desfazer";
    }
}
