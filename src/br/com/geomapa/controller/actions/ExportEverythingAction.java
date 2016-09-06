/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.util.SecurityUtils;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.ui.panels.GeodesicPanel;
import br.com.geomapa.util.UserInterfaceUtil;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author paulocanedo
 */
public class ExportEverythingAction extends AbstractAction {

    private static List<String> errorsPolygonal = new ArrayList<String>();

    public ExportEverythingAction() {
        super("Exportar");
    }

    public static void callExport() {
        long currentTimeMillis = System.currentTimeMillis();
        HashMap<String, JPanel> createPanels = UserInterfaceUtil.createPanels();

        for (JPanel panel : createPanels.values()) {
            if (panel instanceof GeodesicPanel) {
                try {
                    GeodesicPanel gpanel = ((GeodesicPanel) panel);
                    MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();
                    gpanel.export(mainPolygonal);

                    if (!SecurityUtils.gmIsKeyValid()) {
                        continue;
                    }

                    Set<String> polygonalNames = mainPolygonal.polygonalNames();
                    for (String spolygonal : polygonalNames) {
                        gpanel.export(mainPolygonal.createOrGetPolygonal(spolygonal));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ExportEverythingAction.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(Main.getInstance(), "Erro de ES\n. Mais informações: " + ex.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(Main.getInstance(), ex.getMessage());
                }
            }
        }
        System.out.println("Demorou " + (System.currentTimeMillis() - currentTimeMillis) + " milisegs para exportar");
        if (!SecurityUtils.gmIsKeyValid()) {
            JOptionPane.showMessageDialog(Main.getInstance(), "Versão de teste permite exportar apenas a Planta Geral."
                    + "\nEntre em contato em paulocanedo@gmail.com para saber mais informações de como obter a sua licença.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Main main = Main.getInstance();
        ProjectMetadata projectInfo = main.getProjectInfo();
        if (projectInfo == null || projectInfo.getRootFolder() == null) {
            JOptionPane.showMessageDialog(main, "Para exportar os dados é necessário salvar o projeto antes.");
            return;
        }

        callExport();
        int confirm = JOptionPane.showConfirmDialog(main,
                "A exportação foi concluída com sucesso. Deseja visualizar o diretório de saída?"
                + "\nAVISO: Caso queira editar algum arquivo gerado automaticamente pelo GeoMapa, copie para fora do diretório de trabalho.",
                "Pergunta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Desktop.getDesktop().open(projectInfo.getRootFolder());
            } catch (IOException ex) {
                Logger.getLogger(ExportEverythingAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
