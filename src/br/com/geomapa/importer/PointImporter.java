/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer;

import br.com.geomapa.geodesic.datum.Ellipsoid;
import br.com.geomapa.geodesic.GeodesicEnum;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.InvalidGeodesicPointException;
import br.com.geomapa.geodesic.point.MetaDataPoint;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.geodesic.datum.Datum;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author paulocanedo
 */
public abstract class PointImporter {

    private static final int INVALID_VALUE = -1;
    private PointImporterHandle handle;
    protected InputStream stream;
    protected GeodesicEnum[] sourceOrder;
    private boolean imported = false;
    private Ellipsoid ellipsoid;
    private Hemisphere hemisphere;
    private int utmZone;

    public PointImporter(PointImporterHandle handle, InputStream stream, GeodesicEnum[] sourceOrder, int utmZone, Hemisphere hemisphere, Datum datum) {
        if (handle == null || stream == null || sourceOrder == null) {
            throw new NullPointerException("Nenhum dos campos: handle, stream e sourceOrder podem ser nulos");
        }

        this.handle = handle;
        this.stream = stream;
        this.sourceOrder = sourceOrder;
        this.utmZone = utmZone;
        this.hemisphere = hemisphere;
        this.ellipsoid = new Ellipsoid(datum);
    }

    public final void importData() throws IOException, InvalidGeodesicPointException {
        if (imported) {
            throw new RuntimeException("Não pode importar novamente da mesma fonte de dados");
        }

        handle.startImport();
        String[] record;
        while ((record = parseRecord(stream)) != null) {
            record = swapValues(record, sourceOrder);
            handle.handlePoint(readGeodesicPoint(record));
        }

        handle.endImport();
        imported = true;
    }

    public void removeImport() {
        throw new RuntimeException("Not supported");
    }

    /*
     * Deve retornar null caso não exista mais registro para parsear
     */
    protected abstract String[] parseRecord(InputStream stream) throws IOException;

    /*
     * Normalizar o vetor de string de acordo com a sequência definida pela enumeracao em GeodesicContent.values()
     */
    protected String[] swapValues(String[] record, GeodesicEnum[] sourceOrder) {
        String[] newRecord = new String[defaultSequence.length];
        ArrayList<GeodesicEnum> aSourceOrder = new ArrayList<GeodesicEnum>(Arrays.asList(sourceOrder));

        for (int i = 0; i < defaultSequence.length; i++) {
            int position = aSourceOrder.indexOf(defaultSequence[i]);
            if (position < 0) {
                newRecord[i] = null;
                continue;
            }
            newRecord[i] = record[position];
        }

        return newRecord;
    }

