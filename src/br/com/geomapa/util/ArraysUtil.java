/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util;

import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.graphic.cad.compound.ISO_Paper;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.primitives.Arc;
import br.com.geomapa.graphic.cad.spec.DoublePointVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author paulocanedo
 */
public final class ArraysUtil {

    private ArraysUtil() {
    }

    public static List<List<LineDivision>> collectLineDivisionsGrouped(Polygonal polygonal) throws PolygonalException {
        if (!polygonal.isClosed() || !polygonal.isBorderingFilled()) {
            throw new PolygonalException("A poligonal não está fechada ou os limites e confrontações não foram definidos");
        }

        List<List<LineDivision>> collectionMaster = new ArrayList<List<LineDivision>>();
        List<LineDivision> collection = null;

        LineDivision lastLd = null;
        for (LineDivision ld : polygonal.getLineDivisions()) {
            if (lastLd == null || !lastLd.getBorderName().equals(ld.getBorderName())) {
                if (collection != null && !collection.isEmpty()) {
                    collectionMaster.add(collection);
                }
                collection = new ArrayList<LineDivision>();
            }
            collection.add(ld);
            lastLd = ld;
        }
        if (!collectionMaster.contains(collection)) {
            collectionMaster.add(collection);
        }
        return collectionMaster;
    }

    public static double[] collectCorners(VisualObject... visualObjects) {
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE, minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        for (VisualObject vo : visualObjects) {
            double x = vo.getX();
            double y = vo.getY();

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);

            if (vo instanceof DoublePointVisualObject) {
                DoublePointVisualObject dpvo = (DoublePointVisualObject) vo;

                x = dpvo.getEndX();
                y = dpvo.getEndY();

                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }
        return new double[]{minX, minY, maxX, maxY};
    }

    public static double[] collectCorners(Collection<VisualObject> collection) {
        return collectCorners(collection.toArray(new VisualObject[0]));
    }

    public static <T extends VisualObject> Collection<T> collect(Collection<VisualObject> from, Class<T> c) {
        return collect(from.toArray(new VisualObject[0]), c);
    }
    
    public static <T extends VisualObject> Collection<T> collect(VisualObject[] from, Class<T> c) {
        Set<T> set = new HashSet<T>();

        for (VisualObject vo : from) {
            if (c.isAssignableFrom(vo.getClass())) {
                set.add((T) vo);
            }
        }
        return set;
    }

    public static List<LineDivision> collectUniDirection(Set<LineDivision> from) {
        LinkedList<LineDivision> list = new LinkedList<LineDivision>();
        LineDivision division = from.iterator().next();
        list.add(division);

        //add after values
        while (true) {
            boolean found = false;
            for (LineDivision ld : from) {
                if (division.equalsIgnoreWay(ld) == false
                        && ld.getStartPoint().equals(division.getEndPoint())) {

                    list.add(division = ld);
                    found = true;
                }
            }

            if (!found) {
                break;
            }
        }

        //add before values
        while (true) {
            boolean found = false;
            for (LineDivision ld : from) {
                if (division.equalsIgnoreWay(ld) == false
                        && ld.getEndPoint().equals(division.getStartPoint())) {

                    division = ld;
                    if (!list.contains(division)) {
                        list.addFirst(division);
                    }
                    found = true;
                }
            }

            if (!found) {
                break;
            }
        }

        return list;
    }
}
