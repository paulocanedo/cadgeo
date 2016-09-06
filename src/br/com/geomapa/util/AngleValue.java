/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util;

import java.text.DecimalFormat;

/**
 *
 * @author paulocanedo
 */
public class AngleValue {

    public static final String UNICODE_DEGREE = "\u00b0";
    private double decimalValue;
    private Object[] fragmentedValue;

    public AngleValue(double value) {
        this.decimalValue = value;
        this.fragmentedValue = dd2dms(decimalValue);
    }

    public AngleValue(double degree, double minute, double second, boolean positive) {
        this.decimalValue = dms2dd(degree, minute, second, positive);
        this.fragmentedValue = new Object[]{degree, minute, second, positive};
    }

    /**
     * Transform an angle in degree, minute and second to a decimal angle
     *
     * @param degree
     * @param minute
     * @param second
     * @param positive
     * @return the same angle in decimal format
     */
    public static double dms2dd(double degree, double minute, double second, boolean positive) {
        if (positive) {
            return degree + (minute / 60.0) + (second / 3600.0);
        } else {
            return -degree - (minute / 60.0) - (second / 3600.0);
        }
    }

    /**
     * <p>Transform a decimal angle to a degree based angle, minute and
     * second.</p>
     *
     * <b>Attention: </b>values will always return a value greater that zero.
     * To see if this angle is positive, please check for the 4th value of
     * array.
     *
     * @param decimalDegree
     * @return An array of objects contains, in order: (double)degree,
     * (double)minute, (double)second, (boolean)positive
     */
    public static Object[] dd2dms(double decimalDegree) {
        boolean positive = true;
        if (decimalDegree < 0) {
            decimalDegree = -decimalDegree;
            positive = false;
        }

        double degree, minute;
        degree = (int) (decimalDegree);

        double decimalMinute = (decimalDegree - degree) * 60;
        minute = (int) (decimalMinute);

        double decimalSecond = (decimalMinute - minute) * 60;

        if (decimalSecond == 60) {
            decimalSecond = 0;
            minute++;
        }

        if (minute == 60) {
            minute = 0;
            degree++;
        }

        return new Object[]{degree, minute, decimalSecond, positive};
    }

    /**
     * Returns if angle is positive or not
     * @return true if angle is positive
     */
    public boolean isPositive() {
        return decimalValue > 0;
    }

    public int getDegree() {
        return ((Double) (fragmentedValue[0])).intValue();
    }

    public int getMinute() {
        return ((Double) (fragmentedValue[1])).intValue();
    }

    public double getSecond() {
        return (Double) fragmentedValue[2];
    }

    /**
     * Returns decimal value of angle
     * @return decimal value
     */
    public double toDegreeDecimal() {
        return decimalValue;
    }

    public double toRadians() {
        return Math.toRadians(toDegreeDecimal());
    }

    @Override
    public String toString() {
        return toString("+-dd" + UNICODE_DEGREE + "mm\'ss\"");
    }

    public String toString(String pattern) {
        return toString(pattern, 5);
    }

    /**
     * Convert angle value in a String representation
     *
     * <ul>
     * <li><B>+-</B> signal value</li>
     * <li><B>dd</B> degree value</li>
     * <li><B>mm</B> minute value</li>
     * <li><B>ss</B> second value</li>
     * </ul>
     *
     * @param pattern to transform the angle String
     * @param precision number of digits after decimal separator
     * @return
     */
    public String toString(String pattern, int precision) {
        String value = pattern;

        value = value.replace("+", isPositive() ? "+" : "");
        value = value.replace("-", isPositive() ? "" : "-");
        value = value.replace("dd", defaultDegreeDecimalFormat.format(getDegree()));
        value = value.replace("mm", defaultDegreeDecimalFormat.format(getMinute()));

        String second = String.format("%." + precision + "f", getSecond());
        if (getSecond() < 10) {
            second = "0" + second;
        }
        value = value.replace("ss", second);

        return value;
    }
    private static DecimalFormat defaultDegreeDecimalFormat = new DecimalFormat("00");
}
