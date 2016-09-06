/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.util.UserInterfaceUtil;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author paulocanedo
 */
public class ManagerProjectAction extends AbstractAction {

    public ManagerProjectAction() {
        putValue(NAME, "Gerenciar Projeto");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UserInterfaceUtil.showDialog(UserInterfaceUtil.PROJECT_MANAGER_PANEL);
    }
}
