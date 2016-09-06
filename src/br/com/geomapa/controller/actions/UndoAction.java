/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.controller.CadCommandController;
import br.com.geomapa.controller.CommandController;
import br.com.geomapa.controller.command.cad.impl.UndoCommand;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 *
 * @author paulocanedo
 */
public class UndoAction extends AbstractAction {

    public UndoAction() {
        super("Desfazer");
        putValue(MNEMONIC_KEY, (int) 'D');
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((int) 'Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UndoCommand undoCommand = new UndoCommand();
        undoCommand.execute();
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}
