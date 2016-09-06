/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic;

import br.com.geomapa.graphic.cad.linetype.LineType;
import java.awt.Color;

/**
 *
 * @author paulocanedo
 */
public interface Layer extends Comparable<Layer> {

    public String getName();

    public String getDescription();

    public LineType getLineType();

    public Color getColor();
    
    public boolean isVisible();
    
    public void setVisible(boolean flag);
    
    public boolean isDxfVisible();

}
