/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.LayerController;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.cad.geo.GeodesicPointText;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.util.ArraysUtil;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class LabelGeoPointCommand extends AbstractCadCommand {

    private List<GeodesicPointText> list = new ArrayList<GeodesicPointText>();
    private static final String[] messages = new String[]{"Você deseja etiquetar por (S)seleção ou (T)todos os pontos?"};
    private char aChar = ' ';

    public LabelGeoPointCommand() {
        super(messages[0]);
    }

    public LabelGeoPointCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CommandException {
        if (text.isEmpty() && !(getDisplayPanel().getSelectedObjects().length == 0)) {
            execute();
            return true;
        }

        aChar = getCharacter(text, "Você deve informar uma da opções: (S)seleção, (T)todos os pontos", 'S', 'T');
        if (aChar == 'S') {
            super.message = "Selecione os pontos que deseja etiquetar";
        }

        if (!(getDisplayPanel().getSelectedObjects().length == 0) || aChar == 'T') {
            execute();
        }
        return true;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        return false;
    }

    @Override
    public void execute() {
        Collection<GeodesicPoint> collection;
        if (aChar == 'S') {
            collection = ArraysUtil.collect(displayPanel.getSelectedObjects(), GeodesicPoint.class);
        } else {
            if (getPolygonal().isMain()) {
                collection = DataManagement.getAllPoints().toList();
            } else {
                collection = ArraysUtil.collect(getPolygonal().getVisualObjects(), GeodesicPoint.class);
            }
        }

        for (GeodesicPoint gpoint : collection) {
            GeodesicPointText geoPointText = new GeodesicPointText(gpoint);

//            if (!getPolygonal().getVisualObjects().contains(geoPointText)) {
                list.add(geoPointText);
//            }
        }
        for (GeodesicPointText gptext : list) {
            Layer layer = LayerController.getTextLayerByPointType(gptext.referencedObject().getType());
            gptext.setLayer(layer);
            getDisplayPanel().addToVisualObjects(gptext);
        }

        displayPanel.clearSelection();
        finish();
    }

    @Override
    public void undo() {
        for (GeodesicPointText gptext : list) {
            getDisplayPanel().removeFromVisualObjects(gptext);
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
        return "Etiquetar nome do ponto";
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new LabelGeoPointCommand(displayPanel);
    }

    @Override
    public boolean canSelect() {
        return aChar == 'S';
    }

    @Override
    public String getCommandName() {
        return "etiqueta_nome_ponto";
    }
}
