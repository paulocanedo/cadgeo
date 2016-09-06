/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.rinex;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author paulocanedo
 */
public class RinexHeader {

    private String markerName;
    private Date firstObs;
    private Date lastObs;
    private String coord;

    public RinexHeader(String markerName, Date firstObs, Date lastObs, String coord) {
        this.markerName = markerName;
        this.firstObs = firstObs;
        this.lastObs = lastObs;
        this.coord = coord;
    }

    public RinexHeader() {
    }

    public String getCoord() {
        return coord;
    }

    public void setCoord(String coord) {
        this.coord = coord;
    }

    public Date getFirstObs() {
        return firstObs;
    }

    public void setFirstObs(Date firstObs) {
        this.firstObs = firstObs;
    }

    public Date getLastObs() {
        return lastObs;
    }

    public void setLastObs(Date lastObs) {
        this.lastObs = lastObs;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public long getDurationTime() {
        return getLastObs().getTime() - getFirstObs().getTime();
    }

    public String getDurationTimeAsString() {
        return sdf.format(getDurationTime());
    }
    private static final SimpleDateFormat sdf = new SimpleDateFormat("KK'h' mm'm' ss's'");

    static {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }
}
