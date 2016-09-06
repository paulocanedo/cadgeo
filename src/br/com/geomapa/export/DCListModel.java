/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.export;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.importer.Extractor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;

/**
 *
 * @author paulocanedo
 */
public class DCListModel extends DefaultListModel {

    private static final String sDigitPattern = "(\\d)+";
    private static final Pattern digitPattern = Pattern.compile(sDigitPattern);
    private List<GeodesicPoint> list = new ArrayList<GeodesicPoint>();

    public DCListModel() {
    }

    public void addElement(GeodesicPoint point) {
        super.addElement(point);
        int oldSize = list.size();
        list.add(point);

        fireIntervalAdded(this, oldSize, list.size());
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public GeodesicPoint getElementAt(int index) {
        return list.get(index);
    }

    public GeodesicPoint getElement(GeodesicPoint samplePoint) {
        int index = indexOfList(samplePoint);
        if (index < 0) {
            return null;
        }
        return list.get(index);
    }

    public int indexOfList(GeodesicPoint gp) {
        if (gp == null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                GeodesicPoint gpCompare = list.get(i);

                String s1 = Extractor.extractNumber(gpCompare.getName());
                String s2 = Extractor.extractNumber(gp.getName());
                if(s1 == null || s2 == null) {
                    continue;
                }
                
                if(s1.equals(s2)) {
                    return i;
                }
//                if(gpCompare.getName().endsWith(gp.getName())) {
//                    return i;
//                }
            }
        }
        return -1;
    }
}
