/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.main;

import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.paulocanedo.pc9.PSimpleBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.Collection;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author paulocanedo
 */
public class PropertiesEditor {

    private JPanel component = new JPanel();
    private PropertySheet propertySheet = new PropertySheet();

    public PropertiesEditor() {
        component.setLayout(new BorderLayout());
        component.setBorder(new PSimpleBorder(new Insets(5, 8, 5, 8), Color.DARK_GRAY, PSimpleBorder.PaintInPosition.LEFT));

        propertySheet.setObject(new Object());

        component.add(propertySheet, BorderLayout.CENTER);
        JLabel label = new JLabel("Propriedades", JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
        label.setPreferredSize(new Dimension(280, 25));
        component.add(label, BorderLayout.BEFORE_FIRST_LINE);
        
        component.setVisible(false);
    }

    public void setObject(Collection<VisualObject> objects) {
        if (objects.isEmpty()) {
            propertySheet.setObject(new Object[0]);
            propertySheet.revalidate();
            propertySheet.repaint();
            return;
        }
        propertySheet.setObject(objects.iterator().next());
        propertySheet.revalidate();
    }

    public JPanel getComponent() {
        return component;
    }
}
