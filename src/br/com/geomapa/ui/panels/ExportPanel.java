/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ExportPanel.java
 *
 * Created on 06/12/2011, 16:24:28
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.geodesic.InvalidPolygonalException;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.util.UserInterfaceUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author paulocanedo
 */
public class ExportPanel extends javax.swing.JPanel {

    private boolean running = false;
    private boolean canceled = false;
    private static final String exportLabel = "Exportar";
    private static final String cancelLabel = "Cancelar";

    /** Creates new form ExportPanel */
    public ExportPanel() {
        initComponents();
    }

    private void started() {
        canceled = false;
        running = true;

        progressBar.setStringPainted(true);

        marcarTodosButton.setEnabled(false);
        marcarNenhumButton.setEnabled(false);

        memorialField.setEnabled(false);
        calculoAreaField.setEnabled(false);
        dadosCartograficosField.setEnabled(false);
        plantaDxfField.setEnabled(false);
        outputField.setText("");
        exportButton.setText(cancelLabel);
    }

    private void stopped() {
        running = false;

        progressBar.setStringPainted(false);

        marcarTodosButton.setEnabled(true);
        marcarNenhumButton.setEnabled(true);

        memorialField.setEnabled(true);
        calculoAreaField.setEnabled(true);
        dadosCartograficosField.setEnabled(true);
        plantaDxfField.setEnabled(true);
        exportButton.setText(exportLabel);

        JOptionPane.showMessageDialog(Main.getInstance(), canceled ? "Cancelado pelo usuário" : "Processo de exportação concluído, verifique o quadro de aviso para saber se houve algum problema.");
        progressBar.setValue(0);
    }

    private void export() throws IOException {
        started();

        try {
            if (portionList.getSelectedIndex() == -1) {
                throw new RuntimeException("Nenhuma parcela foi selecionada para exportar.");
            }

            if (!memorialField.isSelected() && !calculoAreaField.isSelected() && !dadosCartograficosField.isSelected() && !plantaDxfField.isSelected()) {
                throw new RuntimeException("Marque ao menos uma das caixas que deseja exportar.");
            }

            Object[] selectedValues = portionList.getSelectedValues();
            progressBar.setMaximum(selectedValues.length);
            for (int i = 0; i < selectedValues.length; i++) {
                progressBar.setValue(i + 1);

                Polygonal p = (Polygonal) selectedValues[i];
                if (canceled) {
                    return;
                }
                if (memorialField.isSelected()) {
                    export(UserInterfaceUtil.MEMORIAL, p);
                }

                if (canceled) {
                    return;
                }
                if (calculoAreaField.isSelected()) {
                    export(UserInterfaceUtil.CALCULATION_AREA, p);
                }

                if (canceled) {
                    return;
                }
                if (dadosCartograficosField.isSelected()) {
                    export(UserInterfaceUtil.CARTOGRAFIC_DATA, p);
                }

                if (canceled) {
                    return;
                }
                if (plantaDxfField.isSelected()) {
                    export(UserInterfaceUtil.TOPOGRAPHIC, p);
                }
            }
        } finally {
            stopped();
        }
    }

    private void export(String what, Polygonal p) throws IOException {
        GeodesicPanel gpanel = UserInterfaceUtil.getAsGeodesicPanel(what);
        try {
            progressBar.setString(String.format("Exportando parcela %s", p));
            gpanel.export(p);
        } catch (InvalidPolygonalException ex) {
            String text = outputField.getText();
            outputField.setText(text + p + "-> " + ex.getMessage() + "\n");
        } catch (Exception ex) {
            String text = outputField.getText();
            outputField.setText(text + p + "-> " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + "\n");
        }
    }

    private void askToCancel() {
        int result = JOptionPane.showConfirmDialog(Main.getInstance(), "Tem certeza que deseja cancelar a exportação?", "Pergunta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            canceled = true;
        }
    }

    private class ExporterThread implements Runnable {

        @Override
        public void run() {
            try {
                export();
            } catch (IOException ex) {
                Logger.getLogger(ExportPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Main.getInstance(), ex.getMessage());
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        portionList = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        memorialField = new javax.swing.JCheckBox();
        calculoAreaField = new javax.swing.JCheckBox();
        dadosCartograficosField = new javax.swing.JCheckBox();
        plantaDxfField = new javax.swing.JCheckBox();
        exportButton = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        outputField = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        marcarTodosButton = new javax.swing.JButton();
        marcarNenhumButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(270, 100));

        portionList.setModel(DataManagement.getMainPolygonal().getListModel());
        jScrollPane1.setViewportView(portionList);

        add(jScrollPane1, java.awt.BorderLayout.WEST);

        memorialField.setText("Memorial & Relatório Técnico");

        calculoAreaField.setText("Cálculo de Área");

        dadosCartograficosField.setText("Dados Cartográficos");

        plantaDxfField.setText("Planta em DXF");

        exportButton.setText("Exportar");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setText("Selecione o que deseja exportar:");

        jScrollPane2.setViewportView(outputField);

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel2.setText("Informações de exportação (erros e mensagens)");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(exportButton)
                .addContainerGap())
            .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(memorialField)
                            .add(calculoAreaField)
                            .add(dadosCartograficosField)
                            .add(plantaDxfField)))
                    .add(jLabel1))
                .addContainerGap(174, Short.MAX_VALUE))
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addContainerGap(85, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(memorialField)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(calculoAreaField)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dadosCartograficosField)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(plantaDxfField)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(exportButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        marcarTodosButton.setText("Marcar Todos");
        marcarTodosButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marcarTodosButtonActionPerformed(evt);
            }
        });
        jPanel3.add(marcarTodosButton);

        marcarNenhumButton.setText("Marcar Nenhum");
        marcarNenhumButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marcarNenhumButtonActionPerformed(evt);
            }
        });
        jPanel3.add(marcarNenhumButton);

        add(jPanel3, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void marcarTodosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marcarTodosButtonActionPerformed
        if (portionList.getModel().getSize() > 0) {
            portionList.setSelectionInterval(0, portionList.getModel().getSize() - 1);
        }
    }//GEN-LAST:event_marcarTodosButtonActionPerformed

    private void marcarNenhumButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marcarNenhumButtonActionPerformed
        portionList.clearSelection();
    }//GEN-LAST:event_marcarNenhumButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        if (running) {
            askToCancel();
        } else {
            new Thread(new ExporterThread()).start();
        }
    }//GEN-LAST:event_exportButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox calculoAreaField;
    private javax.swing.JCheckBox dadosCartograficosField;
    private javax.swing.JButton exportButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton marcarNenhumButton;
    private javax.swing.JButton marcarTodosButton;
    private javax.swing.JCheckBox memorialField;
    private javax.swing.JTextPane outputField;
    private javax.swing.JCheckBox plantaDxfField;
    private javax.swing.JList portionList;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
