/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.model;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author paulocanedo
 */
public class GeoPointListModel extends AbstractListModel {

    private GeoPointTableModel tableModel;
    private List<GeodesicPoint> points = new ArrayList<GeodesicPoint>();
    private String filterText = null;

    public GeoPointListModel(GeoPointTableModel tableModel) {
        this.tableModel = tableModel;
        this.tableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.INSERT) {
                    fireIntervalAdded(GeoPointListModel.this, e.getFirstRow(), e.getLastRow());
                } else if (e.getType() == TableModelEvent.DELETE) {
                    fireIntervalRemoved(GeoPointListModel.this, e.getFirstRow(), e.getLastRow());
                } else if (e.getType() == TableModelEvent.UPDATE) {
                    fireContentsChanged(GeoPointListModel.this, e.getFirstRow(), e.getLastRow());
                }

                rebuildList();
            }
        });
    }

    public GeoPointTableModel getTableModel() {
        return tableModel;
    }

    @Override
    public int getSize() {
        if (filterText == null) {
            return tableModel.getRowCount();
        }

        return points.size();
    }

    @Override
    public Object getElementAt(int index) {
//        if (filterText == null) {
//            return tableModel.getValueAt(index);
//        }
        return points.get(index);
    }

    public void setFilterText(String text) {
        filterText = text;

        int oldSize = getSize();
        rebuildList();
        if (oldSize > 0) {
            fireIntervalRemoved(this, 0, oldSize - 1);
        }
        int newSize = getSize();
        if (newSize > 0) {
            fireIntervalAdded(this, 0, newSize - 1);
        }
    }

    private void rebuildList() {
        points.clear();
        if (filterText == null) {
            return;
        }

        for (GeodesicPoint p : tableModel.getMacroList()) {
            String pointName = p.getName();

            if (pointName.toLowerCase().contains(filterText.toLowerCase())) {
                points.add(p);
            }
        }
    }
}
