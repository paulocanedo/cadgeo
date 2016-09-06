/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.rinex;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author paulocanedo
 */
public class RinexFileFilter implements FileFilter {

    public final static RinexFileFilter FILE_FILTER = new RinexFileFilter();

    private RinexFileFilter() {
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        try {
            String ext = f.getName().substring(f.getName().lastIndexOf('.') + 1);
            if (ext.matches("\\d+o") || ext.matches("\\d+O")) {
                return true;
            }
        } catch (Exception ex) {
        }
        return false;
    }
}
