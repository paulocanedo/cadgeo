/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.spec;

/**
 *
 * @author paulocanedo
 */
public interface VisualObjectReferenced<T> extends VisualObject {
    
    public T referencedObject();
    
    public void setOffset(double x, double y);
    
}
