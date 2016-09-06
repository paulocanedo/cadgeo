/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.rbmc;

import br.com.geomapa.ui.panels.options.OptionsPanel;
import br.com.geomapa.util.FileFilterExtension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author paulocanedo
 */
public class UtilRBMC {

    public static File[] getRbmcFiles() {
        File baseDir = OptionsPanel.getBaseDir();
        File rbmcDir = new File(baseDir, "rbmc");

        return rbmcDir.listFiles(new FileFilterExtension("properties"));
    }

    public static File getRbmcFile(String name) {
        File baseDir = OptionsPanel.getBaseDir();
        File rbmcDir = new File(baseDir, "rbmc");

        return new File(rbmcDir, name + ".properties");
    }

    public static List<Properties> getRbmcPropertiesList() throws FileNotFoundException, IOException {
        List<Properties> list = new ArrayList<Properties>();
        for (File file : getRbmcFiles()) {
            list.add(getPropertiesFromFile(file));
        }
        return list;
    }

    public static Properties getPropertiesFromFile(File rbmcFile) throws FileNotFoundException, IOException {
        Properties prop = new Properties();

        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(rbmcFile), "utf-8");
            prop.load(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return prop;
    }

    private static String getNameNoExtension(File file) {
        String name = file.getName();

        name = name.substring(0, name.lastIndexOf("."));
        return name;
    }

    public static List<BaseRBMC> listAllRbmc() throws FileNotFoundException, IOException {
        List<BaseRBMC> list = new ArrayList<BaseRBMC>();
        File[] rbmcFiles = getRbmcFiles();
        if (rbmcFiles == null) {
            return Collections.emptyList();
        }

        for (File file : getRbmcFiles()) {
            String name = getNameNoExtension(file);
            BaseRBMC base = new BaseRBMC(name, getPropertiesFromFile(file));
            list.add(base);
        }

        return list;
    }
}
