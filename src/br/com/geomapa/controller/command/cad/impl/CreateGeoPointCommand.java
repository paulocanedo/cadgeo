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
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.geodesic.datum.Ellipsoid;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.util.AngleValue;
import java.awt.geom.Point2D;

/**
 *
 * @author paulocanedo
 */
public class CreateGeoPointCommand extends AbstractCadCommand {

    private int state = -1;
    private Point2D from;
    private String firstName;
    private char aChar = ' ';
    private boolean multiple = false;
    private AngleValue projectionAzimuth;
    private double projectionDistance;
    private static final String[] messages = new String[]{"Criar ponto por: (C)clique na tela/coordenada, (P)projeção por azimute e distância?"};
    private GeodesicPoint gpoint;

    public CreateGeoPointCommand() {
        super(messages[0]);
    }

    public CreateGeoPointCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CoordinateException, CommandException {
        if (state >= 0 && text.toLowerCase().equals("m")) {
            multiple = true;
            return false;
        }

        if (state == -1) {
            this.aChar = getCharacter(text, "Você deve informar uma das opções: (C)clique na tela/coordenada, (P)projeção por azimute e distância", 'C', 'P');
            state++;
            super.message = "Informe o nome do ponto a ser criado";
        } else if (state == 0) {
            state++;
            if (text.length() < 2 || text.length() > 20) {
                super.message = "O nome do ponto deve ter entre 2 e 20 caracteres";
                return false;
            }

            firstName = text;
            if (aChar == 'C') {
                super.message = "Clique no ponto desejado ou informe a coordenada via linha de comando. (M) múltiplos pontos";
            } else if (aChar == 'P') {
                super.message = "Informe o local de referência para projeção do ponto";
            }
        } else if (state == 1) {
            state++;
            from = parsePoint(text);

            if (aChar == 'C') {
                execute();
            } else if (aChar == 'P') {
                super.message = "Informe o azimute para o próximo ponto";
            }
        } else if (state == 2) {
            projectionAzimuth = getAngleValue(text, "%s não é um valor de azimute válido");
            state++;
            super.message = "Informe a distância para o próximo ponto";
        } else if (state == 3) {
            projectionDistance = getPositiveDouble(text, "%s não é uma valor correto para distância");
            state++;
            execute();
        }
        return true;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        if (state == 1) {
            from = point;

            if (aChar == 'C') {
                state = 100;
                execute();
            } else if (aChar == 'P') {
                state++;
                super.message = "Informe o azimute para o próximo ponto";
            }
            return true;
        }
        return false;
    }

    @Override
    public void execute() {
        ProjectMetadata projectInfo = Main.getInstance().getProjectInfo();
        String nextName = ProjectMetadata.deriveNextPointName(firstName);
        UTMCoordinate coord = null;
        if (aChar == 'C') {
            coord = new UTMCoordinate(new Ellipsoid(projectInfo.getDatum()), projectInfo.getZonaUtm(), projectInfo.getHemisferio(), from.getX(), from.getY());
        } else if (aChar == 'P') {
            double[] toCreate = PolygonalUtils.projection(from.getX(), from.getY(), projectionDistance, projectionAzimuth.toDegreeDecimal());
            coord = new UTMCoordinate(new Ellipsoid(projectInfo.getDatum()), projectInfo.getZonaUtm(), projectInfo.getHemisferio(), toCreate[0], toCreate[1]);
        }

        GeodesicPoint gpoint = new GeodesicPoint(coord, firstName);
        DataManagement.getAllPoints().addElement(gpoint);

        if (!getPolygonal().isMain()) {
            getDisplayPanel().addToVisualObjects(gpoint);
        }

        finish();

        if (multiple) {
            CadCommand command = newInstance(getDisplayPanel());
            command.nextState("" + aChar);
            command.nextState(nextName);
            command.nextState("M");

            if (aChar == 'P') {
                command.nextState(coord.toPoint2D());
            }

            CadCommandController.setCommand(command);
        }
    }

    @Override
    public void undo() {
        getDisplayPanel().removeFromVisualObjects(gpoint);
    }

    @Override
    public boolean isUndoable() {
        return gpoint != null;
    }
    
    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Criar ponto";
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) {
        return new CreateGeoPointCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "novo_ponto";
    }
}
