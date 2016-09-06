/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.compound;

import br.com.geomapa.graphic.cad.primitives.Rectangle;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.main.Bus;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author paulocanedo
 */
public abstract class AbstractTableModelCAD extends AbstractVisualObjectCompound implements TableModel {

    private List<TableModelListener> tmListeners = new ArrayList<TableModelListener>();
    private double width;
    private double height;

    public AbstractTableModelCAD() {
    }

    @Override
    public void refresh() {
        recreate();
        
        double x = getX();
        double y = getY();
        float rowHeight = 2 * Bus.getScale();

        double space = Bus.getScale();
        width = 0;
        delegate.clear();

        for (int col = 0; col < getColumnCount(); col++) {
            double currentColumnWidth = 0;
            height = Bus.getScale();

            for (int row = getRowCount() - 1; row >= 0; row--) {
                Object value = getValueAt(row, col);

                VisualText visualText = new VisualText(x + space + width, y + height, 1.5f, String.valueOf(value));
                delegate.add(visualText); //se texto for vazio não adicionar
                currentColumnWidth = Math.max(currentColumnWidth, visualText.getScaledWidth() + space * 5);
                height += rowHeight + space;
            }
            //header column
            VisualText visualText = new VisualText(x + space + width, y + height, 1.5f, getColumnName(col));
            delegate.add(visualText);

            currentColumnWidth = Math.max(currentColumnWidth, visualText.getScaledWidth() + space * 5);
            height += rowHeight + space;
            //end header column

            width += currentColumnWidth;
        }

        delegate.add(Rectangle.createRect(x, y, width, height));
    }
    
    protected abstract void recreate();

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    @Override
    public final void addTableModelListener(TableModelListener l) {
        tmListeners.add(l);
    }

    @Override
    public final void removeTableModelListener(TableModelListener l) {
        tmListeners.remove(l);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractTableModelCAD other = (AbstractTableModelCAD) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.oid;
        return hash;
    }
    
    @Override
    public void write(PrintStream stream) throws IOException {
    }

}
