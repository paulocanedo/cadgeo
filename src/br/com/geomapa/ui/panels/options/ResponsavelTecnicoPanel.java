/** To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ResponsavelTecnicoPanel.java
 *
 * Created on 16/05/2011, 15:06:13
 */
package br.com.geomapa.ui.panels.options;

import br.com.geomapa.main.Main;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 *
 * @author paulocanedo
 */
public class ResponsavelTecnicoPanel extends javax.swing.JPanel {

    private Preferences pref = OptionsPanel.pref;
    private NewRTPanel newRTPanel = NewRTPanel.INSTANCE;

    /** Creates new form ResponsavelTecnicoPanel */
    public ResponsavelTecnicoPanel() {
        initComponents();

        updateList();
    }

    private void updateList() {
        jList1.setModel(new DefaultComboBoxModel(ResponsavelTecnico.allRTs().toArray()));
    }

    private boolean question() {
        int result = JOptionPane.showConfirmDialog(Main.getInstance(), newRTPanel, "Novo Responsável Técnico", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && newRTPanel.getRT().isInvalid()) {
            JOptionPane.showMessageDialog(Main.getInstance(), "Campos inválidos, nome e código do CREA são obrigatórios!", "Aviso", JOptionPane.ERROR_MESSAGE);
            return true;
        }

        if (result == JOptionPane.CANCEL_OPTION) {
            throw new RuntimeException("cancel");
        }
        return false;
    }

    private void addRT(ResponsavelTecnico rt) {
        for (int i = 0; i < 10; i++) {
            String sResponsavelTecnico = pref.get("rt_" + i, null);
            if (sResponsavelTecnico == null) {
                sResponsavelTecnico = rt.toSerializableString();
                pref.put("rt_" + i, sResponsavelTecnico);
                rt.setId(i);
                return;
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

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton2 = new javax.swing.JButton();

        jButton1.setText("Cadastrar novo Responsável Técnico");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jList1);

        jButton2.setText("Editar Responsável Técnico");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            ResponsavelTecnico rt = null;
            while (question() && (rt = newRTPanel.getRT()).isInvalid()) {
                //do nothing
            }
            if ((rt = newRTPanel.getRT()) != null) {
                addRT(rt);
                pref.sync();
                updateList();
            }
            newRTPanel.clearAll();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ResponsavelTecnicoPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RuntimeException ex) {
            //do nothing
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        ResponsavelTecnico selectedValue = (ResponsavelTecnico) jList1.getSelectedValue();
        if (selectedValue == null) {
            return;
        }

        for (int i = 0; i < 10; i++) {
            if (selectedValue.getId() == i) {
                try {
                    ResponsavelTecnico rt = null;
                    newRTPanel.setResponsavelTecnico(selectedValue);
                    while (question() && (rt = newRTPanel.getRT()).isInvalid()) {
                        //do nothing
                    }
                    if ((rt = newRTPanel.getRT()) != null) {
                        rt.setId(selectedValue.getId());

                        pref.put("rt_" + selectedValue.getId(), rt.toSerializableString());
                        pref.sync();
                        updateList();
                    }
                    newRTPanel.clearAll();
                } catch (BackingStoreException ex) {
                    Logger.getLogger(ResponsavelTecnicoPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RuntimeException ex) {
                    //do nothing
                }
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}