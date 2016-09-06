/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.project.ProjectUtils;
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
public class NewProjectAction extends AbstractAction {

    public NewProjectAction() {
        super("Novo Projeto");
        putValue(SHORT_DESCRIPTION, "Cria um novo projeto vazio");
        putValue(MNEMONIC_KEY, (int) 'N');
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((int) 'N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        putValue(LARGE_ICON_KEY, IconManagerFactory.getNew());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectUtils.unloadCurrentProject();
        
        UserInterfaceUtil.showDialog(UserInterfaceUtil.PROJECT_DATA);
    }
}
