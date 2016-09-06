/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.model;

import br.com.geomapa.geodesic.coordinate.SystemCoordinateTransformer;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.point.MetaDataPoint;
import br.com.geomapa.geodesic.point.MetaDataPoint.MeasurementMethod;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.importer.PointImporterException;
import br.com.geomapa.importer.PointImporterHandle;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.util.mlist.MacroList;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author paulocanedo
 */
public class GeoPointTableModel extends AbstractTableModel implements PointImporterHandle {

    private SystemCoordinateTransformer coordTransformer = SystemCoordinateTransformer.UTM_TRANSFORMER;
    private static final DecimalFormat decimalFormat3c = new DecimalFormat("0.000");
    private MacroList<GeodesicPoint> delegate;
    private List<GeodesicPoint> currentList;
    private List<String> columnNames = Arrays.asList(new String[]{"Ponto", "Coord E", "Coord N", "Alt", "RMS E", "RMS N", "RMS Alt", "Método", "ID"});

    public GeoPointTableModel(MacroList<GeodesicPoint> points) {
        this.delegate = points;
        decimalFormat3c.setGroupingSize(3);
        decimalFormat3c.setGroupingUsed(true);
    }

    @Override
    public int getRowCount() {
        return delegate.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        String columnName = getColumnName(columnIndex);
        if (columnName.equals("Método")) {
            return MetaDataPoint.MeasurementMethod.class;
        } else if (columnName.equalsIgnoreCase("oid")) {
            return Integer.class;
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = "";
        GeodesicPoint gpoint = delegate.get(rowIndex);
        if (gpoint == null) {
            return "";
        }

        MetaDataPoint metaData = gpoint.getMetaData();
        if (columnIndex == 0) {
            value = delegate.get(rowIndex).getName();
        } else if (columnIndex == 1) {
            try {
                value = coordTransformer.getX(delegate.get(rowIndex).getCoordinate());
                if (value instanceof Number) {
                    value = decimalFormat3c.format(value);
                }
            } catch (IllegalArgumentException ex) {
                value = "MC inválido";
            }
        } else if (columnIndex == 2) {
            try {
                value = coordTransformer.getY(delegate.get(rowIndex).getCoordinate());
                if (value instanceof Number) {
                    value = decimalFormat3c.format(value);
                }
            } catch (IllegalArgumentException ex) {
                value = "MC inválido";
            }
        } else if (columnIndex == 3) {
            value = delegate.get(rowIndex).getCoordinate().getEllipsoidalHeight();
            value = decimalFormat3c.format(value);
        } else if (columnIndex == 4) {
            if (metaData != null) {
                value = metaData.getQx();
                value = decimalFormat3c.format(value);
            }
        } else if (columnIndex == 5) {
            if (metaData != null) {
                value = metaData.getQy();
                value = decimalFormat3c.format(value);
            }
        } else if (columnIndex == 6) {
            if (metaData != null) {
                value = metaData.getQz();
                value = decimalFormat3c.format(value);
            }
        } else if (columnIndex == 7) {
            if (metaData != null) {
                MeasurementMethod measurementMethod = metaData.getMeasurementMethod();
                value = measurementMethod == null ? " " : measurementMethod.name();
            }
        } else if (columnIndex == 8) {
            return delegate.get(rowIndex).getOid();
        }
        return value == null ? "" : value.toString();
    }

    @Override
    public void handlePoint(GeodesicPoint point) {
        if (delegate.contains(point)) {
            System.out.println("ponto repetido" + point);
            return;
        }
        this.currentList.add(point);
    }

    @Override
    public void startImport() {
        this.currentList = new ArrayList<GeodesicPoint>();
    }

    @Override
    public void endImport() {
        int oldSize = delegate.size();
        this.delegate.addList(currentList);

        int newSize = delegate.size();
        if (newSize > 0) {
            if (newSize == oldSize) {
                throw new PointImporterException("Todos os pontos contidos no arquivo já foram importados");
            }
            fireTableRowsInserted(oldSize, newSize - 1);
        }
    }

    public void removeLastImport() {
        int oldSize = delegate.size();
        this.delegate.removeList(currentList);
        currentList = delegate.lastList();

        fireTableRowsDeleted(delegate.size(), oldSize - 1);
    }

    public MacroList<GeodesicPoint> getMacroList() {
        return delegate;
    }

    public List<GeodesicPoint> removeRows(Integer... ids) {
        int oldSize = delegate.size();

        List<GeodesicPoint> removedPoints = new ArrayList<GeodesicPoint>();
        for (Integer id : ids) {
            GeodesicPoint point = DataManagement.findPoint(id);
            LineDivision ld = LineDivision.findAnyOccurrence(point);
            if (ld != null) {
                throw new IllegalArgumentException(String.format("Antes de remover este ponto você deve desligar a ligação: %s - %s", ld.getStartPoint(), ld.getEndPoint()));
            }

            if (point != null) {
                DataManagement.getAllPoints().removeElement(point);
                removedPoints.add(point);
            }
        }

        int newSize = delegate.size();
        if (newSize != oldSize) {
            fireTableRowsDeleted(newSize, oldSize - 1);
        }

        return removedPoints;
    }

    public void add(GeodesicPoint point) {
        if (contains(point)) {
            throw new IllegalArgumentException(String.format("O ponto %s não pode ser adicionado porque já existe outro com mesmo nome", point));
        }
        List<GeodesicPoint> list = new ArrayList<GeodesicPoint>();
        list.add(point);
        addAll(list);
    }

    public void addAll(List<GeodesicPoint> list) {
        int oldSize = delegate.size();
        delegate.addList(list);
        int newSize = delegate.size();

        if (oldSize != newSize) {
            fireTableRowsInserted(oldSize, newSize - 1);
        }
    }

    public boolean contains(GeodesicPoint point) {
        return delegate.contains(point);
    }

    public void clear() {
        int oldSize = delegate.size();
        delegate.clear();

        if (oldSize > 0) {
            fireTableRowsDeleted(0, oldSize - 1);
        }
    }

    public boolean load(String points) {
        return false;
    }

    public void setCoordTransformer(SystemCoordinateTransformer coordTransformer) {
        this.coordTransformer = coordTransformer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (GeodesicPoint point : delegate) {
            MetaDataPoint metaData = point.getMetaData();
            sb.append(point.getName()).append(",");
            sb.append(decimalFormat3c.format(point.getCoordinate().toUTM().getEast())).append(",");
            sb.append(decimalFormat3c.format(point.getCoordinate().toUTM().getNorth())).append(",");
            sb.append(decimalFormat3c.format(point.getCoordinate().getEllipsoidalHeight())).append(",");
            if (metaData != null) {
                sb.append(decimalFormat3c.format(metaData.getQx())).append(",");
                sb.append(decimalFormat3c.format(metaData.getQy())).append(",");
                sb.append(decimalFormat3c.format(metaData.getQz())).append(",");

                MeasurementMethod measurementMethod = metaData.getMeasurementMethod();
                sb.append(measurementMethod == null ? " " : measurementMethod.name());
            } else {
                sb.append(",");
                sb.append(",");
                sb.append(",");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
