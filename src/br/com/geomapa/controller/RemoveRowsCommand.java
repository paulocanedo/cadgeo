/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.ui.model.GeoPointTableModel;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author paulocanedo
 */
public class RemoveRowsCommand implements Command {

    private final GeoPointTableModel model;
    private final Integer[] oids;
    private List<GeodesicPoint> removedRows;

    public RemoveRowsCommand(GeoPointTableModel model, Integer... oids) {
        this.model = model;
        this.oids = oids;
    }

    @Override
    public void execute() {
        try {
            removedRows = model.removeRows(oids);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void undo() {
        model.addAll(removedRows);
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }
}
