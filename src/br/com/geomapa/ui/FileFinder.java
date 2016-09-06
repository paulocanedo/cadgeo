/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui;

import java.awt.Component;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author paulocanedo
 */
public class FileFinder {

    public static final FileFilter CSV_FILTER = new ExtensionFileFilter("csv");
    public static final FileFilter RTF_FILTER = new ExtensionFileFilter("rtf");
    public static final FileFilter XLSX_FILTER = new ExtensionFileFilter("xlsx");
    public static final FileFilter HTML_FILTER = new ExtensionFileFilter("html");
    public static final FileFilter GPS_SOURCE_EXTENSIONS = new ExtensionFileFilter("txt", "csv", "rtf", "docx", "html");
    private static final JFileChooser fileChooser = new JFileChooser();
    private static final JFileChooser dirChooser = new JFileChooser();

    public static void setCurrentDirectory(File file) {
        fileChooser.setCurrentDirectory(file);
    }

    public static File selectFileToOpenUI(Component parent, FileFilter filter) {
        boolean selectedFile = showChooserFile(parent, filter, false);

        if (selectedFile) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static File[] selectFilesToOpenUI(Component parent, FileFilter filter) {
        boolean selectedFile = showChooserFile(parent, filter, true);
        if (selectedFile) {
            return fileChooser.getSelectedFiles();
        }
        return null;
    }

    private static boolean showChooserFile(Component parent, FileFilter filter, boolean multiple) {
        fileChooser.setSelectedFile(null);
        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(multiple);

        int showOpenDialog = fileChooser.showOpenDialog(parent);
        return (showOpenDialog == JFileChooser.APPROVE_OPTION);
    }

    public static File selectDirToOpenUI(Component parent) {
        dirChooser.setSelectedFile(null);
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int showOpenDialog = dirChooser.showOpenDialog(parent);

        if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
            return dirChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    public static class ExtensionFileFilter extends FileFilter implements FilenameFilter {

        private String[] extensions;
        private String description;

        public ExtensionFileFilter(String... extensions) {
            this.extensions = extensions;

            StringBuilder sb = new StringBuilder();
            for (String extension : extensions) {
                sb.append(extension).append(", ");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));

            this.description = "Arquivo " + sb;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String filename = f.getName().toLowerCase();
            for (String extension : extensions) {
                if (filename.endsWith(extension.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean accept(File dir, String filename) {
            for (String extension : extensions) {
                if (filename.endsWith(extension.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }
    }
}
