/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.rinex;

import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.project.ProjectUtils;
import br.com.pc9.pswing.components.filebrowser.PFileBrowser;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 *
 * @author paulocanedo
 */
public class RinexImporterAction extends AbstractAction {

    private final static PFileBrowser fileBrowser = new PFileBrowser();

    static {
        fileBrowser.addFileFilter(RinexFileFilter.FILE_FILTER);
    }

    public RinexImporterAction(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Main main = Main.getInstance();
        ProjectMetadata projectInfo = main.getProjectInfo();

        File folder = projectInfo.getRootFolder();
        if (folder == null) {
            JOptionPane.showMessageDialog(main, "É necessário gravar o projeto antes de importar dados RINEX");
            return;
        }

        File rinexFolder = new File(folder, ProjectUtils.rinexDirName);
        if (!rinexFolder.exists()) {
            boolean mkdir = rinexFolder.mkdir();
            if (mkdir == false) {
                JOptionPane.showMessageDialog(main, "N√£o foi poss√≠vel criar diret√≥rio para arquivos RINEX");
                return;
            }
        }

        File selected = fileBrowser.showOpenFileDialog(main);
        if (selected != null) {
            try {
                new RinexObsSplitter().split(selected, rinexFolder);
            } catch (IOException ex) {
                Logger.getLogger(RinexImporterAction.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(main, "Falha na importação dos dados RINEX. E/S, Mais detalhes: \n" + ex.getMessage());
            } catch (Throwable ex) {
                Logger.getLogger(RinexImporterAction.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(main, "Falha na importação dos dados RINEX. \n" + ex.getMessage());
            }
        }
    }
}
