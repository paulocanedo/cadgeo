/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.controller;

/**
 *
 * @author paulocanedo
 */
public interface Command {

    public void execute();

    public void undo();

    public void store();

    public void load();

}
