/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.pcgeocad;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.controller.LineTypeControlller;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.point.LineDivisionType;
import br.com.geomapa.geodesic.point.RoadType;
import br.com.geomapa.graphic.CustomLayer;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.cad.compound.DescriptorPaper;
import br.com.geomapa.graphic.cad.compound.ISO_Paper;
import br.com.geomapa.graphic.cad.geo.AreaAndPerimeterTableModel;
import br.com.geomapa.graphic.cad.geo.AzimuthAndDistanceTableModel;
import br.com.geomapa.graphic.cad.geo.AzimuthDistance;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.GeodesicPointLabelCoord;
import br.com.geomapa.graphic.cad.geo.GeodesicPointReference;
import br.com.geomapa.graphic.cad.geo.GeodesicPointText;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.primitives.Arc;
import br.com.geomapa.graphic.cad.primitives.Circle;
import br.com.geomapa.graphic.cad.primitives.ImmutablePoint;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.primitives.LineArrow;
import br.com.geomapa.graphic.cad.primitives.Point;
import br.com.geomapa.graphic.cad.primitives.Rectangle;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author paulocanedo
 */
public class VisualObjectImporterOneDotZero implements VisualObjectImporter {

    private List<String> auxBufferArgs = new ArrayList<String>();
    private List<String> auxQuotedTexts = new ArrayList<String>();

    private List<String> getQuotedTexts(StringBuilder args) {
        auxQuotedTexts.clear();
        String quotedText = null;
        String sargs = args.toString();
        while (sargs.matches(".*\".*\".*")) {
            int firstIndexOf = args.indexOf("\"");
            int lastIndexOf = args.indexOf("\"", firstIndexOf + 1);

            quotedText = args.substring(firstIndexOf + 1, lastIndexOf);
            auxQuotedTexts.add(quotedText);
            args.delete(firstIndexOf - 1, lastIndexOf + 1);
            sargs = args.toString();
        }
        return auxQuotedTexts;
    }

