/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.controller.Command;
import br.com.geomapa.controller.CommandController;
import br.com.geomapa.controller.MacroCommand;
import br.com.geomapa.controller.PointImporterCommand;
import br.com.geomapa.geodesic.GeodesicEnum;
import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.datum.Datum;
import br.com.geomapa.importer.AstechRTFImporter;
import br.com.geomapa.importer.CSVPointImporter;
import br.com.geomapa.importer.PointImporter;
import br.com.geomapa.importer.TopconDOCXImporter;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.ui.model.GeoPointTableModel;
import br.com.geomapa.ui.panels.PointManagerPanel;
import br.com.pc9.pswing.components.filebrowser.PFileBrowser;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

/**
 *
 * @author paulocanedo
 */
public class ImportAction extends AbstractAction {

    private GeoPointTableModel tableModel = DataManagement.getGeoPointTableModel();
    private String extension;
    private PFileBrowser fileBrowser = new PFileBrowser();

    public ImportAction(final String name, final String shortDescription, final String extension) {
        super(name);
        putValue(SHORT_DESCRIPTION, shortDescription);

        this.extension = extension;
        fileBrowser.addFileFilter(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || (pathname.getName().toLowerCase().endsWith(extension));
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Main main = Main.getInstance();
        ProjectMetadata projectInfo = main.getProjectInfo();
        Integer zonaUtm = projectInfo.getZonaUtm();

        if (zonaUtm == 0) {
            JOptionPane.showMessageDialog(Main.getInstance(), "É necessário informar a zona UTM principal antes de realizar qualquer importação.");
            return;
        }
        Hemisphere hemisferio = projectInfo.getHemisferio();
        Datum datum = projectInfo.getDatum();

        List<File> listFiles = fileBrowser.showOpenListFileDialog(main);
        Command[] batchCommand = new Command[listFiles.size()];
        for (int i = 0; i < listFiles.size(); i++) {
            File file = listFiles.get(i);
            FileInputStream input;
            try {
                input = new FileInputStream(file);
                GeodesicEnum[] sourceOrder = null;
                PointImporter importer = null;
                if (extension.toLowerCase().equals("csv")) {
                    sourceOrder = findCsvSequence(file);
                    importer = new CSVPointImporter(tableModel, input, sourceOrder, zonaUtm, hemisferio, datum) {

                        @Override
                        public void removeImport() {
                            tableModel.removeLastImport();
                        }
                    };
                } else if (extension.toLowerCase().equals("rtf")) {
                    sourceOrder = PointImporter.astechSequence;
                    importer = new AstechRTFImporter(tableModel, input, sourceOrder, zonaUtm, hemisferio, datum) {

                        @Override
                        public void removeImport() {
                            tableModel.removeLastImport();
                        }
                    };
                } else if (extension.toLowerCase().equals("docx")) {
                    sourceOrder = PointImporter.topconPlusSequence;
                    importer = new TopconDOCXImporter(tableModel, input, sourceOrder, zonaUtm, hemisferio, datum) {

                        @Override
                        public void removeImport() {
                            tableModel.removeLastImport();
                        }
                    };
                }

                batchCommand[i] = new PointImporterCommand(importer);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PointManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(main, "Arquivo não encontrado.");
            } catch (IOException ex) {
                Logger.getLogger(PointManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(main, "Não foi possível ler o conteúdo do arquivo: " + file.getName());
            } catch (BadLocationException ex) {
                Logger.getLogger(PointManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(main, "Falha na leitura do arquivo RTF.");
            } catch (OutOfMemoryError ex) {
                JOptionPane.showMessageDialog(main, "Falha na importação, causado por espaço na pilha.\nEntre em contato com o suporte para resolver este problema." + ex.getMessage());
            } catch (Throwable ex) {
                Logger.getLogger(PointManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(main, "Falha na importação:\n" + ex.getMessage());
            }
        }

        MacroCommand macroCommand = new MacroCommand(batchCommand);
        CommandController.setCommand(macroCommand);
        CommandController.executeCommand();
    }

    public static GeodesicEnum[] findCsvSequence(File file) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        int count = countColumns(reader.readLine());

        String line = reader.readLine();
        if (line != null) {
            count = Math.max(count, countColumns(line));

            line = reader.readLine();
            if (line != null) {
                count = Math.max(count, countColumns(line));
            }
        }

        if (count == 4) {
            return PointImporter.simpleCsvSequence;
        } else if (count == 6) {
            return PointImporter.csvSequenceShort;
        }
        return PointImporter.csvSequence;
    }

    private static int countColumns(String line) {
        StringTokenizer st = new StringTokenizer(line, ",");
        int count = 0;
        while ((st.hasMoreTokens())) {
            String token = st.nextToken();
            if (!token.isEmpty()) {
                count++;
            }
        }
        return count;
    }
}