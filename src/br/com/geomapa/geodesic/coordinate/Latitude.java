/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.coordinate;

import br.com.geomapa.util.AngleValue;
import br.com.geomapa.geodesic.Hemisphere;

/**
 *
 * @author paulocanedo
 */
public final class Latitude extends AngleValue {

    public Latitude(double degree, double minute, double second, boolean positive) {
        super(degree, minute, second, positive);
        checkValue();
    }

    public Latitude(double latitude) {
        super(latitude);
        checkValue();
    }

    private void checkValue() {
        if (toDegreeDecimal() < -80.0 || toDegreeDecimal() > 84.0) {
            throw new IllegalArgumentException("Valor de latitude incorreto: " + toString());
        }
    }

    public Hemisphere getHemisphere() {
        return isPositive() ? Hemisphere.NORTH : Hemisphere.SOUTH;
    }

    @Override
    public String toString() {
        return toString("dd" + UNICODE_DEGREE + "mm\'ss\" NS");
    }

    /**
     * Convert latitude value in a String representation
     *
     * <ul>
     * <li><B>+-</B> signal value</li>
     * <li><B>dd</B> degree value</li>
     * <li><B>mm</B> minute value</li>
     * <li><B>ss</B> second value</li>
     * <li><B>NS</B> letter <i>N</i> if longitude has positive value, other case show letter <i>S</i></li>
     * </ul>
     *
     * @param pattern to transform the angle String
     * @param precision number of digits after decimal separator
     * @return
     */
    @Override
    public String toString(String pattern, int precision) {
        String value = super.toString(pattern, precision);
        value = value.replace("NS", isPositive() ? "N" : "S");

        return value;
    }
}
