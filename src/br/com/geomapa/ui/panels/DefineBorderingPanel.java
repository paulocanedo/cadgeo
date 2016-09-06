/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.geodesic.point.LineDivisionType;
import br.com.geomapa.geodesic.point.RoadType;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.model.PolygonalListModel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author paulocanedo
 */
public class DefineBorderingPanel extends JPanel implements GeodesicPanel {

    private Polygonal polygonal = DataManagement.getMainPolygonal();
    private JList list;
    private PolygonalListModel listModel = new PolygonalListModel(polygonal);
    private JTextField borderingTextField;
    private JButton nextButton;
    private JComboBox borderingTypeComboBox;
    private JPanel roadTypePanel;
    private JPanel waterCourseDirectionPanel;
    private JRadioButton estradaPavimentadaRB;
    private JRadioButton estradaNaoPavimentadaRB;
    private JRadioButton faixaDeDominioRB;
    private JTextField idFaixaDominioField;
    private JCheckBox waterCourseDirectionCB;

    public DefineBorderingPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        Border firstLineBorder = BorderFactory.createEmptyBorder(10, 10, 1, 10);
        Border endLineBorder = BorderFactory.createEmptyBorder(5, 10, 10, 10);
        Border border = BorderFactory.createEmptyBorder(1, 10, 2, 10);

        JPanel firstLine = new JPanel();
        JPanel secondLine = new JPanel();
        JPanel thirdLine = new JPanel();
        roadTypePanel = new JPanel();
        waterCourseDirectionPanel = new JPanel();
        JPanel sixthLine = new JPanel();
        JPanel secondColumn = new JPanel();

        //FIRST LINE
        firstLine.setLayout(new BoxLayout(firstLine, BoxLayout.LINE_AXIS));
        firstLine.setBorder(firstLineBorder);
        JLabel label = new JLabel("Definir limites e confrontantes: ");
        firstLine.add(label);
        label = new JLabel(polygonal.toString());
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        firstLine.add(label);
        firstLine.add(Box.createHorizontalGlue());

        //SECOND LINE
        secondLine.setLayout(new BoxLayout(secondLine, BoxLayout.LINE_AXIS));
        secondLine.setBorder(border);
        JLabel labelNomeConfrontante = new JLabel("Nome do confrontante: ");
        secondLine.add(labelNomeConfrontante);
        borderingTextField = new JTextField();
        borderingTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        secondLine.add(borderingTextField);

        //THIRD LINE
        thirdLine.setLayout(new BoxLayout(thirdLine, BoxLayout.LINE_AXIS));
        thirdLine.setBorder(border);
        JLabel labelTipo = new JLabel("Natureza: ");
        thirdLine.add(labelTipo);
        int space = labelNomeConfrontante.getPreferredSize().width - labelTipo.getPreferredSize().width;
        thirdLine.add(Box.createRigidArea(new Dimension(space, 10)));

        borderingTypeComboBox = new JComboBox();
        borderingTypeComboBox.setModel(new DefaultComboBoxModel(LineDivisionType.values()));
        borderingTypeComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        borderingTypeComboBox.addItemListener(new BorderTypeChangedListener());
        thirdLine.add(borderingTypeComboBox);

        thirdLine.add(Box.createRigidArea(new Dimension(10, 10)));
        thirdLine.add(nextButton = new JButton("Próximo"));

