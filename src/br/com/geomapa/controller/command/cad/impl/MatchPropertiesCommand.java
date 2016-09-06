/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class MatchPropertiesCommand extends AbstractCadCommand {

    private int state = 0;
    private VisualObject referencedObject;
    private static final String[] messages = new String[]{"Selecione um objeto de referência para igualar as propriedades"};

    public MatchPropertiesCommand() {
        super(messages[0]);
    }

    public MatchPropertiesCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);

        displayPanel.clearSelection();
    }

    @Override
    public boolean transitToNextState(String text) throws CommandException {
        if (!(displayPanel.getSelectedObjects().length == 0) && text.isEmpty()) {
            execute();
            return true;
        }
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (state == 0 && !((getDisplayPanel().getSelectedObjects().length == 0))) {
            state++;
            referencedObject = getDisplayPanel().getSelectedObjects()[0];
            super.message = "Selecione todos os objetos que receberão a mesma propriedade do objeto de referência";
            return true;
        }
        return false;
    }

    @Override
    public void execute() {
        for (VisualObject vo : displayPanel.getSelectedObjects()) {
            AbstractVisualObject.matchProperties(referencedObject, vo);
        }

        displayPanel.clearSelection();
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
    public String toString() {
        return "Igualar Propriedades";
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new MatchPropertiesCommand(displayPanel);
    }

    @Override
    public boolean canSelect() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException {
        super.setDisplayPanel(displayPanel);

        displayPanel.clearSelection();
    }

    @Override
    public String getCommandName() {
        return "iguala_propriedade";
    }
}
