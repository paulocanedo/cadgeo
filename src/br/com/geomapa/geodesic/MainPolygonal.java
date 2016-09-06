/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.DefaultListModel;

/**
 *
 * @author paulocanedo
 */
public class MainPolygonal extends Polygonal {

    private PortionsListModel listModel = new PortionsListModel();
    private static final String mainName = "Planta Geral";
    private static final TreeMap<String, Polygonal> polygonals = new TreeMap<String, Polygonal>();

    {
        listModel.addElement(this);
    }

    public MainPolygonal(List<LineDivision> list) {
        super(mainName, list);
    }

    public MainPolygonal() {
        super(mainName);
    }

    @Override
    public boolean isMain() {
        return true;
    }

    public DefaultListModel getListModel() {
        return listModel;
    }

    public void add(Polygonal polygonal) {
        if (polygonal.getName() == null) {
            throw new NullPointerException("O nome da parcela não pode ser null");
        }

        polygonals.put(polygonal.getName().toUpperCase(), polygonal);
//        listModel.add(polygonal);

        if (polygonal.getMetadata() == null) {
            polygonal.setMetadata(new PolygonalMetadata());
        }
        polygonal.setLayer(LayerController.createOrGet(polygonal.getName(), true));
    }

    public Polygonal createOrGetPolygonal(String name) {
        if (name.equalsIgnoreCase(mainName)) {
            return this;
        }

        Polygonal polygonal = polygonals.get(name);
        if (polygonal == null) {
            polygonals.put(name.toUpperCase(), polygonal = new Polygonal(name));
//            listModel.add(polygonal);

            polygonal.setMetadata(new PolygonalMetadata());
            polygonal.setLayer(LayerController.createOrGet(name, true));
        }
        return polygonal;
    }

    public Polygonal newPolygonal() {
        return createOrGetPolygonal(String.format("Parcela %s", polygonals.size() + 1));
    }

    public boolean containsPolygonalName(String name) {
        return name.equalsIgnoreCase(mainName) || polygonals.containsKey(name.toUpperCase());
    }

    public void removePolygonal(String polygonal) {
        Polygonal removed = polygonals.remove(polygonal);
//        listModel.remove(removed);
    }

    public void clearAllSecondaryPolygonals() {
        polygonals.clear();
        listModel.clear();
//        listModel.add(this);
    }

    public int getChildrenSize() {
        return polygonals.size();
    }

    public Iterator<Polygonal> childrenIterator() {
        return polygonals.values().iterator();
    }

    public Collection<Polygonal> values() {
        return polygonals.values();
    }

    public Set<String> polygonalNames() {
        return polygonals.keySet();
    }

    public List<String> polygonalNamesSorted() {
        Set<String> keySet = polygonals.keySet();
        ArrayList<String> list = new ArrayList<String>(keySet);
        Collections.sort(list);
        return list;
    }

    public boolean containsInAnyChildrenIgnoreWay(LineDivision lineDivision) {
        for (Polygonal p : polygonals.values()) {
            if (p.containsIgnoreWay(lineDivision)) {
                return true;
            }
        }
        return false;
    }

    public static Polygonal findPolygonal(double x, double y) {
        for (Polygonal p : polygonals.values()) {
            try {
                if (p.isInside(x, y)) {
                    return p;
                }
            } catch (Exception ex) {
            }
        }

        return null;
    }

    public static List<Polygonal> findPolygonals(double x, double y) {
        List<Polygonal> list = new ArrayList<Polygonal>();
        for (Polygonal p : polygonals.values()) {
            try {
                if (p.isInside(x, y)) {
                    list.add(p);
                }
            } catch (Exception ex) {
            }
        }
        return list;
    }

    private class PortionsListModel extends DefaultListModel {

        @Override
        public Object getElementAt(int i) {
            if (i == 0) {
                return MainPolygonal.this;
            }
            List<String> polygonalNamesSorted = polygonalNamesSorted();
            String pname = polygonalNamesSorted.get(i - 1);

            return polygonals.get(pname);
        }

        @Override
        public int getSize() {
            return getChildrenSize() + 1;
        }
    }

    private class PortionComparator implements Comparator<Polygonal> {

        @Override
        public int compare(Polygonal p1, Polygonal p2) {
            PolygonalMetadata metadata1 = p1.getMetadata();
            PolygonalMetadata metadata2 = p2.getMetadata();
            if (metadata1.getNome() == null || metadata2.getNome() == null) {
                System.out.println(p1 + " _ " + p2);
                return -1;
            }

            return metadata1.getNome().compareTo(metadata2.getNome());
        }
    }
}
