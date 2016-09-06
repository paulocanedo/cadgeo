/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.geodesic.point;

/**
 *
 * @author paulocanedo
 */
@Deprecated
public class PointStoredData {

    private String nextPoint;
    private String azimuth;
    private String measurementMethod;
    private String limitType;
    private String bordering; //confrontante
    private float distance;
    private float factorK;

    public PointStoredData() {
    }

    public PointStoredData(String nextPoint, String azimuth, float distance, float factorK) {
        this.nextPoint = nextPoint;
        this.azimuth = azimuth;
        this.distance = distance;
        this.factorK = factorK;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(String azimuth) {
        this.azimuth = azimuth;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getFactorK() {
        return factorK;
    }

    public void setFactorK(float factorK) {
        this.factorK = factorK;
    }

    public String getNextPoint() {
        return nextPoint;
    }

    public void setNextPoint(String nextPoint) {
        this.nextPoint = nextPoint;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public String getMeasurementMethod() {
        return measurementMethod;
    }

    public void setMeasurementMethod(String measurementMethod) {
        this.measurementMethod = measurementMethod;
    }

    public String getBordering() {
        return bordering;
    }

    public void setBordering(String bordering) {
        this.bordering = bordering;
    }

}
