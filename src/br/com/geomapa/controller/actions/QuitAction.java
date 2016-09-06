/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.main.Main;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 *
 * @author paulocanedo
 */
public class QuitAction extends AbstractAction {

    public QuitAction() {
        super("Sair");
        putValue(MNEMONIC_KEY, (int) 'R');
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((int) 'Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Main.getInstance().dispose();
    }
}