    private List<String> getListArgs(StringBuilder args) {
        auxBufferArgs.clear();
        StringTokenizer st = new StringTokenizer(args.toString(), " ");
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            auxBufferArgs.add(token);
        }
        return auxBufferArgs;
    }

    private Color parseColor(String color) {
        if (color.equalsIgnoreCase("BY_LAYER")) {
            return null;
        } else if (color.equalsIgnoreCase("WHITE")) {
            return Color.WHITE;
        } else if (color.equalsIgnoreCase("GREEN")) {
            return Color.GREEN;
        } else if (color.equalsIgnoreCase("GRAY")) {
            return Color.GRAY;
        } else if (color.equalsIgnoreCase("DARK_GRAY")) {
            return Color.DARK_GRAY;
        } else if (color.equalsIgnoreCase("CYAN")) {
            return Color.CYAN;
        } else if (color.equalsIgnoreCase("BLUE")) {
            return Color.BLUE;
        } else if (color.equalsIgnoreCase("YELLOW")) {
            return Color.YELLOW;
        } else if (color.equalsIgnoreCase("MAGENTA")) {
            return Color.MAGENTA;
        }
        throw new RuntimeException(color + " não é uma cor reconhecida.");
    }

    @Override
    public Collection<VisualObject> parse(InputStream stream, double drawingScale, double objectsScale) throws VisualObjectParserException {
        int lineNumber = 0;
        try {
            List<VisualObject> vobjects = new ArrayList<VisualObject>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
            MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();

            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.contains("#")) {
                    line = line.substring(0, line.indexOf("#"));
                }
                if (line.isEmpty()) {
                    continue;
                }

                int indexOf = line.indexOf(" ");

                String command = line.substring(0, indexOf).trim();
                StringBuilder args = new StringBuilder(line.substring(indexOf).trim());

                List<String> quotedTexts = getQuotedTexts(args);
                String quotedText = quotedTexts.isEmpty() ? "" : quotedTexts.get(0);
                List<String> listArgs = getListArgs(args);

                int index = 0;
                String slayer = listArgs.get(index++);
                String scolor = listArgs.get(index++);
                String slineType = listArgs.get(index++);
                String sx = listArgs.get(index++);
                String sy = listArgs.get(index++);

                Layer layer = LayerController.find(slayer);
                Color color = parseColor(scolor);
                LineType ltype = LineTypeControlller.getLineTypeInstance(slineType);
                double x = Double.parseDouble(sx) * objectsScale;
                double y = Double.parseDouble(sy) * objectsScale;
                VisualObject vobject = null;
                if ("rect".equalsIgnoreCase(command)) {
                    double w = Double.parseDouble(listArgs.get(index++)) * objectsScale;
                    double h = Double.parseDouble(listArgs.get(index++)) * objectsScale;

                    vobject = Rectangle.createRect(x, y, w, h);
                } else if ("rectc".equalsIgnoreCase(command)) {
                    double x2 = Double.parseDouble(listArgs.get(index++)) * objectsScale;
                    double y2 = Double.parseDouble(listArgs.get(index++)) * objectsScale;

                    vobject = new Rectangle(x, y, x2, y2);
                } else if ("line".equalsIgnoreCase(command)) {
                    double x2 = Double.parseDouble(listArgs.get(index++)) * objectsScale;
                    double y2 = Double.parseDouble(listArgs.get(index++)) * objectsScale;

                    vobject = new Line(x, y, x2, y2);
                } else if ("line_arrow".equalsIgnoreCase(command)) {
                    double x2 = Double.parseDouble(listArgs.get(index++)) * objectsScale;
                    double y2 = Double.parseDouble(listArgs.get(index++)) * objectsScale;

                    vobject = new LineArrow(x, y, x2, y2);
                } else if ("lined".equalsIgnoreCase(command)) {
                    String sfrom = listArgs.get(index++);
                    String sto = listArgs.get(index++);
                    GeodesicPoint from = DataManagement.findPoint(sfrom);
                    GeodesicPoint to = DataManagement.findPoint(sto);
                    String sborderType = listArgs.get(index++);
                    String borderName = quotedTexts.get(0);
                    String sisClockWise = listArgs.get(index++);
                    String roadName = quotedTexts.size() > 1 ? quotedTexts.get(1) : "";
                    String sroadtype = listArgs.get(index++);

                    LineDivisionType type = sborderType.equals("null") ? LineDivisionType.LA1 : LineDivisionType.valueOf(sborderType);
                    Boolean isClockWise = Boolean.valueOf(sisClockWise);
                    RoadType rtype = sroadtype.equals("null") ? null : RoadType.valueOf(sroadtype);

                    if (from == null || to == null) {
                        System.out.println(String.format("lined miss: %s - %s", sfrom, sto));
                        continue;
                    }
                    
                    LineDivision ld = LineDivision.getInstance(from, to);
                    ld.setBorderName(borderName);
                    ld.setType(type);
                    ld.setWaterCourseClockwiseDirection(isClockWise);
                    ld.setRoadType(rtype);
                    ld.setIdFaixaDeDominio(roadName);

                    vobject = ld;
                } else if ("circle".equalsIgnoreCase(command)) {
                    double radius = Double.parseDouble(listArgs.get(index++)) * objectsScale;

                    vobject = new Circle(x, y, radius);
                } else if ("arc".equalsIgnoreCase(command)) {
                    double radius = Double.parseDouble(listArgs.get(index++)) * objectsScale;
                    double startAngle = Double.parseDouble(listArgs.get(index++));
                    double endAngle = Double.parseDouble(listArgs.get(index++));

                    vobject = new Arc(radius, startAngle, endAngle, x, y);
                } else if ("point".equalsIgnoreCase(command)) {
                    vobject = new Point(x, y);
                } else if ("immutable_point".equalsIgnoreCase(command)) {
                    vobject = new ImmutablePoint(x, y);
                } else if ("area_perimeter_table".equalsIgnoreCase(command)) {
                    vobject = new AreaAndPerimeterTableModel(x, y);
                } else if ("text".equalsIgnoreCase(command)) {
                    float height = Float.parseFloat(listArgs.get(index++));
                    double rotation = Double.parseDouble(listArgs.get(index++));
                    VisualText vt = new VisualText(x, y, height, quotedTexts.get(0));
                    vt.setRotation(rotation);

                    vobject = vt;
                } else if ("paper".equalsIgnoreCase(command)) {
                    String spaper = listArgs.get(index++).replaceAll("_", " ");
                    DescriptorPaper dpaper = new DescriptorPaper(false, spaper);

                    vobject = new ISO_Paper(0, 0, drawingScale, dpaper);
                    vobject.setLocation(x, y);
                } else if ("geodesic_point_label_coord".equalsIgnoreCase(command)) {
                    double ox = Double.parseDouble(listArgs.get(index++)) * objectsScale;
                    double oy = Double.parseDouble(listArgs.get(index++)) * objectsScale;
                    String spoint = listArgs.get(index++);
                    GeodesicPoint point = DataManagement.findPoint(spoint);
                    GeodesicPointLabelCoord labelCoord = new GeodesicPointLabelCoord(point);
                    labelCoord.setOffset(ox, oy);

                    vobject = labelCoord;
                } else if ("azimuth_distance".equalsIgnoreCase(command)) {
                    GeodesicPoint from = DataManagement.findPoint(listArgs.get(index++));
                    GeodesicPoint to = DataManagement.findPoint(listArgs.get(index++));

                    vobject = new AzimuthDistance(from, to);
                } else if ("azimuth_distance_table".equalsIgnoreCase(command)) {
                    vobject = new AzimuthAndDistanceTableModel(mainPolygonal.createOrGetPolygonal(quotedText));
                    vobject.setLocation(x, y);
                } else if ("geopoint_text".equalsIgnoreCase(command)) {
                    double ox = Double.parseDouble(listArgs.get(index++)) * objectsScale;
                    double oy = Double.parseDouble(listArgs.get(index++)) * objectsScale;
                    String spoint = listArgs.get(index++);
                    double rotation = Double.parseDouble(listArgs.get(index++));

                    GeodesicPoint point = DataManagement.findPoint(spoint);
                    if (point == null) {
                        System.out.println("geopoint_text miss: " + spoint);
                        continue;
                    }
                    GeodesicPointText geopointText = new GeodesicPointText(point);
                    geopointText.setOffset(ox, oy);
                    geopointText.setRotation(rotation);

                    vobject = geopointText;
                } else if ("geodesic_point_reference".equalsIgnoreCase(command)) {
                    String spoint = listArgs.get(index++);
                    int utmzone = Integer.parseInt(listArgs.get(index++));
                    GeodesicPoint point = DataManagement.findPoint(spoint);
                    if (point == null) {
                        System.out.println("gpoint_reference: " + spoint);
                        continue;
                    }

                    vobject = new GeodesicPointReference(point, utmzone);
                }

                if (vobject == null) {
                    throw new NullPointerException("Comando " + command + " não identificado.");
                }

                if (layer == null && !(vobject instanceof GeodesicPointReference)) {
                    LayerController.add(layer = new CustomLayer(slayer, null));
                }
                vobject.setLayer(layer);
                vobject.setColor(color);
                vobject.setLineType(ltype);
                vobjects.add(vobject);
            }
            return vobjects;
        } catch (Throwable ex) {
            throw new VisualObjectParserException(String.format("Erro na leitura do arquivo de projeto, linha %d", lineNumber), ex);
        }
    }
}
