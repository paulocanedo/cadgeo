/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.impl;

import br.com.geomapa.controller.command.cad.spec.AbstractCadCommand;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Main;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import java.awt.geom.Point2D;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author paulocanedo
 */
public class IdCommand extends AbstractCadCommand {

    private JTextField textField = new JTextField(30);
    private Point2D point;
    private static final String[] messages = new String[]{"Selecione o local onde deseja consultar as coordenadas"};

    public IdCommand() {
        super(messages[0]);
    }

    public IdCommand(GLTopographicPanel displayPanel) {
        super(displayPanel, messages[0]);
    }

    @Override
    public boolean transitToNextState(String text) throws CommandException {
        return false;
    }

    @Override
    public boolean transitToNextState(Point2D point) {
        this.point = point;
        execute();
        return true;
    }

    @Override
    public void execute() {
        if (point != null) {
            textField.setText(String.format(Locale.ENGLISH, "E: %.3f; N: %.3f", point.getX(), point.getY()));
            JOptionPane.showMessageDialog(Main.getInstance(), textField, "Coordenadas do ponto", JOptionPane.PLAIN_MESSAGE);
        }
        finish();
    }

    @Override
    public void undo() {
    }

    @Override
    public void store() {
    }

    @Override
    public void load() {
    }

    @Override
    public String toString() {
        return "Consultar coordenada";
    }

    @Override
    public VisualObject getVisualObject() {
        return null;
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        return new IdCommand(displayPanel);
    }

    @Override
    public String getCommandName() {
        return "consultar_coordenada";
    }
}
