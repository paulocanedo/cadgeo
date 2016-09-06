/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author paulocanedo
 */
public final class Extractor {

    private static Pattern pattern = Pattern.compile("\\d+[a-zA-Z]?$");

    public static String extractNumber(String value) {
        Matcher m = pattern.matcher(value);
        if (m.find()) {
            return m.group(0).trim();
        }
        return null;
    }
}
