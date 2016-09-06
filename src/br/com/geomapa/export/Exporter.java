/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.export;

import br.com.geomapa.graphic.cad.compound.ISO_Paper;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.project.ProjectUtils;
import br.com.geomapa.ui.panels.CalculationAreaPanel;
import br.com.geomapa.ui.panels.CartograficDataPanel;
import br.com.geomapa.ui.panels.MemorialPanel;
import br.com.geomapa.util.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author paulocanedo
 */
public class Exporter {

    public enum ExporterId {

        TERRA_LEGAL_MEMO,
        INCRA_MEMO,
        CALCULATION_AREA,
        INCRA_CARTOGRAPHIC_DATA,
        TERRA_LEGAL_CARTOGRAPHIC_DATA,
        TECHNICAL_REPORT,
        ISO_PAPER_A1,
        ISO_PAPER_A4,
    }
    public static final String URL_BASE_PACKAGE = "/br/com/geomapa/resources/templates/";

    public static void createTemplateFromPackage(File file) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            InputStream resourceAsStream = ProjectUtils.class.getResourceAsStream(Exporter.URL_BASE_PACKAGE + file.getName());
            FileOutputStream fos = new FileOutputStream(file);
            IOUtils.copyStream(resourceAsStream, fos);
            fos.close();
        }
    }

    public static void prepareTemplates(ProjectMetadata project) throws FileNotFoundException, IOException {
        createTemplateFromPackage(getFile(project, ExporterId.TERRA_LEGAL_MEMO));
        createTemplateFromPackage(getFile(project, ExporterId.INCRA_MEMO));
        createTemplateFromPackage(getFile(project, ExporterId.CALCULATION_AREA));
        createTemplateFromPackage(getFile(project, ExporterId.TECHNICAL_REPORT));
        createTemplateFromPackage(getFile(project, ExporterId.ISO_PAPER_A1));
        createTemplateFromPackage(getFile(project, ExporterId.ISO_PAPER_A4));
    }

    public static InputStream getStream(ProjectMetadata metadata, ExporterId what) throws FileNotFoundException {
            File file = getFile(metadata, what);
            if (!file.exists()) {
                return Exporter.class.getResourceAsStream(URL_BASE_PACKAGE + file.getName());
            }
        return new FileInputStream(file);
        }

    public static File getFile(ProjectMetadata metadata, ExporterId what) {
        switch (what) {
            case TERRA_LEGAL_MEMO: {
                return new File(metadata.getTemplateFolder(), MemorialPanel.FILE_NAME_TERRA_LEGAL);
            }
            case INCRA_MEMO: {
                return new File(metadata.getTemplateFolder(), MemorialPanel.FILE_NAME_INCRA);
            }
            case CALCULATION_AREA: {
                return new File(metadata.getTemplateFolder(), CalculationAreaPanel.FILE_NAME);
            }
            case INCRA_CARTOGRAPHIC_DATA: {
                return new File(metadata.getTemplateFolder(), CartograficDataPanel.FILE_NAME_INCRA);
            }
            case TERRA_LEGAL_CARTOGRAPHIC_DATA: {
                return new File(metadata.getTemplateFolder(), CartograficDataPanel.FILE_NAME_TERRA_LEGAL);
            }
            case TECHNICAL_REPORT: {
                return new File(metadata.getTemplateFolder(), TechnicalReport.FILE_NAME);
            }
            case ISO_PAPER_A1: {
                return new File(metadata.getTemplateFolder(), ISO_Paper.FILE_NAME_A1);
            }
            case ISO_PAPER_A4: {
                return new File(metadata.getTemplateFolder(), ISO_Paper.FILE_NAME_A4);
            }
        }
        return null;
    }
}
