/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geografico.ztest;

import br.com.geomapa.geodesic.coordinate.Longitude;
import br.com.geomapa.util.AngleValue;
import br.com.geomapa.geodesic.coordinate.Latitude;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.tan;
import static java.lang.Math.pow;
import static java.lang.Math.abs;

/**
 *
 * @author paulocanedo
 */
public class ConvertorTest {

    private static boolean toCheck = false;
    private static final double FALSE_NORTHING = 10000000;
    private static final double FALSE_EASTING = 500000;

    public static void main(String... args) {
        double k0 = 0.9996;
        double northing = 8628102.676, easting = 300463.191;
//        double lat = Math.toRadians(new AngleUnit(12, 24, 13.84050, false).toDegreeDecimal());
//        double lon = new AngleUnit(46, 50, 07.70489, false).toDegreeDecimal();

//        int zone;
//        if (lon < 0.0) {
//            zone = ((int) ((180 + lon) / 6.0)) + 1;
//        } else {
//            zone = ((int) (lon / 6)) + 31;
//        }
        double lon0 = -45;

        // <editor-fold desc="inicio dos calculos de variaveis do elipsoide">
        double a = 6378137.0;
        double b = 6356752.31413;
//        double a = 6378388.0;
//        double b = 6356911.94613;
        double f = (a - b) / a;

        double e2 = (pow(a, 2) - pow(b, 2)) / pow(a, 2);
        double e2Test = f * (2 - f);
        checkValues("e2", e2, e2Test);
        checkValues("e2 value", e2, 0.006722670022);

        double el2 = (pow(a, 2) - pow(b, 2)) / pow(b, 2);
        double el2Test = e2 / (1 - e2);
        double el2Test2 = f * (2 - f) / pow((1 - f), 2);
        checkValues("el2", el2, el2Test, el2Test2);

        double el4 = pow(el2, 2);
        double el6 = pow(el2, 3);
        double el8 = pow(el2, 4);

        double n = (a - b) / (a + b);
        double nTest = f / (2 - f);
        checkValues("n", n, nTest);

        //radius of curvature in the meridian
//        double rho = a * (1 - e2)
//                / pow(1 - e2 * pow(sin(lat), 2), 3 / 2.0);
//
//        //radius if curvature in the prime vertical
//        double nu = a
//                / pow((1 - e2 * pow(sin(lat), 2)), 1 / 2.0);
//
//        double vTest = rho * (1 + el2 * pow(cos(lat), 2));
//        checkValues("v", nu, vTest);

        double A = a * (1 - n + 5 / 4.0 * (pow(n, 2) - pow(n, 3)) + 81 / 64.0 * (pow(n, 4) - pow(n, 5)));
        double B = 3 / 2.0 * a * (n - pow(n, 2) + (7 / 8.0) * (pow(n, 3) - pow(n, 4) + (55 / 64.0) * pow(n, 5)));
        double C = 15 / 16.0 * a * (pow(n, 2) - pow(n, 3) + (3 / 4.0) * (pow(n, 4) - pow(n, 5)));
        double D = 35 / 48.0 * a * (pow(n, 3) - pow(n, 4) + (11 / 16.0) * pow(n, 5));
        double E = 315 / 512.0 * a * (pow(n, 4) - pow(n, 5));
        checkValues("A", A, 6367654.500058);
        checkValues("B", B, 16107.034678);
        checkValues("C", C, 16.976211);
        checkValues("D", D, 0.022266);
        checkValues("E", E, 0.000032);

//        double S = A * lat - B * sin(2 * lat) + C * sin(4 * lat) - D * sin(6 * lat) + E * sin(8 * lat);
        //</editor-fold>

//        if (lat < 0) {
        northing = northing - FALSE_NORTHING;
//        }
        double tmdo = calcS(A, B, C, D, E, 0.0);
        double tmd = tmdo + northing / k0;
        double phi1 = tmd / calcRHO(a, e2, 0.0);
        double t10, sr;
        for (int i = 0; i < 5; i++) {
            t10 = calcS(A, B, C, D, E, phi1);
            sr = calcRHO(a, e2, phi1);
            phi1 = phi1 + (tmd - t10) / sr;
        }

        double rho = calcRHO(a, e2, phi1);
        double nu = calcNU(a, e2, phi1);

//        double arc = northing / k0;
//        double mu = arc
//                / (a * (1 - pow(e2, 2) / 4.0 - 3 * pow(e2, 4) / 64.0 - 5 * pow(e2, 6) / 256.0));
//
//        double ei = (1 - pow((1 - e2 * e2), (1 / 2.0)))
//                / (1 + pow((1 - e2 * e2), (1 / 2.0)));
//
//        double ca = 3 * ei / 2 - 27 * pow(ei, 3) / 32.0;
//
//        double cb = 21 * pow(ei, 2) / 16 - 55 * pow(ei, 4) / 32;
//        double cc = 151 * pow(ei, 3) / 96;
//        double cd = 1097 * pow(ei, 4) / 512;
//        double phi1 = mu + ca * sin(2 * mu) + cb * sin(4 * mu) + cc * sin(6 * mu) + cd
//                * sin(8 * mu);

        // <editor-fold desc="inicio calculos de termos auxiliares: T1 ate T31">(ep1 + ep2 + ep3)
//        double T1 = S * k0;
//        double T2 = nu * sin(lat) * cos(lat) * k0 / 2.0;
//        double T3 = nu * sin(lat) * pow(cos(lat), 3) * k0 / 24.0
//                * (5 - pow(tan(lat), 2) + 9 * el2 * pow(cos(lat), 2) + 4 * el4 * pow(cos(lat), 4));
//        double T4 = ((nu * sin(lat) * pow(cos(lat), 5) * k0) / 720.0)
//                * (61 - 58 * pow(tan(lat), 2) + pow(tan(lat), 4) + 270 * el2 * pow(cos(lat), 2) - 330 * pow(tan(lat), 2) * el2 * pow(cos(lat), 2)
//                + 445 * el4 * pow(cos(lat), 4) + 324 * el6 * pow(cos(lat), 6) - 680 * pow(tan(lat), 2) * el4 * pow(cos(lat), 4)
//                + 88 * el8 * pow(cos(lat), 8) - 600 * pow(tan(lat), 2) * el6 * pow(cos(lat), 6) - 192 * pow(tan(lat), 2) * el8 * pow(cos(lat), 8));
//        double T5 = nu * sin(lat) * pow(cos(lat), 7) * k0 / 40320.0
//                * (1385 - 3111 * pow(tan(lat), 2) + 543 * pow(tan(lat), 4) - pow(tan(lat), 6));
//        double T6 = nu * cos(lat) * k0;
//        double T7 = nu * pow(cos(lat), 3) * k0 / 6.0
//                * (1 - pow(tan(lat), 2) + el2 * pow(cos(lat), 2));
//        double T8 = nu * pow(cos(lat), 5) * k0 / 120.0
//                * (5 - 18 * pow(tan(lat), 2) + pow(tan(lat), 4) + 14 * el2 * pow(cos(lat), 2) - 58 * pow(tan(lat), 2) * el2 * pow(cos(lat), 2) + 13 * el4 * pow(cos(lat), 4)
//                + 4 * el6 * pow(cos(lat), 6) - 64 * pow(tan(lat), 2) * el4 * pow(cos(lat), 4) - 24 * pow(tan(lat), 2) * el6 * pow(cos(lat), 6));
//        double T9 = nu * (pow(cos(lat), 7) * k0) / 5040.0
//                * (61 - 479 * pow(tan(lat), 2) + 179 * pow(tan(lat), 4) - pow(tan(lat), 6));
        double T10 = tan(phi1) / (2 * rho * nu * pow(k0, 2));
        double T11 = tan(phi1) / (24.0 * rho * pow(nu, 3) * pow(k0, 4))
                * (5 + 3 * pow(tan(phi1), 2) + el2 * pow(cos(phi1), 2) - 4 * el4 * pow(cos(phi1), 4) - 9 * pow(tan(phi1), 2) * el2 * pow(cos(phi1), 2));
        double T12 = tan(phi1) / (720.0 * rho * pow(nu, 5) * pow(k0, 6))
                * (61 + 90 * pow(tan(phi1), 2) + 46 * el2 * pow(cos(phi1), 2) + 45 * pow(tan(phi1), 4) - 252 * pow(tan(phi1), 2) * el2 * pow(cos(phi1), 2)
                - 3 * el4 * pow(cos(phi1), 4) + 100 * el6 * pow(cos(phi1), 6) - 66 * pow(tan(phi1), 2) * el4 * pow(cos(phi1), 4)
                - 90 * pow(tan(phi1), 4) * el2 * pow(cos(phi1), 2) + 88 * el8 * pow(cos(phi1), 8) + 225 * pow(tan(phi1), 4) * el4 * pow(cos(phi1), 4)
                + 84 * pow(tan(phi1), 2) * el6 * pow(cos(phi1), 6) - 192 * pow(tan(phi1), 2) * el8 * pow(cos(phi1), 8));
        double T13 = tan(phi1) / (40320.0 * rho * pow(nu, 7) * pow(k0, 8))
                * (1385 + 3633 * pow(tan(phi1), 2) + 4095 * pow(tan(phi1), 4) + 1575 * pow(tan(phi1), 6));
        double T14 = 1 / (nu * cos(phi1) * k0);
        double T15 = 1 / (6 * pow(nu, 3) * cos(phi1) * pow(k0, 3))
                * (1 + 2 * pow(tan(phi1), 2) + el2 * pow(cos(phi1), 2));
        double T16 = 1.0 / (120 * pow(nu, 5) * cos(phi1) * pow(k0, 5))
                * (5 + 6 * el2 * pow(cos(phi1), 2) + 28 * pow(tan(phi1), 2) - 3 * el4 * pow(cos(phi1), 4) + 8 * pow(tan(phi1), 2) * el2 * pow(cos(phi1), 2)
                + 24 * pow(tan(phi1), 4) - 4 * el6 * pow(cos(phi1), 6) + 4 * pow(tan(phi1), 2) * el4 * pow(cos(phi1), 4) + 24 * pow(tan(phi1), 2) * el6 * pow(cos(phi1), 6));
        double T17 = 1 / (5040.0 * pow(nu, 7) * cos(phi1) * pow(k0, 7))
                * (61 + 662 * pow(tan(phi1), 2) + 1320 * pow(tan(phi1), 4) + 720 * pow(tan(phi1), 6));
//        double T18 = sin(lat);
//        double T19 = sin(lat) * pow(cos(lat), 2) / 3.0
//                * (1 + 3 * el2 * pow(cos(lat), 2) + 2 * el4 * pow(cos(lat), 4));
//        double T20 = sin(lat) * pow(cos(lat), 4) / 15.0
//                * (2 - pow(tan(lat), 2) + 15 * el2 * pow(cos(lat), 2) + 35 * el4 * pow(cos(lat), 4) - 15 * pow(tan(lat), 2) * el2 * pow(cos(lat), 2 + 33 * el6 * pow(cos(lat), 6))
//                - 50 * pow(tan(lat), 2) * el4 * pow(cos(lat), 4) + 11 * el8 * pow(cos(lat), 8) - 60 * pow(tan(lat), 2) * el6 * pow(cos(lat), 6)
//                - 24 * pow(tan(lat), 2) * el8 * pow(cos(lat), 8));
//        double T21 = sin(lat) * pow(cos(lat), 6) / 315.0
//                * (17 - 26 * pow(tan(lat), 2) + 2 * pow(tan(lat), 4));
        double T22 = tan(phi1) / (nu * k0);
        // </editor-fold>

//        double deltaLon = lon - lon0;
//        double deltaLonSeconds = deltaLon * 60 * 60;
//        double p_ = 0.0001 * abs(deltaLonSeconds);
//        checkValues("p_", p_, 1.0636842);

        double oneSecond = new AngleValue(0, 0, 1, true).toDegreeDecimal();
        double sin1 = sin(Math.toRadians(oneSecond));
//        double f1 = T1;
//        checkValues("f1", f1, 3791005.225);
//
//        double f2 = T2 * pow(sin1, 2) * 1E8;
//        checkValues("f2", f2, 3490.001);
//
//        double f3 = T3 * pow(sin1, 4) * 1E16;
//        checkValues("f3", f3, 2.138);
//
//        double f4 = T6 * sin1 * 1E4;
//        checkValues("f4", f4, 255749.833);
//
//        double f5 = T7 * pow(sin1, 3) * 1E12;
//        checkValues("f5", f5, 37.004);

        double f7 = (T10 / sin1) * 1E12;
        double f8 = (T11 / sin1) * 1E24;
        double f9 = (T14 / sin1) * 1E6;
        double f10 = (T15 / sin1) * 1E18;
//        double f12 = T18 * 1E4;
//        double f13 = (T19 * pow(sin1, 2)) * 1E12;
        double f15 = (T22 / sin1) * 1E6;

        // --------------------------------------------------------------------
//        double A6 = (T4 * pow(sin1, 6)) * 1E24;
//        checkValues("A6", A6, 0.000871);

//        double B5 = (T8 * pow(sin1, 5)) * 1E20;
//        checkValues("B5", B5, -0.0175);

//        double C5 = (T20 * pow(sin1, 4)) * 1E20;
        double D6 = (T12 / sin1) * 1E36;
        double E5 = (T16 / sin1) * 1E30;

        double np1, np2, np3, np4, np5, ep1, ep2, ep3, ep4;

        // <editor-fold desc="transformacao de UTM para geografica">
        double q = 0.000001 * (easting - FALSE_EASTING);

        double de = easting - FALSE_EASTING;
        double lat = phi1 - pow(de, 2.0) * T10 + pow(de, 4.0) * T11 - pow(de, 6.0) * T12
                + pow(de, 8.0) * T13;
        double newDeltaLon = de * T14 - pow(de, 3.0) * T15 + pow(de, 5.0) * T16 - pow(de, 7.0) * T17;
        double lon = lon0 + Math.toDegrees(newDeltaLon);
        System.out.println(new AngleValue(Math.toDegrees(lat)));
        System.out.println(new AngleValue(lon));
        // </editor-fold>

        //<editor-fold desc="convergencia para coordenadas geograficas">
        double T18 = sin(lat);
        double T19 = sin(lat) * pow(cos(lat), 2) / 3.0
                * (1 + 3 * el2 * pow(cos(lat), 2) + 2 * el4 * pow(cos(lat), 4));
        double T20 = sin(lat) * pow(cos(lat), 4) / 15.0
                * (2 - pow(tan(lat), 2) + 15 * el2 * pow(cos(lat), 2) + 35 * el4 * pow(cos(lat), 4) - 15 * pow(tan(lat), 2) * el2 * pow(cos(lat), 2 + 33 * el6 * pow(cos(lat), 6))
                - 50 * pow(tan(lat), 2) * el4 * pow(cos(lat), 4) + 11 * el8 * pow(cos(lat), 8) - 60 * pow(tan(lat), 2) * el6 * pow(cos(lat), 6)
                - 24 * pow(tan(lat), 2) * el8 * pow(cos(lat), 8));

        double deltaLon = lon - lon0;
        double deltaLonSeconds = deltaLon * 60 * 60;
        double p_ = 0.0001 * abs(deltaLonSeconds);


        double C5 = (T20 * pow(sin1, 4)) * 1E20;

        double f12 = T18 * 1E4;
        double f13 = (T19 * pow(sin1, 2)) * 1E12;

        double convergence = f12 * p_ + f13 * pow(p_, 3) + C5 * pow(p_, 5);
        System.out.println("convergence from geographic: " + new AngleValue(convergence / 3600));
        //</editor-fold>
    }

