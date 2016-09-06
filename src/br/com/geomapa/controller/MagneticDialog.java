/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller;

import br.com.geomapa.main.Main;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 *
 * @author paulocanedo
 */
public class MagneticDialog {

    private static Dialog instance;

    private MagneticDialog() {
    }

    public static Dialog getDialog() {
        if (instance == null) {
            instance = new Dialog(Main.getInstance());
            instance.add(new MagneticPanel(instance));
        }
        return instance;
    }
    
    public static class MagneticDialogOpenAction extends AbstractAction {

        public MagneticDialogOpenAction() {
            URL resource = CadCommandController.class.getResource("/br/com/geomapa/resources/icons/r16/" + "magnet"  + ".png");
            
            putValue(SMALL_ICON, new ImageIcon(resource));
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int w = 620, h = 360;
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            Dialog adialog = getDialog();
            
            adialog.setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);
            adialog.setVisible(true);
        }
        
    }
}
