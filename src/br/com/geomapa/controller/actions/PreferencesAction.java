/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.ui.theme.IconManagerFactory;
import br.com.geomapa.util.UserInterfaceUtil;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 *
 * @author paulocanedo
 */
public class PreferencesAction extends AbstractAction {

    public PreferencesAction() {
        super("Preferências");
        putValue(SHORT_DESCRIPTION, "Mostra painel de preferências");
        putValue(MNEMONIC_KEY, (int) 'P');
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((int) 'P', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        putValue(LARGE_ICON_KEY, IconManagerFactory.getPreferences());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UserInterfaceUtil.showDialog(UserInterfaceUtil.OPTIONS);
    }
}
