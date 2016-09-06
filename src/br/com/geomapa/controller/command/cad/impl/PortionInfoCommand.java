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
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Main;
import br.com.geomapa.ui.panels.PortionDataPanel;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author paulocanedo
 */
public class PortionInfoCommand extends AbstractCadCommand {

    private static final PortionDataPanel portionDataPanel = new PortionDataPanel();
    protected static String[] messages = new String[]{
        "Clique dentro da parcela que deseja editar as informações",
        "Parcela não encontrada",
        "Mais de uma parcela foi encontrada: "
    };
    private Polygonal polygonal;
    private List<Polygonal> possiblePolygonals;

    public PortionInfoCommand() {
        super(messages[0]);
    }

    public PortionInfoCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CommandException, CoordinateException {
        if (possiblePolygonals != null && possiblePolygonals.size() > 1) {
            Integer idx = getPositiveInteger(text, "Indice não encontrado: " + text);
            try {
                getDisplayPanel().setTempVO(this.polygonal = possiblePolygonals.get(idx));
                super.canDraw = true;
                redraw();
                execute();

                return true;
            } catch (ArrayIndexOutOfBoundsException ex) {
                super.message = "Indice não encontrado: " + text;
            }
        }
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        this.possiblePolygonals = MainPolygonal.findPolygonals(point.getX(), point.getY());

        if (possiblePolygonals.isEmpty()) {
            super.message = messages[1] + ", " + messages[0];
        } else {
            if (possiblePolygonals.size() == 1) {
                super.canDraw = true;
                getDisplayPanel().setTempVO(this.polygonal = possiblePolygonals.get(0));
                redraw();

                execute();
            } else {
                StringBuilder msg = new StringBuilder(messages[2]);
                for (int i = 0; i < possiblePolygonals.size(); i++) {
                    msg.append(String.format("(%d)", i)).append(possiblePolygonals.get(i).toString());
                }
                super.message = msg.toString();
            }
            return true;
        }
        return false;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
    }

    @Override
    public void execute() {
        Polygonal p = getDisplayPanel().getPolygonal().isMain() ? polygonal : getDisplayPanel().getPolygonal();
        portionDataPanel.setPolygonal(p);
        portionDataPanel.refresh();
        JOptionPane.showMessageDialog(Main.getInstance(), portionDataPanel, "", JOptionPane.PLAIN_MESSAGE);

        getDisplayPanel().setTempVO(null);
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
        return "Editar Informações da Parcela";
    }

    @Override
    public VisualObject getVisualObject() {
        return polygonal;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new PortionInfoCommand(displayPanel);
    }

    @Override
    public boolean canSelect() {
        return false;
    }

    @Override
    public boolean acceptSpaceBar() {
        return false;
    }

    @Override
    public boolean acceptAnyChar() {
        return possiblePolygonals != null && possiblePolygonals.size() > 1;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public boolean hasToBeExecuted() {
        return !getDisplayPanel().getPolygonal().isMain();
    }

    @Override
    public boolean canUseMagnetic() {
        return false;
    }

    @Override
    public String getCommandName() {
        return "info_parcela";
    }
}
