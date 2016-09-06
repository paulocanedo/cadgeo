/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.mlist;

import java.util.List;

/**
 *
 * @author paulocanedo
 */
public interface MacroListListener {
    
    public void sizeChanged(int newSize);
    
    public void elementAdded(Object o);
    
    public void elementRemoved(Object o);
    
    public void listAdded(List l);
    
    public void listRemoved(List l);
    
}
