/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.theme;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author paulocanedo
 */
public class DefaultIconManager implements IconManager {

    private Map<String, Icon> cache = new HashMap<String, Icon>();

    private Icon getIcon(String name) {
        Icon icon = cache.get(name);
        if (icon != null) {
            return icon;
        }

        URL resource = getClass().getResource("/br/com/geomapa/resources/icons/" + name + ".png");
        if (resource == null) {
            return null;
        }
        try {
            return new ImageIcon(ImageIO.read(resource));
        } catch (IOException ex) {
        }
        return null;
    }

    @Override
    public Icon getNew() {
        return getIcon("new");
    }
    
    @Override
    public Icon getOpen() {
        return getIcon("open");
    }

    @Override
    public Icon getSave() {
        return getIcon("save");
    }

    @Override
    public Icon getPreferences() {
        return getIcon("preferences");
    }
}
