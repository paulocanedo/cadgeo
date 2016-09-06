/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller;

import java.util.Stack;

/**
 *
 * @author paulocanedo
 */
public final class CommandController {

    private static final Stack<Command> undoCommands = new Stack<Command>();
    private static final Stack<Command> redoCommands = new Stack<Command>();
    
    private static Command currentCommand;

    private CommandController() {
    }

    public static void setCommand(Command command) {
        currentCommand = command;
    }

    public static void executeCommand() {
        Command push = undoCommands.push(currentCommand);
        push.execute();

        redoCommands.clear();
        currentCommand = null;
    }

    public static void undoLastCommand() {
        if (!undoCommands.isEmpty()) {
            Command command = undoCommands.pop();
            redoCommands.push(command).undo();

            currentCommand = null;
        }
    }

    public static void redoLastCommand() {
        if (!redoCommands.isEmpty()) {
            redoCommands.pop().execute();
            currentCommand = null;
        }
    }
}
