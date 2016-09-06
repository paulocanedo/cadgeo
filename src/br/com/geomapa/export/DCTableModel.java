/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.export;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author paulocanedo
 */
public class DCTableModel extends AbstractTableModel {

    private DecimalFormat decimalFormat3c = new DecimalFormat("0.000");
    private List<GeodesicPoint> list = new ArrayList<GeodesicPoint>();

    public DCTableModel() {
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return GeodesicPoint.class;
            default:
                return String.class;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Vértice";
            case 1:
                return "Este";
            case 2:
                return "Norte";
            case 3:
                return "Altitude";
            case 4:
                return "Rinex";
            default:
                throw new IllegalArgumentException("Unexpected column index: " + column);
        }
    }

    public int getRowCount() {
        return list.size();
    }

    public int getColumnCount() {
        return 5;
    }

    public void addElement(GeodesicPoint point) {
        int oldSize = list.size();
        list.add(point);
        fireTableRowsInserted(oldSize, list.size());
    }

    public void removeElement(GeodesicPoint point) {
        int index = list.indexOf(point);
        if (index >= 0) {
            list.remove(point);
            fireTableRowsDeleted(index, index);
        }
    }

    public boolean contains(GeodesicPoint point) {
        return list.contains(point);
    }

    public List<GeodesicPoint> getList() {
        return list;
    }

    public void clearAll() {
        list.clear();
        fireTableDataChanged();
    }

    public void move(GeodesicPoint point, int offset) {
        int index = list.indexOf(point);
        int newIndex = index + offset;

        if(newIndex >= 0 && newIndex < list.size()) {
            list.remove(point);
            
            list.add(newIndex, point);
            fireTableRowsUpdated(newIndex, newIndex);
            fireTableRowsUpdated(newIndex+offset, newIndex+offset);
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return list.get(rowIndex);
            case 1:
                return decimalFormat3c.format(list.get(rowIndex).getCoordinate().toUTM().getEast());
            case 2:
                return decimalFormat3c.format(list.get(rowIndex).getCoordinate().toUTM().getNorth());
            case 3:
                return decimalFormat3c.format(list.get(rowIndex).getCoordinate().getEllipsoidalHeight());
            case 4:{
                File rinex = list.get(rowIndex).getMetaData().getRinex();
                return rinex != null ? rinex.getName() : "";
            }
            default:
                throw new IllegalArgumentException("Unexpected column index: " + columnIndex);
        }
    }
}
