/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.model;

import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.VariableControl;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.geodesic.point.MetaDataPoint;
import br.com.geomapa.importer.rinex.RinexFile;
import br.com.geomapa.importer.rinex.RinexUtil;
import br.com.geomapa.main.Main;
import br.com.geomapa.util.unit.impl.AzimuthUnit;
import br.com.geomapa.util.unit.impl.Meter;
import br.com.geomapa.util.unit.specs.DirectionUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author paulocanedo
 */
public class GeodesicGridTableModel extends DefaultTableModel {

    private DistanceUnit distanceUnit = new Meter();
    private DirectionUnit directionUnit = new AzimuthUnit();
    private final DecimalFormat decimalFormat3c = new DecimalFormat("0.000");
    private Polygonal polygonal;
    private final List<String> columnNames;

    public GeodesicGridTableModel(Polygonal polygonal, List<String> columnNames) {
        this.polygonal = polygonal;
        this.columnNames = columnNames;
        decimalFormat3c.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void setPolygonal(Polygonal polygonal) {
        this.polygonal = polygonal;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LinkedList<LineDivision> points = polygonal.getLineDivisions();
        LineDivision ld = points.get(rowIndex);
        GeodesicPoint point = ld.getStartPoint();
        GeodesicPoint endPoint = ld.getEndPoint();
        MetaDataPoint metaData = point.getMetaData();
        int zonaUtm = VariableControl.getZonaUtm();

        String columnName = getColumnName(columnIndex);
        Object value = "";
        if (columnName.equals(estacao) || columnName.equals(vertice)) {
            value = point.getName();
        } else if (columnName.equals(vante)) {
            value = endPoint.getName();
        } else if (columnName.equals(coordN)) {
            value = point.getCoordinate(zonaUtm).toUTM().getNorth();
            value = decimalFormat3c.format(value);
        } else if (columnName.equals(coordE)) {
            value = point.getCoordinate(zonaUtm).toUTM().getEast();
            value = decimalFormat3c.format(value);
        } else if (columnName.equals(azimute)) {
            value = directionUnit.toString(point.azimuth(endPoint), 0);
        } else if (columnName.equals(distancia)) {
            value = distanceUnit.toString(point.horizontalDistance(endPoint), 2);
        } else if (columnName.equals(fatorEscala)) {
            value = String.format("%.8f", point.getCoordinate().toGeodesic().getScaleCorrection());
        } else if (columnName.equals(latitude)) {
            value = point.getCoordinate().toGeodesic().getLatitude();
        } else if (columnName.equals(longitude)) {
            value = point.getCoordinate().toGeodesic().getLongitude();
        } else if (columnName.equals(seq)) {
            value = rowIndex + 1;
        } else if (columnName.equals(rmsEste)) {
            value = decimalFormat3c.format(metaData == null ? "" : metaData.getQx());
        } else if (columnName.equals(rmsNorte)) {
            value = decimalFormat3c.format(metaData == null ? "" : metaData.getQy());
        } else if (columnName.equals(coordH)) {
            value = decimalFormat3c.format(point.getCoordinate().getEllipsoidalHeight());
        } else if (columnName.equals(rmsHeight)) {
            value = decimalFormat3c.format(metaData == null ? "" : metaData.getQz());
        } else if (columnName.equals(metodoAplicado)) {
            if (metaData != null && metaData.getMeasurementMethod() != null) {
                value = metaData.getMeasurementMethod().name();
            } else {
                value = "";
            }
        } else if (columnName.equals(tipoLimite)) {
            if (ld.getType() != null) {
                value = ld.getType().name();
            } else {
                value = "";
            }
        } else if (columnName.equals(rinex)) {
            File rinexFolder = Main.getInstance().getProjectInfo().getRinexFolder();
            RinexFile rinexFile = RinexUtil.findRinexFile(rinexFolder, point.getName());

            if (rinexFile == null) {
                value = "";
            } else {
                value = rinexFile.getFile().getName();
            }
        } else if (columnName.equals(nomeConfrontante)) {
            value = ld.getBorderName();
        } else if (columnName.equals(margemAguaCorrente)) {
            value = "";
        } else if (columnName.equals(direcaoAguaCorrente)) {
            value = "";
        }

        return value == null ? "" : value.toString();
    }

    @Override
    public int getRowCount() {
        if (polygonal == null || polygonal.getLineDivisions() == null) {
            return 0;
        }
        return polygonal.getLineDivisions().size();
    }
    public static final String seq = "Seq";
    public static final String estacao = "Esta\u00e7\u00e3o";
    public static final String vertice = "V\u00e9rtice";
    public static final String vante = "Vante";
    public static final String coordN = "Coord N";
    public static final String rmsNorte = "RMS N";
    public static final String coordE = "Coord E";
    public static final String rmsEste = "RMS E";
    public static final String coordH = "Alt";
    public static final String rmsHeight = "RMS Alt";
    public static final String azimute = "Azimute";
    public static final String distancia = "Dist\u00e2ncia";
    public static final String fatorEscala = "Fator Escala";
    public static final String latitude = "Latitude";
    public static final String longitude = "Longitude";
    public static final String metodoAplicado = "M\u00e9todo Aplicado";
    public static final String tipoLimite = "Tipo Limite";
    public static final String rinex = "Rinex";
    public static final String nomeConfrontante = "Confrontante";
    public static final String margemAguaCorrente = "Margem";
    public static final String direcaoAguaCorrente = "Dire\u00e7\u00e3o";
    public static final List<String> columnNamesCalculationArea = Arrays.asList(new String[]{estacao, vante, coordN, coordE, azimute, distancia, fatorEscala, latitude, longitude});
    public static final List<String> columnNamesIncraDadosCartograficos = Arrays.asList(new String[]{seq, vertice, coordE, rmsEste, coordN, rmsNorte, coordH, rmsHeight, metodoAplicado, tipoLimite, rinex, nomeConfrontante, margemAguaCorrente, direcaoAguaCorrente});
    public static final List<String> columnNamesTerraLegalDadosCartograficos = Arrays.asList(new String[]{vertice, seq, coordE, rmsEste, coordN, rmsNorte, coordH, rmsHeight, metodoAplicado, tipoLimite, nomeConfrontante});
}