        //FOURTH LINE
        ButtonGroup bgroup2 = new ButtonGroup();
        roadTypePanel.setLayout(new BoxLayout(roadTypePanel, BoxLayout.LINE_AXIS));
        roadTypePanel.setBorder(border);
        JLabel labelRoadType = new JLabel("Tipo de estrada: ");
        roadTypePanel.add(labelRoadType);
        space = labelNomeConfrontante.getPreferredSize().width - labelRoadType.getPreferredSize().width;
        roadTypePanel.add(Box.createRigidArea(new Dimension(space, 10)));
        estradaPavimentadaRB = new JRadioButton("Pavimentada");
        estradaNaoPavimentadaRB = new JRadioButton("Não pavimentada");
        faixaDeDominioRB = new JRadioButton("Faixa de domínio");
        idFaixaDominioField = new JTextField();
        idFaixaDominioField.setEnabled(false);
        idFaixaDominioField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        idFaixaDominioField.addKeyListener(new IdRoadKeyAdapter());
        bgroup2.add(estradaPavimentadaRB);
        bgroup2.add(estradaNaoPavimentadaRB);
        bgroup2.add(faixaDeDominioRB);
        roadTypePanel.add(estradaPavimentadaRB);
        roadTypePanel.add(estradaNaoPavimentadaRB);
        roadTypePanel.add(faixaDeDominioRB);
        roadTypePanel.add(idFaixaDominioField);
        roadTypePanel.add(Box.createHorizontalGlue());

        estradaPavimentadaRB.addItemListener(new RoadTypeListener());
        estradaNaoPavimentadaRB.addItemListener(new RoadTypeListener());
        faixaDeDominioRB.addItemListener(new RoadTypeListener());

        faixaDeDominioRB.addItemListener(new FaixaDominioEnabler());

        //FIFTH LINE
        waterCourseDirectionPanel.setLayout(new BoxLayout(waterCourseDirectionPanel, BoxLayout.LINE_AXIS));
        waterCourseDirectionPanel.setBorder(border);
        waterCourseDirectionCB = new JCheckBox("O sentido do curso d'água é em sentido horário?", true);
        waterCourseDirectionPanel.add(Box.createRigidArea(new Dimension(labelNomeConfrontante.getPreferredSize().width, 10)));
        waterCourseDirectionPanel.add(waterCourseDirectionCB);
        waterCourseDirectionPanel.add(Box.createHorizontalGlue());
        waterCourseDirectionCB.addItemListener(new WaterCourseListener());

        //SECOND COLUMN
        secondColumn.setLayout(new BoxLayout(secondColumn, BoxLayout.X_AXIS));
        secondColumn.setBorder(endLineBorder);

        list = new JList(listModel);
        JScrollPane scrollPanel = new JScrollPane(list);
        scrollPanel.setPreferredSize(new Dimension(300, 100));
        scrollPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        secondColumn.add(scrollPanel);


        JPanel firstColumn = new JPanel();
        firstColumn.setLayout(new BoxLayout(firstColumn, BoxLayout.Y_AXIS));
        firstColumn.add(firstLine);
        firstColumn.add(secondLine);
        firstColumn.add(thirdLine);
        firstColumn.add(roadTypePanel);
        firstColumn.add(waterCourseDirectionPanel);
        firstColumn.add(sixthLine);
        firstColumn.add(Box.createVerticalGlue());

        enableContainer(roadTypePanel, false);
        enableContainer(waterCourseDirectionPanel, false);

        add(firstColumn);
        add(secondColumn);

        ActionListener listener = new NextActionListener();
        borderingTextField.addKeyListener(new BorderNameChangedListener());
        borderingTextField.addActionListener(listener);
        borderingTextField.addKeyListener(new KeyAdapterControl());
        nextButton.addActionListener(listener);

