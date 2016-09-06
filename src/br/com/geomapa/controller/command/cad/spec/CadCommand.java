/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.spec;

import br.com.geomapa.controller.Command;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public interface CadCommand extends Command {

    public boolean nextState(String text);

    public boolean nextState(Point2D point);

    public boolean transitToNextState(String text) throws Throwable;

    public boolean transitToNextState(Point2D point) throws Throwable;

    public void setCurrentPosition(Point2D point);

    public void setLastPoint(double x, double y);

    public Point2D prepareForOrtho(Point2D point);

    public String getMessageStatus();

    public boolean wasFinished();

    public boolean canDraw();

    public VisualObject getVisualObject();

    public boolean canSelect();

    public boolean singleSelect();

    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException;

    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException;

    public void canceled();

    public boolean isUndoable();

    public boolean acceptSpaceBar();

    public boolean acceptAnyChar();

    public boolean hasToBeExecuted();

    public boolean canUseMagnetic();

    public String getCommandName();
}
