/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.importer.rinex.RinexFile;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.ui.model.RinexListModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author paulocanedo
 */
public class RinexManagerPanel extends JPanel implements GeodesicPanel {

    private Polygonal polygonal = DataManagement.getMainPolygonal();
    private JList list;
    private RinexListModel listModel = new RinexListModel(null);
    private RinexFormPanel formPanel = new RinexFormPanel();

    public RinexManagerPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Border endLineBorder = BorderFactory.createEmptyBorder(5, 10, 10, 10);

        JPanel firstLine = new JPanel();

        //FIRST LINE
        firstLine.setLayout(new BoxLayout(firstLine, BoxLayout.X_AXIS));
        firstLine.setBorder(endLineBorder);

        list = new JList(listModel);

        JScrollPane scrollPanel = new JScrollPane(list);
        scrollPanel.setBackground(getBackground());
        scrollPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Gerenciar RINEX"));
        scrollPanel.setPreferredSize(new Dimension(300, 100));
        scrollPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        firstLine.add(scrollPanel);
        firstLine.add(formPanel);

        add(firstLine);

        list.setFont(Font.decode(Font.MONOSPACED));
        list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object selectedValue = list.getSelectedValue();
                if (selectedValue != null) {
                    RinexFile rinexFile = (RinexFile) selectedValue;
                    formPanel.setRinexFile(rinexFile);
                }
            }
        });

        list.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("F2"), "rename");
        list.getActionMap().put("rename", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object selectedValue = list.getSelectedValue();
                int selectedIndex = list.getSelectedIndex();
                if (selectedValue == null) {
                    return;
                }

                String input = JOptionPane.showInputDialog("Renomear ponto RINEX\n"
                        + "(Não utilize caracteres especiais)", selectedValue);
                if (input != null && !input.isEmpty()) {
                    if (!input.equalsIgnoreCase(selectedValue.toString())) {
                        RinexFile rinexFile = (RinexFile) selectedValue;

                        try {
                            boolean renameTo = rinexFile.renameTo(input);
                            if (renameTo == false) {
                                JOptionPane.showMessageDialog(Main.getInstance(), "Falha ao renomear");
                            } else {
                                refresh();
                                list.setSelectedIndex(selectedIndex);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(RinexManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(Main.getInstance(), ex.getMessage());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void refresh() {
        ProjectMetadata projectInfo = Main.getInstance().getProjectInfo();
        File rinexFolder = projectInfo.getRinexFolder();
        if (rinexFolder != null && rinexFolder.exists()) {
            listModel.setFolder(rinexFolder);
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
    }

    @Override
    public void export(Polygonal polygonal) throws IOException {
    }

}