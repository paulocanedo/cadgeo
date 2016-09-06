/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.controller.CommandController;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 *
 * @author paulocanedo
 */
public class RedoAction extends AbstractAction {

    public RedoAction() {
        super("Refazer");
        putValue(MNEMONIC_KEY, (int) 'f');
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((int) 'Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CommandController.redoLastCommand();
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
    
}
