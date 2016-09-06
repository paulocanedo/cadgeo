/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geografico.ztest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class LineCount {

    private int count = 0;
    public int countFiles = 0;

    public int countLines(File file) throws FileNotFoundException, IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                countLines(child);
            }
            return count;
        }

        if (!file.getName().toLowerCase().endsWith("java")) {
            return count;
        }
        countFiles++;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.readLine() != null) {
            count++;
        }

        return count;
    }

//    private static void clearSvnDir(File root) {
//        if (root.isDirectory()) {
//            File[] listFiles = root.listFiles();
//            for (File f : listFiles) {
//                if (f.isDirectory() && f.getName().equals(".svn")) {
//                    deleteDir(f);
//                    f.delete();
//                }
//                if (f.isDirectory()) {
//                    clearSvnDir(f);
//                }
//            }
//        }
//    }

    private static void deleteDir(File dir) {
        deleteCount = 0;
        File[] listFiles = dir.listFiles();
        for (File f : listFiles) {
            if (f.isDirectory()) {
                deleteDir(f);
            }
            f.delete();
            deleteCount++;
        }
    }
    
    private static int deleteCount = 0;

    public static void main(String... args) throws FileNotFoundException, IOException {
//        clearSvnDir(new File("/Users/paulocanedo/NetBeansProjects/GeoMapa"));
//        System.out.println(deleteCount);
        List<File> projects = new ArrayList<File>();
        projects.add(new File("/Users/paulocanedo/Development/NetBeansProjects/CadGeo/src/"));
        projects.add(new File("/Users/paulocanedo/NetBeansProjects/PC9LookAndFeel"));
        projects.add(new File("/Users/paulocanedo/NetBeansProjects/PC9Components"));
        projects.add(new File("/Users/paulocanedo/NetBeansProjects/cert_digital/certificacao_digital/src"));
        projects.add(new File("/Users/paulocanedo/NetBeansProjects/cert_digital/SCD/src"));
        projects.add(new File("/Users/paulocanedo/NetBeansProjects/SimplePersistence/src"));
        projects.add(new File("/Users/paulocanedo/NetBeansProjects/LocacaoDAO"));
        projects.add(new File("/Users/paulocanedo/NetBeansProjects/locacaoweb"));

        for (File f : projects) {
            LineCount lineCount = new LineCount();
            System.out.println(f + " " + lineCount.countLines(f) + " LOC - " + lineCount.countFiles + " arquivos");
        }
    }
}
