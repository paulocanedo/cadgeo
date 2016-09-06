/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.InvalidGeodesicPointException;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.datum.Datum;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.importer.CalculoAreaRTFImporter;
import br.com.geomapa.importer.PointImporter;
import br.com.geomapa.importer.PointImporterHandle;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.util.FileFilterExtension;
import br.com.pc9.pswing.components.filebrowser.PFileBrowser;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

/**
 *
 * @author paulocanedo
 */
public class NewQuotaFromCalcAreaAction extends AbstractAction {

    public NewQuotaFromCalcAreaAction() {
        super("Nova Parcela a partir do Cálculo de Área");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Main instance = Main.getInstance();

        PFileBrowser fileBrowser = new PFileBrowser();
        fileBrowser.addFileFilter(new FileFilterExtension("rtf"));
        List<File> listFiles = fileBrowser.showOpenListFileDialog(instance);

        for (File f : listFiles) {
            try {
                addFile(instance, f);
            } catch (Throwable ex) {
                Logger.getLogger(NewQuotaFromCalcAreaAction.class.getName()).log(Level.SEVERE, null, ex);
                int result = JOptionPane.showConfirmDialog(instance,
                        String.format("Houve um erro na importação do cálculo de área referente ao arquivo: %s\n" + ex.getMessage()
                        + "\nDeseja tentar importar os outros arquivos mesmo assim?", f.getName()), "Pergunta", JOptionPane.YES_NO_OPTION);

                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }
    }

    private void addFile(Main main, File file) throws FileNotFoundException, IOException, BadLocationException, InvalidGeodesicPointException {
        ProjectMetadata projectInfo = main.getProjectInfo();
        Integer zonaUtm = projectInfo.getZonaUtm();
        Hemisphere hemisferio = projectInfo.getHemisferio();
        Datum datum = projectInfo.getDatum();

        String name = file.getName();
        name = name.substring(0, name.lastIndexOf("."));
        MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();
        Polygonal polygonal = mainPolygonal.createOrGetPolygonal(name);

        FileInputStream stream = new FileInputStream(file);

        PointImporter importer = new CalculoAreaRTFImporter(new Handler(polygonal), stream, PointImporter.defaultCalculoAreaSequence, zonaUtm, hemisferio, datum);
        importer.importData();
    }

    private class Handler implements PointImporterHandle {

        private Polygonal polygonal;

        public Handler(Polygonal polygonal) {
            this.polygonal = polygonal;
        }

        @Override
        public void startImport() {
        }

        @Override
        public void handlePoint(GeodesicPoint point) {
            GeodesicPoint findPoint = DataManagement.findPoint(point);
            if (findPoint == null) {
                GeodesicPointType type = point.getType();
                if (type == GeodesicPointType.O || type == GeodesicPointType.V) {
                    DataManagement.getAllPoints().addElement(point);
                } else {
                    this.polygonal.clearAll();
                    String msg = String.format("O ponto %s não foi encontrado na base de dados desse projeto, a importação será cancelada.", point.getName());
                    throw new RuntimeException(msg);
                }
            }

            this.polygonal.addElement(findPoint);
        }

        @Override
        public void endImport() {
            this.polygonal.forceClose();
        }
    }
}
