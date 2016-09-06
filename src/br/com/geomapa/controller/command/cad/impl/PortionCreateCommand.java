/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.compound.AbstractVisualObjectCompound;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.options.SchemeColors;
import br.com.geomapa.ui.panels.topographic.DijkstraFinderPath;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import br.com.geomapa.util.ArraysUtil;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paulocanedo
 */
public class PortionCreateCommand extends AbstractCadCommand {

    private Polygonal polygonal = new Polygonal("temp");
    private Line line = new Line();
    private AuxVisualObject auxVO = new AuxVisualObject(polygonal, line);
    private static final String[] messages = new String[]{
        "Criar parcela com detecção (A)automática ou por pontos (M)manual?",
        "Indique um ponto que faça parte da parcela.",
        "Enter para prosseguir ou indique outro ponto.",
        "Informe o nome da parcela",
        "Por favor escolha detecção (A)automática ou (M)manual",
        "Informe o primeiro ponto da parcela",
        "Já existe uma parcela com o nome: '%s'",
        "Informe o próximo ponto da parcela. (F)fechar a parcela."
    };
    private boolean auto;
    private int state = 0;

    public PortionCreateCommand() {
        super(messages[0]);
    }

    public PortionCreateCommand(GLTopographicPanel displayPanel) throws PolygonalException, CommandException {
        super(displayPanel, messages[0]);

        checkIfPolygonalIsMain();
        getDisplayPanel().clearSelection();
    }

    private Set<LineDivision> grabLineDivisions() {
        Set<LineDivision> set = (Set<LineDivision>) ArraysUtil.collect(getDisplayPanel().getVisualObjects(), LineDivision.class);
        Set<LineDivision> twoWaysLD = new HashSet<LineDivision>();

        for (LineDivision ld : set) {
            twoWaysLD.add(ld);
            twoWaysLD.add(LineDivision.getInstance(ld.getEndPoint(), ld.getStartPoint()));
        }
        return twoWaysLD;
    }

    private void addGeoPoint(GeodesicPoint gpoint) {
        polygonal.addElement(gpoint);
        line.setLocation(gpoint.getLocation());
    }

    @Override
    public boolean transitToNextState(String text) throws CommandException, PolygonalException {
        if ((state == 3 || state == 4) && text.equalsIgnoreCase("U")) {
            undo();
            return false;
        }

        if (state == 0) {
            auto = 'A' == getCharacter(text, messages[4], 'A', 'M');
            state++;

            if (auto) {
                super.message = messages[1];
            } else {
                super.message = messages[5];
                state = 3;
            }
        } else if (state == 1 && polygonal != null) {
            state++;
            super.message = messages[3];
            return true;
        } else if (state == 2) {
            MainPolygonal mpolygonal = (MainPolygonal) getPolygonal();
            if (mpolygonal.containsPolygonalName(text)) {
                throw new PolygonalException(String.format(messages[6], text));
            } else {
                polygonal.setName(text);
                execute();
                return true;
            }
        } else if (state == 3) {
            GeodesicPoint searchPoint = searchPoint(text);
            addGeoPoint(searchPoint);
            state++;
            super.message = messages[7];
            super.canDraw = true;
        } else if (state == 4) {
            if (text.equalsIgnoreCase("F")) {
                polygonal.forceClose();
            } else {
                GeodesicPoint searchPoint = searchPoint(text);
                addGeoPoint(searchPoint);
            }

            if (polygonal.isClosed()) {
                state = 2;
                super.message = messages[3];
            }

            getDisplayPanel().requestRepaint();
        }
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) throws PolygonalException {
        if (state == 1) {
            for (GeodesicPoint gpoint : DataManagement.getAllPoints()) {
                Point2D location = gpoint.getLocation();

                if (PolygonalUtils.horizontalDistance(location.getX(), location.getY(), point.getX(), point.getY()) < 0.1) {
                    DijkstraFinderPath finder = new DijkstraFinderPath(grabLineDivisions(), gpoint);
                    List<GeodesicPoint> doDijkstraAlgorithm = finder.doDijkstraAlgorithm();

                    polygonal = new Polygonal("temp");
                    auxVO = new AuxVisualObject(polygonal, line);
                    polygonal.addAll(doDijkstraAlgorithm);
                    polygonal.setColor(SchemeColors.SELECTED);
                    polygonal.forceClose();
                    if (!polygonal.isClockwise()) {
                        polygonal.revertDirection();
                    }
                    polygonal.setLineWidth(SchemeColors.SELECTED_AND_BOLD);

                    super.canDraw = true;

                    getDisplayPanel().setTempVO(polygonal);
                    getDisplayPanel().requestRepaint();
                    super.message = messages[1];
                    return true;
                }
            }
        } else if (state == 3 || state == 4) {
            GeodesicPoint searchPoint = findGeoPoint(point);
            if (searchPoint == null) {
                return false;
            }
            addGeoPoint(searchPoint);
            state = 4;
            super.message = messages[7];
            super.canDraw = true;

            if (polygonal.isClosed()) {
                state = 2;
                super.message = messages[3];
            }

            getDisplayPanel().requestRepaint();
        }
        return false;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        line.setEndLocation(point);
    }

    @Override
    public void execute() {
        MainPolygonal mpolygonal = (MainPolygonal) getPolygonal();
        polygonal.autoFillBordersName();
        polygonal.setLayer(LayerController.createOrGet(polygonal.getName()));
        mpolygonal.add(polygonal);

        redraw();
        finish();
    }

    @Override
    public void undo() {
        if (!polygonal.isEmpty()) {
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
        return "Criar parcela";
    }

    @Override
    public VisualObject getVisualObject() {
        return polygonal.isClosed() ? polygonal : auxVO;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        try {
            return new PortionCreateCommand(displayPanel);
        } catch (PolygonalException ex) {
            throw new CommandException(ex);
        }
    }

    @Override
    public boolean canSelect() {
        return false;
    }

    @Override
    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException {
        super.setDisplayPanel(displayPanel);

        checkIfPolygonalIsMain();
        getDisplayPanel().clearSelection();
    }

    @Override
    public boolean acceptSpaceBar() {
        return state == 2;
    }

    @Override
    public boolean acceptAnyChar() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "nova_parcela";
    }

    private class AuxVisualObject extends AbstractVisualObjectCompound {

        public AuxVisualObject(VisualObject vo1, VisualObject vo2) {
            delegate.add(vo1);
            delegate.add(vo2);
        }

        @Override
        public float getLineWidth() {
            return SchemeColors.SELECTED_AND_BOLD;
        }

        @Override
        public Color getColor() {
            return SchemeColors.SELECTED;
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
