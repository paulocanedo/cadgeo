/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.controller.MenuController;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 *
 * @author paulocanedo
 */
public class MenuAction extends AbstractAction {

    private final JPopupMenu menu = new JPopupMenu();

    public MenuAction() {
        super("pcGeoCad");

//        URL url = getClass().getResource("/br/com/geomapa/resources/icons/menu.png");
//        putValue(LARGE_ICON_KEY, new ImageIcon(url));

        for (Object object : MenuController.getSingleMenuList()) {
            if (object instanceof Action) {
                menu.add((Action) object);
            } else if (object instanceof JMenuItem) {
                menu.add((JMenuItem) object);
            } else if (object instanceof JSeparator) {
                menu.add((JSeparator) object);
            } else {
                throw new IllegalArgumentException("Invalid menu component");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractButton src = (AbstractButton) e.getSource();

        menu.show(src, 0, src.getHeight());
    }
}