        list.addListSelectionListener(new ListSelectionControlShow());
    }

    private void enableContainer(Container container, boolean flag) {
        for (Component c : container.getComponents()) {
            c.setEnabled(flag);
        }
        container.setEnabled(flag);
        idFaixaDominioField.setEnabled(false);
    }

    private void saveBorderingType() {
        Object[] selectedValues = list.getSelectedValues();
        for (Object ld : selectedValues) {
            if (ld == null) {
                continue;
            }

            LineDivision selectedValue = (LineDivision) ld;
            LineDivisionType type = (LineDivisionType) borderingTypeComboBox.getSelectedItem();
            selectedValue.setType(type);

            if (type == LineDivisionType.LN1) {
                Layer layer = LayerController.find("CURSO_DAGUA");
                LineDivision inverseLine = LineDivision.getInstance(selectedValue.getEndPoint(), selectedValue.getStartPoint());
                
                selectedValue.setLayer(layer);
                inverseLine.setLayer(layer);
            } else if (type == LineDivisionType.LA4) {
                Layer layer = LayerController.find("Estradas");
                LineDivision inverseLine = LineDivision.getInstance(selectedValue.getEndPoint(), selectedValue.getStartPoint());
                
                selectedValue.setLayer(layer);
                inverseLine.setLayer(layer);
            }
        }
    }

    private void saveRoadType(JRadioButton source) {
        Object[] selectedValues = list.getSelectedValues();
        for (Object ld : selectedValues) {
            if (ld == null) {
                continue;
            }

            LineDivision selectedValue = (LineDivision) ld;
            if (estradaPavimentadaRB == source) {
                selectedValue.setRoadType(RoadType.PAVIMENTADA);
            } else if (estradaNaoPavimentadaRB == source) {
                selectedValue.setRoadType(RoadType.NAO_PAVIMENTADA);
            } else if (faixaDeDominioRB == source) {
                selectedValue.setRoadType(RoadType.FAIXA_DE_DOMINIO);
            }
        }
    }

    private void saveRoadId(String text) {
        Object[] selectedValues = list.getSelectedValues();
        for (Object ld : selectedValues) {
            if (ld == null) {
                continue;
            }

            LineDivision selectedValue = (LineDivision) ld;
            selectedValue.setIdFaixaDeDominio(text);
        }
    }

    private void saveWaterCourseDirection(boolean clockwise) {
        Object[] selectedValues = list.getSelectedValues();
        for (Object ld : selectedValues) {
            if (ld == null) {
                continue;
            }

            LineDivision selectedValue = (LineDivision) ld;
            selectedValue.setWaterCourseClockwiseDirection(clockwise);
        }
    }

    private void saveBorderingName() {
        Object[] selectedValues = list.getSelectedValues();
        for (Object ld : selectedValues) {
            if (ld == null) {
                continue;
            }

            LineDivision selectedValue = (LineDivision) ld;
            selectedValue.setBorderName(borderingTextField.getText());
        }
    }

    private void clearValues() {
        borderingTextField.setText("");
        borderingTypeComboBox.setSelectedItem(LineDivisionType.LA1);
    }

    @Override
    public void refresh() {
        list.clearSelection();
        clearValues();
        listModel.fireDataChanged();

        if (list.getSelectedValue() == null && list.getModel().getSize() > 0) {
            list.setSelectedIndex(0);
        }
    }

    @Override
    public void filter(String text) {
    }

    @Override
    public String action(String text) {
        return null;
    }

    @Override
    public Polygonal getPolygonal() {
        return polygonal;
    }

    @Override
    public void setPolygonal(Polygonal polygonal) {
        this.polygonal = polygonal;
        listModel.setPolygonal(polygonal);
    }

    @Override
    public void export(Polygonal polygonal) throws IOException {
    }

