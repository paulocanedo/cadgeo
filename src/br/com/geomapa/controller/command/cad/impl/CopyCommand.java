/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
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
public class CopyCommand extends AbstractCadCommand {

    private List<VisualObject> list = new ArrayList<VisualObject>();
    protected static String[] messages = new String[]{
        "Selecione o(s) objeto(s) que deseja copiar",
        "Informe o ponto base para inserir o(s) objeto(s)",
        "Informe um segundo ponto de referência para inserir o(s) objeto(s)"};
    private int state = 0;
    private Line lineAux = Line.LINE_SAMPLE;

    public CopyCommand() {
        super(messages[0]);
    }

    public CopyCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);

        if (!(displayPanel.getSelectedObjects().length == 0)) {
            state++;
            super.message = messages[1];
            
            VisualObject[] selectedObjects = getDisplayPanel().getSelectedObjects();
            for (VisualObject vo : selectedObjects) {
                getDisplayPanel().removeSelectedObject(vo);
                VisualObject copy = vo.copy();
                list.add(copy);
                getDisplayPanel().addSelectedObject(copy);
            }
        }
    }

    @Override
    public boolean transitToNextState(String text) throws CommandException, CoordinateException {
        if (state == 0 && !(displayPanel.getSelectedObjects().length == 0)) {
            state++;
            super.message = messages[1];

            VisualObject[] selectedObjects = getDisplayPanel().getSelectedObjects();
            for (VisualObject vo : selectedObjects) {
                getDisplayPanel().removeSelectedObject(vo);
                VisualObject copy = vo.copy();
                list.add(copy);
                getDisplayPanel().addSelectedObject(copy);
            }

            return true;
        } else if (state == 1) {
            lineAux.setLocation(parsePoint(text));
            state++;
            super.message = messages[2];
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
            super.message = messages[2];
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
        for (VisualObject vo : list) {
            getDisplayPanel().addToVisualObjects(vo);
            vo.move(endPoint.getX() - basePoint.getX(), endPoint.getY() - basePoint.getY());
        }

        RenderContext.getInstance(true).offset(0, 0);
        redraw();
        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(list.toArray(new VisualObject[0]));
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
        return "Copiar";
    }

    @Override
    public VisualObject getVisualObject() {
        return lineAux;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new CopyCommand(displayPanel);
    }

    @Override
    public boolean canSelect() {
        return state == 0;
    }

    @Override
    public boolean canUseMagnetic() {
        return !canSelect();
    }
    
    @Override
    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException {
        super.setDisplayPanel(displayPanel);

        if (!(displayPanel.getSelectedObjects().length == 0)) {
            state++;
            super.message = messages[1];
        }
    }

    @Override
    public String getCommandName() {
        return "copiar";
    }
}
