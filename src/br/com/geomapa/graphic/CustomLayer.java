/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic;

import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import java.awt.Color;

/**
 *
 * @author paulocanedo
 */
public class CustomLayer implements Layer {

    private String name;
    private String description;
    private Color color;
    private LineType lineType;
    private boolean visible = true;
    private boolean dxfVisible = true;

    public CustomLayer(String name, String description) {
        this(name, description, Color.WHITE, AbstractVisualObject.CONTINUOUS_LINE_TYPE);
    }

    public CustomLayer(String name, String description, Color color) {
        this(name, description, color, AbstractVisualObject.CONTINUOUS_LINE_TYPE);
    }

    public CustomLayer(String name, String description, Color color, LineType lineType) {
        this.name = name.replaceAll(" ", "_");
        this.description = description;
        this.color = color;
        this.lineType = lineType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public LineType getLineType() {
        return lineType;
    }

    @Override
    public Color getColor() {
        return color;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setDxfVisible(boolean dxfVisible) {
        this.dxfVisible = dxfVisible;
    }

    public boolean isDxfVisible() {
        return dxfVisible;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CustomLayer other = (CustomLayer) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Layer o) {
        return name.compareTo(o.getName());
    }
}
