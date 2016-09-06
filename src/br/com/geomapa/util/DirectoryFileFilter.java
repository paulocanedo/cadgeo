/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author paulocanedo
 */
public class DirectoryFileFilter implements FileFilter {

    public final static FileFilter FILE_FILTER = new DirectoryFileFilter();

    private DirectoryFileFilter() {
    }

    @Override
    public boolean accept(File f) {
        return f.isDirectory();
    }
}
