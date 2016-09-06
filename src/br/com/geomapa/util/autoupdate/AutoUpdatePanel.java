/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AutoUpdatePanel.java
 *
 * Created on 06/07/2011, 17:38:09
 */
package br.com.geomapa.util.autoupdate;

import br.com.geomapa.util.MiscUtils;
import br.com.geomapa.ui.panels.options.OptionsPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import org.apache.poi.util.IOUtils;

/**
 *
 * @author paulocanedo
 */
public class AutoUpdatePanel extends javax.swing.JPanel {

    public static File INSTALL_MAIN_FILE;
    private Timer checkerTimer = new Timer(10 * 60 * 1000, new CheckerUpdate());
    private List<AutoUpdateListener> listeners = new ArrayList<AutoUpdateListener>();
    private static AutoUpdatePanel instance;

    private AutoUpdatePanel() {
        initComponents();

        checkerTimer.setInitialDelay(20);
        checkerTimer.start();
    }

    public static AutoUpdatePanel getInstance() {
        if (instance == null) {
            instance = new AutoUpdatePanel();
        }
        return instance;
    }

    public void addListener(AutoUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AutoUpdateListener listener) {
        listeners.remove(listener);
    }

    private void fireStartListener() {
        for (AutoUpdateListener aul : listeners) {
            aul.onStart();
        }
    }

    private void fireFinishListener(boolean success, String message) {
        for (AutoUpdateListener aul : listeners) {
            aul.onFinish(success, message);
        }
    }

    private class CheckerUpdate implements ActionListener {

        private PC9Downloader downloader;
        private final String base = "http://www.paulocanedo.com.br/geomapa/";
        private String remoteSha1;
        private String jarname = "geomapa.jar";
        private boolean notInitialized = true;

        public CheckerUpdate() {
            try {
                URL url = new URL(base + jarname);

                File updateDir = OptionsPanel.getUpdateDir();
                updateDir.mkdirs();

                final File file = new File(updateDir, jarname);
                downloader = new PC9Downloader(file, url);
                downloader.addListenner(new Pc9DownloaderListener() {

                    @Override
                    public void progressChanged(long partial, long total, double percentual) {
                        headerLabel.setText(String.format("%.0f%% - Recebendo atualização: %.2fMB de %.2fMB.", percentual, partial / 1024 / 1024.0, total / 1024 / 1024.0));
                    }

                    @Override
                    public void downloadFinished(boolean success) {
                        Throwable error = null;
                        try {
                            if (remoteSha1 != null && remoteSha1.equalsIgnoreCase(MiscUtils.sha1(file))) {
                                File installFile = new File(INSTALL_MAIN_FILE.getParentFile(), jarname);

                                FileInputStream istream = null;
                                FileOutputStream ostream = null;
                                try {
                                    istream = new FileInputStream(file);
                                    ostream = new FileOutputStream(installFile);
                                    IOUtils.copy(istream, ostream);
                                    file.delete();
                                } finally {
                                    if (istream != null) {
                                        istream.close();
                                    }
                                    if (ostream != null) {
                                        ostream.close();
                                    }
                                }
                            }
                        } catch (Throwable ex) {
                            Logger.getLogger(AutoUpdatePanel.class.getName()).log(Level.SEVERE, null, ex);
                            error = ex;
                        }
                        checkerTimer.stop();

                        if (error == null) {
                            error = downloader.getError();
                        }
                        fireFinishListener(success, error == null
                                ? "Atualização concluída com sucesso."
                                : "Falha na atualização: \n" + error.getMessage());
                    }
                });
            } catch (MalformedURLException ex) {
                Logger.getLogger(AutoUpdatePanel.class.getName()).log(Level.SEVERE, null, ex);
                checkerTimer.stop();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (hasUpdate() && notInitialized) {
                notInitialized = false;
                fireStartListener();
                Thread thread = new Thread(downloader);
                thread.start();

                checkerTimer.stop();
            }
        }

        private boolean hasUpdate() {
            if (INSTALL_MAIN_FILE == null || INSTALL_MAIN_FILE.getParentFile().exists() == false) {
                return false;
            }

            File main = new File(INSTALL_MAIN_FILE.getParentFile(), jarname);

            try {
                String sha1 = MiscUtils.sha1(main);
                URL url = new URL(base + jarname + ".sha");

                remoteSha1 = PC9Downloader.downloadWithSameThread(url);
                return !sha1.equalsIgnoreCase(remoteSha1);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }
    
    public static void main(String... args) {
        System.out.println(String.format("%.0f%% - Recebendo atualização: %.2fMB de %.2fMB.", 0.123, 0.234, 0.345));
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
        headerLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        headerLabel.setForeground(java.awt.Color.darkGray);
        headerLabel.setText("O aplicativo pcGeoCad está atualizado.");
        jPanel2.add(headerLabel);

        add(jPanel2, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
