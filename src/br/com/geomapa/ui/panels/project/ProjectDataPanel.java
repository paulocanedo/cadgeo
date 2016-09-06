/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewProject.java
 *
 * Created on 30/06/2010, 16:15:30
 */
package br.com.geomapa.ui.panels.project;

import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.coordinate.Longitude;
import br.com.geomapa.geodesic.datum.Datum;
import br.com.geomapa.geodesic.datum.SAD69Datum;
import br.com.geomapa.geodesic.datum.SIRGASDatum;
import br.com.geomapa.geodesic.datum.WGS84Datum;
import br.com.geomapa.geodesic.rbmc.BaseRBMC;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.ui.model.RbmcListModel;
import br.com.geomapa.ui.panels.PortionDataPanel;
import br.com.geomapa.ui.panels.options.ResponsavelTecnico;
import br.com.geomapa.util.UnidadeFederativa;
import br.com.geomapa.util.mlist.MacroListAdapter;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author paulocanedo
 */
public class ProjectDataPanel extends javax.swing.JPanel {
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private ProjectMetadata projectMetadata;
    public static final String PROP_PROJECTMETADATA = "projectMetadata";
    private RbmcListModel model = new RbmcListModel(projectMetadata);
    
    {
        addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PROP_PROJECTMETADATA)) {
                    model.setProjectMetadata((ProjectMetadata) evt.getNewValue());
                }
            }
        });
        
        DataManagement.getAllPoints().addMacroListListener(new MacroListAdapter() {

            @Override
            public void sizeChanged(int newSize) {
                zonaUtmField.setEnabled(newSize == 0);
            }
        });
    }

    /** Creates new form NewProject */
    public ProjectDataPanel() {
        setProjectMetadata(new ProjectMetadata());
        
        initComponents();
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                responsavelTecnicoComboBox.setModel(new DefaultComboBoxModel(ResponsavelTecnico.allRTs().toArray()));
                responsavelTecnicoComboBox.setSelectedItem(projectMetadata.getResponsavelTecnico());
            }
        });
        responsavelTecnicoComboBox.setModel(new DefaultComboBoxModel(ResponsavelTecnico.allRTs().toArray()));
        responsavelTecnicoComboBox.setSelectedItem(projectMetadata.getResponsavelTecnico());
        
        zonaUtmField.getDocument().addDocumentListener(new ZonaMeridianoSyncListener());
        updateMeridianoLabel();

        rbmcListField.setModel(model);
    }

    public ProjectMetadata getProjectInfo() {
        projectMetadata.setNome(nomeTextField.getText());
        projectMetadata.setNomeImovel(nomeImovelTextField.getText());
        projectMetadata.setComarca(comarcaTextField.getText());
        projectMetadata.setCircunscricao(circunscricaoTextField.getText());
        projectMetadata.setArtNumero(artnumeroField.getText());
        projectMetadata.setMunicipio(municipioTextField.getText());
        projectMetadata.setUf((UnidadeFederativa) ufComboBox.getSelectedItem());
        projectMetadata.setDatum((Datum) datumComboBox.getSelectedItem());
        projectMetadata.setResponsavelTecnico((ResponsavelTecnico) responsavelTecnicoComboBox.getSelectedItem());

        String szona = zonaUtmField.getText();
        try {
            int zona = Integer.parseInt(szona);
            projectMetadata.setZonaUtm(zona);
        } catch (NumberFormatException ex) {
            projectMetadata.setZonaUtm(0);
        }

        projectMetadata.setHemisferio(northField.isSelected() ? Hemisphere.NORTH : Hemisphere.SOUTH);

        return projectMetadata;
    }

    private void clearFields() {
        nomeTextField.setText("");
        nomeImovelTextField.setText("");
        comarcaTextField.setText("");
        circunscricaoTextField.setText("");
        artnumeroField.setText("");
        municipioTextField.setText("");
        ufComboBox.setSelectedItem(UnidadeFederativa.TOCANTINS);
        datumComboBox.setSelectedItem("");
        zonaUtmField.setText("");

        southField.setSelected(true);
        northField.setSelected(false);

        responsavelTecnicoComboBox.setSelectedIndex(-1);
    }

    public void setAndLabelProjectMetadata(ProjectMetadata projectInfo) {
        if (projectInfo == null) {
            setProjectMetadata(new ProjectMetadata());
            clearFields();
            return;
        }
        this.projectMetadata = projectInfo;
        this.model.setProjectMetadata(projectInfo);

        nomeTextField.setText(projectInfo.getNome());
        nomeImovelTextField.setText(projectInfo.getNomeImovel());
        comarcaTextField.setText(projectInfo.getComarca());
        circunscricaoTextField.setText(projectInfo.getCircunscricao());
        artnumeroField.setText(projectInfo.getArtNumero());
        municipioTextField.setText(projectInfo.getMunicipio());
        ufComboBox.setSelectedItem(projectInfo.getUf());
        datumComboBox.setSelectedItem(projectInfo.getDatum());
        zonaUtmField.setText(String.valueOf(projectInfo.getZonaUtm()));

        Hemisphere hemisferio = projectInfo.getHemisferio();
        southField.setSelected(hemisferio == Hemisphere.SOUTH);
        northField.setSelected(hemisferio == Hemisphere.NORTH);

        responsavelTecnicoComboBox.setSelectedItem(projectInfo.getResponsavelTecnico());
    }

    private void updateMeridianoLabel() {
        String szona = zonaUtmField.getText();
        String label = "Meridiano Central: %s";

        try {
            int zona = Integer.parseInt(szona);
            if (zona < 1 || zona > 60) {
                zonaUtmField.setBackground(new Color(0xfaf0d9));
                return;
            }
            Longitude centralMeridian = Longitude.getCentralMeridian(zona);
            meridianoCentralLabel.setText(String.format(label, centralMeridian.toMeridianCentralString()));
            zonaUtmField.setBackground(Color.WHITE);
        } catch (Exception ex) {
            meridianoCentralLabel.setText(String.format(label, ""));
        }
    }

    private void addRbmc(String text) {
        try {
            projectMetadata.addToRBMC(text);
            model.fireElementsAdded();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(Main.getInstance(), String.format("O arquivo de informações da base RBMC não foi encontrado.\n%s", ex.getMessage()));
        } catch (IOException ex) {
            Logger.getLogger(PortionDataPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(Main.getInstance(), String.format("Ocorreu um erro de E/S na leitura do arquivo da base.\n%s.", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(Main.getInstance(), String.format("%s", ex.getMessage()));
        }

        rbmcAddField.setText("");
    }

    private void removeRbmc(BaseRBMC rbmc) {
        BaseRBMC selectedValue = (BaseRBMC) rbmcListField.getSelectedValue();
        int selectedIndex = rbmcListField.getSelectedIndex();

        if (selectedValue != null) {
            projectMetadata.getRbmc().remove(selectedValue);
            model.fireElementsRemoved(selectedIndex);
        }
    }

    private class ZonaMeridianoSyncListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateMeridianoLabel();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateMeridianoLabel();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateMeridianoLabel();
        }
    }

    public final void setProjectMetadata(ProjectMetadata projectMetadata) {
        ProjectMetadata oldProjectMetadata = this.projectMetadata;
        this.projectMetadata = projectMetadata;
        propertyChangeSupport.firePropertyChange(PROP_PROJECTMETADATA, oldProjectMetadata, projectMetadata);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        hemisferioGroupButton = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        nomeTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        nomeImovelTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        comarcaTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        circunscricaoTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        artnumeroField = new javax.swing.JTextField();
        municipioTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        ufComboBox = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        zonaUtmField = new javax.swing.JTextField();
        meridianoCentralLabel = new javax.swing.JLabel();
        southField = new javax.swing.JRadioButton();
        northField = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        responsavelTecnicoComboBox = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        datumComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        rbmcAddField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        rbmcListField = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Nome do projeto");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel1, gridBagConstraints);

        nomeTextField.setColumns(45);
        nomeTextField.setMinimumSize(new java.awt.Dimension(120, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(nomeTextField, gridBagConstraints);

        jLabel2.setText("Nome do imóvel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel2, gridBagConstraints);

        nomeImovelTextField.setColumns(45);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(nomeImovelTextField, gridBagConstraints);

        jLabel5.setText("Comarca");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel5, gridBagConstraints);

        comarcaTextField.setColumns(45);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(comarcaTextField, gridBagConstraints);

        jLabel6.setText("Circunscrição");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel6, gridBagConstraints);

        circunscricaoTextField.setColumns(45);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(circunscricaoTextField, gridBagConstraints);

        jLabel7.setText("Município");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(artnumeroField, gridBagConstraints);

        municipioTextField.setColumns(45);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(municipioTextField, gridBagConstraints);

        jLabel8.setText("Unidade Federativa");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel8, gridBagConstraints);

        ufComboBox.setModel(new javax.swing.DefaultComboBoxModel(UnidadeFederativa.values()));
        ufComboBox.setPreferredSize(new java.awt.Dimension(500, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(ufComboBox, gridBagConstraints);

        jLabel13.setText("Zona UTM principal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel13, gridBagConstraints);

        zonaUtmField.setColumns(10);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(zonaUtmField, gridBagConstraints);

        meridianoCentralLabel.setText("Meridiano Central: 00o Xgr");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(meridianoCentralLabel, gridBagConstraints);

        hemisferioGroupButton.add(southField);
        southField.setSelected(true);
        southField.setText("Hemisfério Sul");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(southField, gridBagConstraints);

        hemisferioGroupButton.add(northField);
        northField.setText("Hemisfério Norte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        add(northField, gridBagConstraints);

        jLabel12.setText("Responsável Técnico");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel12, gridBagConstraints);

        responsavelTecnicoComboBox.setPreferredSize(new java.awt.Dimension(500, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        add(responsavelTecnicoComboBox, gridBagConstraints);

        jLabel11.setText("Sistema Geodésico de Referência");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel11, gridBagConstraints);

        datumComboBox.setModel(new javax.swing.DefaultComboBoxModel(new Datum[] { new SIRGASDatum(), new SAD69Datum(), new WGS84Datum() }));
        datumComboBox.setToolTipText("Escolha um Datum para o trabalho");
        datumComboBox.setPreferredSize(new java.awt.Dimension(500, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        add(datumComboBox, gridBagConstraints);

        jLabel3.setText("Adicionar RBMC");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel3, gridBagConstraints);

        rbmcAddField.setColumns(35);
        rbmcAddField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbmcAddFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(rbmcAddField, gridBagConstraints);

        jScrollPane1.setViewportView(rbmcListField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);

        jButton1.setText("Adicionar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jButton1, gridBagConstraints);

        jButton2.setText("Remover RBMC");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jButton2, gridBagConstraints);

        jLabel4.setText("ART número");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel4, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        addRbmc(rbmcAddField.getText());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void rbmcAddFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbmcAddFieldActionPerformed
        addRbmc(rbmcAddField.getText());
    }//GEN-LAST:event_rbmcAddFieldActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        BaseRBMC selectedValue = (BaseRBMC) rbmcListField.getSelectedValue();
        if (selectedValue != null) {
            removeRbmc(selectedValue);
        }
    }//GEN-LAST:event_jButton2ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField artnumeroField;
    private javax.swing.JTextField circunscricaoTextField;
    private javax.swing.JTextField comarcaTextField;
    private javax.swing.JComboBox datumComboBox;
    private javax.swing.ButtonGroup hemisferioGroupButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel meridianoCentralLabel;
    private javax.swing.JTextField municipioTextField;
    private javax.swing.JTextField nomeImovelTextField;
    private javax.swing.JTextField nomeTextField;
    private javax.swing.JRadioButton northField;
    private javax.swing.JTextField rbmcAddField;
    private javax.swing.JList rbmcListField;
    private javax.swing.JComboBox responsavelTecnicoComboBox;
    private javax.swing.JRadioButton southField;
    private javax.swing.JComboBox ufComboBox;
    private javax.swing.JTextField zonaUtmField;
    // End of variables declaration//GEN-END:variables
    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(listener);
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
