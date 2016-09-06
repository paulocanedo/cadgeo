/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.main.Main;
import br.com.geomapa.ui.panels.AboutPanel;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;

/**
 *
 * @author paulocanedo
 */
public class AboutAction extends AbstractAction {

    public AboutAction() {
        super("Sobre");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int w = 620, h = 360;
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Dialog adialog = getAboutDialog();

        adialog.setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);
        adialog.setVisible(true);
    }
    
    private Dialog getAboutDialog() {
        if (aboutDialog == null) {
            this.aboutDialog = new JDialog(Main.getInstance());
            aboutDialog.add(new AboutPanel(aboutDialog));
            aboutDialog.setResizable(false);
        }
        return aboutDialog;
    }
    
    private JDialog aboutDialog;
}