    public static void checkValues(String nameTest, double val, double... otherValues) {
        if (toCheck == false) {
            return;
        }
        if (nameTest != null) {
            System.out.println("testing " + nameTest);
        }
        checkValues(val, otherValues);
        System.out.println("=================================");
    }

    public static void checkValues(double val, double... otherValues) {
        if (toCheck == false) {
            return;
        }
        double tolerance = 1;

        for (int i = 0; i < otherValues.length; i++) {
            double percentage = (val / otherValues[i]) * 100;
            if (percentage < (100 - tolerance) || percentage > (100 + tolerance)) {
                throw new RuntimeException(String.format("Unexpected different values: %f != %f in test number %d; percentage: %.2f", val, otherValues[i], i + 1, percentage));
            } else {
                System.out.println(percentage);
            }
        }
    }

    public static double calcRHO(double a, double e2, double latRad) {
        return a * (1 - e2)
                / pow(1 - e2 * pow(sin(latRad), 2), 3 / 2.0);
    }

    public static double calcNU(double a, double e2, double latRad) {
        return a
                / pow((1 - e2 * pow(sin(latRad), 2)), 1 / 2.0);
    }

    public static double calcS(double A, double B, double C, double D, double E, double latRad) {
        return A * latRad - B * sin(2 * latRad) + C * sin(4 * latRad) - D * sin(6 * latRad) + E * sin(8 * latRad);
    }
}
