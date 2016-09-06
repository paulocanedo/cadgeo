/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.pcgeocad;

import br.com.geomapa.controller.LayerController;
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
import java.io.BufferedReader;
import java.io.IOException;
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
public class VisualObjectImporterBeta implements VisualObjectImporter {

    @Override
    public Collection<VisualObject> parse(InputStream stream, double drawingScale, double objectsScale) throws VisualObjectParserException {
        try {
            List<String> bufferArgs = new ArrayList<String>();
            List<VisualObject> vobjects = new ArrayList<VisualObject>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
            MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("#")) {
                    line = line.substring(0, line.indexOf("#"));
                }
                if (line.isEmpty()) {
                    continue;
                }

                int indexOf = line.indexOf(" ");

                String command = line.substring(0, indexOf).trim();
                String args = line.substring(indexOf).trim();
                bufferArgs.clear();
                String quotedText = null;
                List<String> quotedTexts = new ArrayList<String>();

                while (args.matches(".*\".*\".*")) {
                    int firstIndexOf = args.indexOf("\"");
                    int lastIndexOf = args.indexOf("\"", firstIndexOf + 1);

                    quotedText = args.substring(firstIndexOf + 1, lastIndexOf);
                    quotedTexts.add(quotedText);
                    args = args.substring(0, firstIndexOf - 1) + args.substring(lastIndexOf + 1);
                }

                StringTokenizer st = new StringTokenizer(args, " ");
                while (st.hasMoreTokens()) {
                    String token = st.nextToken().trim();
                    bufferArgs.add(token);
                }

                String slayer = bufferArgs.get(0);
                Layer layer = LayerController.find(slayer);
                VisualObject vobject = null;
                if ("rect".equalsIgnoreCase(command)) {
                    vobject = Rectangle.createRect(Double.parseDouble(bufferArgs.get(1)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(2)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(3)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(4)) * objectsScale);

                } else if ("rectc".equalsIgnoreCase(command)) {
                    vobject = new Rectangle(Double.parseDouble(bufferArgs.get(1)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(2)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(3)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(4)) * objectsScale);

                } else if ("line".equalsIgnoreCase(command)) {
                    vobject = new Line(Double.parseDouble(bufferArgs.get(1)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(2)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(3)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(4)) * objectsScale);

                } else if ("line_arrow".equalsIgnoreCase(command)) {
                    vobject = new LineArrow(Double.parseDouble(bufferArgs.get(1)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(2)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(3)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(4)) * objectsScale);

                } else if ("lined".equalsIgnoreCase(command)) {
                    GeodesicPoint from = DataManagement.findPoint(bufferArgs.get(1));
                    GeodesicPoint to = DataManagement.findPoint(bufferArgs.get(2));
                    String borderName = quotedTexts.get(0);

                    LineDivisionType type = bufferArgs.get(3).equals("null") ? null : LineDivisionType.valueOf(bufferArgs.get(3));
                    Boolean isClockWise = Boolean.valueOf(bufferArgs.get(4));
                    RoadType rtype = bufferArgs.get(5).equals("null") ? null : RoadType.valueOf(bufferArgs.get(5));
                    String roadName = quotedTexts.size() > 1 ? quotedTexts.get(1) : "";

                    if (from == null || to == null) {
                        System.out.println(String.format("lined miss: %s - %s", bufferArgs.get(1), bufferArgs.get(2)));
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
                    vobject = new Circle(Double.parseDouble(bufferArgs.get(1)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(2)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(3)) * objectsScale);

                } else if ("arc".equalsIgnoreCase(command)) {
                    vobject = new Arc(Double.parseDouble(bufferArgs.get(3)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(4)),
                            Double.parseDouble(bufferArgs.get(5)),
                            Double.parseDouble(bufferArgs.get(1)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(2)) * objectsScale);
                } else if ("point".equalsIgnoreCase(command)) {
                    vobject = new Point(Double.parseDouble(bufferArgs.get(1)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(2)) * objectsScale);

                } else if ("immutable_point".equalsIgnoreCase(command)) {
                    vobject = new ImmutablePoint(Double.parseDouble(bufferArgs.get(1)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(2)) * objectsScale);

                } else if ("area_perimeter_table".equalsIgnoreCase(command)) {
                    vobject = new AreaAndPerimeterTableModel(
                            Double.parseDouble(bufferArgs.get(1)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(2)) * objectsScale);

                } else if ("text".equalsIgnoreCase(command)) {
                    VisualText vt = new VisualText(Double.parseDouble(bufferArgs.get(1)) * objectsScale,
                            Double.parseDouble(bufferArgs.get(2)) * objectsScale,
                            Float.parseFloat(bufferArgs.get(3)),
                            String.valueOf(quotedText));

                    try {
                        String rot = bufferArgs.get(4);
                        vt.setRotation(Double.parseDouble(rot));
                    } catch (Throwable ex) {
                    }

                    vobject = vt;

                } else if ("paper".equalsIgnoreCase(command)) {
                    DescriptorPaper dpaper = new DescriptorPaper(false, bufferArgs.get(3).replaceAll("_", " "));
                    double x = Double.parseDouble(bufferArgs.get(1)) * objectsScale;
                    double y = Double.parseDouble(bufferArgs.get(2)) * objectsScale;

                    vobject = new ISO_Paper(0, 0, drawingScale, dpaper);
                    vobject.setLocation(x, y);
                } else if ("geodesic_point_label_coord".equalsIgnoreCase(command)) {
                    GeodesicPoint point = DataManagement.findPoint(bufferArgs.get(3));
                    GeodesicPointLabelCoord labelCoord = new GeodesicPointLabelCoord(point);
                    labelCoord.setOffset(Double.parseDouble(bufferArgs.get(1)), Double.parseDouble(bufferArgs.get(2)));

                    vobject = labelCoord;
                } else if ("azimuth_distance".equalsIgnoreCase(command)) {
                    GeodesicPoint from = DataManagement.findPoint(bufferArgs.get(1));
                    GeodesicPoint to = DataManagement.findPoint(bufferArgs.get(2));

                    vobject = new AzimuthDistance(from, to);
                } else if ("azimuth_distance_table".equalsIgnoreCase(command)) {
                    vobject = new AzimuthAndDistanceTableModel(mainPolygonal.createOrGetPolygonal(quotedText));
                    vobject.setLocation(Double.parseDouble(bufferArgs.get(1)), Double.parseDouble(bufferArgs.get(2)));
                } else if ("geopoint_text".equalsIgnoreCase(command)) {
                    GeodesicPoint point = DataManagement.findPoint(bufferArgs.get(3));
                    if (point == null) {
                        System.out.println("geopoint_text miss: " + bufferArgs.get(3));
                        break;
                    }
                    GeodesicPointText geopointText = new GeodesicPointText(point);
                    geopointText.setOffset(Double.parseDouble(bufferArgs.get(1)), Double.parseDouble(bufferArgs.get(2)));

                    try {
                        String rot = bufferArgs.get(4);
                        geopointText.setRotation(Double.parseDouble(rot));
                    } catch (Throwable ex) {
                    }

                    vobject = geopointText;
                } else if ("geodesic_point_reference".equalsIgnoreCase(command)) {
                    GeodesicPoint point = DataManagement.findPoint(bufferArgs.get(0));
                    if (point == null) {
                        System.out.println("gpoint_reference: " + bufferArgs.get(0));
                        break;
                    }
                    Integer utmZoneOverrided = Integer.parseInt(bufferArgs.get(1));

                    vobject = new GeodesicPointReference(point, utmZoneOverrided);
                }

                if (vobject == null) {
                    throw new NullPointerException("Comando " + command + " não identificado.");
                }

                if (layer == null && !(vobject instanceof GeodesicPointReference)) {
                    LayerController.add(layer = new CustomLayer(slayer, null));
                }
                vobject.setLayer(layer);
                vobjects.add(vobject);
            }
            return vobjects;
        } catch (Throwable ex) {
            throw new VisualObjectParserException("Erro no processamento do projeto", ex);
        }
    }
}
