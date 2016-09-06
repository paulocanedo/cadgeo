/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller;

import br.com.geomapa.geodesic.InvalidGeodesicPointException;
import br.com.geomapa.importer.PointImporter;
import br.com.geomapa.main.Main;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author paulocanedo
 */
public class PointImporterCommand implements Command {

    private PointImporter importer;

    public PointImporterCommand(PointImporter importer) {
        this.importer = importer;
    }

    @Override
    public void execute() {
        Main main = Main.getInstance();
        try {
            importer.importData();
        } catch (InvalidGeodesicPointException ex) {
            Logger.getLogger(PointImporterCommand.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(main, "<html>Não foi possível importar: <br/ ><b>" + ex.getMessage() + "</b></html>");
        } catch (Throwable ex) {
            Logger.getLogger(PointImporterCommand.class.getName()).log(Level.SEVERE, null, ex);
            String msg = String.format("<html><div width=400px>Não foi possível importar, informaçõees para depurar: <br />"
                    + "<b><font color='#22389a'>%s: </font>%s</b></div></html>", ex.getClass().getSimpleName(), ex.getMessage());
            JOptionPane.showMessageDialog(main, msg);
        }
    }

    @Override
    public void undo() {
        importer.removeImport();
    }

    @Override
    public void store() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void load() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
