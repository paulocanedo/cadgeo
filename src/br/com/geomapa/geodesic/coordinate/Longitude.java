/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.coordinate;

import br.com.geomapa.util.AngleValue;

/**
 *
 * @author paulocanedo
 */
public final class Longitude extends AngleValue {

    public Longitude(double degree, double minute, double second, boolean positive) {
        super(degree, minute, second, positive);

        if (degree < -180.0 || degree > 180.0) {
            throw new IllegalArgumentException("Valor de longitude incorreto: " + toString());
        }
    }

    public Longitude(double longitude) {
        super(longitude);

        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Valor de longitude incorreto: " + longitude);
        }
    }

    public Longitude getCentralMeridian() {
        return getCentralMeridian(getZone());
    }

    public static Longitude getCentralMeridian(int zone) {
        return new Longitude(calcCentralMeridian(zone));
    }

    public static double calcCentralMeridian(int zone) {
        return 6 * zone - 183;
    }

    public int getZone() {
        double longitude = toDegreeDecimal();
        if (longitude < 0.0) {
            return ((int) ((180 + longitude) / 6.0)) + 1;
        } else {
            return ((int) (longitude / 6)) + 31;
        }
    }

    @Override
    public String toString() {
        return toString("dd" + UNICODE_DEGREE + "mm\'ss\" EW");
    }

    /**
     * Convert longitude value in a String representation
     * 
     * <ul>
     * <li><B>+-</B> signal value</li>
     * <li><B>dd</B> degree value</li>
     * <li><B>mm</B> minute value</li>
     * <li><B>ss</B> second value</li>
     * <li><B>EW</B> letter <i>E</i> if longitude has positive value, other case show letter <i>W</i></li>
     * </ul>
     *
     * @param pattern to transform the angle String
     * @param precision number of digits after decimal separator
     * @return
     */
    @Override
    public String toString(String pattern, int precision) {
        String value = super.toString(pattern, precision);
        value = value.replace("EW", isPositive() ? "E" : "W");

        return value;
    }

    public String toMeridianCentralString() {
        return toString("dd" + AngleValue.UNICODE_DEGREE + "EWgr", 0);
    }
}
