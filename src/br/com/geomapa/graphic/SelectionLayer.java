/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic;

import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.ui.panels.options.SchemeColors;
import java.awt.Color;

/**
 *
 * @author paulocanedo
 */
public class SelectionLayer implements Layer {

    @Override
    public String getName() {
        return "Seleção";
    }

    @Override
    public String getDescription() {
        return "Seleção";
    }

    @Override
    public LineType getLineType() {
        return AbstractVisualObject.CONTINUOUS_LINE_TYPE;
    }

    @Override
    public Color getColor() {
        return SchemeColors.SELECTED;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public int compareTo(Layer o) {
        return -1;
    }

    @Override
    public void setVisible(boolean flag) {
    }

    @Override
    public boolean isDxfVisible() {
        return false;
    }
    
}
