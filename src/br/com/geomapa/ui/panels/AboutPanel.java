/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AboutPanel.java
 *
 * Created on 29/06/2011, 15:17:53
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.controller.actions.SaveProjectAction;
import br.com.geomapa.main.Main;
import br.com.geomapa.util.autoupdate.AutoUpdateListener;
import br.com.geomapa.util.autoupdate.AutoUpdatePanel;
import br.com.pc9.pswing.util.SystemUtilsOS;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author paulocanedo
 */
public class AboutPanel extends javax.swing.JPanel {

    private static final AutoUpdatePanel autoUpdatePanel = AutoUpdatePanel.getInstance();

    /** Creates new form AboutPanel */
    public AboutPanel(final JDialog parent) {
        initComponents();

        parent.getRootPane().setDefaultButton(jButton1);
        InputStream buildNumberStream = getClass().getResourceAsStream("/buildnumber.txt");
        Properties prop = new Properties();
        try {
            prop.load(buildNumberStream);
        } catch (IOException ex) {
            Logger.getLogger(AboutPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        versaoLabel.setText("1.0." + prop.getProperty("build.number"));

        jButton1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                parent.dispose();
            }
        });

        initUpdatePanel();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        versaoLabel = new javax.swing.JLabel();
        updatePanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jButton1.setText("Fechar Janela");
        jPanel2.add(jButton1);

        add(jPanel2, java.awt.BorderLayout.SOUTH);

        jEditorPane1.setEditable(false);
        jEditorPane1.setText("Software desenvolvido por Paulo Canedo Costa Rodrigues\nTodos os direitos são reservados ao autor.\n\nÉ proibido repassar cópias desse software sem a autorização do autor.\n\nInformações, favor contatar o email: paulocanedo@gmail.com");
        jEditorPane1.setSize(new java.awt.Dimension(400, 40));
        jScrollPane1.setViewportView(jEditorPane1);

        jLabel2.setText("Versão: ");
        jPanel4.add(jLabel2);

        versaoLabel.setText("1.0");
        jPanel4.add(versaoLabel);

        progressBar.setIndeterminate(true);
        updatePanel.add(progressBar);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1)
                    .add(updatePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(updatePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | java.awt.Font.BOLD, 36));
        jLabel4.setText("pcGeoCad");
        jPanel3.add(jLabel4);

        add(jPanel3, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel updatePanel;
    private javax.swing.JLabel versaoLabel;
    // End of variables declaration//GEN-END:variables

    private void initUpdatePanel() {
        updatePanel.add(autoUpdatePanel);
        progressBar.setVisible(false);
        autoUpdatePanel.addListener(new AutoUpdateListener() {

            @Override
            public void onStart() {
                progressBar.setVisible(true);
            }

            @Override
            public void onFinish(boolean success, String message) {
                progressBar.setVisible(false);
                if (success) {
                    JOptionPane.showMessageDialog(Main.getInstance(), message + "\nClique em OK para reabrir o aplicativo.");
                    Main instance = Main.getInstance();
                    File rootFolder = instance.getProjectInfo().getRootFolder();
                    if (rootFolder != null && rootFolder.exists()) {
                        try {
                            new SaveProjectAction().save();
                        } catch (IOException ex) {
                        }
                    }

                    File file = AutoUpdatePanel.INSTALL_MAIN_FILE;
                    File joglDir = new File(file.getParentFile(), "jogl");

                    String command = String.format("%s \"-Djava.library.path=%s\" -Xms256m -Xmx512m -jar \"%s\"", SystemUtilsOS.isWindows() ? "javaw" : "java", joglDir.getAbsolutePath(), file.getAbsolutePath());
                    try {
                        Runtime.getRuntime().exec(command);
                        System.exit(0);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(Main.getInstance(), "Falha ao reiniciar aplicativo.");
                    }
                }
            }
        });
    }
}
