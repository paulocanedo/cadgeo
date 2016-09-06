/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic;

import br.com.geomapa.graphic.cad.linetype.LineType;
import javax.swing.Icon;

/**
 *
 * @author paulocanedo
 */
public class LineTypeByLayer implements LineType {

    private Layer layer;

    public LineTypeByLayer(Layer layer) {
        this.layer = layer;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }
    
    @Override
    public boolean isContinuous() {
        return layer.getLineType().isContinuous();
    }

    @Override
    public String getName() {
        return layer.getLineType().getName();
    }

    @Override
    public String getDxfName() {
        return layer.getLineType().getDxfName();
    }

    @Override
    public short getStipple() {
        return layer.getLineType().getStipple();
    }

    @Override
    public Icon getIcon() {
        return layer.getLineType().getIcon();
    }

    @Override
    public String toString() {
        return "BY_LAYER";
    }
}
