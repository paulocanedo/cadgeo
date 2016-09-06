/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels.topographic;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author paulocanedo
 */
public class FinderPath {

    private GeodesicPoint point;
    private Set<LineDivision> set;
    
    private List<GeodesicPoint> path;
    private List<GeodesicPoint> completeList = new ArrayList<GeodesicPoint>();
    private int iterates = 0;

    public FinderPath(Set<LineDivision> set) {
        this.set = set;
    }

    public List<GeodesicPoint> findPath() throws PolygonalException {
        this.point = searchNextPointOrphan();
        path = new ArrayList<GeodesicPoint>();
        
        if(this.point == null) {
            return null;
        }
        
        while (findPath(point)) {
        }
        
        completeList.addAll(path);
        return path;
    }

    private boolean findPath(GeodesicPoint from) throws PolygonalException {
        if(++iterates > set.size()) {
            throw new PolygonalException("Não foi possível determinar o perímetro da parcela.");
        }
        path.add(from);

        Collection<GeodesicPoint> collection = findAdjacentsPoints(set, from);
        if (path.size() > 2 && collection.contains(point)) {
            path.add(point);
            return true;
        }
        normalize(collection);

//        GeodesicPoint next = collection.iterator().next();
        GeodesicPoint next = nearestPoint(collection, point);
        if(next == null) {
            throw new PolygonalException(String.format("Não é possível determinar a sequência de divisões a partir do ponto %s", from));
        }
        findPath(next);

        return false;
    }
    
    private GeodesicPoint nearestPoint(Collection<GeodesicPoint> collection, GeodesicPoint refer) {
        if(collection.isEmpty()) {
            return null;
        }
        
        GeodesicPoint nearest = collection.iterator().next();
        for(GeodesicPoint p : collection) {
            if(p.horizontalDistance(refer) < nearest.horizontalDistance(refer)) {
                nearest = p;
            }
        }
        return nearest;
    }

    private void normalize(Collection<GeodesicPoint> collection) {
        for (GeodesicPoint p : path) {
            collection.remove(p);
        }
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
    
    private GeodesicPoint searchNextPointOrphan() {
        for(LineDivision ld : set) {
            if(path == null || !completeList.contains(ld.getStartPoint())) {
                return ld.getStartPoint();
            }
            
            if(!completeList.contains(ld.getEndPoint())) {
                return ld.getEndPoint();
            }
        }
        
        return null;
    }
    
    private Collection<LineDivision> auxFindLineDivision = new HashSet<LineDivision>();
    private Collection<GeodesicPoint> auxFindAdjacentPoints = new HashSet<GeodesicPoint>();
    
//    private static <T> void removeFromValue(List<T> list, T o) {
//        int indexOf = list.indexOf(o);
//        if (indexOf >= 0 && indexOf < list.size()) {
//            for (int i = list.size(); i > indexOf + 1; i--) {
//                list.remove(i - 1);
//            }
//        }
//    }
}
