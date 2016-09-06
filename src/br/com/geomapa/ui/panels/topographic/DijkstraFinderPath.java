/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels.topographic;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author paulocanedo
 */
public class DijkstraFinderPath {

    private HashMap<GeodesicPoint, HashMap<String, Object>> control = new HashMap<GeodesicPoint, HashMap<String, Object>>();
    private GeodesicPoint source;
    private GeodesicPoint secondPoint;
    private Set<LineDivision> set;

    public DijkstraFinderPath(Set<LineDivision> set, GeodesicPoint source) throws PolygonalException {
        this.set = set;
        this.source = source;
        Collection<GeodesicPoint> adjacentsPoints = findAdjacentsPoints(set, source);
        if (adjacentsPoints.isEmpty()) {
            throw new PolygonalException("O ponto de referência está isolado, assim não é possível determinar uma parcela.");
        }
        this.secondPoint = secondPoint(adjacentsPoints);

        if (!contains(set, source)) {
            throw new PolygonalException(String.format("O ponto %s não está contido na seleção.", source));
        }

        for (LineDivision ld : set) { //initialize of data struct used to dijkstra algorithm
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(estimated, Double.MAX_VALUE);
            map.put(precessor, null);
            map.put(closed, false);

            control.put(ld.getStartPoint(), map);
        }
        setupFirstNode(secondPoint); //setup the first node as secondpoint

        set.remove(LineDivision.getInstance(secondPoint, source)); //remove the vector pointing to source, this forces the path to find another way instead of direct connection
    }

    private void setupFirstNode(GeodesicPoint point) {
        control.get(point).put(estimated, 0.0);
        control.get(point).put(precessor, point);
    }

    private GeodesicPoint secondPoint(Collection<GeodesicPoint> adjacentsPoints) {
        GeodesicPoint candidate = null;
        for (GeodesicPoint geoPoint : adjacentsPoints) {
            double azimuth = source.azimuth(geoPoint).toDegreeDecimal();
            if (azimuth >= 0 && azimuth <= 180) {
                candidate = geoPoint;
            }
        }

        if (candidate == null) {
            for (GeodesicPoint geoPoint : adjacentsPoints) {
                double azimuth = source.azimuth(geoPoint).toDegreeDecimal();
                if (azimuth >= 180 && azimuth <= 270) {
                    candidate = geoPoint;
                }
            }
        }

        System.out.println(candidate);
        return candidate != null ? candidate : adjacentsPoints.iterator().next();
    }

    public List<GeodesicPoint> doDijkstraAlgorithm() {
        Map<String, Object> item;
        while (true) {
            GeodesicPoint smallestEstimated = smallestEstimated();
            item = control.get(smallestEstimated);
            if (item == null) {
                break;
            }
            item.put(closed, true);

            Double value = (Double) item.get(estimated);
            Collection<GeodesicPoint> adjacentsPoints = findAdjacentsPoints(set, smallestEstimated);
            for (GeodesicPoint point : adjacentsPoints) {
                if (!isOpened(point)) {
                    continue;
                }

                HashMap<String, Object> get = control.get(point);
                double cost = value + smallestEstimated.horizontalDistance(point);

                Double currentCost = (Double) get.get(estimated);
                if (cost < currentCost) {
                    get.put(estimated, cost);
                    get.put(precessor, smallestEstimated);
                }
            }
        }

        List<GeodesicPoint> path = new ArrayList<GeodesicPoint>();
        HashMap<String, Object> get = control.get(source);
        path.add(source);

        while (true) {
            GeodesicPoint precessorPoint = (GeodesicPoint) get.get(precessor);
            path.add(precessorPoint);

            get = control.get(precessorPoint);
            if (secondPoint.equals(precessorPoint)) {
                return path;
            }
        }
    }

    private GeodesicPoint smallestEstimated() {
        GeodesicPoint smallestPoint = null;
        Double smallestValue = Double.MAX_VALUE;

        for (GeodesicPoint gp : control.keySet()) {
            HashMap<String, Object> get = control.get(gp);

            Double value = (Double) get.get(estimated);
            if (value < smallestValue && isOpened(gp)) {
                smallestPoint = gp;
                smallestValue = value;
            }
        }
        return smallestPoint;
    }

    private boolean isOpened(GeodesicPoint point) {
        return !((Boolean) control.get(point).get(closed));
    }

    private boolean contains(Set<LineDivision> set, GeodesicPoint point) {
        for (LineDivision ld : set) {
            if (point.equals(ld.getStartPoint()) || point.equals(ld.getEndPoint())) {
                return true;
            }
        }
        return false;
    }

    private Collection<GeodesicPoint> findAdjacentsPoints(Set<LineDivision> set, GeodesicPoint p) {
        Collection<LineDivision> collection = findLineDivisionFrom(set, p);
        auxFindAdjacentPoints.clear();

        for (LineDivision lineDivison : collection) {
            auxFindAdjacentPoints.add(lineDivison.getStartPoint());
            auxFindAdjacentPoints.add(lineDivison.getEndPoint());
        }
        auxFindAdjacentPoints.remove(p);
        return auxFindAdjacentPoints;
    }

    private Collection<LineDivision> findLineDivisionFrom(Set<LineDivision> set, GeodesicPoint point) {
        auxFindLineDivision.clear();
        for (Iterator<LineDivision> it = set.iterator(); it.hasNext();) {
            LineDivision lineDivison = it.next();
            if (lineDivison.getStartPoint().equals(point)) {
                auxFindLineDivision.add(lineDivison);
            }
        }
        return auxFindLineDivision;
    }
    private Collection<LineDivision> auxFindLineDivision = new HashSet<LineDivision>();
    private Collection<GeodesicPoint> auxFindAdjacentPoints = new HashSet<GeodesicPoint>();
    private final String estimated = "estimated";
    private final String precessor = "precessor";
    private final String closed = "closed";
}
