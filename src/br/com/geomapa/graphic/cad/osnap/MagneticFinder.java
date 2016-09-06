/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.osnap;

import br.com.geomapa.controller.MagneticController.MagneticType;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.compound.VisualObjectCompound;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.GeodesicPointReference;
import br.com.geomapa.graphic.cad.primitives.Circle;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.primitives.Rectangle;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Bus;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class MagneticFinder {

    private int treshold = 12;
    private GLTopographicPanel displayPanel;
    private static MagneticFinder instance;

    private MagneticFinder(GLTopographicPanel displayPanel) {
        this.displayPanel = displayPanel;
    }

    private VisualObject[] getVisualObjects() {
        Polygonal currentPolygonal = Bus.getCurrentPolygonal();
        if (currentPolygonal == null) {
            return new VisualObject[0];
        } else {
            return currentPolygonal.getVisualObjects();
        }
    }

    private int[] getScreenCoords(Point2D location) {
        int[] project = displayPanel.project(location.getX(), location.getY());

        return project;
    }

    private double[] getReverseScreenCoords(int x, int y) {
        return displayPanel.unProject(x, y);
    }

    private boolean isValid(Point2D point) {
        return displayPanel.isInsideView(point);
    }

    public static MagneticFinder getInstance(GLTopographicPanel displayPanel) {
        if (instance == null) {
            instance = new MagneticFinder(displayPanel);
        }
        return instance;
    }

    /**
     * 
     * @param point mouse clicked point
     * @return 
     */
    public MagneticPoint getIntersectionPoint(Point point) {
        List<Line> listLine = new ArrayList<Line>();
        for (VisualObject vo : getVisualObjects()) {
            if (vo instanceof Line) {
                Line line = (Line) vo;
                int[] coords1 = getScreenCoords(line.getLocation());
                int[] coords2 = getScreenCoords(line.getEndLocation());

                if (isNear(coords1, coords2, point)) {
                    listLine.add(line);
                }
            }
        }

        for (Line line : listLine) {
            MagneticPoint intersectionPoint = getIntersectionPoint(line, listLine);
            if (intersectionPoint != null) {
                return intersectionPoint;
            }
        }
        
        return null;
    }

    private MagneticPoint getIntersectionPoint(Line line, Collection<Line> lines) {
        for (Line l : lines) {
            if (l == line) {
                continue;
            }

            if (Line2D.linesIntersect(l.getX(), l.getY(), l.getEndX(), l.getEndY(), line.getX(), line.getY(), line.getEndX(), line.getEndY())) {
                Point2D intersection = l.intersection(line);
                if (intersection != null) {
                    return new MagneticPoint(intersection, MagneticType.INTERSECTION_POINT);
                }
            }
        }
        return null;
    }
    
    public MagneticPoint getEndPoint(Point point) {
        List<MagneticPoint> possibleEndPoints = new ArrayList<MagneticPoint>();
        for (VisualObject vo : getVisualObjects()) {
            MagneticPoint endpoint = getEndPoint(vo, point);
            if (endpoint != null) {
                possibleEndPoints.add(endpoint);
            }
        }

        if (possibleEndPoints.isEmpty()) { //get the most near end point, not working properly
            return null;
        } else {
            double[] reverseScreenCoords = getReverseScreenCoords(point.x, point.y);
            MagneticPoint candidate = possibleEndPoints.get(0);
            double candidateDistance = PolygonalUtils.horizontalDistance(candidate.getPoint().getX(), candidate.getPoint().getY(), reverseScreenCoords[0], reverseScreenCoords[1]);
            for (MagneticPoint mp : possibleEndPoints) {
                if (PolygonalUtils.horizontalDistance(mp.getPoint().getX(), mp.getPoint().getY(), reverseScreenCoords[0], reverseScreenCoords[1])
                        < candidateDistance) {
                    candidate = mp;
                    candidateDistance = PolygonalUtils.horizontalDistance(candidate.getPoint().getX(), candidate.getPoint().getY(), reverseScreenCoords[0], reverseScreenCoords[1]);
                }
            }
            return candidate;
        }
    }

    private MagneticPoint getEndPoint(VisualObject vo, Point point) {
        if (vo instanceof Line) {
            Line l = (Line) vo;

            Point2D location = l.getLocation();
            Point2D location2 = l.getEndLocation();

            MagneticPoint endPoint = getEndPoint(location, location2, point);
            if (endPoint != null) {
                return endPoint;
            }
        } else if (vo instanceof Rectangle) {
            Rectangle r = (Rectangle) vo;

            Point2D location1 = r.getLocation();
            Point2D location3 = r.getEndLocation();
            Point2D location2 = new Point2D.Double(location1.getX(), location3.getY());
            Point2D location4 = new Point2D.Double(location3.getX(), location1.getY());

            MagneticPoint endPoint = getEndPoint(location1, location2, point);
            if (endPoint != null) {
                return endPoint;
            }

            endPoint = getEndPoint(location2, location3, point);
            if (endPoint != null) {
                return endPoint;
            }

            endPoint = getEndPoint(location3, location4, point);
            if (endPoint != null) {
                return endPoint;
            }

            endPoint = getEndPoint(location4, location1, point);
            if (endPoint != null) {
                return endPoint;
            }
        } else if (vo instanceof VisualObjectCompound) {
            VisualObjectCompound voc = (VisualObjectCompound) vo;
            for (VisualObject vo2 : voc) {
                MagneticPoint endPoint = getEndPoint(vo2, point);
                if (endPoint != null) {
                    return endPoint;
                }
            }
        }
        return null;
    }

    private MagneticPoint getEndPoint(Point2D location1, Point2D location2, Point point) {
        int[] coord1 = getScreenCoords(location1);
        int[] coord2 = getScreenCoords(location2);

        if (isNear(coord1, coord2, point)) {
//        if (Line2D.ptSegDist(coord1[0], coord1[1], coord2[0], coord2[1], point.x, point.y) <= treshold) {
            double dist1 = PolygonalUtils.horizontalDistance(coord1[0], coord1[1], point.x, point.y);
            double dist2 = PolygonalUtils.horizontalDistance(coord2[0], coord2[1], point.x, point.y);

            if (dist1 < dist2 && isValid(location1)) {
                return new MagneticPoint(location1, MagneticType.END_POINT);
            } else if (isValid(location2)) {
                return new MagneticPoint(location2, MagneticType.END_POINT);
            }
        }
        return null;
    }

    private boolean isNear(int[] coord1, int[] coord2, Point point) {
        return (Line2D.ptSegDist(coord1[0], coord1[1], coord2[0], coord2[1], point.x, point.y) <= treshold);
    }
    
    private boolean isNear(int[] coord1, double radius, Point point) {
        double distance = PolygonalUtils.horizontalDistance(coord1[0], coord1[1], point.x, point.y);
        return Math.abs(distance - radius) < treshold;
    }

    public MagneticPoint getMidPoint(Point point) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public MagneticPoint getCenterPoint(Point point) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public MagneticPoint getGeoPoint(Point point) {
        Polygonal currentPolygonal = Bus.getCurrentPolygonal();
        Iterable<GeodesicPoint> iterable;
        if (currentPolygonal.isMain()) {
            iterable = DataManagement.getAllPoints();
        } else {
            List<GeodesicPoint> list = new ArrayList<GeodesicPoint>();
            for (VisualObject vo : currentPolygonal.getVisualObjects()) {
                if (vo instanceof GeodesicPoint) {
                    list.add((GeodesicPoint) vo);
                } else if (vo instanceof GeodesicPointReference) {
                    list.add(((GeodesicPointReference) vo).getGeopoint());
                }
            }
            iterable = list;
        }

        for (GeodesicPoint gpoint : iterable) {
            Point2D location = gpoint.getLocation();
            if (!isValid(location)) {
                continue;
            }
            int[] screenCoords = getScreenCoords(location);

            if (PolygonalUtils.horizontalDistance(point.x, point.y, screenCoords[0], screenCoords[1]) <= treshold) {
                return new MagneticPoint(gpoint, MagneticType.GEODESIC_POINT);
            }
        }
        return null;
    }

    public int getTreshold() {
        return treshold;
    }

    public void setTreshold(int treshold) {
        this.treshold = treshold;
    }
}
