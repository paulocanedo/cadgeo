/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.dxf;

import br.com.geomapa.graphic.CustomLayer;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.cad.compound.AbstractVisualObjectCompound;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.primitives.Polyline;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

/**
 *
 * @author paulocanedo
 */
public class DxfOverlayVisualObject extends AbstractVisualObjectCompound {

    private String command = null;
    private String subCommand = null;
    private Line line = null;
    private Polyline polyline = null;
    private Color color = new Color(230, 230, 150, 50);
    private Layer layer = new CustomLayer("overlay", "over layer", color);

    public DxfOverlayVisualObject(File file) throws FileNotFoundException, IOException {
        parse(new BufferedReader(new FileReader(file)));

        for (VisualObject vo : delegate) {
            vo.setLayer(layer);
            vo.setColor(color);
        }
        setLayer(layer);
    }

    public void add(VisualObject vo) {
        delegate.add(vo);
    }

    public void addAll(Collection<VisualObject> vos) {
        delegate.addAll(vos);
    }

    @Override
    public void write(PrintStream stream) throws IOException {
    }

    @Override
    public String getVisualObjectName() {
        return "generic_visual_object_compound";
    }

    private void parse(BufferedReader reader) throws IOException {
        Double lastX = null, lastY = null, lastEndX = null, lastEndY = null;
        String lineText;
        while ((lineText = reader.readLine()) != null) {
            lineText = lineText.trim();
            if (lineText.equals("LINE") || lineText.equals("POLYLINE") || lineText.equals("AcDbPolyline")) {
                command = lineText;
                line = new Line();
                polyline = new Polyline();
                lastX = lastY = lastEndX = lastEndY = null;
                continue;
            }

            if (lineText.equals("VERTEX") || lineText.equals("SEQEND")) {
                subCommand = lineText;
                continue;
            }

            if ("LINE".equals(command)) {
                if ("10".equals(lineText)) {
                    lastX = Double.parseDouble(reader.readLine());
                } else if ("20".equals(lineText)) {
                    lastY = Double.parseDouble(reader.readLine());
                } else if ("11".equals(lineText)) {
                    lastEndX = Double.parseDouble(reader.readLine());
                } else if ("21".equals(lineText)) {
                    lastEndY = Double.parseDouble(reader.readLine());
                    line.setLocation(lastX, lastY);
                    line.setEndLocation(lastEndX, lastEndY);

                    add(line);
                    line = null;
                    command = null;
                }
            } else if ("POLYLINE".equals(command)) {
                if ("VERTEX".equals(subCommand)) {
                    if ("10".equals(lineText)) {
                        lastX = Double.parseDouble(reader.readLine());
                    } else if ("20".equals(lineText)) {
                        lastY = Double.parseDouble(reader.readLine());

                        polyline.addVertex(lastX, lastY);
                    }
                } else if ("SEQEND".equals(subCommand)) {
                    add(polyline);

                    line = null;
                    polyline = null;
                    command = null;
                    subCommand = null;
                }
            } else if ("AcDbPolyline".equals(command)) {
                if ("10".equals(lineText)) {
                    lastX = Double.parseDouble(reader.readLine());
                } else if ("20".equals(lineText)) {
                    lastY = Double.parseDouble(reader.readLine());

                    polyline.addVertex(lastX, lastY);
                } else if ("0".equals(lineText)) {
                    add(polyline);

                    line = null;
                    polyline = null;
                    command = null;
                    subCommand = null;
                } else {
                    reader.readLine();
                }
            }
        }
    }

    @Override
    public boolean canMove() {
        return false;
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public void move(double offsetX, double offsetY) {
    }

    @Override
    public void writeToDxf(PrintStream stream) throws IOException {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DxfOverlayVisualObject other = (DxfOverlayVisualObject) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.oid;
        return hash;
    }
    
}
