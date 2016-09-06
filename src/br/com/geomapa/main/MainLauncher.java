/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.main;

import br.com.geomapa.ui.panels.RegistrationPanel;
import br.com.geomapa.util.SecurityUtils;
import br.com.geomapa.util.autoupdate.AutoUpdatePanel;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

/**
 *
 * @author paulocanedo
 */
public class MainLauncher {

    private static final RegistrationPanel registrationPanel = new RegistrationPanel();

    public static void main(String args[]) {
        try {
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);
            ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
            if (!System.getProperty("os.name").toLowerCase().contains("os x")) {
                UIManager.setLookAndFeel("br.com.paulocanedo.pc9.laf.PC9LookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                UIManager.setLookAndFeel("br.com.paulocanedo.pc9.laf.PC9LookAndFeel");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
//        System.setProperty("awt.useSystemAAFontSettings", "on");
//        System.setProperty("swing.aatext", "true");
//        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "pcGeoCad");
//        System.setProperty("apple.awt.showGrowBox", "true");

        URL location = MainLauncher.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            File file = new File(location.toURI());
            String name = file.getName().toLowerCase();

            if (name.endsWith(".jar")) {
                AutoUpdatePanel.INSTALL_MAIN_FILE = file;
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(MainLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!SecurityUtils.gmIsKeyValid()) {
            JDialog registrationDialog = getRegistrationDialog();
            registrationDialog.setVisible(true);
            if (!RegistrationPanel.canOpen()) {
                System.exit(0);
            }
        }

//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
        Main.getInstance().setVisible(true);
//                } catch (Exception ex) {
//                    Logger.getLogger(MainLauncher.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
    }

    private static JDialog getRegistrationDialog() {
        JDialog dialog = new JDialog(new JFrame(), true);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        int w = 600, h = 360;
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);
        dialog.add(registrationPanel);

        return dialog;
    }
}
