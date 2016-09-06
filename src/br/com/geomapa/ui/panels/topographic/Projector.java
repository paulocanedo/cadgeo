/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels.topographic;

/**
 *
 * @author paulocanedo
 */
public interface Projector {
    
    public int[] project(double x, double y);
    
    public double[] unProject(int x, int y);
    
}
