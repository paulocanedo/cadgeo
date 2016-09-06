/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CadCommandController;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.graphic.cad.text.VisualTextDialog;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class TextEditCommand extends AbstractCadCommand {

    private VisualText vtext;
    private String originalText;
    private int state = 0;
    private static VisualTextDialog vtextDialog;
    private static final String prompt = "Clique no texto que deseja editar";

    public TextEditCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, prompt);
    }

    public TextEditCommand() {
        super(prompt);
    }

    @Override
    public boolean transitToNextState(String text) throws Throwable {
        if (state == 1) {
            vtext.setText(text);
            execute();
        }
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) throws Throwable {
        if (state > 0) {
            return false;
        }

        VisualObject singleSelection = getDisplayPanel().singleSelection(point);
        if (singleSelection != null && singleSelection.getClass() == VisualText.class) {
//            state++;
            vtext = (VisualText) singleSelection;
            originalText = vtext.getTextOriginal();
            super.message = "Informe o novo texto";

            VisualTextDialog visualTextDialog = getVisualTextDialog();
            visualTextDialog.setPromptText(originalText);
            visualTextDialog.setRotationAngle(vtext.getRotation());
            visualTextDialog.setVisible(true);
            if (visualTextDialog.getReturnStatus() == VisualTextDialog.RET_OK) {
                vtext.setText(visualTextDialog.getText());
                double rotationAngle = visualTextDialog.getRotationAngle();
                if (rotationAngle != Double.MAX_VALUE) {
                    vtext.setRotation(rotationAngle);
                }
            } else {
                canceled();
                CadCommandController.setCommand(null);
            }
            execute();
            return true;
        }
        return false;

    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public void execute() {
        redraw();
        finish();
    }

    @Override
    public void undo() {
        if (vtext != null) {
            vtext.setText(originalText);
        }
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public void canceled() {
        undo();
    }

    @Override
    public boolean canSelect() {
        return false;
    }

    @Override
    public boolean singleSelect() {
        return true;
    }

    @Override
    public boolean canUseMagnetic() {
        return false;
    }
    
    @Override
    public boolean acceptSpaceBar() {
        return true;
    }

    private VisualTextDialog getVisualTextDialog() {
        if (vtextDialog == null) {
            vtextDialog = new VisualTextDialog(displayPanel);
        }
        vtextDialog.clear();
        vtextDialog.setVisualTextRefer(vtext);
        return vtextDialog;
    }

    @Override
    public String toString() {
        return "Edição de texto";
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new TextEditCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "editar_texto";
    }
}
