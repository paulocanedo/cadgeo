/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.point;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 *
 * @author paulocanedo
 */
public class MetaDataPoint {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd kk mm ss");
    private Boolean fixedAmbiguity;
    private MeasurementMethod measurementMethod;
    private double qx;
    private double qy;
    private double qz;
    private File rinex;

    public MetaDataPoint(Boolean fixedAmbiguity, double qx, double qy, double qz) {
        this.fixedAmbiguity = fixedAmbiguity;
        this.qx = qx;
        this.qy = qy;
        this.qz = qz;
    }

    public Boolean isFixedAmbiguity() {
        return fixedAmbiguity;
    }

    public void setQx(double qx) {
        this.qx = qx;
    }
    
    public double getQx() {
        return qx;
    }

    public void setQy(double qy) {
        this.qy = qy;
    }
    
    public double getQy() {
        return qy;
    }

    public void setQz(double qz) {
        this.qz = qz;
    }
    
    public double getQz() {
        return qz;
    }

    /**
     * Use RinexUtil.findRinexFile instead
     * @return
     * @deprecated
     */
    @Deprecated
    public File getRinex() {
        return rinex;
    }

    /**
     * Does not exist anymore
     * @param rinex
     * @deprecated
     */
    @Deprecated
    public void setRinex(File rinex) {
        this.rinex = rinex;
    }

    public MeasurementMethod getMeasurementMethod() {
        return measurementMethod;
    }

    public void setMeasurementMethod(MeasurementMethod measurementMethod) {
        this.measurementMethod = measurementMethod;
    }

    public enum MeasurementMethod {

        LT1("Levantamento por poligonal coincidente ao limite"),
        LT2("Levantamento de limite por irradiação"),
        LG1("Posicionamento relativo estático"),
        LG2("Posicionamento relativo estático rápido"),
        LG3("Posicionamento relativo semicinemático (stop and go)"),
        LG4("Posicionamento relativo cinemático"),
        LG5("Posicionamento RTK"),
        LG6("Posicionamento por DGPS ou WADGPS"),
        LG7("Posicionamento diferencial por meio do código C/A"),
        LV("Levantamento por método indireto");

        private MeasurementMethod(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
        private String description;

        @Override
        public String toString() {
            return getDescription();
        }
    }
}
