/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.geo;

import br.com.geomapa.geodesic.InvalidPolygonalException;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.graphic.cad.compound.AbstractTableModelCAD;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.util.unit.impl.HectareUnit;
import br.com.geomapa.util.unit.impl.Meter;
import br.com.geomapa.util.unit.specs.AreaUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public class AreaAndPerimeterTableModel extends AbstractTableModelCAD {

    private static final DistanceUnit distanceUnit = new Meter();
    private static final AreaUnit areaUnit = new HectareUnit();

    public AreaAndPerimeterTableModel() {
        refresh();
    }

    public AreaAndPerimeterTableModel(double x, double y) {
        refresh();
        setLocation(x, y);
    }

    @Override
    public String getVisualObjectName() {
        return "area_perimeter_table";
    }

    @Override
    public int getRowCount() {
        MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();
        if (mainPolygonal == null) {
            return 0;
        }
        return mainPolygonal.getChildrenSize() + 1;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "Parcela";
        } else if (columnIndex == 1) {
            return "Proprietário";
        } else if (columnIndex == 2) {
            return "CPF";
        } else if (columnIndex == 3) {
            return "Área";
        } else if (columnIndex == 4) {
            return "Perímetro";
        }
        return "?";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();
        Polygonal polygonal;
        if (rowIndex < getRowCount() - 1) {
            List<String> polygonalNamesSorted = mainPolygonal.polygonalNamesSorted();
            polygonal = mainPolygonal.createOrGetPolygonal(polygonalNamesSorted.get(rowIndex));
        } else {
            polygonal = mainPolygonal;
        }

        try {
            if (columnIndex == 0) {
                return polygonal.getName();
            } else if (columnIndex == 1) {
                return polygonal.getMetadata().getNomeProprietario();
            } else if (columnIndex == 2) {
                return polygonal.getMetadata().getCpfCnpj();
            } else if (columnIndex == 3) {
                return areaUnit.toString(polygonal.area(), 4);
            } else if (columnIndex == 4) {
                return distanceUnit.toString(polygonal.perimeter(), 2);
            }
        } catch (InvalidPolygonalException ex) {
            return "xxxxxx";
        }
        return "?";
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f", 
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY()));
    }

    @Override
    protected void recreate() {
    }

    @Override
    public VisualObject copy() {
        AreaAndPerimeterTableModel other = new AreaAndPerimeterTableModel(getX(), getY());

        return other;
    }
}
