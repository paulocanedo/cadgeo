/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.geodesic.point.LineDivisionType;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.primitives.Polyline;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import br.com.geomapa.ui.panels.topographic.Projector;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author paulocanedo
 */
public class Polygonal extends AbstractVisualObject implements Comparable<Polygonal> {

    private Point2D centroidPoint = new Point2D.Double();
    private GeodesicPoint tempPoint;
    private LinkedList<LineDivision> delegate = new LinkedList<LineDivision>();
    private PolygonalMetadata metadata;
    private String name;
    private Set<VisualObject> visualObjects = new HashSet<VisualObject>();
    public static final String PROP_METADATA = "metadata";
    public static final String PROP_NAME = "name";
    public static final String PROP_IS_CLOSED = "isClosed";
    public static final String PROP_SIZE_CHANGED = "sizeChanged";
    private boolean initializedZoom = false;

    public Polygonal(String name) {
        setName(name);
    }

    public Polygonal(String name, List<LineDivision> list) {
        setName(name);
        this.delegate.addAll(list);
        defineFirstElement();
    }

    public double area() {
        if (!isClosed()) {
            throw new InvalidPolygonalException(String.format("%s: O perímetro da área não está fechado.", getName()));
        }
        ArrayList<UTMCoordinate> coords = new ArrayList<UTMCoordinate>();
        for (LineDivision ld : getLineDivisions()) {
            coords.add(ld.getStartPoint().getCoordinate().toUTM());
        }
        return PolygonalUtils.area(coords.toArray(new UTMCoordinate[0]));
    }

    public double perimeter() {
        if (!isClosed()) {
            throw new InvalidPolygonalException(String.format("%s: O perímetro da área não está fechado.", getName()));
        }
        double sum = 0;
        for (LineDivision ld : getLineDivisions()) {
            sum += ld.distance();
        }
        return sum;
    }

    private void recalculateCenterPoint() {
        if (!isClosed()) {
            centroidPoint.setLocation(0, 0);
            return;
        }

        ArrayList<UTMCoordinate> coords = new ArrayList<UTMCoordinate>();
        for (LineDivision ld : getLineDivisions()) {
            coords.add(ld.getStartPoint().getCoordinate().toUTM());
        }
        coords.add(getLineDivisions().getFirst().getStartPoint().getCoordinate().toUTM());
        centroidPoint.setLocation(PolygonalUtils.centroid(coords.toArray(new UTMCoordinate[0])));
    }

    public boolean addElement(GeodesicPoint endPoint) {
        if (isClosed()) {
            return false;
        }

        if (delegate.isEmpty() && tempPoint == null) {
            tempPoint = endPoint;
            return false;
        }

        GeodesicPoint startPoint = (delegate.isEmpty()) ? tempPoint : delegate.getLast().getEndPoint();
        LineDivision ld = LineDivision.getInstance(startPoint, endPoint);
        return addElement(ld);
    }

    public boolean addElement(LineDivision lineDivision) {
        if (lineDivision.getStartPoint() == null || lineDivision.getEndPoint() == null || lineDivision.getStartPoint() == lineDivision.getEndPoint()) {
            return false;
        }

        if (!delegate.isEmpty() && !lineDivision.getStartPoint().equals(delegate.getLast().getEndPoint())) {
            throw new IllegalArgumentException("Falha ao adicionar confrontante no perímetro, ponto inicial do confrontante deve ser igual ao ponto final do perímetro marcado no momento");
        }

        if (delegate.contains(lineDivision) || isClosed()) {
            return false;
        }

        LineDivision ld = createOrGetLD(lineDivision.getStartPoint(), lineDivision.getEndPoint());
        delegate.add(ld);

        DataManagement.getMainPolygonal().addToVisualObjects(ld);
        visualObjects.add(ld);
        if (!isMain()) {
            visualObjects.add(ld.getStartPoint());
            visualObjects.add(ld.getEndPoint());
        }

        if (ld.getEndPoint().equals(delegate.getFirst().getStartPoint())) {
            polygonalWasClosed();
        }
        return true;
    }

    public void addAll(List<GeodesicPoint> points) {
        for (GeodesicPoint point : points) {
            addElement(point);
        }
    }

