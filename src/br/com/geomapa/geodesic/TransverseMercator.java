/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic;

import br.com.geomapa.util.AngleValue;
import br.com.geomapa.geodesic.coordinate.Longitude;
import br.com.geomapa.geodesic.coordinate.Latitude;
import br.com.geomapa.geodesic.datum.Ellipsoid;
import br.com.geomapa.geodesic.coordinate.GeographicCoordinate;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.tan;
import static java.lang.Math.pow;
import static java.lang.Math.abs;

/**
 *
 * @author paulocanedo
 */
public final class TransverseMercator {

    private double deltaEasting = 40000000.0;
    private double deltaNorthing = 40000000.0;
    private double falseEasting = 500000.0;
    private double falseNorthing = 10000000.0;
    private double scaleFactor0 = 0.9996;
    private Longitude centralMeridian = new Longitude(0.0);
    private Latitude latitudeOfTrueScale = new Latitude(0.0);
    private Ellipsoid ellipsoid;
    private static final double sin1 = sin(new AngleValue(0, 0, 1, true).toRadians());

    public TransverseMercator(Longitude centralMeridian, Ellipsoid ellipsoid) {
        this.centralMeridian = centralMeridian;
        this.ellipsoid = ellipsoid;
    }

    public TransverseMercator(Longitude centralMeridian, Latitude latitudeOfTrueScale, Ellipsoid ellipsoid) {
        this.centralMeridian = centralMeridian;
        this.latitudeOfTrueScale = latitudeOfTrueScale;
        this.ellipsoid = ellipsoid;
    }

    public TransverseMercator(double deltaEasting, double deltaNorthing, double falseEasting, double falseNorthing, double scaleFactor, Longitude centralMeridian, Latitude latitudeOfTrueScale, Ellipsoid ellipsoid) {
        this.deltaEasting = deltaEasting;
        this.deltaNorthing = deltaNorthing;
        this.falseEasting = falseEasting;
        this.falseNorthing = falseNorthing;
        this.scaleFactor0 = scaleFactor;
        this.centralMeridian = centralMeridian;
        this.latitudeOfTrueScale = latitudeOfTrueScale;
        this.ellipsoid = ellipsoid;
    }

    public UTMCoordinate convertoToUTM(GeographicCoordinate coordinate, double centralMeridian) {
        double lon = coordinate.getLongitude().toDegreeDecimal();
        double latRad = coordinate.getLatitude().toRadians();

        double el2 = ellipsoid.getEl2();
        double eta = el2 * pow(cos(latRad), 2);
        double el4 = eta * eta;
        double el6 = el4 * eta;
        double el8 = el6 * eta;

        double S = ellipsoid.calcS(latRad);
        double nu = ellipsoid.calcNU(latRad);

        double T1 = S * scaleFactor0;
        double T2 = nu * sin(latRad) * cos(latRad) * scaleFactor0 / 2.0;
        double T3 = nu * sin(latRad) * pow(cos(latRad), 3) * scaleFactor0 / 24.0
                * (5 - pow(tan(latRad), 2) + 9 * el2 * pow(cos(latRad), 2) + 4 * el4 * pow(cos(latRad), 4));
        double T4 = ((nu * sin(latRad) * pow(cos(latRad), 5) * scaleFactor0) / 720.0)
                * (61 - 58 * pow(tan(latRad), 2) + pow(tan(latRad), 4) + 270 * el2 * pow(cos(latRad), 2) - 330 * pow(tan(latRad), 2) * el2 * pow(cos(latRad), 2)
                + 445 * el4 * pow(cos(latRad), 4) + 324 * el6 * pow(cos(latRad), 6) - 680 * pow(tan(latRad), 2) * el4 * pow(cos(latRad), 4)
                + 88 * el8 * pow(cos(latRad), 8) - 600 * pow(tan(latRad), 2) * el6 * pow(cos(latRad), 6) - 192 * pow(tan(latRad), 2) * el8 * pow(cos(latRad), 8));
        double T6 = nu * cos(latRad) * scaleFactor0;
        double T7 = nu * pow(cos(latRad), 3) * scaleFactor0 / 6.0
                * (1 - pow(tan(latRad), 2) + el2 * pow(cos(latRad), 2));
        double T8 = nu * pow(cos(latRad), 5) * scaleFactor0 / 120.0
                * (5 - 18 * pow(tan(latRad), 2) + pow(tan(latRad), 4) + 14 * el2 * pow(cos(latRad), 2) - 58 * pow(tan(latRad), 2) * el2 * pow(cos(latRad), 2) + 13 * el4 * pow(cos(latRad), 4)
                + 4 * el6 * pow(cos(latRad), 6) - 64 * pow(tan(latRad), 2) * el4 * pow(cos(latRad), 4) - 24 * pow(tan(latRad), 2) * el6 * pow(cos(latRad), 6));

        double deltaLon = lon - centralMeridian;

        double deltaLonSeconds = deltaLon * 60 * 60;
        double p = 0.0001 * abs(deltaLonSeconds);

        double f1 = T1;
        double f2 = T2 * pow(sin1, 2) * 1E8;
        double f3 = T3 * pow(sin1, 4) * 1E16;
        double f4 = T6 * sin1 * 1E4;
        double f5 = T7 * pow(sin1, 3) * 1E12;

        double A6 = (T4 * pow(sin1, 6)) * 1E24;
        double B5 = (T8 * pow(sin1, 5)) * 1E20;

        double utm_north = f1 + f2 * pow(p, 2) + f3 * pow(p, 4) + A6 * pow(p, 6);
        if (latRad < 0) {
            utm_north = falseNorthing + utm_north;
        }

        double deltaEast = (f4 * p + f5 * pow(p, 3) + B5 * pow(p, 5));
        double utm_east = deltaLon > 0 ? (falseEasting + deltaEast) : (falseEasting - deltaEast);

        int zone = coordinate.getLongitude().getZone();
        Hemisphere hemisphere = coordinate.getLatitude().getHemisphere();

        UTMCoordinate utmCoord = new UTMCoordinate(ellipsoid, zone, hemisphere, utm_east, utm_north);

        return utmCoord;
    }

