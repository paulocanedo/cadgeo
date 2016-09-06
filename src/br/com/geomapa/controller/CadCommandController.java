/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller;

import br.com.geomapa.controller.command.cad.helper.CadCommandList;
import br.com.geomapa.controller.command.cad.impl.ZoomCommand;
import br.com.geomapa.controller.command.cad.impl.TableAzimuthDistanceCommand;
import br.com.geomapa.controller.command.cad.impl.LineDivisionCommand;
import br.com.geomapa.controller.command.cad.impl.LineCommand;
import br.com.geomapa.controller.command.cad.impl.PortionCreateCommand;
import br.com.geomapa.controller.command.cad.impl.AzimuthDistanceCommand;
import br.com.geomapa.controller.command.cad.impl.PortionLabelCommand;
import br.com.geomapa.controller.command.cad.impl.RectangleCommand;
import br.com.geomapa.controller.command.cad.impl.ResetPortionCommand;
import br.com.geomapa.controller.command.cad.impl.RedrawCommand;
import br.com.geomapa.controller.command.cad.impl.CreateGeoPointCommand;
import br.com.geomapa.controller.command.cad.impl.PaperCommand;
import br.com.geomapa.controller.command.cad.impl.UndoCommand;
import br.com.geomapa.controller.command.cad.impl.GeodesicPointEditCommand;
import br.com.geomapa.controller.command.cad.impl.MatchPropertiesCommand;
import br.com.geomapa.controller.command.cad.impl.GoToMainPolygonalCommand;
import br.com.geomapa.controller.command.cad.impl.GridCoordCommand;
import br.com.geomapa.controller.command.cad.impl.IdCommand;
import br.com.geomapa.controller.command.cad.impl.DefinePerimeterCommand;
import br.com.geomapa.controller.command.cad.impl.LabelCoordCommand;
import br.com.geomapa.controller.command.cad.impl.BorderingDefineCommand;
import br.com.geomapa.controller.command.cad.impl.LineArrowCommand;
import br.com.geomapa.controller.command.cad.impl.TextEditCommand;
import br.com.geomapa.controller.command.cad.impl.MoveCommand;
import br.com.geomapa.controller.command.cad.impl.TableAreaPerimeterCommand;
import br.com.geomapa.controller.command.cad.impl.BorderingSeparatorCommand;
import br.com.geomapa.controller.command.cad.impl.TextCommand;
import br.com.geomapa.controller.command.cad.impl.BorderingNameCommand;
import br.com.geomapa.controller.command.cad.impl.CopyCommand;
import br.com.geomapa.controller.command.cad.impl.ProjectLineCommand;
import br.com.geomapa.controller.command.cad.impl.RotateCommand;
import br.com.geomapa.controller.command.cad.impl.CircleCommand;
import br.com.geomapa.controller.command.cad.impl.PortionInfoCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author paulocanedo
 */
public class CadCommandController {

    private static CadCommand storedCommand;
    private static CadCommand lastCommand;
    private static CadCommand command;
    public static String ERROR_MESSAGE;

    private CadCommandController() {
    }

    public static void setCommand(CadCommand ccommand) {
        if (ccommand != null) {
            lastCommand = ccommand;
        }
        command = ccommand;
    }

    public static CadCommand getCommand() {
        return command;
    }

    public static void callLastCommand(GLTopographicPanel displayPanel) {
        if (lastCommand == null) {
            return;
        }

        try {
            command = lastCommand.newInstance(displayPanel);
        } catch (Throwable ex) {
            Logger.getLogger(CadCommandController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void storeCurrentCommand() {
        storedCommand = command;
    }

    public static boolean restoreOldCommand() {
        if (storedCommand == null) {
            return false;
        } else {
            command = storedCommand;
            storedCommand = null;
        }
        return true;
    }

    public static boolean nextState(String text) {
        if (command != null) {
            if (command.nextState(text) && command != null) {
                if (command.getMessageStatus().equals("")) {
                    command = null;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean nextState(Point2D point) {
        if (command != null) {
            command.prepareForOrtho(point);
            command.setLastPoint(point.getX(), point.getY());
            return command.nextState(point);
        }
        return false;
    }

    public static String getCurrentMessageStatus() {
        if (command == null) {
            return "";
        } else {
            return command.toString() + " - " + command.getMessageStatus();
        }
    }

    public static boolean isOrthoActivated() {
        return ORTHO;
    }
    public static boolean ORTHO = false;

    public static CadCommand find(String alias, GLTopographicPanel displayPanel) throws CommandException {
        if ("z".equalsIgnoreCase(alias)) {
            return new ZoomCommand(displayPanel);
        } else if ("id".equalsIgnoreCase(alias)) {
            return new IdCommand(displayPanel);
        } else if ("m".equalsIgnoreCase(alias)) {
            return new MoveCommand(displayPanel);
        } else if ("ro".equalsIgnoreCase(alias)) {
            return new RotateCommand(displayPanel);
        } else if ("u".equalsIgnoreCase(alias)) {
            return new UndoCommand(displayPanel);
        } else if ("c".equalsIgnoreCase(alias)) {
            return new CopyCommand(displayPanel);
        } else if ("edt".equalsIgnoreCase(alias)) {
            return new TextEditCommand(displayPanel);
        } else if ("edp".equalsIgnoreCase(alias)) {
            return new GeodesicPointEditCommand(displayPanel);
        } else if ("ed".equalsIgnoreCase(alias)) {
            return new PortionInfoCommand(displayPanel);
        }
        
        CadCommand commandByName = getCommandByName(alias, displayPanel);
        if (commandByName == null) {
            throw new CommandException("Comando não encontrado: " + alias);
        }
        return commandByName;
    }

    private static CadCommand getCommandByName(String name, GLTopographicPanel displayPanel) throws CommandException {
        for (CadCommand c : CadCommandList.list) {
            if (c.getCommandName().equalsIgnoreCase(name)) {
                return c.newInstance(displayPanel);
            }
        }
        return null;
    }

    public static CadCommand newInstance(Class<? extends CadCommand> command, GLTopographicPanel displayPanel) {
        try {
            CadCommand cadcommand = command.newInstance();
            cadcommand.setDisplayPanel(displayPanel);
            if (cadcommand.hasToBeExecuted()) {
                cadcommand.execute();
            }
            return cadcommand;
        } catch (Throwable ex) {
            Logger.getLogger(CadCommandController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String getText(Class<? extends CadCommand> clazz) {
        try {
            CadCommand cmd = clazz.newInstance();
            return cmd.toString();
        } catch (InstantiationException ex) {
            Logger.getLogger(CadCommandController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CadCommandController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "***";
    }

    public static Icon getIcon(Class<? extends CadCommand> clazz) {
        URL resource = CadCommandController.class.getResource("/br/com/geomapa/resources/icons/r16/" + clazz.getSimpleName() + ".png");
        if (resource == null) {
            return null;
        }

        return new ImageIcon(resource);
    }
}
