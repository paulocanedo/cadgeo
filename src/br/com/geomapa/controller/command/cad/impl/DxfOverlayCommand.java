/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.graphic.cad.dxf.DxfOverlayVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Main;
import br.com.geomapa.ui.FileFinder.ExtensionFileFilter;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.FileDialog;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author paulocanedo
 */
public class DxfOverlayCommand extends AbstractCadCommand {

    private static final FilenameFilter fileFilter = new ExtensionFileFilter("dxf");

    public DxfOverlayCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, "");
    }

    public DxfOverlayCommand() {
        super("");
    }

    @Override
    public boolean transitToNextState(String text) throws Throwable {
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) throws Throwable {
        return false;
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public void execute() {
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setFilenameFilter(fileFilter);
        fileDialog.setVisible(true);

        if (fileDialog.getFile() != null) {
            File file = new File(fileDialog.getDirectory(), fileDialog.getFile());
            if (file.exists() && file.isFile()) {
                try {
                    DxfOverlayVisualObject dxfOverlayVisualObject = new DxfOverlayVisualObject(file);
                    if (!dxfOverlayVisualObject.isEmpty()) {
                        getDisplayPanel().addToVisualObjects(dxfOverlayVisualObject);
                    } else {
                        JOptionPane.showMessageDialog(Main.getInstance(), "O sistema não foi capaz de extrair nenhuma informação do arquivo DXF apontado.");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(DxfOverlayCommand.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(Main.getInstance(), "Houve um erro ao inserir a camada dxf sobreposta\n\n" + ex.getMessage());
                }
            }
        }
        redraw();
        finish();
    }

    @Override
    public void undo() {
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public boolean acceptAnyChar() {
        return false;
    }

    @Override
    public boolean acceptSpaceBar() {
        return false;
    }

    @Override
    public boolean canSelect() {
        return false;
    }

    @Override
    public boolean canUseMagnetic() {
        return false;
    }

    @Override
    public boolean hasToBeExecuted() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new DxfOverlayCommand();
    }

    @Override
    public boolean singleSelect() {
        return false;
    }

    @Override
    public String toString() {
        return "Importar DXF sobreposto";
    }

    @Override
    public String getCommandName() {
        return "dxf_sobreposto";
    }
}
