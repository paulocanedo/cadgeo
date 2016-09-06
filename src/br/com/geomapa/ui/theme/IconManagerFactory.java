/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.theme;

import javax.swing.Icon;

/**
 *
 * @author paulocanedo
 */
public class IconManagerFactory {

    private static IconManager manager = new DefaultIconManager();

    private IconManagerFactory() {
    }

    public static IconManager getInstance() {
        return manager;
    }
    
    public static Icon getNew() {
        return manager.getNew();
    }

    public static Icon getOpen() {
        return manager.getOpen();
    }

    public static Icon getSave() {
        return manager.getSave();
    }

    public static Icon getPreferences() {
        return manager.getPreferences();
    }
}
