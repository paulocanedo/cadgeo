/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.main.Main;
import br.com.geomapa.ui.theme.IconManagerFactory;
import br.com.geomapa.util.UserInterfaceUtil;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author paulocanedo
 */
public class OpenProjectAction extends AbstractAction {

    public OpenProjectAction() {
        super("Abrir projeto");
        putValue(SHORT_DESCRIPTION, "Mostra painel com projetos para abrir");
        putValue(MNEMONIC_KEY, (int) 'A');
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((int) 'O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        putValue(LARGE_ICON_KEY, IconManagerFactory.getOpen());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Main.getInstance().getProjectInfo().isPersisted()) {
            int result = JOptionPane.showConfirmDialog(null, "Deseja salvar as alterações antes de abrir outro projeto?");

            if (result == JOptionPane.YES_OPTION) {
                try {
                    new SaveProjectAction().save();
                } catch (Throwable ex) {
                    JOptionPane.showMessageDialog(Main.getInstance(), ex.getMessage());
                    return;
                }
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        UserInterfaceUtil.showDialog(UserInterfaceUtil.OPEN_PROJECT);
    }
}
