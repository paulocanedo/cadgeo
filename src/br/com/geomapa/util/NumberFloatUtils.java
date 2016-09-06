/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author paulocanedo
 */
public final class NumberFloatUtils {

    private NumberFloatUtils() {
    }

    private static String prepareFloatNumber(String value) {
        int dotIndex = value.lastIndexOf('.');
        int commaIndex = value.lastIndexOf(',');

        int index = Math.max(dotIndex, commaIndex);
        if (index < 0) {
            return value;
        } else {
            value = value.replaceAll("\\.", " ").replaceAll(",", " ");
            value = String.format("%s.%s", value.substring(0, index), value.substring(index));
            return value.replaceAll(" ", "");
        }
    }

    public static float parseFloat(String value) {
        return Float.parseFloat(prepareFloatNumber(value));
    }

    public static double parseDouble(String value) {
        return Double.parseDouble(prepareFloatNumber(value));
    }

    public static AngleValue parseDirection(String value) {
        if (value.startsWith("d")) {
            return new AngleValue(Double.parseDouble(value.substring(1)));
        }

        Matcher matcher = azimuthPattern.matcher(value);
        if (matcher.find()) {
            int degreee = Integer.parseInt(matcher.group(1));
            int min = Integer.parseInt(matcher.group(2));
            int sec = Integer.parseInt(matcher.group(3));
            checkAzimuth(degreee, min, sec);

            return new AngleValue(degreee, min, sec, true);
        }

        String[] split = value.split("\\.|,");
        if (split[1].length() != 4) {
            throw new NumberFormatException(String.format("Azimute não reconhecido: %s", value));
        }

        int degree = Integer.parseInt(split[0]);
        int min = Integer.parseInt(split[1].substring(0, 2));
        int sec = Integer.parseInt(split[1].substring(2, 4));
        checkAzimuth(degree, min, sec);
        
        return new AngleValue(degree, min, sec, true);
    }
    
    private static void checkAzimuth(int degree, int minute, int second) {
        if(degree < 0 || degree >= 360) {
            throw new NumberFormatException("Valor referente ao minuto no azimute deve ser entre 0 e 359");
        }else if(minute < 0 || minute >= 60) {
            throw new NumberFormatException("Valor referente ao minuto no azimute deve ser entre 0 e 59");
        } else if(second < 0 || second >= 60) {
            throw new NumberFormatException("Valor referente ao segundo no azimute deve ser entre 0 e 59");
        }
    }
    private static final Pattern azimuthPattern = Pattern.compile("(\\d{1,3}).(\\d{1,2})'(\\d{1,2})\"");
}
