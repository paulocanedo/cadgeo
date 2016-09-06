/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.main.Main;
import br.com.pc9.pswing.util.SystemUtilsOS;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.apache.poi.util.IOUtils;

/**
 *
 * @author paulocanedo
 */
public class InstallTBCReportAction extends AbstractAction {

    public InstallTBCReportAction() {
        super("Instalar relatório no TBC");
        putValue(SHORT_DESCRIPTION, "Instala relatório personalizado formato CSV no Trimble Business Center.\n Requer versão 2.40 ou superior do TBC.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        InputStream resourceAsStream = getClass().getResourceAsStream("/br/com/geomapa/resources/templates/P,N,E,H,Q_HOR,Q_VER.ocexp");

        File dirout;
        if (SystemUtilsOS.isWindowsXP()) {
            dirout = new File(SystemUtilsOS.getUserHomeAsFile(), "Configurações locais\\Dados de aplicativos\\Trimble\\Trimble Business Center Survey");
        } else {
            dirout = new File(SystemUtilsOS.getUserHomeAsFile(), "AppData\\Roaming\\Trimble\\Trimble Business Center Survey");
        }

        File[] listFiles = dirout.listFiles();
        if (listFiles != null) {
            for (File f : listFiles) {
                String name = f.getName();
                try {
                    double version = Double.parseDouble(name);
                    if (version > 9.0) {
                        File outputfile = new File(f, "P,N,E,H,Q_HOR,Q_VER.ocexp");
                        try {
                            IOUtils.copy(resourceAsStream, new FileOutputStream(outputfile));
                            JOptionPane.showMessageDialog(Main.getInstance(), "Relatório CSV personalizado para TBC foi instalado com sucesso!");
                            return;
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(Main.getInstance(), "Falha ao escrever arquivo de relatório.");
                        }
                    }
                } catch (NumberFormatException ex) {
                    continue;
                }
            }
        }
        JOptionPane.showMessageDialog(Main.getInstance(), "Não foi encontrado diretório de relatórios personalizados do TBC versão 2.40 ou superior");
    }
}
