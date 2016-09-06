/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.autoupdate;

/**
 *
 * @author paulocanedo
 */
public interface Pc9DownloaderListener {

    public void progressChanged(long partial, long total, double percentual);

    public void downloadFinished(boolean success);
}
