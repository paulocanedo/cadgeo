/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.rbmc;

import br.com.geomapa.geodesic.coordinate.GeographicCoordinate;
import br.com.geomapa.geodesic.coordinate.Latitude;
import br.com.geomapa.geodesic.coordinate.Longitude;
import br.com.geomapa.geodesic.datum.Ellipsoid;
import br.com.geomapa.geodesic.datum.SIRGASDatum;
import br.com.geomapa.util.AngleValue;
import br.com.geomapa.util.NumberFloatUtils;
import java.util.Properties;

/**
 *
 * @author paulocanedo
 */
public class BaseRBMC implements Comparable<BaseRBMC> {
    
    private static Ellipsoid ellipsoidRBMC = new Ellipsoid(new SIRGASDatum());
    private String name;
    private String cidade;
    private String uf;
    private GeographicCoordinate coordinate;

    public BaseRBMC(String name, String cidade, String uf, GeographicCoordinate coordinate) {
        this.name = name;
        this.cidade = cidade;
        this.uf = uf;
        this.coordinate = coordinate;
    }

    public BaseRBMC(String name, Properties properties) {
        this.name = name;
        
        String lat = properties.getProperty("latitude");
        String lon = properties.getProperty("longitude");
        String alt = properties.getProperty("altitude_elipsoidal");
        Latitude latitude = new Latitude(parseStringAngle(lat).toDegreeDecimal());
        Longitude longitude = new Longitude(parseStringAngle(lon).toDegreeDecimal());
        double altitude = NumberFloatUtils.parseDouble(alt);
        
        this.coordinate = new GeographicCoordinate(ellipsoidRBMC, latitude, longitude, altitude);
        this.cidade = properties.getProperty("cidade");
        this.uf = properties.getProperty("uf");
    }
    
    private static AngleValue parseStringAngle(String angle) {
        String[] split = angle.trim().split(" ");
        
        if(split.length != 3) {
            return null;
        }
        
        double degree = Integer.parseInt(split[0].replaceAll("-", ""));
        double min = Integer.parseInt(split[1]);
        double sec = NumberFloatUtils.parseDouble(split[2]);
        
        return new AngleValue(degree, min, sec, !angle.trim().startsWith("-"));
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public GeographicCoordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(GeographicCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseRBMC other = (BaseRBMC) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equalsIgnoreCase(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(BaseRBMC o) {
        return getName().compareTo(o.getName());
    }

}
