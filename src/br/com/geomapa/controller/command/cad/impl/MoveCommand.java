/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.graphic.RenderContext;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class MoveCommand extends AbstractCadCommand {

    private List<VisualObject> list = new ArrayList<VisualObject>();
    private static final String[] messages = new String[]{"Selecione o(s) objeto(s) que deseja mover"};
    private int state = 0;
    private Line lineAux = Line.LINE_SAMPLE;

    public MoveCommand() {
        super(messages[0]);
    }

    public MoveCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
        
        if(!(displayPanel.getSelectedObjects().length == 0)) {
            state++;
            super.message = "Informe o ponto base para mover o(s) objeto(s)";
        }
    }

    @Override
    public boolean transitToNextState(String text) throws CommandException, CoordinateException {
        if (state == 0 && !(displayPanel.getSelectedObjects().length == 0)) {
            state++;
            super.message = "Informe o ponto base para mover o(s) objeto(s)";
            return true;
        } else if (state == 1) {
            lineAux.setLocation(parsePoint(text));
            state++;
            super.message = "Informe um segundo ponto de referência para mover o(s) objeto(s)";
            super.canDraw = true;
            return true;
        } else if (state == 2) {
            lineAux.setEndLocation(parsePoint(text));
            execute();
            return true;
        }
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (state == 1) {
            state++;
            lineAux.setLocation(point);
            super.message = "Informe um segundo ponto de referência para mover o(s) objeto(s)";
            super.canDraw = true;
            return true;
        } else if (state == 2) {
            lineAux.setEndLocation(point);
            execute();
            return true;
        }
        return false;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        lineAux.setEndLocation(point);
        
        Point2D basePoint = lineAux.getLocation();
        Point2D endPoint = lineAux.getEndLocation();
        RenderContext.getInstance(true).offset(endPoint.getX() - basePoint.getX(), endPoint.getY() - basePoint.getY());
    }

    @Override
    public void execute() {
        Point2D basePoint = lineAux.getLocation();
        Point2D endPoint = lineAux.getEndLocation();
        for (VisualObject vo : displayPanel.getSelectedObjects()) {
            list.add(vo);
            vo.move(endPoint.getX() - basePoint.getX(), endPoint.getY() - basePoint.getY());
        }

        getDisplayPanel().clearSelection();
        RenderContext.getInstance(true).offset(0, 0);
        redraw();
        finish();
    }

    @Override
    public void undo() {
        Point2D basePoint = lineAux.getLocation();
        Point2D endPoint = lineAux.getEndLocation();
        for (VisualObject vo : list) {
            vo.move(basePoint.getX() - endPoint.getX(), basePoint.getY() - endPoint.getY());
        }
        list.clear();
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Mover";
    }

    @Override
    public VisualObject getVisualObject() {
        return lineAux;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new MoveCommand(displayPanel);
    }

    @Override
    public boolean canSelect() {
        return state == 0;
    }

    @Override
    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException {
        super.setDisplayPanel(displayPanel);
        
        if(!(displayPanel.getSelectedObjects().length == 0)) {
            state++;
            super.message = "Informe o ponto base para mover o(s) objeto(s)";
        }
    }

    @Override
    public String getCommandName() {
        return "mover";
    }
    
}
