/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.main;

import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.util.HashMap;

/**
 *
 * @author paulocanedo
 */
public final class Bus {

    private final static HashMap<String, Object> delegate = new HashMap<String, Object>();
    private static Polygonal currentPolygonal;
    private final static Bus instance = new Bus();
    public final static String PROPERTIES_EDITOR = "propertiesEditor";

    public static Bus getInstance() {
        return instance;
    }

    private Bus() {
    }

    public static Object get(String key) {
        return delegate.get(key);
    }

    public static void put(String key, Object object) {
        if (delegate.get(key) == null) {
            delegate.put(key, object);
        } else {
            System.out.println(object.getClass().getName());
        }
    }

    public static float getScale() {
        return getCurrentPolygonal().getMetadata().getEscala();
    }

    public static GLTopographicPanel getDisplayPanel() {
        return (GLTopographicPanel) Bus.get("gl_panel");
    }

    public static ProjectMetadata getCurrentProjectMetadata() {
        return Main.getInstance().getProjectInfo();
    }

    public static Polygonal getCurrentPolygonal() {
        if (currentPolygonal == null) {
            return getDisplayPanel().getPolygonal();
        }
        return currentPolygonal;
    }

    public static void setCurrentPolygonal(Polygonal polygonal) {
        currentPolygonal = polygonal;
    }
}
