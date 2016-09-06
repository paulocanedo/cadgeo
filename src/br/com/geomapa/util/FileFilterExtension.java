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
public class FileFilterExtension implements FileFilter {

    private String extension;

    public FileFilterExtension(String extension) {
        if (!extension.contains(".")) {
            extension = "." + extension;
        }
        this.extension = extension;
    }

    @Override
    public boolean accept(File pathname) {
        return pathname.isDirectory() || (pathname.getName().toLowerCase().endsWith(extension));
    }
}
