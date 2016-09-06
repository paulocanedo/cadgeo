/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CadCommandController;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.graphic.cad.geo.AzimuthDistance;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import br.com.geomapa.util.AngleValue;
import br.com.geomapa.util.ArraysUtil;
import br.com.geomapa.util.unit.impl.AzimuthUnit;
import br.com.geomapa.util.unit.impl.Meter;
import br.com.geomapa.util.unit.specs.AngleUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author paulocanedo
 */
public class AzimuthDistanceCommand extends AbstractCadCommand {

    private Line line = new Line(0, 0, 0, 0);
    private int state = -1;
    private GeodesicPoint from;
    private GeodesicPoint to;
    private char aChar = ' ';
    private static final String[] messages = new String[]{"Inserir azimute e distância em (T)todos possíveis, (I)individual, (C)consulta?"};

    public AzimuthDistanceCommand() {
        super(messages[0]);
    }

    public AzimuthDistanceCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws PolygonalException, CommandException {
        if (state == -1) {
            this.aChar = getCharacter(text, "Você deve informar uma das opções: (T)todos (I)individual (C)consulta", 'T', 'I', 'C');
            state++;

            if (aChar == 'T') {
                execute();
            } else {
                super.message = "Informe o ponto inicial de inserção";
            }
        } else if (aChar == 'I' || aChar == 'C') {
            GeodesicPoint findPoint = searchPoint(text);
            if (findPoint == null) {
                throw new PolygonalException(String.format("O ponto %s não foi encontrado.", text));
            }

            if (from == null) {
                setStartPoint(findPoint);
            } else {
                to = findPoint;
                execute();
            }
        }
        return true;
    }

    private void setStartPoint(GeodesicPoint gpoint) {
        this.from = gpoint;
        this.line.setLocation(gpoint.getLocation());
        super.message = "Informe o próximo ponto";
        super.canDraw = true;
    }

    private boolean exists(AzimuthDistance azimuthDistance) {
        Collection<AzimuthDistance> set = ArraysUtil.collect(getPolygonal().getVisualObjects(), AzimuthDistance.class);
        return (set.contains(azimuthDistance));
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (state >= 0 && (aChar == 'I' || aChar == 'C')) {
            for (GeodesicPoint gpoint : DataManagement.getAllPoints()) {
                Point2D location = gpoint.getLocation();

                if (PolygonalUtils.horizontalDistance(location.getX(), location.getY(), point.getX(), point.getY()) < 0.1) {
                    if (from == null) {
                        setStartPoint(gpoint);
                    } else {
                        to = gpoint;

                        if (aChar == 'C') {
                            AngleValue azimuth = from.azimuth(to);
                            double horizontalDistance = from.horizontalDistance(to);

                            setStartPoint(to);
                            message = String.format("%s - %.2f", aUnit.toString(azimuth, 0), horizontalDistance);
                            to = null;
                        } else {
                            execute();
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setCurrentPositionInternal(Point2D point) {
        if (from != null) {
            line.setEndLocation(point);
        }
    }
    private static final AngleUnit aUnit = new AzimuthUnit();
    private static final DistanceUnit dUnit = new Meter();
    private static final JTextField field = new JTextField(30);

    @Override
    public void execute() {
        if (aChar == 'C') {
            AngleValue azimuth = from.azimuth(to);
            double horizontalDistance = from.horizontalDistance(to);

            field.setText("Azimute: " + aUnit.toString(azimuth, 0) + " - Distância: " + dUnit.toString(horizontalDistance, 2));
            JOptionPane.showMessageDialog(Main.getInstance(), field);
            finish();

            AzimuthDistanceCommand ldCommand = new AzimuthDistanceCommand(getDisplayPanel());
            ldCommand.nextState("C");
            ldCommand.nextState(to.getNameNoSeparators());
            CadCommandController.setCommand(ldCommand);
        } else if (aChar == 'I') {
            AzimuthDistance azimuthDistance = new AzimuthDistance(from, to);
            add(azimuthDistance);
            finish();

            AzimuthDistanceCommand ldCommand = new AzimuthDistanceCommand(getDisplayPanel());
            ldCommand.nextState("I");
            ldCommand.nextState(to.getNameNoSeparators());
            CadCommandController.setCommand(ldCommand);
        } else if (aChar == 'T') {
            for (LineDivision ld : getPolygonal().getLineDivisions()) {
                AzimuthDistance azdt = new AzimuthDistance(ld.getStartPoint(), ld.getEndPoint());
                add(azdt);
            }

            if (getPolygonal().isMain()) {
                MainPolygonal mainPolygonal = (MainPolygonal) getPolygonal();
                Collection<LineDivision> collection = new ArrayList<LineDivision>();
                if (getPolygonal().isMain()) {
                    for (Iterator<Polygonal> it = mainPolygonal.childrenIterator(); it.hasNext();) {
                        Polygonal p = it.next();

                        for (LineDivision ld : p.getLineDivisions()) {
                            LineDivision inverseLd = LineDivision.getInstance(ld.getEndPoint(), ld.getStartPoint());
                            if (collection.contains(ld) || collection.contains(inverseLd)) {
                                continue;
                            }

                            AzimuthDistance azdt = new AzimuthDistance(ld.getStartPoint(), ld.getEndPoint());
                            add(azdt);
                            collection.add(ld);
                        }
                    }
                }
            }
            finish();

            String msg = inserted.isEmpty() ? "Nenhum azimute/distância foi inserido." : String.format("%d azimute(s)/distância(s) foram inseridos.", inserted.size());
            JOptionPane.showMessageDialog(Main.getInstance(), msg);
        }
    }

    private void add(AzimuthDistance azimuthDistance) {
        if (!exists(azimuthDistance)) {
            getDisplayPanel().addToVisualObjects(azimuthDistance);
            inserted.add(azimuthDistance);
        }
    }
    private Collection<AzimuthDistance> inserted = new ArrayList<AzimuthDistance>();

    @Override
    public void undo() {
        for (AzimuthDistance azdt : inserted) {
            getDisplayPanel().removeFromVisualObjects(azdt);
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
        return "Azimute e distância";
    }

    @Override
    public Line getVisualObject() {
        return line;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) {
        return new AzimuthDistanceCommand(displayPanel);
    }

    @Override
    public boolean canUseMagnetic() {
        return state >= 0;
    }

    @Override
    public String getCommandName() {
        return "azimute_distancia";
    }
}