    public boolean forceClose() {
        if (!isClosed() && delegate.size() >= 2) {
            GeodesicPoint startPoint = delegate.getLast().getEndPoint();
            GeodesicPoint endPoint = delegate.getFirst().getStartPoint();
            LineDivision ld = LineDivision.getInstance(startPoint, endPoint);
            if (!delegate.contains(ld)) {
                delegate.add(ld);
                visualObjects.add(ld);

                polygonalWasClosed();
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        if (delegate.isEmpty()) {
            return true;
        } else {
            eraseTempPoint();
            return false;
        }
    }

    public boolean contains(LineDivision pointBorder) {
        return delegate.contains(pointBorder);
    }

    public boolean containsIgnoreWay(LineDivision pointBorder) {
        for (LineDivision ld : delegate) {
            if (ld.equalsIgnoreWay(pointBorder)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(GeodesicPoint point) {
        for (LineDivision ld : delegate) {
            if (ld.getStartPoint().equals(point) || ld.getEndPoint().equals(point)) {
                return true;
            }
        }
        return false;
    }

    /**
     * calculate the index of point
     * @param gpoint
     * @return index of GeodesicPoint based on start point
     */
    public int indexOf(GeodesicPoint gpoint) {
        for (int i = 0; i < delegate.size(); i++) {
            LineDivision ld = delegate.get(i);
            if (ld.getStartPoint().equals(gpoint)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isBorderingFilled() {
        if (!isClosed()) {
            return false;
        }

        boolean flag = false;
        for (LineDivision ld : delegate) {
            if (ld.getType() == null || ld.getBorderName() == null/* || ld.getBorderName().isEmpty()*/) {
                flag = true;
                System.out.println(ld);
                break;
            }
        }
        return !flag;
    }

    public void clearAll() {
        setInitializedZoom(false);
        visualObjects.clear();
        resetPerimeter();
        setMetadata(new PolygonalMetadata());
    }

    public void resetPerimeter() {
        eraseTempPoint();
        delegate.clear();
    }

    public LinkedList<LineDivision> getLineDivisions() {
        if (isClosed()) {
            defineFirstElement();
        }
        return delegate;
    }

    public final void defineFirstElement() {
        if (delegate.isEmpty()) {
            return;
        }

        int firstElementIndex = findFirstElementIndex(delegate);
        LineDivision firstElement = delegate.get(firstElementIndex);

        if (firstElementIndex > 0) {
            int size = delegate.size();

            boolean rotateToLeft;
            if (firstElementIndex < size / 2) {
                rotateToLeft = true;
            } else {
                rotateToLeft = false;
            }

            int count = 0;
            while (!(delegate.getFirst().equals(firstElement))) {
                if (++count > delegate.size()) {
                    throw new RuntimeException("Ocorreu um erro na ordenacao do primeiro ponto da poligonal");
                }

                if (rotateToLeft) {
                    rotateToLeft();
                } else {
                    rotateToRight();
                }
            }
        }
    }

    private void rotateToLeft() {
        if (!delegate.isEmpty()) {
            delegate.addLast(delegate.removeFirst());
        }
    }

    private void rotateToRight() {
        if (!delegate.isEmpty()) {
            delegate.addFirst(delegate.removeLast());
        }
    }

    private int findFirstElementIndex(LinkedList<LineDivision> pointBorders) {
        LineDivision maxNorthBorder = null;
        int count = 0;
        int index = -1;
        for (LineDivision pb : pointBorders) {
            if (maxNorthBorder == null && pb.getStartPoint().getType() == GeodesicPointType.M) {
                maxNorthBorder = pb;
                index = count;
                continue;
            }

            if (maxNorthBorder == null) {
                continue;
            }

            double pnorth = pb.getStartPoint().getCoordinate().toUTM().getNorth();
            double mnorth = maxNorthBorder.getStartPoint().getCoordinate().toUTM().getNorth();

            count++;
            if (pnorth > mnorth && pb.getStartPoint().getType() == GeodesicPointType.M) {
                maxNorthBorder = pb;
                index = count;
            }
        }
        return index;
    }

    public boolean isClockwise() throws PolygonalException {
        if (!isClosed()) {
            throw new PolygonalException("A poligonal não está fechada");
        }
        LineDivision first = delegate.getFirst();
        LineDivision last = delegate.getLast();

        double firstAzimuth = first.azimuth().toDegreeDecimal();
        double lastAzimuth = last.azimuth().toDegreeDecimal();

        return (firstAzimuth >= 90 && firstAzimuth <= 180) || (lastAzimuth >= 0 && lastAzimuth <= 90);
    }

    public void revertDirection() throws PolygonalException {
        if (!isClosed()) {
            throw new PolygonalException("A poligonal não está fechada");
        }

        LinkedList<LineDivision> newList = new LinkedList<LineDivision>();
        for (Iterator<LineDivision> it = delegate.descendingIterator(); it.hasNext();) {
            LineDivision ld = it.next();
            newList.add(LineDivision.getInstance(ld.getEndPoint(), ld.getStartPoint()));
        }
        delegate.clear();
        this.delegate = newList;
    }

    private LineDivision createOrGetLD(GeodesicPoint startpoint, GeodesicPoint endpoint) {
        for (VisualObject vo : visualObjects) {
            if (vo instanceof LineDivision) {
                LineDivision ld = ((LineDivision) vo);
                if (ld.getStartPoint().equals(startpoint) && ld.getEndPoint().equals(endpoint)) {
                    return ld;
                }
            }
        }

        return LineDivision.getInstance(startpoint, endpoint);
    }

    public void polygonalWasClosed() {
        recalculateCenterPoint();
        refresh();
    }

    @Override
    public void refresh() {
        for (VisualObject vo : visualObjects) {
            vo.refresh();
        }
    }

    public boolean isMain() {
        return false;
    }

    public boolean isClosed() {
        if (delegate.size() < 3) {
            return false;
        }
        return delegate.getFirst().getStartPoint().equals(delegate.getLast().getEndPoint());
    }

    public GeodesicPoint getTempPoint() {
        return tempPoint;
    }

    private void eraseTempPoint() {
        tempPoint = null;
    }

    public VisualObject[] getVisualObjects() {
        return visualObjects.toArray(new VisualObject[0]);
    }

    public boolean addToVisualObjects(VisualObject vo) {
        return visualObjects.add(vo);
    }

    public boolean removeFromVisualObjects(VisualObject vo) {
        return visualObjects.remove(vo);
    }

    public Set<Integer> getVisualObjectsOids() {
        Set<Integer> set = new HashSet<Integer>();
        for (VisualObject vo : getVisualObjects()) {
            set.add(vo.getOid());
        }
        return set;
    }

    public GeodesicPoint referencePoint() {
        GeodesicPoint max = null;
        for (LineDivision ld : getLineDivisions()) {
            GeodesicPoint startPoint = ld.getStartPoint();
            GeodesicPoint endPoint = ld.getEndPoint();

            if (startPoint.getType() != GeodesicPointType.M || endPoint.getType() != GeodesicPointType.M) {
                continue;
            }

            if (max == null) {
                max = startPoint;
            }

            if (max.getY() < startPoint.getY() && startPoint.getType() == GeodesicPointType.M) {
                max = startPoint;
            }

            if (max.getY() < endPoint.getY() && endPoint.getType() == GeodesicPointType.M) {
                max = endPoint;
            }
        }
        return max;
    }

    public Point2D getCentroidPoint() {
        return centroidPoint;
    }

    public PolygonalMetadata getMetadata() {
        if (this.metadata == null) {
            setMetadata(new PolygonalMetadata());
        }
        return metadata;
    }

    public void setMetadata(PolygonalMetadata metadata) {
        PolygonalMetadata oldMetadata = this.metadata;
        this.metadata = metadata;
        propertyChangeSupport.firePropertyChange(PROP_METADATA, oldMetadata, metadata);
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        String oldName = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);
    }

    @Override
    public String toString() {
        return getName();
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public List<GeodesicPoint> toListGeoPoints() {
        auxlistToTransform.clear();
        for (Iterator<LineDivision> it = delegate.iterator(); it.hasNext();) {
            LineDivision next = it.next();
            auxlistToTransform.add(next.getStartPoint());
        }
        return auxlistToTransform;
    }
    private List<GeodesicPoint> auxlistToTransform = new ArrayList<GeodesicPoint>();

    @Override
    public boolean selection(int x, int y, Projector projector) {
        return false;
    }

    @Override
    public boolean selectionFull(int x1, int y1, int x2, int y2, Projector projector) {
        return false;
    }

    @Override
    public boolean selectionPartial(int x1, int y1, int x2, int y2, Projector projector) {
        return false;
    }

    @Override
    public String getVisualObjectName() {
        return "polygonal";
    }

    @Override
    public void write(PrintStream stream) throws IOException {
    }

    public boolean isInitializedZoom() {
        return initializedZoom;
    }

    public void setInitializedZoom(boolean initializedZoom) {
        this.initializedZoom = initializedZoom;
    }

    @Override
    public void writeToDxf(PrintStream stream) {
        Layer layer = LayerController.createOrGet(getName());

        stream.println("0");
        stream.println("POLYLINE");
        stream.println("8");
        stream.println(layer);
        stream.println("10");
        stream.println("0");
        stream.println("20");
        stream.println("0");
        stream.println("30");
        stream.println("0");
        stream.println("70");
        stream.println("1");
        stream.println("62");
        stream.println("7");
        stream.println("66");
        stream.println("1");
        for (LineDivision ld : delegate) {
            stream.println("0");
            stream.println("VERTEX");
            stream.println("8");
            stream.println(layer);
            stream.println("62");
            stream.println("7");
            stream.println("10");
            stream.println(ld.getX());
            stream.println("20");
            stream.println(ld.getY());
        }
        stream.println("0");
        stream.println("SEQEND");
    }

    @Override
    public int compareTo(Polygonal o) {
        try {
            Integer n0 = GeodesicPoint.getNumericValue(name);
            Integer n1 = GeodesicPoint.getNumericValue(o.getName());

            if (n0 != 0 && n1 == 0) {
                return n0.compareTo(n1);
            }
        } catch (Exception ex) {
        }
        return getName().compareTo(o.getName());
    }

    @Override
    public double[] getArrayVertex() {
        Polyline polyline = new Polyline();
        LinkedList<LineDivision> lineDivisions = getLineDivisions();
        for (LineDivision ld : lineDivisions) {
            UTMCoordinate coord = ld.getStartPoint().getCoordinate().toUTM();
            polyline.addVertex(coord.getEast(), coord.getNorth());
        }

        if (!lineDivisions.isEmpty()) {
            UTMCoordinate coord = lineDivisions.getLast().getEndPoint().getCoordinate().toUTM();
            polyline.addVertex(coord.getEast(), coord.getNorth());
        }

        return polyline.getArrayVertex();
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public void setLocation(double x, double y) {
    }

    public boolean isInside(double x, double y) {
        List<GeodesicPoint> toListGeoPoints = toListGeoPoints();
        double[] xs = new double[toListGeoPoints.size()];
        double[] ys = new double[toListGeoPoints.size()];
        for (int i = 0; i < toListGeoPoints.size(); i++) {
            GeodesicPoint gpoint = toListGeoPoints.get(i);
            xs[i] = gpoint.getX();
            ys[i] = gpoint.getY();
        }
        return isInsidePolygonal(x, y, xs, ys);
    }

    public static boolean isInsidePolygonal(double x, double y, double[] polygonXs, double[] polygonYs) {
        if (polygonXs.length != polygonYs.length) {
            throw new IllegalArgumentException("Number of x's is different from y's");
        }

        if (polygonXs.length < 3 || polygonYs.length < 3) {
            throw new IllegalArgumentException("Argument get is not a polygon");
        }

        Line2D referenceLine = new Line2D.Double(x, y, Double.MAX_VALUE, y);
        int count = 0;
        double x1, y1, x2, y2;
        for (int i = 0; i < polygonXs.length; i++) {
            x1 = polygonXs[i];
            y1 = polygonYs[i];

            if (i == polygonXs.length - 1) { //last one
                x2 = polygonXs[0];
                y2 = polygonYs[0];
            } else {
                x2 = polygonXs[i + 1];
                y2 = polygonYs[i + 1];
            }

            if (referenceLine.intersectsLine(x1, y1, x2, y2)) {
                count++;
            }
        }

        return count % 2 != 0;
    }

    public void autoFillBordersName() {
        if (isClosed()) {
            for (LineDivision ld : getLineDivisions()) {
                LineDivision reverseLD = LineDivision.getInstance(ld.getEndPoint(), ld.getStartPoint());
                if (reverseLD.getType() == null || reverseLD.getType() == LineDivisionType.LA1) {
                    if (reverseLD.getBorderName() == null || reverseLD.getBorderName().isEmpty()) {
                        reverseLD.setBorderName(getName());
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Polygonal other = (Polygonal) obj;
        if (this.centroidPoint != other.centroidPoint && (this.centroidPoint == null || !this.centroidPoint.equals(other.centroidPoint))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.centroidPoint != null ? this.centroidPoint.hashCode() : 0);
        return hash;
    }
}