    public GeographicCoordinate convertToGeographic(UTMCoordinate coordinate) {
        double northing = coordinate.getNorth(), easting = coordinate.getEast();
        if (coordinate.getHemisphere() == Hemisphere.SOUTH) {
            northing = northing - falseNorthing;
        }

        double lon0 = centralMeridian.toDegreeDecimal();
        double phi1 = latitudeOfTrueScale.toRadians();

        double tmdo = ellipsoid.calcS(phi1);
        double tmd = tmdo + northing / scaleFactor0;
        phi1 = tmd / ellipsoid.calcRHO(phi1);
        double t10, sr;
        for (int i = 0; i < 5; i++) {
            t10 = ellipsoid.calcS(phi1);
            sr = ellipsoid.calcRHO(phi1);
            phi1 = phi1 + (tmd - t10) / sr;
        }
        double el2 = ellipsoid.getEl2();
        double eta = el2 * pow(cos(phi1), 2.0);
        double el4 = eta * eta;
        double el6 = el4 * eta;
        double el8 = el6 * eta;

        double rho = ellipsoid.calcRHO(phi1);
        double nu = ellipsoid.calcNU(phi1);

        double T10 = tan(phi1) / (2 * rho * nu * pow(scaleFactor0, 2));
        double T11 = tan(phi1) / (24.0 * rho * pow(nu, 3) * pow(scaleFactor0, 4))
                * (5 + 3 * pow(tan(phi1), 2) + el2 * pow(cos(phi1), 2) - 4 * el4 * pow(cos(phi1), 4) - 9 * pow(tan(phi1), 2) * el2 * pow(cos(phi1), 2));
        double T12 = tan(phi1) / (720.0 * rho * pow(nu, 5) * pow(scaleFactor0, 6))
                * (61 + 90 * pow(tan(phi1), 2) + 46 * el2 * pow(cos(phi1), 2) + 45 * pow(tan(phi1), 4) - 252 * pow(tan(phi1), 2) * el2 * pow(cos(phi1), 2)
                - 3 * el4 * pow(cos(phi1), 4) + 100 * el6 * pow(cos(phi1), 6) - 66 * pow(tan(phi1), 2) * el4 * pow(cos(phi1), 4)
                - 90 * pow(tan(phi1), 4) * el2 * pow(cos(phi1), 2) + 88 * el8 * pow(cos(phi1), 8) + 225 * pow(tan(phi1), 4) * el4 * pow(cos(phi1), 4)
                + 84 * pow(tan(phi1), 2) * el6 * pow(cos(phi1), 6) - 192 * pow(tan(phi1), 2) * el8 * pow(cos(phi1), 8));
        double T13 = tan(phi1) / (40320.0 * rho * pow(nu, 7) * pow(scaleFactor0, 8))
                * (1385 + 3633 * pow(tan(phi1), 2) + 4095 * pow(tan(phi1), 4) + 1575 * pow(tan(phi1), 6));
        double T14 = 1.0 / (nu * cos(phi1) * scaleFactor0);
        double T15 = (1.0 / (6 * pow(nu, 3) * cos(phi1) * pow(scaleFactor0, 3)))
                * (1 + 2 * pow(tan(phi1), 2) + el2 * pow(cos(phi1), 2));
        double T16 = 1.0 / (120 * pow(nu, 5) * cos(phi1) * pow(scaleFactor0, 5))
                * (5 + 6 * el2 * pow(cos(phi1), 2) + 28 * pow(tan(phi1), 2) - 3 * el4 * pow(cos(phi1), 4) + 8 * pow(tan(phi1), 2) * el2 * pow(cos(phi1), 2)
                + 24 * pow(tan(phi1), 4) - 4 * el6 * pow(cos(phi1), 6) + 4 * pow(tan(phi1), 2) * el4 * pow(cos(phi1), 4) + 24 * pow(tan(phi1), 2) * el6 * pow(cos(phi1), 6));

        double de = easting - falseEasting;

        if (Math.abs(de) < 0.0001) {
            de = 0.0;
        }
        double q = 0.000001 * de;
        double f9 = (T14 / sin1) * 1E6;
        double f10 = (T15 / sin1) * 1E18;
        double E5 = (T16 / sin1) * 1E30;

        double lat = phi1 - pow(de, 2.0) * T10 + pow(de, 4.0) * T11 - pow(de, 6.0) * T12
                + pow(de, 8.0) * T13;
        double dlon = f9 * q - f10 * pow(q, 3) + E5 * pow(q, 5);
        double lon = lon0 + dlon / 3600;

        GeographicCoordinate geoCoord = new GeographicCoordinate(ellipsoid, new Latitude(Math.toDegrees(lat)), new Longitude(lon), coordinate.getEllipsoidalHeight());

        return geoCoord;
    }

