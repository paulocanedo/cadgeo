/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.controller.CommandController;
import br.com.geomapa.controller.RemoveRowsCommand;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.coordinate.SystemCoordinateTransformer;

import br.com.geomapa.geodesic.point.MetaDataPoint.MeasurementMethod;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.ui.model.GeoPointTableModel;
import br.com.geomapa.ui.theme.PointDescriptorButtonUI;
import br.com.geomapa.util.mlist.MacroListAdapter;
import br.com.paulocanedo.pc9.PSimpleBorder;
import br.com.paulocanedo.pc9.util.AddRemoveButtonUI;
import br.com.paulocanedo.pc9.util.LafUtils;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author paulocanedo
 */
public class PointManagerPanel extends JPanel implements GeodesicPanel {

    private CardLayout cardLayout = new CardLayout();
    private JTable table;
    private PointManagerFormPanel formPanel = new PointManagerFormPanel();
    private GeoPointTableModel tableModel = DataManagement.getGeoPointTableModel();
    private JPanel secondLine;
    private AbstractButton filterButton;
    private AbstractButton mergeButton;
    private JButton backwardButton;
    private JButton forwardButton;
    private AbstractButton editarButton = new JToggleButton(new FormModeAction());
    private JComboBox multipleEditPointCB = new JComboBox(MeasurementMethod.values());

    public PointManagerPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        Border border = new EmptyBorder(10, 10, 10, 10);
        Dimension dimension = new Dimension(24, 24);

        final JButton addButton = new JButton("add");
        addButton.addActionListener(new AddAction());
        addButton.addActionListener(new FormModeAction());
        addButton.setUI(AddRemoveButtonUI.getAddInstanceUI());
        addButton.putClientProperty("JButton.segmentPosition", "middle");

        JButton removeButton = new JButton("remove");
        removeButton.addActionListener(new RemoveAction());
        removeButton.setUI(AddRemoveButtonUI.getRemoveInstanceUI());
        removeButton.putClientProperty("JButton.segmentPosition", "middle");

        final JPanel firstLine = new JPanel();
        secondLine = new JPanel(cardLayout);

        firstLine.setLayout(new BoxLayout(firstLine, BoxLayout.LINE_AXIS));
        firstLine.setBorder(new PSimpleBorder(new Insets(10, 10, 10, 10), Color.BLACK, PSimpleBorder.PaintInPosition.BOTTOM));
        firstLine.add(Box.createRigidArea(new Dimension(5, 5)));

        ButtonGroup group = new ButtonGroup();
        editarButton = new JToggleButton(new FormModeAction());
        AbstractButton gridButton = new JToggleButton(new GridModeAction());
        gridButton.setSelected(true);
        group.add(editarButton);
        group.add(gridButton);

