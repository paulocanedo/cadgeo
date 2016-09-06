/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.graphic.cad.primitives.LineArrow;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class LineArrowCommand extends AbstractCadCommand {
    
    private LineArrow linearrow = new LineArrow();
    private int state = 0;

    private static final String[] messages = {
        "Informe o ponto inicial",
        "Informe o ponto final da seta"
    };

    public LineArrowCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    public LineArrowCommand() {
        super(messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws Throwable {
        Point2D point = parsePoint(text);
        return nextState(point);
    }

    @Override
    public boolean transitToNextState(Point2D point) throws Throwable {
        switch (state) {
            case 0:
                linearrow.setLocation(point);
                this.message = messages[0];
                this.canDraw = true;
                break;
            case 1: {
                linearrow.setEndLocation(point);

                execute();
                break;
            }
            default:
                throw new AssertionError();
        }

        state++;
        return true;
    }

    @Override
    public VisualObject getVisualObject() {
        return linearrow;
    }

    @Override
    public void execute() {
        getDisplayPanel().addToVisualObjects(linearrow);

        finish();
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(linearrow);
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new LineArrowCommand(displayPanel);
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        linearrow.setEndLocation(point);
    }

    @Override
    public String toString() {
        return "Linha com seta";
    }

    @Override
    public String getCommandName() {
        return "linha_seta";
    }
    
}
