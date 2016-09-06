/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.compound;

import br.com.geomapa.export.Exporter;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 * @author paulocanedo
 */
public class DescriptorPaper {

    private boolean custom;
    private String name;
    private final String base = Exporter.URL_BASE_PACKAGE;

    public DescriptorPaper(boolean custom, String name) {
        this.custom = custom;
        this.name = name;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InputStream getSourceAsStream() {
        if (getName().equalsIgnoreCase("formato a1")) {
            return getFormatoA1();
        } else if (getName().equalsIgnoreCase("formato a4")) {
            return getFormatoA4();
        }
        return getFormatoA1();
    }

    private InputStream getFormatoA1() {
        try {
            ProjectMetadata projectInfo = Main.getInstance().getProjectInfo();
            return Exporter.getStream(projectInfo, Exporter.ExporterId.ISO_PAPER_A1);
        } catch (FileNotFoundException ex) {
        }
        return getClass().getResourceAsStream(base + ISO_Paper.FILE_NAME_A1);
    }

    private InputStream getFormatoA4() {
        try {
            ProjectMetadata projectInfo = Main.getInstance().getProjectInfo();
            return Exporter.getStream(projectInfo, Exporter.ExporterId.ISO_PAPER_A4);
        } catch (FileNotFoundException ex) {
        }
        return getClass().getResourceAsStream(base + ISO_Paper.FILE_NAME_A4);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DescriptorPaper other = (DescriptorPaper) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return name;
    }
}
