/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geografico.ztest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paulocanedo
 */
public class Defensor {

    private static final Properties properties = new Properties();

    static {
        try {
            InputStream resourceAsStream = Defensor.class.getResourceAsStream("/br/com/geografico/resources/properties/defensor.properties");
            properties.load(resourceAsStream);
        } catch (IOException ex) {
            Logger.getLogger(Defensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}
