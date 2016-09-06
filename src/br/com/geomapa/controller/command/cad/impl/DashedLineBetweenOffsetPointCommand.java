/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.util.ArraysUtil;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author paulocanedo
 */
public class DashedLineBetweenOffsetPointCommand extends AbstractCadCommand {

    private HashMap<LineDivision, LineType> collectionToUndo = new HashMap<LineDivision, LineType>();

    public DashedLineBetweenOffsetPointCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, "");
    }

    public DashedLineBetweenOffsetPointCommand() {
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
        Collection<LineDivision> collect = ArraysUtil.collect(getPolygonal().getVisualObjects(), LineDivision.class);

        for (LineDivision ld : collect) {
            GeodesicPoint startPoint = ld.getStartPoint();
            GeodesicPoint endPoint = ld.getEndPoint();
            if (startPoint.getType() == GeodesicPointType.O
                    && endPoint.getType() == GeodesicPointType.O) {
                if (collectionToUndo.containsKey(ld)) {
                    continue;
                }
                LineDivision rld = LineDivision.getInstance(endPoint, startPoint);

                collectionToUndo.put(ld, ld.getLineType());
                collectionToUndo.put(rld, rld.getLineType());

                ld.setLineType(AbstractVisualObject.DASHED_LINE_TYPE);
                rld.setLineType(AbstractVisualObject.DASHED_LINE_TYPE);
            }
        }
        redraw();
        finish();
    }

    @Override
    public void undo() {
        for (LineDivision ld : collectionToUndo.keySet()) {
            ld.setLineType(collectionToUndo.get(ld));
        }
        collectionToUndo.clear();
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
        return new DashedLineBetweenOffsetPointCommand(displayPanel);
    }

    @Override
    public String toString() {
        return "Pontilhar entre pontos Offset";
    }

    @Override
    public String getCommandName() {
        return "pontilhar_entre_offset";
    }
}
