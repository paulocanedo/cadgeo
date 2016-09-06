/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;

/**
 *
 * @author paulocanedo
 */
public class ManualAction extends AbstractAction {

    public ManualAction() {
        super("Manual Online");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("http://www.paulocanedo.com.br/geomapa"));
        } catch (Exception ex) {
            Logger.getLogger(ManualAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}