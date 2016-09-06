/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.coordinate;

import br.com.geomapa.util.AngleValue;
import br.com.geomapa.geodesic.datum.Ellipsoid;
import br.com.geomapa.geodesic.TransverseMercator;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public class GeographicCoordinate implements Coordinate {

    private Ellipsoid ellipsoid;
    private Latitude latitude;
    private Longitude longitude;
    private double height;
    private TransverseMercator transverseMercator;

    public GeographicCoordinate(Ellipsoid ellipsoid, Latitude latitude, Longitude longitude) {
        this.ellipsoid = ellipsoid;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeographicCoordinate(Ellipsoid ellipsoid, Latitude latitude, Longitude longitude, double height) {
        this.ellipsoid = ellipsoid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
    }

    @Override
    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    public Latitude getLatitude() {
        return latitude;
    }

    public Longitude getLongitude() {
        return longitude;
    }

    @Override
    public double getEllipsoidalHeight() {
        return height;
    }

    @Override
    public CartesianCoordinate toCartesian() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UTMCoordinate toUTM() {
        return getTransverseMercator().convertoToUTM(this, getLongitude().getCentralMeridian().toDegreeDecimal());
    }
    
    public UTMCoordinate toUTM(int zone) {
        return getTransverseMercator().convertoToUTM(this, Longitude.calcCentralMeridian(zone));
    }

    @Override
    public GeographicCoordinate toGeodesic() {
        return this;
    }

    @Override
    public AngleValue getMeridianConvergence() {
        return getTransverseMercator().convergenceFromGeographic(this);
    }

    public double getScaleCorrection() {
        return getTransverseMercator().scaleCorrection(this);
    }

    private TransverseMercator getTransverseMercator() {
        if (ellipsoid == null) {
            throw new IllegalArgumentException("Não é possível realizar a conversão porque esta coordenada não possui um datum definido.");
        }
        if (this.transverseMercator == null) {
            this.transverseMercator = new TransverseMercator(longitude.getCentralMeridian(), ellipsoid);
        }
        return this.transverseMercator;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s , %s", getLatitude(), getLongitude());
    }
}

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/* Vincenty Direct Solution of Geodesics on the Ellipsoid (c) Chris Veness 2005-2011              */
/*                                                                                                */
/* from: Vincenty direct formula - T Vincenty, "Direct and Inverse Solutions of Geodesics on the  */
/*       Ellipsoid with application of nested equations", Survey Review, vol XXII no 176, 1975    */
/*       http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf                                             */
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/**
 * Calculates destination point given start point lat/long, bearing & distance, 
 * using Vincenty inverse formula for ellipsoids
 *
 * @param   azimuth
 * @param   distance
 * @return  coordinate
 */
//    public GeographicCoordinate projection(AngleValue azimuth, double distance) {
//        Datum datum = getEllipsoid().getDatum();
//        double a = datum.getSemiMajorAxis(), b = datum.getSemiMinorAxis(), f = datum.getInverseFlattening();
//        double s = distance;
//        double alpha1 = azimuth.toRadians();
//        double sinAlpha1 = Math.sin(alpha1);
//        double cosAlpha1 = Math.cos(alpha1);
//
//        double lat1 = getLatitude().toRadians();
//        double lon1 = getLongitude().toRadians();
//
//        double tanU1 = (1 - f) * Math.tan(lat1);
//        double cosU1 = 1 / Math.sqrt((1 + tanU1 * tanU1)), sinU1 = tanU1 * cosU1;
//        double sigma1 = Math.atan2(tanU1, cosAlpha1);
//        double sinAlpha = cosU1 * sinAlpha1;
//        double cosSqAlpha = 1 - sinAlpha * sinAlpha;
//        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
//        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
//        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
//
//        double cos2SigmaM = 0, sinSigma = 0, cosSigma = 0, deltaSigma;
//
//        double sigma = s / (b * A), sigmaP = 2 * Math.PI;
//        while (Math.abs(sigma - sigmaP) > 1e-12) {
//            cos2SigmaM = Math.cos(2 * sigma1 + sigma);
//            sinSigma = Math.sin(sigma);
//            cosSigma = Math.cos(sigma);
//            deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)
//                    - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
//            sigmaP = sigma;
//            sigma = s / (b * A) + deltaSigma;
//        }
//
//        double tmp = sinU1 * sinSigma - cosU1 * cosSigma * cosAlpha1;
//        double lat2 = Math.atan2(sinU1 * cosSigma + cosU1 * sinSigma * cosAlpha1,
//                (1 - f) * Math.sqrt(sinAlpha * sinAlpha + tmp * tmp));
//        double lambda = Math.atan2(sinSigma * sinAlpha1, cosU1 * cosSigma - sinU1 * sinSigma * cosAlpha1);
//        double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
//        double L = lambda - (1 - C) * f * sinAlpha
//                * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
//        double lon2 = (lon1 + L + 3 * Math.PI) % (2 * Math.PI) - Math.PI;  // normalise to -180...+180
//
////        Math.atan2(sinAlpha, -tmp);  // final bearing, if required
//
//        return new GeographicCoordinate(getEllipsoid(), new Latitude(lat2), new Longitude(lon2));
//    }