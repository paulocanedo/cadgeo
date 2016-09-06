/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.graphic.cad.compound.DescriptorPaper;
import br.com.geomapa.graphic.cad.compound.ISO_Paper;
import br.com.geomapa.importer.pcgeocad.VisualObjectParserException;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;
import java.io.IOException;

/**
 *
 * @author paulocanedo
 */
public class PaperCommand extends AbstractCadCommand {

    private ISO_Paper paper;
    private int state = -1;
    private static final String[] messages = new String[]{"Qual formato deseja inserir? A(1) A(2) A(3) A(4)?"};

    public PaperCommand() {
        super(messages[0]);
    }

    public PaperCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CoordinateException, CommandException, VisualObjectParserException {
        if (state == -1) {
            char aChar = getCharacter(text, "Você deve informar uma das opções: A(1) A(2) A(3) A(4)", '1', '4');
            paper = new ISO_Paper(0, 0, new DescriptorPaper(false, "Formato A" + aChar));

            state++;
            super.message = "Informe o ponto de inserção do quadro";
            super.canDraw = true;

            return true;
        } else if (state == 0) {
            Point2D point = parsePoint(text);
            return nextState(point);
        }
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (state == -1) {
            return false;
        } else if (state == 0) {
            paper.setLocation(point);

            execute();
        }

        state++;
        return true;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        if (state == 0) {
            paper.setLocation(point);
        }
    }

    @Override
    public void execute() {
        getDisplayPanel().addToVisualObjects(paper);

        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(paper);
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Formato de Papel";
    }

    @Override
    public ISO_Paper getVisualObject() {
        return paper;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new PaperCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "formato_papel";
    }
}
