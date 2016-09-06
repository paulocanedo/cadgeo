/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.datum;

import static java.lang.Math.sin;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 *
 * @author paulocanedo
 */
public class Ellipsoid {

    private final double a;
    private final double b;
    private final double e2;
    private final double el2;
    private final double n;
    private final double A;
    private final double B;
    private final double C;
    private final double D;
    private final double E;
    private final Datum datum;

    public Ellipsoid(Datum datum) {
        this.datum = datum;
        this.a = datum.getSemiMajorAxis();
        this.b = datum.getSemiMinorAxis();

        this.n = (a - b) / (a + b);

        this.e2 = (pow(a, 2) - pow(b, 2)) / pow(a, 2);
        this.el2 = (pow(a, 2) - pow(b, 2)) / pow(b, 2);

        this.A = a * (1 - n + 5 / 4.0 * (pow(n, 2) - pow(n, 3)) + 81 / 64.0 * (pow(n, 4) - pow(n, 5)));
        this.B = 3 / 2.0 * a * (n - pow(n, 2) + (7 / 8.0) * (pow(n, 3) - pow(n, 4) + (55 / 64.0) * pow(n, 5)));
        this.C = 15 / 16.0 * a * (pow(n, 2) - pow(n, 3) + (3 / 4.0) * (pow(n, 4) - pow(n, 5)));
        this.D = 35 / 48.0 * a * (pow(n, 3) - pow(n, 4) + (11 / 16.0) * pow(n, 5));
        this.E = 315 / 512.0 * a * (pow(n, 4) - pow(n, 5));

    }

    public double getE2() {
        return e2;
    }

    public double getN() {
        return n;
    }

    public double getEl2() {
        return this.el2;
    }

    /**
     * Radius of Curvature in the meridian
     * @param latRad
     * @return sr
     */
    public double calcRHO(double latRad) {
        return a * (1 - e2)
                / pow(1 - e2 * pow(sin(latRad), 2), 3 / 2.0);
    }

    /**
     * radius of curvature in prime vertical
     * @param latRad
     * @return sn
     */
    public double calcNU(double latRad) {
        return a / sqrt(1.e0 - e2 * pow(sin(latRad), 2.0));
    }

    /**
     * True Meridianal Distances
     * @param latRad
     * @return tmd
     */
    public double calcS(double latRad) {
        return calcS(A, B, C, D, E, latRad);
    }

    public static double calcS(double A, double B, double C, double D, double E, double latRad) {
        return A * latRad - B * sin(2 * latRad) + C * sin(4 * latRad) - D * sin(6 * latRad) + E * sin(8 * latRad);
    }

    public Datum getDatum() {
        return datum;
    }
    
}
