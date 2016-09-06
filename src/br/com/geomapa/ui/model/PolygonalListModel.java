/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.model;

import br.com.geomapa.geodesic.Polygonal;
import javax.swing.DefaultListModel;

/**
 *
 * @author paulocanedo
 */
public class PolygonalListModel extends DefaultListModel {

    private Polygonal polygonal;

    public PolygonalListModel(Polygonal polygonal) {
        this.polygonal = polygonal;
    }

    @Override
    public Object getElementAt(int index) {
        return polygonal.getLineDivisions().get(index);
    }

    @Override
    public int getSize() {
        return polygonal.getLineDivisions().size();
    }

    public void fireDataChanged() {
        fireContentsChanged(this, 0, getSize() - 1);
    }

    public void setPolygonal(Polygonal polygonal) {
        this.polygonal = polygonal;
        fireDataChanged();
    }
}
