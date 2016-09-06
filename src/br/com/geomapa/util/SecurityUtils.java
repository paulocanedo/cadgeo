/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util;

import br.com.geomapa.util.MiscUtils;
import br.com.geomapa.ui.panels.options.OptionsPanel;
import br.com.pc9.pswing.util.SystemUtilsOS;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author paulocanedo
 */
public final class SecurityUtils {

    private static final File userHome = new File(System.getProperty("user.home"));
    private static final String requisiteLicense = "requisiteLicense.req";
    private static File requisiteFile = new File(userHome, requisiteLicense);
    private static final String license = "dclicense.lic";
    private static File licenseFile = new File(userHome, license);

    public static String createRequestFile() throws FileNotFoundException, IOException {
        long currentTimeMillis = System.currentTimeMillis();
        String time = new Long(currentTimeMillis).toString();

        String toWrite = customFormat(time);
        writeToFile(requisiteFile, toWrite);

        return toWrite;
    }

    public static String readRequestFile() throws FileNotFoundException, IOException {
        return readFile(requisiteFile);
    }

    public static boolean register(String key) throws IOException {
        try {
            String request = readRequestFile();
            if (key.equals(customFormat(MiscUtils.sha1(request)))) {
                writeToFile(licenseFile, key);
                return true;
            }
        } catch (FileNotFoundException ex) {
        }
        return false;
    }

    public static boolean verifyRegister() throws IOException {
        String request;
        String keyRequest;
        try {
            request = readRequestFile();
            keyRequest = customFormat(MiscUtils.sha1(request));
        } catch (FileNotFoundException ex) {
            request = createRequestFile();
            keyRequest = customFormat(MiscUtils.sha1(request));
        }

        try {
            String readFile = readFile(licenseFile);
            return keyRequest.equals(readFile);
        } catch (FileNotFoundException ex) {
        }
        return false;
    }

    private static String readFile(File file) throws FileNotFoundException, IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        return reader.readLine();
    }

    private static void writeToFile(File file, String output) throws FileNotFoundException, IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(output.getBytes());
        } finally {
            out.close();
        }
    }

    private static String customFormat(String time) {
        StringBuilder sb = new StringBuilder(time);

        sb.insert(9, "-");
        sb.insert(6, "-");
        sb.insert(3, "-");

        return sb.toString().substring(0, 16);
    }

    public static String showInputKeyRegister() throws IOException {
        JLabel label1 = new JLabel("Por favor, informe a chave de registro: ");
        JLabel label2 = new JLabel("Requisição: " + readRequestFile());
        JTextField textField = new JTextField(16);
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 80));

        panel.setLayout(new FlowLayout());
        panel.add(label1);
        panel.add(label2);
        panel.add(textField);

        JOptionPane.showMessageDialog(null, panel);
        return (textField.getText());
    }
    
    public static boolean gmIsKeyValid() {
        return gmIsKeyValid(OptionsPanel.licenseNumber(), gmGetLocalKey());
    }

    public static boolean gmIsKeyValid(long licenseNumber, String key) {
        String sha1 = MiscUtils.sha1("" + licenseNumber + "cifragemDeHash");
        return sha1.equalsIgnoreCase(key);
    }

    public static String gmGetLocalKey() {
        try {
            return readFile(gmGetLicenseFile());
        } catch (Exception ex) {
            return null;
        }
    }

    public static void gmWriteKey(String key) throws IOException {
        File file = gmGetLicenseFile();
        FileWriter fw = new FileWriter(file);
        fw.write(key);
        fw.flush();
        fw.close();
    }

    public static int gmTrialRemainning(long license) {
        try {
            Date date = new Date(license);
            Calendar instance = Calendar.getInstance();

            instance.setTime(date);
            instance.add(Calendar.DAY_OF_MONTH, 30);
            Date currentDateTimeFromWeb = MiscUtils.currentDateTimeFromWeb();

            long diffTime = instance.getTimeInMillis() - currentDateTimeFromWeb.getTime();
            int idiffTime = (int) Math.ceil(diffTime / (24 * 60 * 60 * 1000.0));
            
            return (idiffTime > 0 ? idiffTime : 0);
        } catch (IOException ex) {
            Logger.getLogger(SecurityUtils.class.getName()).log(Level.SEVERE, null, ex);
            javax.swing.JOptionPane.showMessageDialog(null, "A versão de teste necessita de conexão com a internet.");
        }
        return 0;
    }
    
    private static File gmGetLicenseFile() {
        File homeDir = SystemUtilsOS.getUserHomeAsFile();

        File licFile = new File(homeDir, "/.config/geomapa/license");
        licFile.getParentFile().mkdirs();
        return licFile;
    }

    public static void main(String... args) {
//        try {
//            if (verifyRegister()) {
//                System.out.println("ok");
//            } else {
//                String keyEntered = showInputKeyRegister();
//                if (register(keyEntered)) {
//                    System.out.println("ok");
//                } else {
//                    JOptionPane.showMessageDialog(null, "Chave inválida.\nO programa será fechado.");
//                    System.exit(0);
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(SecurityUtils.class.getName()).log(Level.SEVERE, null, ex);
//            JOptionPane.showMessageDialog(null, "Falha na leitura do disco...");
//        }
//        System.out.println((MiscUtils.sha1("1330353364000cifragemDeHash")));
        System.out.println(customFormat(MiscUtils.sha1("134-124-756-8379")));
    }
}
