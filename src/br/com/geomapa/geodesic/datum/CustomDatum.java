/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.datum;

import static java.lang.Math.pow;

/**
 *
 * @author paulocanedo
 */
public class CustomDatum implements Datum {

    protected double A0;
    protected double B0;
    protected double C0;
    protected double D0;
    protected double E0;
    protected double a;
    protected double b;
    protected double flattening;
    protected double inverseFlattening;
    protected double n;
    protected double rm;
    protected double e;
    protected double e1sq;

    public CustomDatum(double a, double b) {
        this.a = a;
        this.b = b;

        calculateAllValues();
    }

    public CustomDatum(double a, double b, double inverseFlattening) {
        this.a = a;
        this.b = b;
        this.inverseFlattening = inverseFlattening;

        calculateAllValues();
    }

    private void calculateAllValues() {
        if (flattening == 0) {
            flattening = calculateFlattening();
        }

        if (inverseFlattening == 0) {
            inverseFlattening = calculateInverseFlattening();
        }

        if (n == 0) {
            n = calculate_n();
        }

        if (rm == 0) {
            rm = calculateMeanRadius();
        }

        if (e == 0) {
            e = calculateEccentricity();
        }

        if (e1sq == 0) {
            e1sq = calculateEccentricitySq();
        }

        A0 = calculateA0();
        B0 = calculateB0();
        C0 = calculateC0();
        D0 = calculateD0();
        E0 = calculateE0();
    }

    protected double calculate_n() {
        return (a - b) / (a + b);
    }

    protected double calculateFlattening() {
        return (a - b) / a;
    }

    protected double calculateInverseFlattening() {
        return 1 / flattening;
    }

    protected double calculateMeanRadius() {
        return pow(a * b, 1 / 2.0);
    }

    /**
     * This is the eccentricity of the earth's elliptical cross-section.
     * @return eccentricity value
     */
    protected double calculateEccentricity() {
        return Math.sqrt(1 - pow(b / a, 2));
    }

    /**
     * The quantity e' only occurs in even powers so it need only be calculated
     * as e'2.
     *
     * @return e' value
     */
    protected double calculateEccentricitySq() {
        return e * e / (1 - e * e);
    }

    protected double calculateA0() {
        return a * (1 - n + 5 / 4.0 * (pow(n, 2) - pow(n, 3)) + 81 / 64.0 * (pow(n, 4) - pow(n, 5)));
    }

    protected double calculateB0() {
        return 3 / 2.0 * a * (n - pow(n, 2) + (7 / 8.0) * (pow(n, 3) - pow(n, 4) + (55 / 64.0) * pow(n, 5)));
    }

    protected double calculateC0() {
        return 15 / 16.0 * a * (pow(n, 2) - pow(n, 3) + (3 / 4.0) * (pow(n, 4) - pow(n, 5)));
    }

    protected double calculateD0() {
        return 35 / 48.0 * a * (pow(n, 3) - pow(n, 4) + (11 / 16.0) * pow(n, 5));
    }

    protected double calculateE0() {
        return 315 / 512.0 * a * (pow(n, 4) - pow(n, 5));
    }

    @Override
    public double getSemiMajorAxis() {
        return a;
    }

    @Override
    public double getSemiMinorAxis() {
        return b;
    }

    @Override
    public double getFlattening() {
        return flattening;
    }

    @Override
    public double getInverseFlattening() {
        return inverseFlattening;
    }

    public double getMeanRadius() {
        return rm;
    }

    public double getEccentricity() {
        return e;
    }

    public double getEccentricitySq() {
        return e1sq;
    }

    public double getA0() {
        return A0;
    }

    public double getB0() {
        return B0;
    }

    public double getC0() {
        return C0;
    }

    public double getD0() {
        return D0;
    }

    public double getE0() {
        return E0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Datum)) {
            return false;
        }
        Datum otherDatum = (Datum) obj;
        return otherDatum.getSemiMajorAxis() == getSemiMajorAxis() && otherDatum.getSemiMinorAxis() == getSemiMinorAxis();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.a) ^ (Double.doubleToLongBits(this.a) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.b) ^ (Double.doubleToLongBits(this.b) >>> 32));
        return hash;
    }
}