    public AngleValue convergenceFromGeographic(GeographicCoordinate coordinate) {
        double latRad = coordinate.getLatitude().toRadians();
        double lon = coordinate.getLongitude().toDegreeDecimal();
        double lon0 = coordinate.getLongitude().getCentralMeridian().toDegreeDecimal();

        double deltaLon = lon - lon0;
        double deltaLonSeconds = deltaLon * 60 * 60;
        double p = 0.0001 * abs(deltaLonSeconds);

        double el2 = ellipsoid.getEl2();
        double eta = el2 * pow(cos(latRad), 2.0);
        double el4 = eta * eta;
        double el6 = el4 * eta;
        double el8 = el6 * eta;

        double T18 = sin(latRad);
        double T19 = sin(latRad) * pow(cos(latRad), 2) / 3.0
                * (1 + 3 * el2 * pow(cos(latRad), 2) + 2 * el4 * pow(cos(latRad), 4));
        double T20 = sin(latRad) * pow(cos(latRad), 4) / 15.0
                * (2 - pow(tan(latRad), 2) + 15 * el2 * pow(cos(latRad), 2) + 35 * el4 * pow(cos(latRad), 4) - 15 * pow(tan(latRad), 2) * el2 * pow(cos(latRad), 2) + 33 * el6 * pow(cos(latRad), 6)
                - 50 * pow(tan(latRad), 2) * el4 * pow(cos(latRad), 4) + 11 * el8 * pow(cos(latRad), 8) - 60 * pow(tan(latRad), 2) * el6 * pow(cos(latRad), 6)
                - 24 * pow(tan(latRad), 2) * el8 * pow(cos(latRad), 8));

        double C5 = (T20 * pow(sin1, 4)) * 1E20;

        double f12 = T18 * 1E4;
        double f13 = (T19 * pow(sin1, 2)) * 1E12;

        double convergence = f12 * p + f13 * pow(p, 3) + C5 * pow(p, 5);
        convergence = abs(convergence);
        
        if ((deltaLon < 0 && latRad > 0) || (deltaLon > 0 && latRad < 0)) {
            convergence *= -1;
        }

        return new AngleValue(convergence / 3600);
    }

    public double scaleCorrection(GeographicCoordinate coordinate) {
        double latRad = coordinate.getLatitude().toRadians();
        double lon = coordinate.getLongitude().toDegreeDecimal();
        double lon0 = coordinate.getLongitude().getCentralMeridian().toDegreeDecimal();

        double el2 = ellipsoid.getEl2();
        double eta = el2 * pow(cos(latRad), 2.0);
        double el4 = eta * eta;
        double el6 = el4 * eta;

        double deltaLon = lon - lon0;
        double deltaLonSeconds = deltaLon * 60 * 60;
        double p = 0.0001 * abs(deltaLonSeconds);

        double T26 = pow(cos(latRad), 2) / 2 * (1 + el2 * pow(cos(latRad), 2));
        double T27 = pow(cos(latRad), 4)
                / 24.0 * (5 - 4 * pow(tan(latRad), 2) + 14 * el2 * pow(cos(latRad), 2) + 13 * el4 * pow(cos(latRad), 4) - 28 * pow(tan(latRad), 2) * el2 * pow(cos(latRad), 2) + 4 * el6 * pow(cos(latRad), 6)
                - 48 * pow(tan(latRad), 2) * el4 * pow(cos(latRad), 4) - 24 * pow(tan(latRad), 2) * el6 * pow(cos(latRad), 6));

        double f20 = T26 * pow(sin1, 2) * 1E8;
        double f21 = T27 * pow(sin1, 4) * 1E16;

        return scaleFactor0 * (1 + (f20 * pow(p, 2)) + (f21 * pow(p, 4)));
    }
}
