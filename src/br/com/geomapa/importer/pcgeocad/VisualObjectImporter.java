/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.pcgeocad;

import br.com.geomapa.graphic.cad.spec.VisualObject;
import java.io.InputStream;
import java.util.Collection;

/**
 *
 * @author paulocanedo
 */
public interface VisualObjectImporter {
    
    public Collection<VisualObject> parse(InputStream stream, double drawingScale, double objectsScale) throws VisualObjectParserException;
    
}
