/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.project;

/**
 *
 * @author paulocanedo
 */
public interface ProjectLoaderObserver {
    
    public void start(int total);
    
    public void fileLoaded();
    
    public void end();
    
}
