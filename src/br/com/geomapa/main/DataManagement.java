/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.main;

import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.geodesic.coordinate.Coordinate;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.ui.model.GeoPointTableModel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import br.com.geomapa.util.mlist.MacroList;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;

/**
 *
 * @author paulocanedo
 */
public class DataManagement {

    private static final MacroList<GeodesicPoint> allPoints = new MacroList<GeodesicPoint>();
    private static final GeoPointTableModel geoPointTableModel = new GeoPointTableModel(allPoints);
    private static final MainPolygonal mainPolygonal = new MainPolygonal();
    private static final Coordinate auxCoord = new UTMCoordinate(1, Hemisphere.SOUTH, 0, 0);

    public static MacroList<GeodesicPoint> getAllPoints() {
        return allPoints;
    }

    public static GeodesicPoint findPoint(Integer oid) {
        for (GeodesicPoint point : allPoints) {
            if (point.getOid().equals(oid)) {
                return point;
            }
        }
        return null;
    }

    public static GeodesicPoint findPoint(String name) {
        for (GeodesicPoint gpoint : getAllPoints()) {
            if (gpoint.getName().equals(name)) {
                return gpoint;
            }
        }
        return null;
    }

    public static GeodesicPoint findPoint(Iterable<GeodesicPoint> collection, String name) {
        for (GeodesicPoint point : collection) {
            String pname = point.getNameNoSeparators();
            name = GeodesicPoint.getNameNoSeparators(name);
            if (pname.equals(name)) {
                return point;
            }
        }
        return null;
    }

    public static List<Integer> findDuplicatedPointsOIDS() {
        List<Integer> list = new ArrayList<Integer>();
        for (GeodesicPoint gpoint1 : allPoints) {
            for (GeodesicPoint gpoint2 : allPoints) {
                boolean repeatedCoord = PolygonalUtils.horizontalDistance(gpoint1.getLocation(), gpoint2.getLocation()) < 3 && !gpoint1.getName().equals(gpoint2.getName());
                boolean repeatedName = gpoint1.getName().equalsIgnoreCase(gpoint2.getName()) && gpoint1 != gpoint2;
                if (repeatedCoord || repeatedName) {
                    list.add(gpoint1.getOid());
                }
            }
        }

        return list;
    }

    public static List<Entry<GeodesicPoint, GeodesicPoint>> findDuplicatedPointsByCoord() {
        List<Entry<GeodesicPoint, GeodesicPoint>> list = new ArrayList<Entry<GeodesicPoint, GeodesicPoint>>();
        for (GeodesicPoint gpoint1 : allPoints) {
            for (GeodesicPoint gpoint2 : allPoints) {
                if (PolygonalUtils.horizontalDistance(gpoint1.getLocation(), gpoint2.getLocation()) < 0.01 && !gpoint1.getName().equals(gpoint2.getName())) {
                    SimpleEntry<GeodesicPoint, GeodesicPoint> entry = new SimpleEntry<GeodesicPoint, GeodesicPoint>(gpoint1, gpoint2);
                    list.add(entry);
                }
            }
        }

        return list;
    }

    public static List<Entry<GeodesicPoint, GeodesicPoint>> findDuplicatedPointsByName() {
        List<Entry<GeodesicPoint, GeodesicPoint>> list = new ArrayList<Entry<GeodesicPoint, GeodesicPoint>>();
        for (GeodesicPoint gpoint1 : allPoints) {
            for (GeodesicPoint gpoint2 : allPoints) {
                if (gpoint1.getName().equalsIgnoreCase(gpoint2.getName()) && gpoint1 != gpoint2) {
                    SimpleEntry<GeodesicPoint, GeodesicPoint> entry = new SimpleEntry<GeodesicPoint, GeodesicPoint>(gpoint1, gpoint2);
                    list.add(entry);
                }
            }
        }

        return list;
    }

    public static GeodesicPoint findPointByAprox(String name) throws PolygonalException {
        return findPointByAprox(getAllPoints(), name);
    }

    public static GeodesicPoint findPointByAprox(Iterable<GeodesicPoint> collection, String name) throws PolygonalException {
        GeodesicPoint found = null;
        for (GeodesicPoint point : collection) {
            String pname = GeodesicPoint.getNameNoSeparators(point.getName());
            name = GeodesicPoint.getNameNoSeparators(name);
            if (pname.toLowerCase().contains(name.toLowerCase())) {
                if (found != null) {
                    throw new PolygonalException(String.format("Foi encontrada mais de uma ocorrência para: %s", name));
                }
                found = point;
            }
        }
        return found;
    }

    public static GeodesicPoint findPoint(GeodesicPoint toFind) {
        for (GeodesicPoint point : getAllPoints()) {
            if (point.equals(toFind)) {
                return point;
            }
        }
        return null;
    }

    public static GeoPointTableModel getGeoPointTableModel() {
        return geoPointTableModel;
    }

    public static MainPolygonal getMainPolygonal() {
        return mainPolygonal;
    }

    public static void setFavoritePoints(String names, boolean flag) {
        StringTokenizer st = new StringTokenizer(names, ",");
        while (st.hasMoreTokens()) {
            String name = st.nextToken();
            GeodesicPoint geoPoint = findPoint(name);
            if (geoPoint != null) {
                geoPoint.setFavorite(flag);
            }
        }
    }

    public static void setSatGeoPoints(String names, boolean flag) {
        StringTokenizer st = new StringTokenizer(names, ",");
        while (st.hasMoreTokens()) {
            String name = st.nextToken();
            GeodesicPoint geoPoint = findPoint(name);
            if (geoPoint != null) {
                geoPoint.setSatGeo(flag);
            }
        }
    }

    public static List<GeodesicPoint> listFavoritePoints() {
        List<GeodesicPoint> list = new ArrayList<GeodesicPoint>();
        for (GeodesicPoint gpoint : getAllPoints()) {
            if (gpoint.isFavorite()) {
                list.add(gpoint);
            }
        }
        return list;
    }

    public static List<GeodesicPoint> listSatGeoPoints() {
        List<GeodesicPoint> list = new ArrayList<GeodesicPoint>();
        for (GeodesicPoint gpoint : getAllPoints()) {
            if (gpoint.isSatGeo()) {
                list.add(gpoint);
            }
        }
        return list;
    }

    public static Integer newPointValue(GeodesicPointType type) {
        int max = 0;
        for (GeodesicPoint point : allPoints) {
            if (point.getType() == type) {
                max = Math.max(max, point.getNumericValue());
            }
        }
        return max + 1;
    }
}
