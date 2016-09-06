/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels.topographic;

import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.CadCommandController;
import br.com.geomapa.controller.actions.CopyToClipboardAction;
import br.com.geomapa.controller.actions.PasteFromClipboardAction;
import br.com.geomapa.controller.command.cad.impl.EraseCommand;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.graphic.RenderContext;
import br.com.geomapa.main.Bus;
import br.com.geomapa.main.GLConfigure;
import br.com.geomapa.controller.ToolBarController;
import br.com.geomapa.ui.panels.GeodesicPanel;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.plaf.metal.MetalTextFieldUI;
import javax.swing.text.JTextComponent;

/**
 *
 * @author paulocanedo
 */
public class TopographicPanel extends JPanel implements GeodesicPanel {

    private final GLTopographicPanel delegate;
    private final JTextField promptField;

    public TopographicPanel() {
        delegate = new GLTopographicPanel(GLConfigure.caps);
        promptField = new JTextField();

        init();
    }

    public TopographicPanel(Polygonal currentPolygonal) {
        delegate = new GLTopographicPanel(GLConfigure.caps);
        promptField = new JTextField();

        init();
        delegate.setPolygonal(currentPolygonal);
    }

    private void init() {
        setLayout(new BorderLayout());

        Bus.put("gl_panel", delegate);
        delegate.getComponent().setFocusable(false);
        delegate.getComponent().addMouseListener(new AMouseListener());

        add(delegate.getComponent(), BorderLayout.CENTER);
        add(promptField, BorderLayout.SOUTH);

        promptField.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        promptField.setUI(new MetalTextFieldUI());
        promptField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        promptField.getActionMap().put("escape", new EscapeAction());
        AbstractAction enterAction = new EnterAction();
        promptField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
        promptField.getActionMap().put("enter", enterAction);
        promptField.addKeyListener(new PromptKeyListener());

        CopyToClipboardAction copyAction = new CopyToClipboardAction();
        promptField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) copyAction.getValue(Action.ACCELERATOR_KEY), "copy_special");
        promptField.getActionMap().put("copy_special", copyAction);

        PasteFromClipboardAction pasteAction = new PasteFromClipboardAction();
        promptField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) pasteAction.getValue(Action.ACCELERATOR_KEY), "paste_special");
        promptField.getActionMap().put("paste_special", pasteAction);

        ToggleOrthoAction toggleOrthoAction = new ToggleOrthoAction();
        promptField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F8"), "ortho");
        promptField.getActionMap().put("ortho", toggleOrthoAction);

        addComponentListener(new FocusComponentAdapter());
    }

    @Override
    public void grabFocus() {
        super.grabFocus();
        promptField.grabFocus();
    }

    @Override
    public void filter(String text) {
        delegate.filter(text);
    }

    @Override
    public String action(String text) {
        return delegate.action(text);
    }

    @Override
    public void refresh() {
        delegate.refresh();
    }

    @Override
    public void setPolygonal(Polygonal polygonal) {
        delegate.setPolygonal(polygonal);
    }

    @Override
    public Polygonal getPolygonal() {
        return delegate.getPolygonal();
    }

    @Override
    public void export(Polygonal polygonal) throws IOException {
        delegate.export(polygonal);
    }

    private class AMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            promptField.grabFocus();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            promptField.grabFocus();
        }
    }

    private class EscapeAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            delegate.clearSelection();
            CadCommand command = CadCommandController.getCommand();
            if (command != null) {
                command.canceled();
            }

            if (!CadCommandController.restoreOldCommand()) {
                CadCommandController.setCommand(null);
                delegate.setTempVO(null);
                delegate.setMagneticVO(null);
            }
            CadCommandController.ERROR_MESSAGE = null;
            RenderContext.getInstance(true).offset(0, 0);
            RenderContext.getInstance(true).angleRotation(0);
            promptField.setText("");
            delegate.requestRepaint();
            
            ToolBarController.hidePopups();
        }
    }

    private class EnterAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent textComponent = (JTextComponent) e.getSource();
            String input = textComponent.getText().trim();

            actionCommand(input);
        }
    }

    private class ToggleOrthoAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            CadCommandController.ORTHO = !CadCommandController.ORTHO;
        }
    }

    private void actionCommand(String input) {
        CadCommand command = CadCommandController.getCommand();
        if (command == null) {
            try {
                if (!input.isEmpty()) {
                    command = CadCommandController.find(input, delegate);

                    if (command.hasToBeExecuted()) {
                        command.execute();
                    } else {
                        CadCommandController.setCommand(command);
                    }
                } else {
                    CadCommandController.callLastCommand(delegate);
                }
            } catch (CommandException ex) {
                CadCommandController.ERROR_MESSAGE = ex.getMessage();
            }
        } else {
            CadCommandController.nextState(input);
        }

        promptField.setText("");
        delegate.requestRepaint();
    }

    private class FocusComponentAdapter extends ComponentAdapter {

        @Override
        public void componentShown(ComponentEvent e) {
            promptField.grabFocus();
        }
    }

    private class PromptKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE
                    || (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && e.isMetaDown())) {

                if (!(delegate.getSelectedObjects().length == 0)) {
                    CadCommandController.setCommand(new EraseCommand(delegate));
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            CadCommand command = CadCommandController.getCommand();
            if ((command == null || !command.acceptSpaceBar()) && e.getKeyChar() == ' ') {
                e.consume();

                String input = promptField.getText().trim();
                actionCommand(input);
            }

            if (command != null && !command.acceptAnyChar()) {
                Toolkit.getDefaultToolkit().beep();
                e.consume();
            }
        }
    }
}
