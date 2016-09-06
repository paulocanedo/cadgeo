/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class GeodesicPointEditCommand extends AbstractCadCommand {

    private GeodesicPoint gpoint;
    private int state = 0;
    private static final String prompt = "Informe o nome do ponto ou clique no ponto que deseja editar";

    public GeodesicPointEditCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, prompt);
    }

    public GeodesicPointEditCommand() {
        super(prompt);
    }

    @Override
    public boolean transitToNextState(String text) throws Throwable {
        if(text.isEmpty()) {
            execute();
            return true;
        }
        
        this.gpoint = searchPoint(text);
        if (gpoint == null) {
            super.message = String.format("O ponto %s não foi localizado, informe corretamente o ponto", text);
            return false;
        }
        state++;

        pointFormPanel.setCurrentGeoPoint(gpoint);
        pointFormPanel.setFields(gpoint);
        getPointDialog().setVisible(true);
        redraw();
        return true;
    }

    @Override
    public boolean transitToNextState(Point2D point) throws Throwable {
        this.gpoint = findGeoPoint(point);
        if (gpoint == null) {
            return false;
        }
        state++;

        pointFormPanel.setCurrentGeoPoint(gpoint);
        pointFormPanel.setFields(gpoint);
        getPointDialog().setVisible(true);
        redraw();
        return true;
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public void execute() {
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
    public void canceled() {
        undo();
    }

    @Override
    public boolean canSelect() {
        return false;
    }

    @Override
    public String toString() {
        return "Edição de Ponto";
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new GeodesicPointEditCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "edita_ponto";
    }

}
