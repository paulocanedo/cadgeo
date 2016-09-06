/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller;

import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.graphic.cad.osnap.MagneticFinder;
import br.com.geomapa.graphic.cad.osnap.MagneticPoint;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.Point;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author paulocanedo
 */
public class MagneticController {

    public enum MagneticType {

        GEODESIC_POINT,
        END_POINT,
        MID_POINT,
        INTERSECTION_POINT,
        ACTIVATED
    }
    private static Map<MagneticType, Boolean> tableValues = new EnumMap<MagneticType, Boolean>(MagneticType.class);
    private static Map<MagneticType, Boolean> auxTableValues = new EnumMap<MagneticType, Boolean>(MagneticType.class);

    public static boolean isGeodesicPointActive() {
        return isActivated() && tableValues.get(MagneticType.GEODESIC_POINT);
    }

    public static boolean isEndPointActive() {
        return isActivated() && tableValues.get(MagneticType.END_POINT);
    }

    public static boolean isMidPointActive() {
        return isActivated() && tableValues.get(MagneticType.MID_POINT);
    }

    public static boolean isIntersectionPointActive() {
        return isActivated() && tableValues.get(MagneticType.INTERSECTION_POINT);
    }

    public static boolean isActivated() {
        return tableValues.get(MagneticType.ACTIVATED);
    }

    public static void setGeodesicPointActive(boolean flag) {
        tableValues.put(MagneticType.GEODESIC_POINT, flag);
    }

    public static void setEndPointActive(boolean flag) {
        tableValues.put(MagneticType.END_POINT, flag);
    }

    public static void setMidPointActive(boolean flag) {
        tableValues.put(MagneticType.MID_POINT, flag);
    }

    public static void setIntersectionPointActive(boolean flag) {
        tableValues.put(MagneticType.INTERSECTION_POINT, flag);
    }

    public static void setActivated(boolean flag) {
        tableValues.put(MagneticType.ACTIVATED, flag);
    }

    public static void setValuesForAll(boolean flag) {
        setGeodesicPointActive(flag);
        setEndPointActive(flag);
        setMidPointActive(flag);
        setIntersectionPointActive(flag);
        setActivated(flag);
    }

    public static void holdValues() {
        for (MagneticType type : tableValues.keySet()) {
            auxTableValues.put(type, tableValues.get(type));
        }
    }

    public static void restoreValues() {
        for (MagneticType type : auxTableValues.keySet()) {
            tableValues.put(type, auxTableValues.get(type));
        }
    }

    public static MagneticPoint find(GLTopographicPanel displayPanel, Point point) {
        CadCommand command = CadCommandController.getCommand();
        if(command == null) {
            return null;
        }
        if(!command.canUseMagnetic()) {
            return null;
        }
        
        MagneticFinder finder = MagneticFinder.getInstance(displayPanel);

        MagneticPoint mpoint = null;
        boolean flag = false;
        if(!flag && isGeodesicPointActive()) {
            mpoint = finder.getGeoPoint(point);
            flag = mpoint != null;
        } 
        
        if (!flag && isIntersectionPointActive()) {
            mpoint = finder.getIntersectionPoint(point);
            flag = mpoint != null;
        }
        
        if (!flag && isEndPointActive()) {
            mpoint = finder.getEndPoint(point);
            flag = mpoint != null;
        }
        return mpoint;
    }
    
    static {
        setValuesForAll(true);
    }
}
