/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * QuoteDataPanel.java
 *
 * Created on 26/06/2011, 21:04:35
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.PolygonalMetadata;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.graphic.RenderContext;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.main.Bus;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import br.com.geomapa.util.receitafederal.Cnpj;
import br.com.geomapa.util.receitafederal.Cpf;
import br.com.geomapa.util.receitafederal.PessoaRFException;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 *
 * @author paulocanedo
 */
public class PortionDataPanel extends javax.swing.JPanel implements GeodesicPanel {

    private Polygonal polygonal = DataManagement.getMainPolygonal();

    /** Creates new form QuoteDataPanel */
    public PortionDataPanel() {
        initComponents();

        satRef1Field.setModel(new SatListModel());
        satRef2Field.setModel(new SatListModel());

        SyncListener syncListener = new SyncListener();
        polygonalField.addKeyListener(syncListener);
        descricaoField.addKeyListener(syncListener);
        codigoSncrField.addKeyListener(syncListener);
        cpfCnpjField.addKeyListener(syncListener);
        proprietarioField.addKeyListener(syncListener);
        numeroMatriculaField.addKeyListener(syncListener);
        zonaUtmField.addKeyListener(syncListener);
        escalaDesenhoField.addKeyListener(syncListener);
        escalaDesenhoField.addItemListener(new EscalaChangeListener());
        satRef1Field.addItemListener(new SatChangeListener());
        satRef2Field.addItemListener(new SatChangeListener());

        cpfCnpjField.getDocument().addDocumentListener(new CpfCnpjValidatorListener(cpfCnpjField));
        cpfCnpjField.addFocusListener(new CpfCnpjFormatterListener());

        polygonalField.addFocusListener(new FocusAdapter() {

            private String text;

            @Override
            public void focusGained(FocusEvent e) {
                text = polygonalField.getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                String newText = polygonalField.getText();

                if (!newText.equals(text) && !polygonal.isMain()) {
                    for (LineDivision ld : polygonal.getLineDivisions()) {
                        LineDivision otherLd = LineDivision.getInstance(ld.getEndPoint(), ld.getStartPoint());
                        otherLd.setBorderName(newText);
                    }
                }
            }
        });
    }

    @Override
    public void filter(String text) {
    }

    @Override
    public String action(String text) {
        return null;
    }

    @Override
    public void refresh() {
        polygonalField.setText(polygonal.getName());
        PolygonalMetadata metadata = polygonal.getMetadata();

        descricaoField.setText(metadata.getDescricao());
        codigoSncrField.setText(metadata.getCodigoSncr());
        cpfCnpjField.setText(metadata.getCpfCnpj());
        proprietarioField.setText(metadata.getNomeProprietario());
        numeroMatriculaField.setText(metadata.getNumeroMatricula());
        satRef1Field.setSelectedItem(metadata.getSat1());
        satRef2Field.setSelectedItem(metadata.getSat2());
        zonaUtmField.setText("" + (metadata.getZonaUtm() == 0 ? Bus.getCurrentProjectMetadata().getZonaUtm() : metadata.getZonaUtm()));
        escalaDesenhoField.setSelectedItem("" + metadata.getEscala());

        try {
            directionLabel.setText(String.format("A parcela está em sentido %s", polygonal.isClockwise() ? "horário" : "anti-horário"));
        } catch (PolygonalException ex) {
            directionLabel.setText("Não é possível determinar o sentido da parcela no momento");
        }
        invertPortionButton.setEnabled(getPolygonal().isClosed());

        showInfo(polygonal);
    }

