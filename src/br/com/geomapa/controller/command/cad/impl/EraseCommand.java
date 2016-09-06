/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class EraseCommand extends AbstractCadCommand {

    private List<VisualObject> list = new ArrayList<VisualObject>();
    private static final String[] messages = new String[]{"Selecione o(s) objeto(s) que deseja excluir"};

    public EraseCommand() {
        super(messages[0]);
    }

    public EraseCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CommandException {
        if (!(displayPanel.getSelectedObjects().length == 0)) {
            execute();
            return true;
        }
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        return false;
    }

    @Override
    public void execute() {
        for (VisualObject vo : displayPanel.getSelectedObjects()) {
            if (vo instanceof GeodesicPoint) {
                GeodesicPoint gp = (GeodesicPoint) vo;
                if (!LineDivision.anyOccurrence(gp)) {
                    list.add(vo);
                    DataManagement.getAllPoints().removeElement(gp);
                }
            } else if (vo instanceof LineDivision) {
                LineDivision ld = (LineDivision) vo;
                LineDivision ld2 = ld.reverseLineDivision();
                LineDivision.removeInstance(ld);
                LineDivision.removeInstance(ld2);
                
                list.add(ld);
                list.add(ld2);
            } else {
                list.add(vo);
            }
            getDisplayPanel().removeFromVisualObjects(vo);
        }

        displayPanel.clearSelection();
        redraw();
        finish();
    }

    @Override
    public void undo() {
        for (VisualObject vo : list) {
            getDisplayPanel().addToVisualObjects(vo);
            if (vo instanceof GeodesicPoint) {
                DataManagement.getAllPoints().addElement((GeodesicPoint) vo);
            }
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
        return "Excluir";
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new EraseCommand(displayPanel);
    }

    @Override
    public boolean canSelect() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "apagar";
    }
}
