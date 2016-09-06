/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import br.com.geomapa.util.mlist.MacroList;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author paulocanedo
 */
public class ProjectLineCommand extends AbstractCadCommand {

    private List<Line> lineDivisions = new ArrayList<Line>();
    private Line line = new Line(0, 0, 0, 0);
    private GeodesicPoint from;
    private double offsetDistance;
    private String initialName;
    private List<Line> linesInserted = new ArrayList<Line>();
    private List<GeodesicPoint> pointsInserted = new ArrayList<GeodesicPoint>();
    private boolean rightPosition = true;
    private int state = -1;
    private static final String[] messages = new String[]{"Informe a distância da linha paralela ao caminho traçado"};

    public ProjectLineCommand() {
        super(messages[0]);
    }

    public ProjectLineCommand(GLTopographicPanel displayPanel) throws CommandException {
        super(displayPanel, messages[0]);

        checkIfPolygonalIsMain();
    }

    @Override
    public boolean transitToNextState(String text) throws PolygonalException, CommandException {
        if (state == -1) {
            this.offsetDistance = getPositiveDouble(text, String.format("Você deve informar um valor maior que zero para a distância da linha paralela: %s", text));
            super.message = "Informe o nome do ponto inicial (deixe em branco para não criar pontos)";
            state++;
            return true;
        } else if (state == 0) {
            if (DataManagement.findPoint(text) != null) {
                super.message = "Já existe um ponto com este nome, favor informe outro";
                return false;
            }

            this.initialName = text;
            super.message = "A linha será projetada a (D)direita ou (E)esquerda do caminho original?";
            state++;
            return true;
        } else if (state == 1) {
            Character character = getCharacter(text, "Você deve informar uma das opções: (D)direita (E)esquerda", 'D', 'E');
            this.rightPosition = character == 'D';
            super.message = "Informe o primeiro ponto conhecido do caminho da linha";
            state++;
            return true;
        }

        if (text.isEmpty()) {
            if (from == null) {
                return false;
            }
            execute();
            return true;
        }

        GeodesicPoint findPoint = searchPoint(text);
        if (findPoint == null) {
            throw new PolygonalException(String.format("O ponto %s não foi encontrado.", text));
        }

        if (from == null) {
            setStartPoint(findPoint);
        } else {
            setNextPoint(findPoint);
        }
        return true;
    }

    private void setStartPoint(GeodesicPoint gpoint) {
        this.from = gpoint;
        this.line.setLocation(gpoint.getLocation());
        super.message = "Informe o caminho da linha";
        super.canDraw = true;
    }

    private void setNextPoint(GeodesicPoint gpoint) {
        lineDivisions.add(LineDivision.getInstance(from, gpoint));
        from = gpoint;
        super.message = "Informe o próximo ponto do caminho da linha(<ENTER> para finalizar)";
        line.setLocation(from.getLocation());
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (state == -1 || state == 0 || state == 1) {
            return false;
        }

        for (GeodesicPoint gpoint : DataManagement.getAllPoints()) {
            Point2D location = gpoint.getLocation();

            if (PolygonalUtils.horizontalDistance(location.getX(), location.getY(), point.getX(), point.getY()) < 0.1) {
                if (from == null) {
                    setStartPoint(gpoint);
                } else {
                    setNextPoint(gpoint);
                }
                return true;
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

    @Override
    public void execute() {
        if (!lineDivisions.isEmpty()) {
            try {
                Layer customLayer = chooseLayer();

                List<Line> offset = Line.offset(lineDivisions, offsetDistance, rightPosition);
                if (initialName != null && !initialName.isEmpty()) {
                    ProjectMetadata projectInfo = Main.getInstance().getProjectInfo();
                    Integer zonaUtm = projectInfo.getZonaUtm();
                    Hemisphere hemisferio = projectInfo.getHemisferio();

                    String lastName = null;
                    for (Line l : offset) {
                        String name1 = (lastName == null ? initialName : lastName);
                        String name2 = lastName = (projectInfo.deriveNextPointName(name1));

                        GeodesicPoint p1 = new GeodesicPoint(new UTMCoordinate(zonaUtm, hemisferio, l.getX(), l.getY()), name1);
                        GeodesicPoint p2 = new GeodesicPoint(new UTMCoordinate(zonaUtm, hemisferio, l.getEndX(), l.getEndY()), name2);

                        if (name1.equals(initialName)) {
                            pointsInserted.add(p1);
                        }
                        pointsInserted.add(p2);

                        if (name1.equals(initialName)) {
                            DataManagement.getGeoPointTableModel().add(p1);
                        }
                        DataManagement.getGeoPointTableModel().add(p2);

                        LineDivision lineDivision = LineDivision.getInstance(p1, p2);
                        LineDivision lineInverted = LineDivision.getInstance(p2, p1);

                        lineDivision.setLayer(customLayer == null ? LayerController.getCurrentLayer() : customLayer);
                        lineInverted.setLayer(customLayer == null ? LayerController.getCurrentLayer() : customLayer);

                        linesInserted.add(lineDivision);
                    }

                    if (customLayer != null) {
                        for (Line ld : lineDivisions) {
                            LineDivision lineDivison = (LineDivision) ld;
                            LineDivision lineInverted = LineDivision.getInstance(lineDivison.getEndPoint(), lineDivison.getStartPoint());

                            lineDivison.setLayer(customLayer);
                            lineInverted.setLayer(customLayer);
                        }
                    }
                } else {
                    linesInserted = offset;
                }

                for (Line l : linesInserted) {
                    l.setLayer(customLayer == null ? LayerController.getCurrentLayer() : customLayer);
                    getDisplayPanel().addToVisualObjects(l);
                }
            } catch (Exception ex) {
                undo();
                ex.printStackTrace();
                JOptionPane.showMessageDialog(Main.getInstance(), ex.getMessage() + "\nA execução do comando será cancelada");
            }
        }

        redraw();
        finish();
    }

    @Override
    public void undo() {
        MacroList<GeodesicPoint> allPoints = DataManagement.getAllPoints();
        for (GeodesicPoint gpoint : pointsInserted) {
            allPoints.removeElement(gpoint);
        }

        for (Line l : linesInserted) {
            getDisplayPanel().removeFromVisualObjects(l);
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
        return "Projetar linha";
    }

    @Override
    public Line getVisualObject() {
        return line;
    }

    @Override
    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException {
        super.setDisplayPanel(displayPanel);

        checkIfPolygonalIsMain();
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new ProjectLineCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "projetar_linha";
    }
}
