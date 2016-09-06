/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.point.MetaDataPoint;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.project.ProjectUtils;
import br.com.geomapa.ui.panels.options.OptionsPanel;
import br.com.geomapa.ui.theme.IconManagerFactory;
import br.com.geomapa.util.IOUtils;
import br.com.geomapa.util.mlist.MacroList;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 *
 * @author paulocanedo
 */
public class SaveProjectAction extends AbstractAction {

    private static final SimpleDateFormat timeStampSdf = new SimpleDateFormat("yyyy-MM-dd kk-mm");
    private Calendar calendar = Calendar.getInstance();

    public SaveProjectAction() {
        super("Salvar Projeto");
        putValue(SHORT_DESCRIPTION, "Salva o projeto atual no disco");
        putValue(MNEMONIC_KEY, (int) 'S');
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((int) 'S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        putValue(LARGE_ICON_KEY, IconManagerFactory.getSave());
    }

    private void checkFolderExists(File folder) throws IOException {
        if ((folder.exists() && folder.isDirectory()) == false) {
            throw new IOException("Não foi salvar o projeto, motivo: Falha ao criar diretório");
        }
    }

    private void save(ProjectMetadata projectInfo) throws IOException {
        if (projectInfo == null || projectInfo.getNome().length() < 3) {
            throw new IOException("Para salvar o projeto, informe um nome com pelo menos 3 caracteres");
        }

        if (projectInfo.getRootFolder() == null) {
            String projectName = projectInfo.getNome();
            calendar.setTime(new Date());
            File rootFolder = new File(OptionsPanel.getProjectDir(), String.format("%d/%d/%s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, projectName));
            if (rootFolder.exists()) {
                throw new IOException("Já existe um projeto com esse nome, por favor utilize outro nome");
            }

            projectInfo.setRootFolder(rootFolder);
        }

        File metadaDataFolder = projectInfo.getMetadataFolder();

        if (metadaDataFolder.exists()) {
            backup(metadaDataFolder, projectInfo.getHistoryFolder());
        }
        metadaDataFolder.mkdirs();

        checkFolderExists(metadaDataFolder);
        MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();

        //Save metadata
        saveMetadata(new File(metadaDataFolder, ProjectUtils.metadataFileName), projectInfo.toProperties());

        //Save all Points
        savePoints(new File(metadaDataFolder, ProjectUtils.pointsFileName), DataManagement.getAllPoints());
        saveMetadataPoints(new File(metadaDataFolder, ProjectUtils.pointsMetadataFileName));

        //Save main Polygonal
        save(metadaDataFolder, mainPolygonal, "0");

        //Save all the rest Polygonals
        int count = 0;
        for (String polygonalName : mainPolygonal.polygonalNames()) {
            Polygonal polygonal = mainPolygonal.createOrGetPolygonal(polygonalName);
            save(metadaDataFolder, polygonal, String.valueOf(++count));
        }
    }

    private void backup(File metadaDataFolder, File destine) throws IOException {
        IOUtils.copyDirectory(metadaDataFolder, new File(destine, "" + System.currentTimeMillis() / (long) 1E4));
    }

    private void saveMetadata(File file, Properties prop) throws FileNotFoundException, IOException {
        prop.put("ultima_modificacao", timeStampSdf.format(new Date()));
        prop.put("version", "1.0");
        FileOutputStream fos = new FileOutputStream(file);
        prop.store(fos, "Dados do projeto");
    }

    private void saveMetadataPoints(File file) throws IOException {
        List<GeodesicPoint> listFavoritePoints = DataManagement.listFavoritePoints();
        List<GeodesicPoint> listSatGeoPoints = DataManagement.listSatGeoPoints();
        StringBuilder favorites = new StringBuilder();
        StringBuilder satGeos = new StringBuilder();
        for (GeodesicPoint gpoint : listFavoritePoints) {
            favorites.append(gpoint.getName()).append(",");
        }
        for (GeodesicPoint gpoint : listSatGeoPoints) {
            satGeos.append(gpoint.getName()).append(",");
        }

        Properties prop = new Properties();
        prop.put("favoritos", favorites.toString());
        prop.put("sat", satGeos.toString());
        FileOutputStream fos = new FileOutputStream(file);
        prop.store(fos, "Pontos favoritos e marcos satelites");
    }

    private void savePoints(File file, MacroList<GeodesicPoint> allPoints) throws IOException {
        FileWriter csvWriter = null;
        try {
            csvWriter = new FileWriter(file);
            String line;
            for (GeodesicPoint point : allPoints) {
                UTMCoordinate coord = point.getCoordinate().toUTM();
                MetaDataPoint metaData = point.getMetaData();
                line = String.format(Locale.US, "%s,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%s", point.getName(),
                        coord.getEast(), coord.getNorth(), coord.getEllipsoidalHeight(),
                        metaData == null ? " " : metaData.getQx(), metaData == null ? " " : metaData.getQy(), metaData == null ? " " : metaData.getQz(),
                        metaData == null ? " " : metaData.getMeasurementMethod() == null ? " " : metaData.getMeasurementMethod().name());
                csvWriter.write(line + "\n");
            }
            csvWriter.flush();
        } finally {
            csvWriter.close();
        }
    }

    private void save(File metadataFolder, Polygonal polygonal, String fileName) throws IOException {
        FileWriter writer = null;
        File dir = new File(metadataFolder, "polygonals");

        File filePolygonal = new File(dir, fileName);
        File filePolygonalVO = new File(dir, fileName + ".vo");
        File filePolygonalMeta = new File(dir, fileName + ".properties");

        filePolygonal.getParentFile().mkdirs();
        checkFolderExists(filePolygonal.getParentFile());

        try {
            writer = new FileWriter(filePolygonal);

            List<GeodesicPoint> points = polygonal.toListGeoPoints();
            for (int i = 0; i < points.size(); i++) {
                GeodesicPoint point = points.get(i);

                if (i > 0) {
                    writer.write(",");
                }
                writer.write(point.getName());
            }
            writer.flush();

            Properties prop = polygonal.getMetadata().toProperties();
            prop.put("nome", polygonal.getName());

            OutputStreamWriter owriter = new OutputStreamWriter(new FileOutputStream(filePolygonalMeta), "utf-8");
            prop.store(owriter, "Metadados da poligonal");
            owriter.flush();
            owriter.close();

            PrintStream stream = new PrintStream(filePolygonalVO, "utf-8");
            for (VisualObject vo : polygonal.getVisualObjects()) {
                vo.write(stream);
            }
            polygonal.write(stream);
            stream.flush();
            stream.close();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void save() throws IOException {
        Main mainInstance = Main.getInstance();
        ProjectMetadata projectInfo = mainInstance.getProjectInfo();
        save(projectInfo);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            save();
        } catch (IOException ex) {
            Logger.getLogger(SaveProjectAction.class.getName()).log(Level.SEVERE, null, ex);
            javax.swing.JOptionPane.showMessageDialog(Main.getInstance(), "Falha ao gravar projeto, detalhes: \n\n" + ex.getMessage());
        }
    }
}