    protected GeodesicPoint readGeodesicPoint(String[] record) throws InvalidGeodesicPointException {
        String name = null, timeStartObservation = null, timeEndObservation = null;
        double east = 0, north = 0, height_elipsoidal = 0;
        float qx = INVALID_VALUE, qy = INVALID_VALUE, qz = INVALID_VALUE;
        Boolean ambiguity = null;
        String currentValue;

        //valores importados, nao devem ser recalculados
        String nextPoint = null, azimuth = null, measurementMethod = null, limitType = null, bordering = null;
        float distance = 0.0f, factorK = 0.0f;

        for (GeodesicEnum geocontent : sourceOrder) {
            switch (geocontent) {
                case NAME:
                    name = record[geocontent.ordinal()].trim();
                    break;
                case FIXED_AMBIGUITY:
                    currentValue = record[geocontent.ordinal()];
                    ambiguity = (currentValue != null) ? (currentValue.equals("yes") || currentValue.equals("sim")) : null;
                    break;
                case EAST:
                    currentValue = parseFloatValue(record[geocontent.ordinal()]).trim();
                    east = currentValue != null ? Double.parseDouble(currentValue) : Double.NEGATIVE_INFINITY;
                    //TODO change to check empty currentValue instead just check if is null
                    break;
                case NORTH:
                    currentValue = parseFloatValue(record[geocontent.ordinal()]).trim();
                    north = currentValue != null ? Double.parseDouble(currentValue) : Double.NEGATIVE_INFINITY;
                    break;
                case LATITUDE:
                    break;
                case LONGITUDE:
                    break;
                case ELIPSOIDAL_HEIGHT:
                    currentValue = parseFloatValue(record[geocontent.ordinal()]);
                    height_elipsoidal = (currentValue != null ? Float.parseFloat(currentValue) : Double.NEGATIVE_INFINITY);
                    break;
                case QUALITY_HOR: {
                    currentValue = parseFloatValue(record[geocontent.ordinal()]);
                    try {
                        qx = qy = currentValue != null ? Float.parseFloat(currentValue) : INVALID_VALUE;
                    } catch (NumberFormatException ex) {
                        qx = qy = INVALID_VALUE;
                    }
                    break;
                }
                case QUALITY_X:
                    currentValue = parseFloatValue(record[geocontent.ordinal()]);
                    qx = currentValue != null ? Float.parseFloat(currentValue) : INVALID_VALUE;
                    break;
                case QUALITY_Y:
                    currentValue = parseFloatValue(record[geocontent.ordinal()]);
                    qy = currentValue != null ? Float.parseFloat(currentValue) : INVALID_VALUE;
                    break;
                case QUALITY_VER:
                case QUALITY_Z:
                    currentValue = parseFloatValue(record[geocontent.ordinal()]);
                    try {
                        qz = currentValue != null ? Float.parseFloat(currentValue) : INVALID_VALUE;
                    } catch (NumberFormatException ex) {
                        qz = INVALID_VALUE;
                    }
                    break;
                case NEXT_POINT:
                    nextPoint = record[geocontent.ordinal()];
                    break;
                case AZIMUTH:
                    azimuth = record[geocontent.ordinal()];
                    break;
                case DISTANCE:
                    currentValue = record[geocontent.ordinal()];
                    distance = currentValue != null ? Float.parseFloat(parseFloatValue(currentValue.replace("m", "").trim())) : INVALID_VALUE;
                    break;
                case FACTOR_K:
                    currentValue = parseFloatValue(record[geocontent.ordinal()]);
                    factorK = currentValue != null ? Float.parseFloat(currentValue) : INVALID_VALUE;
                    break;
                case MEASUREMENT_METHOD:
                    measurementMethod = record[geocontent.ordinal()];
                    break;
                case LIMIT_TYPE:
                    limitType = record[geocontent.ordinal()];
                    break;
                case BORDERING:
                    bordering = record[geocontent.ordinal()];
                    break;
                case TIME_FIRST_OBSERVATION:
                    timeStartObservation = record[geocontent.ordinal()];
                    break;
                case TIME_LAST_OBSERVATION:
                    timeEndObservation = record[geocontent.ordinal()];
                    break;
                case UNKOWN:
                    break;
                default:
                    throw new AssertionError();
            }
        }

        MetaDataPoint metadata = new MetaDataPoint(ambiguity, qx, qy, qz);
//        metadata.setTimeStartObservation(timeStartObservation);
//        metadata.setTimeEndObservation(timeEndObservation);
        if (measurementMethod != null) {
            metadata.setMeasurementMethod(measurementMethod.trim().isEmpty() ? null : MetaDataPoint.MeasurementMethod.valueOf(measurementMethod));
        }

        UTMCoordinate coord = new UTMCoordinate(ellipsoid, utmZone, hemisphere, east, north, height_elipsoidal);
        try {
            GeodesicPoint point = new GeodesicPoint(coord, name, metadata);
            if (name == null || name.length() == 0) {
                throw new InvalidGeodesicPointException(point);
            }

            return point;
        } catch (ExceptionInInitializerError ex) {
            Throwable exception = ex.getException();
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String parseFloatValue(String value) {
        if (value != null && value.contains(",")) {
            value = value.replace(".", "").replace(",", ".");
        }
        return value;
    }
    public static final GeodesicEnum[] defaultSequence = GeodesicEnum.values();
    public static final GeodesicEnum[] simpleCsvSequence = new GeodesicEnum[]{
        GeodesicEnum.NAME,
        GeodesicEnum.EAST,
        GeodesicEnum.NORTH,
        GeodesicEnum.ELIPSOIDAL_HEIGHT
    };
    public static final GeodesicEnum[] csvSequenceShort = new GeodesicEnum[]{
        GeodesicEnum.NAME,
        GeodesicEnum.EAST,
        GeodesicEnum.NORTH,
        GeodesicEnum.ELIPSOIDAL_HEIGHT,
        GeodesicEnum.QUALITY_HOR,
        GeodesicEnum.QUALITY_VER
    };
    public static final GeodesicEnum[] csvSequence = new GeodesicEnum[]{
        GeodesicEnum.NAME,
        GeodesicEnum.EAST,
        GeodesicEnum.QUALITY_X,
        GeodesicEnum.NORTH,
        GeodesicEnum.QUALITY_Y,
        GeodesicEnum.ELIPSOIDAL_HEIGHT,
        GeodesicEnum.QUALITY_Z,};
    public static final GeodesicEnum[] projectSequence = new GeodesicEnum[]{
        GeodesicEnum.NAME,
        GeodesicEnum.EAST,
        GeodesicEnum.NORTH,
        GeodesicEnum.ELIPSOIDAL_HEIGHT,
        GeodesicEnum.QUALITY_X,
        GeodesicEnum.QUALITY_Y,
        GeodesicEnum.QUALITY_Z,
        GeodesicEnum.MEASUREMENT_METHOD
    };
    public static final GeodesicEnum[] projectOtherZonesSequence = new GeodesicEnum[]{
        GeodesicEnum.NAME,
        GeodesicEnum.EAST,
        GeodesicEnum.NORTH
    };
    public static final GeodesicEnum[] defaultCalculoAreaSequence = new GeodesicEnum[]{
        GeodesicEnum.NAME,
        GeodesicEnum.NEXT_POINT,
        GeodesicEnum.NORTH,
        GeodesicEnum.EAST,
        GeodesicEnum.AZIMUTH,
        GeodesicEnum.DISTANCE,
        GeodesicEnum.FACTOR_K,
        GeodesicEnum.LATITUDE,
        GeodesicEnum.LONGITUDE
    };
    public static final GeodesicEnum[] astechSequence = new GeodesicEnum[]{
        GeodesicEnum.NAME,
        GeodesicEnum.EAST,
        GeodesicEnum.NORTH,
        GeodesicEnum.ELIPSOIDAL_HEIGHT,
        GeodesicEnum.QUALITY_X,
        GeodesicEnum.QUALITY_Y,
        GeodesicEnum.QUALITY_Z
    };
    public static final GeodesicEnum[] topconPlusSequence = new GeodesicEnum[]{
        GeodesicEnum.NAME,
        GeodesicEnum.NORTH,
        GeodesicEnum.EAST,
        GeodesicEnum.ELIPSOIDAL_HEIGHT,
        GeodesicEnum.QUALITY_X,
        GeodesicEnum.QUALITY_Y,
        GeodesicEnum.QUALITY_Z
    };
}
