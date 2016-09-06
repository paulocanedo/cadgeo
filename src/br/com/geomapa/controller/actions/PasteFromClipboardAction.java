/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.importer.pcgeocad.VisualObjectParser;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Bus;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 *
 * @author paulocanedo
 */
public class PasteFromClipboardAction extends AbstractAction {

    public PasteFromClipboardAction() {
        super("Colar");
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift control V"));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable contents = systemClipboard.getContents(null);
        if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String s = (String) contents.getTransferData(DataFlavor.stringFlavor);
                ByteArrayInputStream istream = new ByteArrayInputStream(s.getBytes());

                Collection<VisualObject> objs = VisualObjectParser.getInstance().parse(istream);
                for (VisualObject vo : objs) {
                    Bus.getDisplayPanel().addToVisualObjects(vo);
                }

                Bus.getDisplayPanel().requestRepaint();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }
}