        BackwardAction backwardAction = new BackwardAction();
        backwardButton = new JButton(backwardAction);
        backwardButton.putClientProperty("JButton.segmentPosition", "first");
        LafUtils.applyMaxRoundCorner(backwardButton, 5);
        backwardButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "backward");
        backwardButton.getActionMap().put("backward", backwardAction);
        backwardButton.setPreferredSize(dimension);

        ForwardAction forwardAction = new ForwardAction();
        forwardButton = new JButton(forwardAction);
        forwardButton.putClientProperty("JButton.segmentPosition", "middle");
        LafUtils.applyMaxRoundCorner(forwardButton, 5);
        forwardButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "forward");
        forwardButton.getActionMap().put("forward", forwardAction);
        forwardButton.setPreferredSize(dimension);

        filterButton = new JToggleButton(new FilterAction());
        filterButton.setPreferredSize(dimension);
        LafUtils.applyMaxRoundCorner(filterButton, 5);
        filterButton.putClientProperty("JButton.segmentPosition", "middle");
        filterButton.setIcon(new ImageIcon(getClass().getResource("/br/com/geomapa/resources/icons/filter.png")));

        mergeButton = new JButton(new MergeAction());
        mergeButton.setPreferredSize(dimension);
        LafUtils.applyMaxRoundCorner(mergeButton, 5);
        mergeButton.putClientProperty("JButton.segmentPosition", "middle");
        mergeButton.setIcon(new ImageIcon(getClass().getResource("/br/com/geomapa/resources/icons/merge.png")));

        multipleEditPointCB.setSelectedItem(null);
        multipleEditPointCB.setMaximumSize(new Dimension(400, 21));
        multipleEditPointCB.setPreferredSize(new Dimension(400, 21));
        multipleEditPointCB.setEnabled(false);
        multipleEditPointCB.addItemListener(new MultipleEditionListener());

        final JComboBox estiloCoordComboBox = new JComboBox(new String[]{"Coordenadas UTM", "Coordenadas Geográficas"});
        multipleEditPointCB.setMaximumSize(new Dimension(300, 21));
        multipleEditPointCB.setPreferredSize(new Dimension(300, 21));
        estiloCoordComboBox.addItemListener(new EstiloItemListener());

        firstLine.add(backwardButton);
        firstLine.add(forwardButton);
        firstLine.add(addButton);
        firstLine.add(removeButton);
        firstLine.add(filterButton);
        firstLine.add(mergeButton);
        firstLine.add(Box.createHorizontalGlue());
        firstLine.add(multipleEditPointCB);
        firstLine.add(estiloCoordComboBox);
        firstLine.add(Box.createHorizontalGlue());

        firstLine.add(gridButton);
        firstLine.add(editarButton);

        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener(new PointsSelectionListener());

        TableRowSorter<GeoPointTableModel> tableRowSorter = new TableRowSorter<GeoPointTableModel>(tableModel) {

            @Override
            public Comparator<?> getComparator(int i) {
                if (i == 8) {
                    return new Comparator<Integer>() {

                        @Override
                        public int compare(Integer t, Integer t1) {
                            return t.compareTo(t1);
                        }
                    };
                }
                return new Comparator<String>() {

                    @Override
                    public int compare(String t, String t1) {
                        return t.compareTo(t1);
                    }
                };
            }
        };
        table.setRowSorter(tableRowSorter);
        secondLine.add(new JScrollPane(table), "grid");
        secondLine.add(formPanel, "form");
        secondLine.setBorder(border);

        add(firstLine);
        add(secondLine);

        DataManagement.getAllPoints().addMacroListListener(new MacroListAdapter() {

            @Override
            public void sizeChanged(int newSize) {
                if (newSize == 0) {
                    backwardButton.setEnabled(false);
                    forwardButton.setEnabled(false);
                    editarButton.setEnabled(false);
                    multipleEditPointCB.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void filter(final String text) {
        TableRowSorter<? extends TableModel> rowSorter = (TableRowSorter<? extends TableModel>) table.getRowSorter();
        String regexFilter = String.format("(.*)(?i)%s(.*)", text);
        rowSorter.setRowFilter(RowFilter.regexFilter(regexFilter, 0));
    }

    public void filter(Integer... oids) {
        TableRowSorter<? extends TableModel> rowSorter = (TableRowSorter<? extends TableModel>) table.getRowSorter();
        List<RowFilter<Object, Object>> listRowsFilter = new ArrayList<RowFilter<Object, Object>>();

        for (Integer id : oids) {
            RowFilter<Object, Object> numberFilter = RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, id, table.getColumnCount() - 1);
            listRowsFilter.add(numberFilter);
        }

        rowSorter.setRowFilter(RowFilter.orFilter(listRowsFilter));
    }

    @Override
    public String action(String text) {
        return null;
    }

    @Override
    public void refresh() {
        tableModel.fireTableDataChanged();

        refreshValues();
    }

    @Override
    public void setPolygonal(Polygonal polygonal) {
    }

    @Override
    public Polygonal getPolygonal() {
        return null;
    }

    @Override
    public void export(Polygonal polygonal) throws IOException {
    }

    private class AddAction extends AbstractAction {

        public AddAction() {
            putValue(SHORT_DESCRIPTION, "Criar um novo ponto");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            table.getSelectionModel().clearSelection();
            formPanel.novoPonto();
            editarButton.setSelected(true);
        }
    }

    private class RemoveAction extends AbstractAction {

        public RemoveAction() {
            putValue(SHORT_DESCRIPTION, "Remover pontos selecionados");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int[] selectedRows = table.getSelectedRows();
            System.out.println(selectedRows.length);
            List<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < selectedRows.length; i++) {
                list.add((Integer) (table.getValueAt(selectedRows[i], table.getColumnCount() - 1)));
            }
            CommandController.setCommand(new RemoveRowsCommand(tableModel, list.toArray(new Integer[0])));
            CommandController.executeCommand();

            if (selectedRows.length > 1) {
                table.clearSelection();
            }
        }
    }

    private class FormModeAction extends AbstractAction {

        public FormModeAction() {
            super("Modo Edição");
            putValue(SHORT_DESCRIPTION, "Permite editar o ponto selecionado");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            multipleEditPointCB.setVisible(false);
            refreshValues();
            cardLayout.show(secondLine, "form");
        }

        @Override
        public boolean isEnabled() {
            if (table == null) {
                return false;
            }
            int selectedRow = table.getSelectedRow();
            int rowCount = table.getRowCount();

            return rowCount > 0 && selectedRow >= 0;
        }
    }

    private class GridModeAction extends AbstractAction {

        public GridModeAction() {
            super("Modo Grade");
            putValue(SHORT_DESCRIPTION, "Exibe modo visualização de todos os pontos em grade");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            multipleEditPointCB.setVisible(true);
            refreshValues();
            cardLayout.show(secondLine, "grid");
        }
    }

    private class BackwardAction extends AbstractAction {

        public BackwardAction() {
            super(" < ");
            putValue(SHORT_DESCRIPTION, "Seleciona o ponto anterior");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                selectedRow = 0;
            }
            int rowCount = table.getRowCount();

            if (selectedRow < rowCount && rowCount > 0) {
                --selectedRow;
                table.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
            }
        }

        @Override
        public boolean isEnabled() {
            if (table == null) {
                return false;

            }
            int selectedRow = table.getSelectedRow();
            int rowCount = table.getRowCount();

            return selectedRow > 0 && rowCount > 0;
        }
    }

    private class ForwardAction extends AbstractAction {

        public ForwardAction() {
            super(" > ");
            putValue(SHORT_DESCRIPTION, "Seleciona o próximo ponto");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            int rowCount = table.getRowCount();

            if (selectedRow < rowCount - 1 && rowCount > 0) {
                ++selectedRow;
                table.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
            }
        }

        @Override
        public boolean isEnabled() {
            if (table == null) {
                return false;
            }
            int selectedRow = table.getSelectedRow();
            int rowCount = table.getRowCount();

            return selectedRow < rowCount - 1 && rowCount > 0;
        }
    }

    private class FilterAction extends AbstractAction {

        public FilterAction() {
            putValue(SHORT_DESCRIPTION, "Filtrar apenas Pontos repetidos");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            AbstractButton src = (AbstractButton) evt.getSource();
            if (src.isSelected()) {

                List<Integer> list = DataManagement.findDuplicatedPointsOIDS();
                filter(list.toArray(new Integer[0]));
            } else {
                filter("");
            }
        }
    }

    private class MergeAction extends AbstractAction {

        public MergeAction() {
            putValue(SHORT_DESCRIPTION, "Mesclar dois pontos");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length != 2) {
                JOptionPane.showMessageDialog(Main.getInstance(), "Selecione dois pontos para fazer a mesclagem");
                return;
            }
            ButtonGroup bgroup = new ButtonGroup();
            Integer id1 = (Integer) table.getValueAt(selectedRows[0], table.getColumnCount() - 1);
            Integer id2 = (Integer) table.getValueAt(selectedRows[1], table.getColumnCount() - 1);
            GeodesicPoint gpoint1 = DataManagement.findPoint(id1);
            GeodesicPoint gpoint2 = DataManagement.findPoint(id2);

            JPanel panel = new JPanel(new GridLayout());
            panel.setPreferredSize(new Dimension(400, 150));
            AbstractButton button1 = new JToggleButton();
            button1.setUI(new PointDescriptorButtonUI());
            AbstractButton button2 = new JToggleButton();
            button2.setUI(new PointDescriptorButtonUI());
            button1.putClientProperty("JButton.GeodesicPoint", gpoint1);
            button2.putClientProperty("JButton.GeodesicPoint", gpoint2);
            bgroup.add(button1);
            bgroup.add(button2);
            panel.add(button1);
            panel.add(button2);

            int confirm = JOptionPane.showConfirmDialog(Main.getInstance(), panel, "Escolha o ponto a manter as configurações", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (confirm == JOptionPane.OK_OPTION && (button1.isSelected() || button2.isSelected())) {
                GeodesicPoint selected = (button1.isSelected() ? gpoint1 : gpoint2);
                GeodesicPoint noSelected = (button2.isSelected() ? gpoint1 : gpoint2);

                for (LineDivision ld : LineDivision.findFrom(noSelected)) {
                    ld.setStartPoint(selected);
                }

                for (LineDivision ld : LineDivision.findTo(noSelected)) {
                    ld.setEndpoint(selected);
                }

                tableModel.removeRows(noSelected.getOid());
                filter("");
                filterButton.setSelected(false);
            }
        }
    }

    private class EstiloItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                JComboBox source = (JComboBox) e.getSource();
                int selectedIndex = source.getSelectedIndex();
                if (selectedIndex == 0) {
                    tableModel.setCoordTransformer(SystemCoordinateTransformer.UTM_TRANSFORMER);
                } else if (selectedIndex == 1) {

                    tableModel.setCoordTransformer(SystemCoordinateTransformer.GEOGRAPHIC_TRANSFORMER);
                }
                tableModel.fireTableDataChanged();
            }
        }
    }

    private class MultipleEditionListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                JComboBox source = (JComboBox) e.getSource();
                MeasurementMethod selectedItem = (MeasurementMethod) source.getSelectedItem();

                int[] selectedRows = table.getSelectedRows();
                for (int i = 0; i < selectedRows.length; i++) {
                    String name = String.valueOf(table.getValueAt(selectedRows[i], 0));
                    GeodesicPoint valueAt = DataManagement.findPoint(name);
                    valueAt.getMetaData().setMeasurementMethod(selectedItem);
                }
                table.repaint();
            }
        }
    }

    private class PointsSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            refreshValues();
        }
    }

    private void refreshValues() {
        if (table.getRowCount() == 0) {
            return;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            selectedRow = 0;
        }
        Integer id = (Integer) (table.getValueAt(selectedRow, table.getColumnCount() - 1));
        GeodesicPoint valueAt = DataManagement.findPoint(id);
        formPanel.setCurrentGeoPoint(valueAt);
        formPanel.setFields(valueAt);

        backwardButton.setEnabled(backwardButton.getAction().isEnabled());
        forwardButton.setEnabled(forwardButton.getAction().isEnabled());
        editarButton.setEnabled(editarButton.getAction().isEnabled());
        multipleEditPointCB.setEnabled(editarButton.getAction().isEnabled());

        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 1) {
            multipleEditPointCB.setSelectedItem(valueAt.getMetaData().getMeasurementMethod());
        } else if (selectedRows.length > 1) {
            MeasurementMethod measurementMethod = valueAt.getMetaData().getMeasurementMethod();
            for (int i = 0; i < selectedRows.length; i++) {
                selectedRow = selectedRows[i];
                id = (Integer) (table.getValueAt(selectedRow, table.getColumnCount() - 1));
                valueAt = DataManagement.findPoint(id);

                if (valueAt.getMetaData().getMeasurementMethod() != measurementMethod) {
                    multipleEditPointCB.setSelectedItem(null);
                    return;
                }
            }
            multipleEditPointCB.setSelectedItem(measurementMethod);
        } else {
            multipleEditPointCB.setSelectedItem(null);
        }
    }
}
