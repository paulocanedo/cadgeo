/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller;

import br.com.geomapa.controller.MagneticDialog.MagneticDialogOpenAction;
import br.com.geomapa.controller.actions.MenuAction;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import br.com.geomapa.controller.command.cad.impl.CircleCommand;
import br.com.geomapa.controller.command.cad.impl.PortionCreateCommand;
import br.com.geomapa.controller.command.cad.impl.EraseCommand;
import br.com.geomapa.controller.command.cad.impl.GoToMainPolygonalCommand;
import br.com.geomapa.controller.command.cad.impl.LineArrowCommand;
import br.com.geomapa.controller.command.cad.impl.LineCommand;
import br.com.geomapa.controller.command.cad.impl.LineDivisionCommand;
import br.com.geomapa.controller.command.cad.impl.MatchPropertiesCommand;
import br.com.geomapa.controller.command.cad.impl.MoveCommand;
import br.com.geomapa.controller.command.cad.impl.RectangleCommand;
import br.com.geomapa.controller.command.cad.impl.RedrawCommand;
import br.com.geomapa.controller.command.cad.impl.RotateCommand;
import br.com.geomapa.controller.command.cad.impl.TextCommand;
import br.com.geomapa.controller.command.cad.impl.TextEditCommand;
import br.com.geomapa.controller.command.cad.impl.ZoomCommand;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.LineTypeByLayer;
import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.Bus;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.paulocanedo.pc9.PButtonBarUI;
import br.com.paulocanedo.pc9.laf.PRoundBorder;
import br.com.paulocanedo.pc9.util.LafUtils;
import br.com.pc9.pswing.components.PToolBar;
import br.com.pc9.pswing.components.SearchComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 *
 * @author paulocanedo
 */
public class ToolBarController {

    private static int heightComponents = 40;
    private static PToolBar toolBar = new PToolBar();
    private static JPanel drawToolbarPanel = new JPanel(new FlowLayout());
    private static JComboBox choiceLayers;
    private static JComboBox choiceLineType;
    private static AbstractButton colorButton = new JButton();
    private static LineTypeByLayer lineTypeByLayer = new LineTypeByLayer(LayerController.DEFAULT_LAYER);

