/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package br.com.geomapa.graphic.cad.geo;

import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.graphic.cad.compound.AbstractTableModelCAD;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.util.unit.impl.AzimuthUnit;
import br.com.geomapa.util.unit.impl.Meter;
import br.com.geomapa.util.unit.specs.DirectionUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author paulocanedo
 */
public class AzimuthAndDistanceTableModel extends AbstractTableModelCAD {

    private DistanceUnit distanceUnit = new Meter();
    private DirectionUnit directionUnit = new AzimuthUnit();
    private List<LineDivision> linesDivision;
    private Polygonal polygonal;

    public AzimuthAndDistanceTableModel(Polygonal polygonal) {
        this.polygonal = polygonal;

        refresh();
    }
    private Stack<Integer> secondaryPolygonalsMarkers = new Stack<Integer>();
    private Map<Integer, String> secondaryPolygonalsMarkersName = new HashMap<Integer, String>();

    private int calcOffset(int row) {
        int offset = 0;
        for (Iterator<Integer> it = secondaryPolygonalsMarkers.listIterator(); it.hasNext();) {
            Integer next = it.next();

            if (row < next) {
                return offset;
            }
            offset++;
        }
        return offset;
    }

    private List<LineDivision> getUnique(MainPolygonal mpolygonal) {
        List<LineDivision> list = new ArrayList<LineDivision>();

        list.addAll(mpolygonal.getLineDivisions());
        secondaryPolygonalsMarkers.clear();

        List<String> polygonalNamesSorted = mpolygonal.polygonalNamesSorted();
        for (String sp : polygonalNamesSorted) {
            Polygonal p = mpolygonal.createOrGetPolygonal(sp);
            boolean flag = false;
            Integer marker = list.size() + secondaryPolygonalsMarkers.size();
            secondaryPolygonalsMarkers.push(marker);
            secondaryPolygonalsMarkersName.put(marker, sp);

            LinkedList<LineDivision> lds = p.getLineDivisions();
            for (LineDivision ld : lds) {
                if (!containsLineDivisionIgnoringWay(list, ld)) {
                    list.add(ld);
                    flag = true;
                }
            }
            if (!flag) {
                secondaryPolygonalsMarkers.pop();
            }
        }
        return list;
    }

    private boolean containsLineDivisionIgnoringWay(Collection<LineDivision> collection, LineDivision lineDivision) {
        for (LineDivision ld : collection) {
            if (ld.equalsIgnoreWay(lineDivision)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getRowCount() {
        if (polygonal.isMain()) {
            MainPolygonal mpolygonal = (MainPolygonal) polygonal;
            return getUnique(mpolygonal).size() + secondaryPolygonalsMarkers.size();
        }
        return polygonal.getLineDivisions().size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "De";
        } else if (columnIndex == 1) {
            return "Para";
        } else if (columnIndex == 2) {
            return "Azimute";
        } else if (columnIndex == 3) {
            return "Distância";
        } else if (columnIndex == 4) {
            return "Coord Este";
        } else if (columnIndex == 5) {
            return "Coord Norte";
        }
        return "??";
    }

    @Override
    protected void recreate() {
        if (polygonal.isMain()) {
            MainPolygonal mpolygonal = (MainPolygonal) polygonal;
            linesDivision = getUnique(mpolygonal);
        } else {
            linesDivision = polygonal.getLineDivisions();
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int offset = 0;
        if (polygonal.isMain()) {
            if (secondaryPolygonalsMarkers.contains(rowIndex)) {
                if (columnIndex == 0) {
                    return secondaryPolygonalsMarkersName.get(rowIndex) == null ?
                            "---" : secondaryPolygonalsMarkersName.get(rowIndex);
                } else {
                    return "";
                }
            }
            offset = calcOffset(rowIndex);
        }

        LineDivision current = linesDivision.get(rowIndex - offset);
        GeodesicPoint from = current.getStartPoint();
        GeodesicPoint to = current.getEndPoint();
        UTMCoordinate coordUTM = from.getCoordinate().toUTM();


        if (columnIndex == 0) {
            return from;
        } else if (columnIndex == 1) {
            return to;
        } else if (columnIndex == 2) {
            return directionUnit.toString(from.azimuth(to), 0);
        } else if (columnIndex == 3) {
            return distanceUnit.toString(from.horizontalDistance(to), 2);
        } else if (columnIndex == 4) {
            return String.format("%.3f", coordUTM.getEast());
        } else if (columnIndex == 5) {
            return String.format("%.3f", coordUTM.getNorth());
        }
        return "??";
    }

    @Override
    public String getVisualObjectName() {
        return "azimuth_distance_table";
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f \"%s\"", 
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(), polygonal.getName()));
    }

    @Override
    public VisualObject copy() {
        AzimuthAndDistanceTableModel other = new AzimuthAndDistanceTableModel(polygonal);
        other.setLocation(getX(), getY());

        return other;
    }
}
