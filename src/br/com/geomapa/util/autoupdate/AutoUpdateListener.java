/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.autoupdate;

/**
 *
 * @author paulocanedo
 */
public interface AutoUpdateListener {
    
    public void onStart();
    
    public void onFinish(boolean success, String message);
    
}
