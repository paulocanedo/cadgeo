/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.geomapa.ui;

/**
 *
 * @author paulocanedo
 */
public interface Prompt {

    public void close() throws PromptException;

    public void cancel();

    public String defaultMessage();

    public String closeString();

}
