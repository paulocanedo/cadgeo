/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OptionsPanel.java
 *
 * Created on 16/05/2011, 14:18:09
 */
package br.com.geomapa.ui.panels.options;

import br.com.geomapa.main.Main;
import br.com.geomapa.util.DirectoryFileFilter;
import br.com.geomapa.util.MiscUtils;
import br.com.pc9.pswing.components.filebrowser.PFileBrowser;
import br.com.pc9.pswing.util.SystemUtilsOS;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;

/**
 *
 * @author paulocanedo
 */
public final class OptionsPanel extends javax.swing.JPanel {

    public static final Preferences pref = Preferences.userRoot().node("pcgc");

    /** Creates new form OptionsPanel */
    public OptionsPanel() {
        initComponents();
        
        String diretorioBase = pref.get("diretorio_base", baseDir());
        diretorioBaseField.setText(diretorioBase);
    }

    private static String baseDir() {
        try {
            return SystemUtilsOS.getUserFolders().getDocumentFolder().getAbsolutePath() + File.separator + "GeoCad";
        } catch (Exception ex) {
            Logger.getLogger(OptionsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return SystemUtilsOS.getUserHome() + File.separator + "GeoCad";
    }

    public static File getBaseDir() {
        return new File(pref.get("diretorio_base", baseDir()));
    }

    public static File getProjectDir() {
        return new File(getBaseDir(), "projetos");
    }
    
    public static File getUpdateDir() {
        return new File(getBaseDir(), "update");
    }
    
    public static File getRBMCDir() {
        File file = new File(getBaseDir(), "rbmc");
        file.mkdirs();
        return file;
    }

    public static boolean isBenchmarkGL() {
        return pref.getBoolean("benchmarkGL", Boolean.FALSE);
    }

    public static long licenseNumber() {
        try {
            long ln = pref.getLong("afe90ffcdf", 0);
            if (ln <= 0) {
//                ln = new Date().getTime();
                ln = MiscUtils.currentDateTimeFromWeb().getTime();
                pref.putLong("afe90ffcdf", ln);
                pref.sync();
            }
            return ln;
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(Main.getInstance(), "Uma conexão com a internet é requerida no momento do registro, verifique sua conexão e tente novamente.");
            Logger.getLogger(OptionsPanel.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
            return -1;
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
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        diretorioBaseField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        benchmarkGLCheckBox = new javax.swing.JCheckBox();
        jPanel2 = new ResponsavelTecnicoPanel();
        jPanel3 = new RBMCPanel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Diretório base");
        jPanel1.add(jLabel1, new java.awt.GridBagConstraints());

        diretorioBaseField.setColumns(30);
        diretorioBaseField.setEditable(false);
        jPanel1.add(diretorioBaseField, new java.awt.GridBagConstraints());

        jButton1.setText("Procurar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new java.awt.GridBagConstraints());

        benchmarkGLCheckBox.setText("Exibir FPS (Quadros por Segundos) na janela de Mapas");
        benchmarkGLCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                benchmarkGLCheckBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(benchmarkGLCheckBox, gridBagConstraints);

        jTabbedPane1.addTab("Geral", jPanel1);
        jTabbedPane1.addTab("Responsável Técnico", jPanel2);
        jTabbedPane1.addTab("Bases RBMC", jPanel3);

        add(jTabbedPane1);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        PFileBrowser fileBrowser = new PFileBrowser();
        fileBrowser.addFileFilter(DirectoryFileFilter.FILE_FILTER);
        File file = fileBrowser.showOpenFileDialog(Main.getInstance());

        if (file != null) {
            pref.put("diretorio_base", file.getAbsolutePath());
            try {
                pref.sync();
                diretorioBaseField.setText(file.getAbsolutePath());
            } catch (BackingStoreException ex) {
                Logger.getLogger(OptionsPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void benchmarkGLCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_benchmarkGLCheckBoxItemStateChanged
        JCheckBox src = (JCheckBox) evt.getSource();

        pref.putBoolean("benchmarkGL", src.isSelected());
        try {
            pref.sync();
        } catch (BackingStoreException ex) {
            Logger.getLogger(OptionsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_benchmarkGLCheckBoxItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox benchmarkGLCheckBox;
    private javax.swing.JTextField diretorioBaseField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
