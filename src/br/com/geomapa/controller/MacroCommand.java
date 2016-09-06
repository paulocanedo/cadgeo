/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller;


/**
 *
 * @author paulocanedo
 */
public final class MacroCommand implements Command {

    private Command[] commands;

    public MacroCommand(Command[] commands) {
        this.commands = commands;
    }

    @Override
    public void execute() {
        for (int i = 0; i < commands.length; i++) {
            commands[i].execute();
        }
    }

    @Override
    public void undo() {
        for (int i = commands.length - 1; i >= 0; i--) {
            commands[i].undo();
        }
    }

    @Override
    public void store() {
        for (int i = 0; i < commands.length; i++) {
            commands[i].store();
        }
    }

    @Override
    public void load() {
        for (int i = 0; i < commands.length; i++) {
            commands[i].load();
        }
    }
}
