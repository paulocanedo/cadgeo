/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.PolygonalMetadata;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Bus;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class PortionLabelCommand extends AbstractCadCommand {

    protected static String[] messages = new String[]{
        "O que deseja etiquetar: (N)nome da parcela, (P)nome do proprietário, (A)ambos?",
        "Clique dentro da parcela que deseja etiquetar"
    };
    private int state = 0;
    private int items = 0;
    private char choice = '0';
    private List<VisualObject> list = new ArrayList<VisualObject>();
    private Polygonal polygonal;

    public PortionLabelCommand() {
        super(messages[0]);
    }

    public PortionLabelCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CommandException, CoordinateException {
        if (state == 0) {
            choice = getCharacter(text, "Informe uma das opções: (N)nome da parcela, (P)nome do proprietário, (A)ambos", 'N', 'P', 'A');
            state++;
            super.message = messages[1];
            return true;
        }
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (state == 1) {
            polygonal = MainPolygonal.findPolygonal(point.getX(), point.getY());
            if (polygonal != null && isInCurrentPolygonal(point)) {
                items = 0;
                PolygonalMetadata pmetadata = polygonal.getMetadata();

                if (choice == 'N' || choice == 'A') {
                    addVisualText(polygonal, polygonal.getName());
                }
                if (choice == 'P' || choice == 'A') {
                    addVisualText(polygonal, pmetadata.getNomeProprietario());
                }

                return true;
            } else {
                super.message = "Parcela não encontrada, " + messages[1];
            }
        }
        return false;
    }

    private boolean isInCurrentPolygonal(Point2D point) {
        Polygonal currentPolygonal = getDisplayPanel().getPolygonal();
        if (currentPolygonal.isMain()) {
            return true;
        }
        return currentPolygonal.isInside(point.getX(), point.getY());
    }

    private void addVisualText(Polygonal polygonal, String text) {
        Point2D centroidPoint = polygonal.getCentroidPoint();

        VisualText visualText = new VisualText(0, 0, 2, text);
        visualText.setLocation(centroidPoint.getX() - visualText.getScaledWidth() / 2, centroidPoint.getY() - (items * (2 + 1) * Bus.getScale()));
        list.add(visualText);
        getDisplayPanel().addToVisualObjects(visualText);
        items++;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
    }

    @Override
    public void execute() {
        redraw();

        finish();
    }

    @Override
    public void undo() {
        for (VisualObject vo : list) {
            getDisplayPanel().removeFromVisualObjects(vo);
        }
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Etiquetar Parcela";
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new PortionLabelCommand(displayPanel);
    }

    @Override
    public boolean canSelect() {
        return false;
    }

    @Override
    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException {
        super.setDisplayPanel(displayPanel);
    }

    @Override
    public boolean acceptSpaceBar() {
        return false;
    }

    @Override
    public boolean acceptAnyChar() {
        return state == 0;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public void canceled() {
        if (list.size() > 0) {
            execute();
        }
    }

    @Override
    public String getCommandName() {
        return "etiqueta_parcela";
    }
}