//    private class BorderingListCellRenderer extends DefaultListCellRenderer {
//
//        private LineDivision linedivision;
//
//        public BorderingListCellRenderer() {
//        }
//
//        public LineDivision getLineDivision() {
//            return linedivision;
//        }
//
//        public void setPointBorder(LineDivision pointBorder) {
//            this.linedivision = pointBorder;
//        }
//
//        @Override
//        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//            JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//            if (linedivision != null && linedivision.getStartPoint() != null && linedivision.getEndPoint() != null) {
//                boolean isMarco = linedivision.getStartPoint().getType() == GeodesicPointType.M && linedivision.getEndPoint().getType() == GeodesicPointType.M;
//                LineDivisionType borderType = (LineDivisionType) value;
//
//                if (isMarco && borderType == LineDivisionType.LA1) {
//                    renderer.setBackground(Color.GREEN);
//                }
//            }
//            return renderer;
//        }
//    }
    private class NextActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = list.getSelectedIndex();
            if (selectedIndex < list.getModel().getSize()) {
                list.setSelectedIndex(selectedIndex + 1);
            }
        }
    }

    private class KeyAdapterControl extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int borderingTypeSelectedIndex = borderingTypeComboBox.getSelectedIndex();
            int listSelectedIndex = list.getSelectedIndex();
            if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_DOWN) {
                if (borderingTypeSelectedIndex < borderingTypeComboBox.getModel().getSize() - 1) {
                    borderingTypeComboBox.setSelectedIndex(borderingTypeSelectedIndex + 1);
                }
            } else if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_UP) {
                if (borderingTypeSelectedIndex > 0) {
                    borderingTypeComboBox.setSelectedIndex(borderingTypeSelectedIndex - 1);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                if (listSelectedIndex < list.getModel().getSize() - 1) {
                    list.setSelectedIndex(listSelectedIndex + 1);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                if (listSelectedIndex > 0) {
                    list.setSelectedIndex(listSelectedIndex - 1);
                }
            }
        }
    }

    private class ListSelectionControlShow implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            Object[] selectedValues = list.getSelectedValues();
            LineDivision ldSelected = (LineDivision) list.getSelectedValue();

            if (ldSelected == null) {
                return;
            }

            boolean flagBordername = false, flagBorderType = false;
            for (Object selectValue : selectedValues) {
                LineDivision ld = (LineDivision) selectValue;

                if (ldSelected.getBorderName() == null || !(ldSelected.getBorderName().equals(ld.getBorderName()))) {
                    flagBordername = true;
                }

                if (ldSelected.getType() != ld.getType()) {
                    flagBorderType = true;
                }
            }

            if (!flagBordername) {
                String borderName = ldSelected.getBorderName();
                if (borderName == null || borderName.equals("null")) {
                    borderingTextField.setText("");
                } else {
                    borderingTextField.setText(borderName);
                }
            } else {
                borderingTextField.setText("");
            }

            if (!flagBorderType) {
                borderingTypeComboBox.setSelectedItem(ldSelected.getType());
            } else {
                borderingTypeComboBox.setSelectedItem(null);
            }
            RoadType roadType = ldSelected.getRoadType();
            estradaPavimentadaRB.setSelected(roadType != null && roadType == RoadType.PAVIMENTADA);
            estradaNaoPavimentadaRB.setSelected(roadType != null && roadType == RoadType.NAO_PAVIMENTADA);
            faixaDeDominioRB.setSelected(roadType != null && roadType == RoadType.FAIXA_DE_DOMINIO);

            String idFaixaDominio = ldSelected.getRoadType() == RoadType.FAIXA_DE_DOMINIO ? ldSelected.getNomeEstrada() : "";
            idFaixaDominioField.setText(idFaixaDominio == null ? "" : idFaixaDominio);

            Boolean flag = ldSelected.isWaterCourseClockwiseDirection();
            waterCourseDirectionCB.setSelected(flag != null && flag == true);
        }
    }

    private class BorderTypeChangedListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            JComboBox source = (JComboBox) e.getSource();
            LineDivisionType selectedItem = (LineDivisionType) source.getSelectedItem();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                saveBorderingType();

                enableContainer(roadTypePanel, selectedItem == LineDivisionType.LA4);
                enableContainer(waterCourseDirectionPanel, selectedItem == LineDivisionType.LN1);
            }
            if (selectedItem == null) {
                enableContainer(roadTypePanel, false);
                enableContainer(waterCourseDirectionPanel, false);
            }
        }
    }

    private class BorderNameChangedListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            saveBorderingName();
        }
    }

    private class WaterCourseListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            saveWaterCourseDirection(e.getStateChange() == ItemEvent.SELECTED);
        }
    }

    private class RoadTypeListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                JRadioButton source = (JRadioButton) e.getSource();
                saveRoadType(source);
            }
        }
    }

    private class IdRoadKeyAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            JTextField field = (JTextField) e.getSource();
            saveRoadId(field.getText());
        }
    }

    private class FaixaDominioEnabler implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            idFaixaDominioField.setEnabled(roadTypePanel.isEnabled() && e.getStateChange() == ItemEvent.SELECTED);
        }
    }
}
