/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.geodesic.Polygonal;
import java.io.IOException;

/**
 *
 * @author paulocanedo
 */
public interface GeodesicPanel {

    public void filter(String text);

    public String action(String text);

    public void refresh();

    public void setPolygonal(Polygonal polygonal);

    public Polygonal getPolygonal();

    public void export(Polygonal polygonal) throws IOException;
    
}