    public static void initDefaults() {
        JComponent menuDropDown = createDropDownMenu(new MenuAction());
        menuDropDown.setPreferredSize(new Dimension(120, 40));
        toolBar.addComponentToLeft(menuDropDown);

        toolBar.addComponentToCenter(drawToolbarPanel);
        ComboBoxModel model = LayerController.getModel();
        choiceLayers = new JComboBox(model);
        choiceLayers.setRenderer(new LayerCellRenderer());
        choiceLayers.setFocusable(false);
        choiceLayers.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                JComboBox src = (JComboBox) evt.getSource();
                Layer layer = (Layer) src.getSelectedItem();
                VisualObject[] selectedObjects = Bus.getDisplayPanel().getSelectedObjects();
                if (selectedObjects.length == 0) {
                    LayerController.setCurrentLayer(layer);
                } else {
                    if (layer == null) {
                        return;
                    }
                    for (VisualObject vo : selectedObjects) {
                        vo.setLayer(layer);
                    }
                }
            }
        });

        choiceLineType = new JComboBox(new LineType[]{
                    lineTypeByLayer,
                    AbstractVisualObject.CONTINUOUS_LINE_TYPE,
                    AbstractVisualObject.DASHED_LINE_TYPE});

        choiceLineType.setRenderer(new LineTypeCellRenderer());
        choiceLineType.setFocusable(false);

        choiceLineType.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                JComboBox src = (JComboBox) evt.getSource();
                LineType ltype = (LineType) src.getSelectedItem();
                VisualObject[] selectedObjects = Bus.getDisplayPanel().getSelectedObjects();
                if (selectedObjects.length == 0) {
                    LayerController.setCurrentLineType(ltype);
                } else {
                    if (ltype == null) {
                        return;
                    }
                    for (VisualObject vo : selectedObjects) {
                        vo.setLineType(ltype);
                    }
                }
            }
        });
        choiceLineType.setSelectedItem(lineTypeByLayer);

        colorButton.setUI(new PButtonBarUI());
        LafUtils.applyMaxRoundCorner(colorButton, 2);
        colorButton.setOpaque(true);
        colorButton.setBorder(new PRoundBorder(new Insets(2, 2, 2, 2), Color.BLACK));

        drawToolbarPanel.add(colorButton);
        drawToolbarPanel.add(choiceLayers);
        drawToolbarPanel.add(choiceLineType);
        drawToolbarPanel.add(createCadButton(GoToMainPolygonalCommand.class, ""));
        drawToolbarPanel.add(createCadButton(MoveCommand.class, "first"));
        drawToolbarPanel.add(createCadButton(RotateCommand.class, "middle"));
        drawToolbarPanel.add(createCadButton(MatchPropertiesCommand.class, "middle"));
        drawToolbarPanel.add(createCadButton(LineDivisionCommand.class, "middle"));
        drawToolbarPanel.add(createCadButton(LineCommand.class, "middle"));
        drawToolbarPanel.add(createCadButton(LineArrowCommand.class, "middle"));
        drawToolbarPanel.add(createCadButton(RectangleCommand.class, "middle"));
        drawToolbarPanel.add(createCadButton(CircleCommand.class, "middle"));
        drawToolbarPanel.add(createCadButton(TextCommand.class, "middle"));
        drawToolbarPanel.add(createCadButton(TextEditCommand.class, "middle"));
        drawToolbarPanel.add(createCadButton(EraseCommand.class, "middle"));
        drawToolbarPanel.add(createCadButton(RedrawCommand.class, "last"));
        drawToolbarPanel.add(createButton(new MagneticDialogOpenAction()));
        drawToolbarPanel.add(createCadButton(ZoomCommand.class, "first"));
        drawToolbarPanel.add(createButton(new ZoomExtendsAction()));

        final SearchComponent searchComponent = new SearchComponent();
        searchComponent.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GLTopographicPanel displayPanel = Bus.getDisplayPanel();
                displayPanel.action(searchComponent.getText());
            }
        });
        searchComponent.addSearchTextChangedListener(new SearchComponent.SearchTextChangedListener() {

            @Override
            public void textChanged(String newText) {
            }
        });
        searchComponent.getComponent().setPreferredSize(new Dimension(250, heightComponents));
        toolBar.addComponentToRight(searchComponent.getComponent());
    }

    public static void hidePopups() {
        choiceLayers.hidePopup();
        choiceLineType.hidePopup();
    }

    public static void selectionChanged(VisualObject[] selectedObjects) {
        Layer layer = null;
        Color color = null;
        LineType ltype = null;
        if (selectedObjects.length == 0) {
            LayerController.restoreCurrentLayer();
            colorButton.setBackground(LayerController.getCurrentColor());
            choiceLineType.setSelectedItem(LayerController.getCurrentLineType() == null ? lineTypeByLayer : LayerController.getCurrentLineType());
            return;
        } else {
            boolean layerEquals = true;
            boolean colorEquals = true;
            boolean ltypeEquals = true;
            Layer layerTemp = null;
            Color colorTemp = null;
            LineType ltypeTemp = null;
            for (VisualObject vo : selectedObjects) {
                if (layerTemp != null) {
                    if (layerTemp != vo.getLayer()) {
                        layerEquals = false;
                    }
                }
                layerTemp = vo.getLayer();

                //----------------
                if (colorTemp != null) {
                    if (colorTemp != vo.getColor()) {
                        colorEquals = false;
                    }
                }
                colorTemp = vo.isColorLayer() ? null : vo.getColor();
                //----------------
                if (ltypeTemp != null) {
                    if (ltypeTemp != vo.getLineType()) {
                        ltypeEquals = false;
                    }
                }
                ltypeTemp = vo.isLineTypeLayer() ? lineTypeByLayer : vo.getLineType();
            }
            if (layerEquals) {
                if (layer != null) {
                    lineTypeByLayer.setLayer(layer);
                }
                layer = layerTemp;
            }
            if (colorEquals) {
                color = colorTemp;
            }
            if (ltypeEquals) {
                ltype = ltypeTemp;
            }
        }

        colorButton.setBackground(color);
        choiceLayers.setSelectedItem(layer);
        choiceLineType.setSelectedItem(ltype);
    }

    private static JButton createButton(Action action) {
        JButton button = new JButton(action);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setText("");
        button.setUI(new PButtonBarUI());
        return button;
    }

    public static void setVisibleDrawToolbarPanel(boolean flag) {
        drawToolbarPanel.setVisible(flag);
    }

    public static JButton createCadButton(final Class<? extends CadCommand> command, String position) {
        JButton button = new JButton();
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder(3, 0, 2, 0));
//        button.setContentAreaFilled(false);
        Icon icon = CadCommandController.getIcon(command);
        String text = CadCommandController.getText(command);
        if (icon == null) {
            button.setText(text);
        }
        button.setToolTipText(text);
        button.setIcon(icon);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    CadCommand cadcommand = CadCommandController.newInstance(command, Bus.getDisplayPanel());
                    CadCommandController.setCommand(cadcommand.wasFinished() ? null : cadcommand);
                } catch (Throwable ex) {
                    Logger.getLogger(ToolBarController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        button.setUI(new PButtonBarUI());
        return button;
    }

    public static Action createCadAction(final Class<? extends CadCommand> command) {
        AbstractAction action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CadCommand cadcommand = CadCommandController.newInstance(command, Bus.getDisplayPanel());
                if (cadcommand instanceof PortionCreateCommand) {
                    cadcommand.nextState("M");
                }
                CadCommandController.setCommand(cadcommand.wasFinished() ? null : cadcommand);
            }
        };

        action.putValue(Action.NAME, CadCommandController.getText(command));
        action.putValue(Action.SHORT_DESCRIPTION, CadCommandController.getText(command));

        return action;
    }

    public static ToolBarController getInstance() {
        if (instance == null) {
            instance = new ToolBarController();
            initDefaults();
        }
        return instance;
    }

    public JComponent getToolBarComponent() {
        return toolBar.getComponent();
    }

    private static JComponent createDropDownMenu(Action action) {
        final JButton menuButton = new JButton(action);
//        menuButton.setIcon(new ArrowIcon(8, 8));
        menuButton.setFocusable(false);
//        menuButton.setHorizontalTextPosition(SwingConstants.LEFT);
//        menuButton.setIconTextGap(20);
//        menuButton.putClientProperty("maxRoundCorner", 7);

//        Border border1 = BorderFactory.createEmptyBorder();
//        Border border2 = BorderFactory.createLineBorder(Color.BLACK);
//        menuButton.setBorder(BorderFactory.createCompoundBorder(border1, border2));
        return menuButton;
    }
    private static ToolBarController instance;

    private static class ZoomExtendsAction extends AbstractAction {

        public ZoomExtendsAction() {
            super("Zoom Estendido");

            URL resource = getClass().getResource("/br/com/geomapa/resources/icons/r16/ZoomExtendsCommand.png");
            putValue(SHORT_DESCRIPTION, "Aplicar zoom que exiba todos os objetos dentro da tela");
            putValue(SMALL_ICON, new ImageIcon(resource));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new ZoomCommand(Bus.getDisplayPanel()).nextState("E");
            new RedrawCommand(Bus.getDisplayPanel()).execute();
        }
    }
}

class LineTypeCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel component = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value == null) {
            return component;
        }

        LineType ltype = (LineType) value;
        component.setIcon(ltype.getIcon());

        return component;
    }
}

class LayerCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel component = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value == null) {
            return component;
        }

        Layer layer = (Layer) value;
        component.setIcon(getIcon(layer.getColor()));

        return component;
    }

    private Icon getIcon(Color color) {
        int size = 12;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, size, size);
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 0, size - 1, size - 1);

        return new ImageIcon(image);
    }
}