/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * VisualTextDialog.java
 *
 * Created on 19/09/2011, 10:27:36
 */
package br.com.geomapa.graphic.cad.text;

import br.com.geomapa.geodesic.VariableNamesPreset;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.main.Main;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.util.NumberFloatUtils;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author paulocanedo
 */
public class VisualTextDialog extends javax.swing.JDialog {

    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;

    /** Creates new form VisualTextDialog */
    public VisualTextDialog(final GLTopographicPanel displayPanel) {
        super(Main.getInstance(), true);
        initComponents();

        VariableNamesPreset[] items = VariableNamesPreset.values();
        Arrays.sort(items);

        varList.setModel(new DefaultComboBoxModel(items));

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        String confirmName = "confirm";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), confirmName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
        actionMap.put(confirmName, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doClose(RET_OK);
            }
        });

        RefreshAdapter refreshAdapter = new RefreshAdapter(displayPanel);
        textField.addKeyListener(refreshAdapter);
        rotationTextField.addKeyListener(refreshAdapter);
    }

    /** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
    public int getReturnStatus() {
        return returnStatus;
    }

    public String getText() {
        return textField.getText();
    }

    public void setPromptText(String text) {
        textField.setText(text);
        textField.selectAll();
    }

    public void clear() {
        textField.setText("");
    }

    public void setRotationAngle(double rotation) {
        rotationTextField.setText("" + rotation);
    }

    public double getRotationAngle() {
        try {
            return NumberFloatUtils.parseDouble(rotationTextField.getText());
        } catch (NumberFormatException ex) {
            return Double.MAX_VALUE;
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (textField.getText().isEmpty()) {
            textField.setText("Informe o texto aqui");
            textField.selectAll();
        }

        super.setVisible(b);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        varList = new javax.swing.JList();
        textField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        rotationTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 35), new java.awt.Dimension(20, 35), new java.awt.Dimension(20, 35));

        setTitle("Inserir texto");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        varList.setDragEnabled(true);
        varList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                varListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(varList);

        jLabel1.setText("Ângulo de rotação do texto");

        rotationTextField.setText("0");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, textField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(rotationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 169, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(textField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rotationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        jPanel2.add(filler1);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel2.add(okButton);
        getRootPane().setDefaultButton(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel2.add(cancelButton);
        jPanel2.add(filler2);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-694)/2, (screenSize.height-418)/2, 694, 418);
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void varListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_varListMouseClicked
        if (evt.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(evt)) {
            Object selectedValue = varList.getSelectedValue();
            if (selectedValue != null) {
                int caretPosition = textField.getCaretPosition();

                try {
                    String before = textField.getText(0, caretPosition);
                    String after = textField.getText(caretPosition, textField.getText().length() - caretPosition);

                    textField.setText(before + selectedValue.toString() + after);
                } catch (Exception ex) {
                }
            }
        }
    }//GEN-LAST:event_varListMouseClicked

    private void doClose(int retStatus) {
        if (retStatus == RET_OK && getRotationAngle() == Double.MAX_VALUE) {
            rotationTextField.grabFocus();
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField rotationTextField;
    private javax.swing.JTextField textField;
    private javax.swing.JList varList;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;

    public void setVisualTextRefer(VisualText vtext) {
        this.vtextRefer = vtext;
    }
    private VisualText vtextRefer;

    private class RefreshAdapter extends KeyAdapter {

        GLTopographicPanel displayPanel;

        public RefreshAdapter(GLTopographicPanel displayPanel) {
            this.displayPanel = displayPanel;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (vtextRefer != null) {
                vtextRefer.setText(textField.getText());
                double rotationAngle = getRotationAngle();
                if (rotationAngle != Double.MAX_VALUE) {
                    vtextRefer.setRotation(rotationAngle);
                }

                displayPanel.requestRepaint();
            }
        }
    }
}
