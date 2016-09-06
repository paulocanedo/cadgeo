/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JWindow;

/**
 *
 * @author paulocanedo
 */
public class PromptCancelAction implements ActionListener {

    private JWindow dialog;
    private Prompt prompt;

    public PromptCancelAction(JWindow window, Prompt prompt) {
        this.dialog = window;
        this.prompt = prompt;
    }

    public void actionPerformed(ActionEvent e) {
        prompt.cancel();

        dialog.dispose();
    }

}