    private void showInfo(Polygonal polygonal) {
        if (!polygonal.isClosed()) {
            infoEditorPane.setText("<B>A parcela deve ter o perímetro definido para que o sistema sugira a escala.</B>");
            return;
        }

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = 0, maxY = 0;
        for (LineDivision ld : polygonal.getLineDivisions()) {
            GeodesicPoint startPoint = ld.getStartPoint();
            GeodesicPoint endPoint = ld.getEndPoint();

            minX = Math.min(minX, startPoint.getX());
            minX = Math.min(minX, endPoint.getX());

            minY = Math.min(minY, startPoint.getY());
            minY = Math.min(minY, endPoint.getY());

            maxX = Math.max(maxX, startPoint.getX());
            maxX = Math.max(maxX, endPoint.getX());

            maxY = Math.max(maxX, startPoint.getY());
            maxY = Math.max(maxX, endPoint.getY());
        }
        double width = maxX - minX;
        double height = maxY - minY;

        StringBuilder sb = new StringBuilder();
        sb.append("<B>Sugestão de escala para formato A1: </B>").append(String.format("1 / %.3f", Math.max(width / 640, height / 580))).append("<BR />");
        sb.append("<B>Sugestão de escala para formato A4: </B>").append(String.format("1 / %.3f", Math.max(width / 196, height / 270))).append("<BR />");
        sb.append("<B>Marcos: </B>").append(PolygonalUtils.countOccurrences(polygonal, GeodesicPointType.M)).append("<BR />");
        sb.append("<B>Pontos Offset: </B>").append(PolygonalUtils.countOccurrences(polygonal, GeodesicPointType.O)).append("<BR />");
        sb.append("<B>Pontos tipo P: </B>").append(PolygonalUtils.countOccurrences(polygonal, GeodesicPointType.P)).append("<BR />");
        sb.append("<B>Pontos virtuais: </B>").append(PolygonalUtils.countOccurrences(polygonal, GeodesicPointType.V)).append("<BR />");
        sb.append("<B>Pontos outros: </B>").append(PolygonalUtils.countOccurrences(polygonal, GeodesicPointType.X)).append("<BR />");
        File file = polygonal.getMetadata().getFile();
        sb.append("<B>Arquivo: </B>").append((file == null || !file.exists()) ? "Ainda não foi salvo" : file.getName()).append("<BR />");

        infoEditorPane.setText(sb.toString());
    }

    @Override
    public void setPolygonal(Polygonal polygonal) {
        this.polygonal = polygonal;
    }

    @Override
    public Polygonal getPolygonal() {
        return polygonal;
    }

    @Override
    public void export(Polygonal polygonal) throws IOException {
    }

    private class SyncListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            polygonal.setName(polygonalField.getText());
            PolygonalMetadata metadata = polygonal.getMetadata();

            metadata.setDescricao(descricaoField.getText());
            metadata.setCodigoSncr(codigoSncrField.getText());
            metadata.setCpfCnpj(cpfCnpjField.getText());
            metadata.setNomeProprietario(proprietarioField.getText());
            metadata.setNumeroMatricula(numeroMatriculaField.getText());

