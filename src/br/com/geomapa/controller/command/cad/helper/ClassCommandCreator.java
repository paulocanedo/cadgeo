/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.helper;

import br.com.geomapa.ui.FileFinder.ExtensionFileFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paulocanedo
 */
public class ClassCommandCreator {

    public static void main(String... args) {
        File folder = new File("/Users/paulocanedo/NetBeansProjects/CadGeo/src/");

        assert folder.exists();

        File commandFolder = new File(folder, "br/com/geomapa/controller/command/cad");
        File commandFolderImpl = new File(commandFolder, "impl");
        File commandFolderHelper = new File(commandFolder, "helper");
        StringBuilder sb = new StringBuilder();

        sb.append("package br.com.geomapa.controller.command.cad.helper;").append("\n");
        sb.append("\n");
        sb.append("import br.com.geomapa.controller.command.cad.impl.*;").append("\n");
        sb.append("import br.com.geomapa.controller.command.cad.spec.CadCommand;").append("\n");
        sb.append("import java.util.ArrayList;").append("\n");
        sb.append("import java.util.Collections;").append("\n");
        sb.append("import java.util.List;").append("\n");
        sb.append("\n");
        sb.append("/**").append("\n");
        sb.append(" *  THIS FILE IS GENERATED AUTOMATICALLY BY CLASS ClassCommandCreator, DO NOT EDIT!").append("\n");
        sb.append(" */").append("\n");
        sb.append("public class CadCommandList {").append("\n");
        sb.append("\n");
        sb.append("\tpublic static final List<CadCommand> list = new ArrayList<CadCommand>();").append("\n");
        sb.append("\n");
        sb.append("\tstatic {").append("\n");

        for (File f : commandFolderImpl.listFiles(new ExtensionFileFilter("java"))) {
            String fileName = f.getName().replace(".java", "");
            sb.append(String.format("\t\tlist.add(new %s());", fileName)).append("\n");
        }
        sb.append("\t\tCollections.sort(list, new CadCommandComparator());").append("\n");
        sb.append("\t}").append("\n");
        sb.append("\n");
        sb.append("\tprivate CadCommandList() {").append("\n");
        sb.append("\t}").append("\n");
        sb.append("}").append("\n");

        try {
            FileOutputStream fos = new FileOutputStream(new File(commandFolderHelper, "CadCommandList.java"));
            fos.write(sb.toString().getBytes());

            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClassCommandCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClassCommandCreator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
