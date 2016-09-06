/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.GeodesicPointReference;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Bus;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 *
 * @author paulocanedo
 */
public class CopyToClipboardAction extends AbstractAction {

    public CopyToClipboardAction() {
        super("Copiar");
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift control C"));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            VisualObject[] selectedObjects = Bus.getDisplayPanel().getSelectedObjects();
            if (selectedObjects.length == 0) {
                return;
            }

            StringBuilder spoints = new StringBuilder();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(baos);
            for (VisualObject vo : selectedObjects) {
                if (vo instanceof GeodesicPoint) {
                    vo = new GeodesicPointReference((GeodesicPoint) vo);
                }
                vo.write(stream);
            }

            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            systemClipboard.setContents(new StringSelection(new String(baos.toByteArray()) + spoints.toString()), null);
            
            Bus.getDisplayPanel().clearSelection();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