            metadata.setZonaUtm(zonaUtmField.getText());
            metadata.setEscala(String.valueOf(escalaDesenhoField.getSelectedItem()));
        }
    }

    private class CpfCnpjValidatorListener implements DocumentListener {

        private Color warningColor = new Color(255, 255, 200);
        private JTextComponent src;

        public CpfCnpjValidatorListener(JTextComponent src) {
            this.src = src;
        }

        public void validateRF(DocumentEvent e) {
            try {
                e.getDocument().getText(0, e.getLength());
                String text = src.getText();

                if (text != null && text.length() > 0) {
                    String s = Cnpj.retiraSimbolos(text);

                    try {
                        if (s.length() == 14) {
                            src.setBackground(Cnpj.isValid(s) ? Color.WHITE : warningColor);
                        } else if (s.length() == 11) {
                            src.setBackground(Cpf.isValid(s) ? Color.WHITE : warningColor);
                        } else {
                            src.setBackground(warningColor);
                        }
                    } catch (PessoaRFException ex) {
                        src.setBackground(warningColor);
                    }
                } else {
                    src.setBackground(Color.WHITE);
                }
            } catch (BadLocationException ex) {
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            validateRF(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            validateRF(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            validateRF(e);
        }
    }

    private class CpfCnpjFormatterListener extends FocusAdapter {

        @Override
        public void focusLost(FocusEvent e) {
            JTextComponent src = (JTextComponent) e.getSource();
            String text = src.getText();

            if (text != null && text.length() > 0) {
                String s = Cnpj.retiraSimbolos(text);

                try {
                    if (s.length() == 14) {
                        if (Cnpj.isValid(s)) {
                            src.setText(Cnpj.format(s));
                        }
                    } else if (s.length() == 11) {
                        if (Cpf.isValid(s)) {
                            src.setText(Cpf.format(s));
                        }
                    }
                } catch (PessoaRFException ex) {
                }
            }
        }
    }

    private class EscalaChangeListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String escala = String.valueOf(escalaDesenhoField.getSelectedItem());
                polygonal.getMetadata().setEscala(escala);
            }
        }
    }

    private class SatChangeListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent evt) {
            JComboBox source = (JComboBox) evt.getSource();
            if (source == satRef1Field) {
                polygonal.getMetadata().setSat1(String.valueOf(source.getSelectedItem()));
            } else if (source == satRef2Field) {
                polygonal.getMetadata().setSat2(String.valueOf(source.getSelectedItem()));
            }
        }
    }

    private class SatListModel implements ComboBoxModel {

        private Object selected;

        @Override
        public void setSelectedItem(Object o) {
            this.selected = o;
        }

        @Override
        public Object getSelectedItem() {
            return selected;
        }

        @Override
        public int getSize() {
            return DataManagement.listSatGeoPoints().size();
        }

        @Override
        public Object getElementAt(int i) {
            return DataManagement.listSatGeoPoints().get(i);
        }

        @Override
        public void addListDataListener(ListDataListener ll) {
        }

        @Override
        public void removeListDataListener(ListDataListener ll) {
        }
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

        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        polygonalField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        descricaoField = new javax.swing.JTextField();
        proprietarioField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cpfCnpjField = new javax.swing.JTextField();
        codigoSncrField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        numeroMatriculaField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        zonaUtmField = new javax.swing.JTextField();
        escalaDesenhoField = new javax.swing.JComboBox();
        satRef1Field = new javax.swing.JComboBox();
        satRef2Field = new javax.swing.JComboBox();
        directionLabel = new javax.swing.JLabel();
        invertPortionButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        infoEditorPane = new javax.swing.JTextPane();
        jLabel8 = new javax.swing.JLabel();

        jLabel5.setText("jLabel5");

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("ID/Número Parcela:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel1, gridBagConstraints);

        polygonalField.setColumns(45);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(polygonalField, gridBagConstraints);

        jLabel4.setText("Proprietário:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(descricaoField, gridBagConstraints);

        proprietarioField.setColumns(45);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(proprietarioField, gridBagConstraints);

        jLabel2.setText("Código SNCR:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel2, gridBagConstraints);

        jLabel3.setText("CPF/CNPJ:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel3, gridBagConstraints);

        cpfCnpjField.setColumns(45);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(cpfCnpjField, gridBagConstraints);

        codigoSncrField.setColumns(45);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(codigoSncrField, gridBagConstraints);

        jLabel6.setText("Matrícula:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel6, gridBagConstraints);

        numeroMatriculaField.setColumns(45);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(numeroMatriculaField, gridBagConstraints);

        jLabel7.setText("Escala Desenho:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel7, gridBagConstraints);

        jLabel9.setText(" * 1000");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        add(jLabel9, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(zonaUtmField, gridBagConstraints);

        escalaDesenhoField.setEditable(true);
        escalaDesenhoField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5", "10", "20", "50", "100" }));
        escalaDesenhoField.setMinimumSize(new java.awt.Dimension(300, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(escalaDesenhoField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(satRef1Field, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(satRef2Field, gridBagConstraints);

        directionLabel.setText("Direção da parcela");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 5);
        add(directionLabel, gridBagConstraints);

        invertPortionButton.setText("Inverter");
        invertPortionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertPortionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        add(invertPortionButton, gridBagConstraints);

        jButton1.setText("Abrir Mapa");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jButton1, gridBagConstraints);

        jLabel10.setText("Descrição Parcela:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel10, gridBagConstraints);

        jLabel12.setText("Ponto SAT de Referência 1:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel12, gridBagConstraints);

        jLabel13.setText("Ponto SAT de Referência 2:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel13, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(400, 140));

        infoEditorPane.setContentType("text/html");
        infoEditorPane.setEditable(false);
        infoEditorPane.setSize(new java.awt.Dimension(0, 40));
        jScrollPane1.setViewportView(infoEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        jLabel8.setText("Zona UTM:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel8, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void invertPortionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertPortionButtonActionPerformed
        try {
            polygonal.revertDirection();
            refresh();
        } catch (PolygonalException ex) {
        }
    }//GEN-LAST:event_invertPortionButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        GLTopographicPanel displayPanel = Bus.getDisplayPanel();
        displayPanel.setPolygonal(this.polygonal);

        Window[] windows = Frame.getWindows();
        for (Window f : windows) {
            if (f instanceof JDialog) {
                ((JDialog) f).setVisible(false);
            }
        }
        displayPanel.refresh();

        displayPanel.requestRepaint();
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField codigoSncrField;
    private javax.swing.JTextField cpfCnpjField;
    private javax.swing.JTextField descricaoField;
    private javax.swing.JLabel directionLabel;
    private javax.swing.JComboBox escalaDesenhoField;
    private javax.swing.JTextPane infoEditorPane;
    private javax.swing.JButton invertPortionButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField numeroMatriculaField;
    private javax.swing.JTextField polygonalField;
    private javax.swing.JTextField proprietarioField;
    private javax.swing.JComboBox satRef1Field;
    private javax.swing.JComboBox satRef2Field;
    private javax.swing.JTextField zonaUtmField;
    // End of variables declaration//GEN-END:variables
}
