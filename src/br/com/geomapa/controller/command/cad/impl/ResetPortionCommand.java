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
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class ResetPortionCommand extends AbstractCadCommand {

    private boolean remove = false;
    private static final String[] messages = new String[]{"Tem certeza que deseja excluir as definições de perímetro desta parcela? (SIM) (N)Não"};

    public ResetPortionCommand() {
        super(messages[0]);
    }

    public ResetPortionCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) {
        remove = text.equalsIgnoreCase("SIM");
        execute();
        return true;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        return false;
    }

    @Override
    public void execute() {
        if(remove) {
            getPolygonal().resetPerimeter();
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
        return "Redefinir parcela";
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new ResetPortionCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "redefine_parcela";
    }
}
