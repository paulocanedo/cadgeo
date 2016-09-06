/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CadCommandController;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.graphic.cad.text.VisualTextDialog;
import br.com.geomapa.main.Bus;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class TextCommand extends AbstractCadCommand {

    private VisualText vtext = new VisualText();
    private int state = -1;
    //
    //auxiliar variables
    private Point2D aPoint = new Point2D.Double();
    private Line aLine = new Line();
    private static VisualTextDialog vtextDialog;
    private static final String[] messages = new String[]{"Informe o ponto de inserção do texto"};

    public TextCommand() {
        super(messages[0]);
    }

    public TextCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CoordinateException {
        if (state == -1) {
            Point2D point = parsePoint(text);
            return nextState(point);
        } else if (state == 0 || state == 1) {
            float value = getPositiveDouble(text, "Valor da altura do texto deve ser maior que zero: %s").floatValue();
            this.vtext.setHeight(value);
            super.message = "Informe o ângulo de rotação do texto";
            this.state = 2;
        } else if (state == 2 || state == 3) {
            double value = getDouble(text, "Valor de rotação do texto incorreto: %s");
            this.vtext.setRotation(value);
            this.state = 4;
            insertText();
        }
        return true;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (state == -1) {
            vtext.setLocation(point);
            super.message = "Informe a altura do texto";
            state++;
        } else if (state == 0) {
            aPoint.setLocation(point);
            aLine.setLocation(point);
            super.canDraw = true;
            state++;
        } else if (state == 1) {
            vtext.setHeight((float) aPoint.distance(point) / Bus.getScale());
            super.message = "Informe o ângulo de rotação do texto";
            state++;
        } else if (state == 2) {
            aLine.setLocation(point);
            state++;
        } else if (state == 3) {
            super.message = "Informe o texto a ser inserido";
            double angle = 90 - PolygonalUtils.azimuth(aLine.getLocation(), point).toDegreeDecimal();
            vtext.setRotation(angle);
            state++;
            insertText();
        }

        return true;
    }

    private boolean insertText() {
        displayPanel.setTempVO(vtext);
        VisualTextDialog visualTextDialog = getVisualTextDialog();
        visualTextDialog.setRotationAngle(vtext.getRotation());
        visualTextDialog.setVisible(true);
        if (visualTextDialog.getReturnStatus() == VisualTextDialog.RET_OK) {
            vtext.setText(visualTextDialog.getText());
            execute();
        } else {
            CadCommandController.setCommand(null);
        }
        displayPanel.setTempVO(null);
        return true;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        if (state > 0) {
            aLine.setEndLocation(point);
        }
    }

    @Override
    public void execute() {
        getDisplayPanel().addToVisualObjects(vtext);

        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(vtext);
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Texto";
    }

    @Override
    public VisualObject getVisualObject() {
        if (state == 1 || state == 3) {
            return aLine;
        }
        return null;
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
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new TextCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "texto";
    }
}
