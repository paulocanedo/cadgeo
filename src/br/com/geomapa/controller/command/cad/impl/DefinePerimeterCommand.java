/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.compound.AbstractVisualObjectCompound;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.options.SchemeColors;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

/**
 *
 * @author paulocanedo
 */
public class DefinePerimeterCommand extends AbstractCadCommand {

    private Line line = new Line();
    private AuxVisualObject auxVO;
    private static final String[] messages = new String[]{"Informe a parcela a ser definido o perímetro"};
    private Polygonal polygonal;

    public DefinePerimeterCommand() {
        super(messages[0]);
    }

    public DefinePerimeterCommand(GLTopographicPanel displayPanel) throws CommandException {
        super(displayPanel, messages[0]);

        checkIfPolygonalIsMain();
        polygonal = chooseOpenedPolygonal();
        if (polygonal == null) {
            throw new CommandException();
        } else {
            auxVO = new AuxVisualObject(line, polygonal);
            getDisplayPanel().clearSelection();
            super.message = "Informe o ponto inicial";
        }
    }
    
    private void insert(GeodesicPoint gpoint) {
        super.canDraw = true;
        line.setLocation(gpoint.getLocation());
        polygonal.addElement(gpoint);
        LinkedList<LineDivision> lineDivisions = polygonal.getLineDivisions();
        if (!lineDivisions.isEmpty()) {
            line.setLocation(lineDivisions.getLast().getEndLocation());
        }
        if (polygonal.isClosed()) {
            execute();
        }
        super.message = "Informe o próximo ponto";
    }

    @Override
    public boolean transitToNextState(String text) throws PolygonalException {
        if(text.equalsIgnoreCase("u")) {
            undo();
            return false;
        }
        
        GeodesicPoint findPoint = searchPoint(text);
        if (findPoint == null) {
            throw new PolygonalException(String.format("O ponto %s não foi encontrado.", text));
        }

        insert(findPoint);
        return true;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (polygonal == null) {
            return false;
        }

        for (GeodesicPoint gpoint : DataManagement.getAllPoints()) {
            Point2D location = gpoint.getLocation();

            if (PolygonalUtils.horizontalDistance(location.getX(), location.getY(), point.getX(), point.getY()) < 0.1) {
                insert(gpoint);
                return true;
            }
        }
        return false;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        if (polygonal != null && !polygonal.isClosed()) {
            line.setEndLocation(point);
        }
    }

    @Override
    public void execute() {
        redraw();
        
        finish();
    }

    @Override
    public void undo() {
        if(!polygonal.isEmpty()) {
            polygonal.getLineDivisions().removeLast();
            
            LineDivision last = polygonal.getLineDivisions().getLast();
            line.setLocation(last.getEndLocation());
        }
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Definir perímetro";
    }

    @Override
    public VisualObject getVisualObject() {
        return auxVO;
    }

    @Override
    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException {
        super.setDisplayPanel(displayPanel);

        checkIfPolygonalIsMain();
        polygonal = chooseOpenedPolygonal();
        if (polygonal == null) {
            throw new CommandException();
        } else {
            auxVO = new AuxVisualObject(line, polygonal);
            getDisplayPanel().clearSelection();
            super.message = "Informe o ponto inicial";
        }
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new DefinePerimeterCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "define_perimetro";
    }

    private class AuxVisualObject extends AbstractVisualObjectCompound {

        @Override
        public float getLineWidth() {
            return SchemeColors.SELECTED_AND_BOLD;
        }

        @Override
        public Color getColor() {
            return SchemeColors.SELECTED;
        }

        public AuxVisualObject(VisualObject vo1, VisualObject vo2) {
            delegate.add(vo1);
            delegate.add(vo2);
        }

        @Override
        public void write(PrintStream stream) throws IOException {
        }

        @Override
        public String getVisualObjectName() {
            return "auxVO";
        }
       
    }
}
