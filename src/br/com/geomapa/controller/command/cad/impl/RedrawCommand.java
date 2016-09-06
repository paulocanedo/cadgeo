/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class RedrawCommand extends AbstractCadCommand {

    public RedrawCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, "");
    }

    public RedrawCommand() {
        super("");
    }
    
    @Override
    public boolean transitToNextState(String text) throws Throwable {
        return true;
    }

    @Override
    public boolean transitToNextState(Point2D point) throws Throwable {
        return true;
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public void execute() {
        getDisplayPanel().refresh();
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
        return "Redesenhar";
    }

    @Override
    public boolean hasToBeExecuted() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "redesenhar";
    }
    
}
