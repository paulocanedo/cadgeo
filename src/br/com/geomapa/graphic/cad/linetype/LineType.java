/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.graphic.cad.linetype;

import javax.swing.Icon;

/**
 *
 * @author paulocanedo
 */
public interface LineType {
    
    public boolean isContinuous();
    
    public String getName();
    
    public String getDxfName();

    public short getStipple();
    
    public Icon getIcon();
    
}
