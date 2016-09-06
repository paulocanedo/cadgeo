/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PointManagerFormPanel.java
 *
 * Created on 11/05/2011, 14:55:08
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.geodesic.datum.Datum;
import br.com.geomapa.geodesic.datum.Ellipsoid;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.point.MetaDataPoint.MeasurementMethod;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.util.NumberFloatUtils;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author paulocanedo
 */
public class PointManagerFormPanel extends javax.swing.JPanel {

    private GeodesicPoint lastGeoPoint;
    private GeodesicPoint currentGeoPoint;
    public static final String PROP_CURRENTGEOPOINT = "currentGeoPoint";
    private Color errorBackgroundColor = new Color(240, 225, 215);

    /** Creates new form PointManagerFormPanel */
    public PointManagerFormPanel() {
        initComponents();
        pointFields.addAll(Arrays.asList(new JTextField[]{nomeField, coordEsteField, coordNorteField, altitudeField, rmsXField, rmsYField, rmsZField}));

        metodoField.addItemListener(new MetodoItemListener());

        for (JTextField field : pointFields) {
            installDocumentListener(field);
        }

        addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (PROP_CURRENTGEOPOINT.equals(evt.getPropertyName())) {
                    lastGeoPoint = (GeodesicPoint) evt.getOldValue();
                }
            }
        });
    }

    private void installDocumentListener(JTextField field) {
        field.getDocument().addDocumentListener(new FieldListener(field));
    }

    private void fireValueChanged(JTextField field) {
        String fieldName = field.getName();
        if (currentGeoPoint != null && fieldName != null && !fieldName.isEmpty()) {
            try {
                String svalue = field.getText();

                if ("nome".equals(fieldName) && !svalue.isEmpty()) {
                    currentGeoPoint.setName(svalue);
                } else if ("este".equals(fieldName) && !svalue.isEmpty()) {
                    double este = NumberFloatUtils.parseDouble(svalue);

                    currentGeoPoint.setEast(este);
                } else if ("norte".equals(fieldName) && !svalue.isEmpty()) {
                    double norte = NumberFloatUtils.parseDouble(svalue);

                    currentGeoPoint.setNorth(norte);
                } else if ("altitude".equals(fieldName) && !svalue.isEmpty()) {
                    double altitude = NumberFloatUtils.parseDouble(svalue);

                    UTMCoordinate coordinate = currentGeoPoint.getCoordinate().toUTM();
                    coordinate.setEllipsoidalHeight(altitude);
                    currentGeoPoint.setCoordinate(coordinate);
                } else if ("rmsX".equals(fieldName) && !svalue.isEmpty()) {
                    double rmsX = NumberFloatUtils.parseDouble(svalue);

                    currentGeoPoint.getMetaData().setQx(rmsX);
                } else if ("rmsY".equals(fieldName) && !svalue.isEmpty()) {
                    double rmsY = NumberFloatUtils.parseDouble(svalue);

                    currentGeoPoint.getMetaData().setQy(rmsY);
                } else if ("rmsZ".equals(fieldName) && !svalue.isEmpty()) {
                    double rmsZ = NumberFloatUtils.parseDouble(svalue);

                    currentGeoPoint.getMetaData().setQz(rmsZ);
                }
                field.setBackground(Color.WHITE);
            } catch (Exception ex) {
                field.setBackground(errorBackgroundColor);
            }
        } else if (currentGeoPoint == null) {
            Main main = Main.getInstance();
            ProjectMetadata projectInfo = main.getProjectInfo();
            Integer zonaUtm = projectInfo.getZonaUtm();
            Hemisphere hemisferio = projectInfo.getHemisferio();
            Datum datum = projectInfo.getDatum();

            String nome = nomeField.getText();
            String seste = coordEsteField.getText();
            String snorte = coordNorteField.getText();
            String salt = altitudeField.getText().isEmpty() ? "0" : altitudeField.getText();

            if (!nome.isEmpty() && !seste.isEmpty() && !snorte.isEmpty()) {
                try {
                    double este, norte, altitude;
                    try {
                        este = NumberFloatUtils.parseDouble(seste);
                    } catch (Exception ex) {
                        coordEsteField.setBackground(errorBackgroundColor);
                        return;
                    }
                    try {
                        norte = NumberFloatUtils.parseDouble(snorte);
                    } catch (Exception ex) {
                        coordNorteField.setBackground(errorBackgroundColor);
                        return;
                    }
                    try {
                        altitude = NumberFloatUtils.parseDouble(salt);
                    } catch (Exception ex) {
                        altitudeField.setBackground(errorBackgroundColor);
                        return;
                    }
                    UTMCoordinate coord = new UTMCoordinate(new Ellipsoid(datum), zonaUtm, hemisferio, este, norte, altitude);
                    GeodesicPoint newGeoPoint = new GeodesicPoint(coord, nome);
                    setCurrentGeoPoint(newGeoPoint);

                    DataManagement.getGeoPointTableModel().add(currentGeoPoint);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void clearPointFields() {
        for (JTextField field : pointFields) {
            field.setText("");
            metodoField.setSelectedIndex(-1);
        }
    }

    public void novoPonto() {
        ProjectMetadata projectInfo = Main.getInstance().getProjectInfo();

        clearPointFields();
        setCurrentGeoPoint(null);

        String newPointName = "";
        if (lastGeoPoint != null) {
            newPointName = projectInfo.newPointName(lastGeoPoint.getType(), lastGeoPoint.getName().length());
        }
        nomeField.setText(newPointName);
        nomeField.grabFocus();
    }

    public void setFields(GeodesicPoint point) {
        for (JTextField field : pointFields) {
            field.setBackground(Color.WHITE);
        }

        nomeField.setText(point.getName());
        metodoField.setSelectedItem(point.getMetaData().getMeasurementMethod());
        coordEsteField.setText(String.format("%.3f", point.getCoordinate().toUTM().getEast()));
        coordNorteField.setText(String.format("%.3f", point.getCoordinate().toUTM().getNorth()));
        altitudeField.setText(String.format("%.3f", point.getCoordinate().getEllipsoidalHeight()));
        rmsXField.setText(String.format("%.3f", point.getMetaData().getQx()));
        rmsYField.setText(String.format("%.3f", point.getMetaData().getQy()));
        rmsZField.setText(String.format("%.3f", point.getMetaData().getQz()));
        
        satGeoCB.setSelected(point.isSatGeo());
        favoriteCB.setSelected(point.isFavorite());
    }

    private class FieldListener implements DocumentListener {

        private JTextField field;

        public FieldListener(JTextField field) {
            this.field = field;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            fireValueChanged(field);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            fireValueChanged(field);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            fireValueChanged(field);
        }
    }

    private class MetodoItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                MeasurementMethod item = (MeasurementMethod) e.getItem();
                if (currentGeoPoint != null) {
                    currentGeoPoint.getMetaData().setMeasurementMethod(item);
                }
            }
        }
    }

    public GeodesicPoint getCurrentGeoPoint() {
        return currentGeoPoint;
    }

    public void setCurrentGeoPoint(GeodesicPoint currentGeoPoint) {
        GeodesicPoint oldCurrentGeoPoint = this.currentGeoPoint;
        this.currentGeoPoint = currentGeoPoint;
        propertyChangeSupport.firePropertyChange(PROP_CURRENTGEOPOINT, oldCurrentGeoPoint, currentGeoPoint);
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @Override
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
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

        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        nomeField = new javax.swing.JTextField();
        coordEsteField = new javax.swing.JTextField();
        coordNorteField = new javax.swing.JTextField();
        altitudeField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        metodoField = new javax.swing.JComboBox(MeasurementMethod.values());
        rmsXField = new javax.swing.JTextField();
        rmsYField = new javax.swing.JTextField();
        rmsZField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        satGeoCB = new javax.swing.JCheckBox();
        favoriteCB = new javax.swing.JCheckBox();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        jLabel9.setFont(jLabel9.getFont().deriveFont(jLabel9.getFont().getStyle() | java.awt.Font.BOLD, 16));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Informações do ponto");
        jPanel4.add(jLabel9);

        add(jPanel4);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Nome");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 90);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Coord Norte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 90);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel5.setText("Coord Este");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 90);
        jPanel1.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Altitude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 90);
        jPanel1.add(jLabel6, gridBagConstraints);

        nomeField.setColumns(35);
        nomeField.setName("nome"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel1.add(nomeField, gridBagConstraints);

        coordEsteField.setColumns(35);
        coordEsteField.setName("este"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel1.add(coordEsteField, gridBagConstraints);

        coordNorteField.setColumns(35);
        coordNorteField.setName("norte"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel1.add(coordNorteField, gridBagConstraints);

        altitudeField.setColumns(35);
        altitudeField.setActionCommand("<Não definido>");
        altitudeField.setName("altitude"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        jPanel1.add(altitudeField, gridBagConstraints);

        add(jPanel1);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("Método de Levantamento");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jLabel3, gridBagConstraints);

        jLabel4.setText("RMS eixo X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jLabel4, gridBagConstraints);

        jLabel7.setText("RMS eixo Y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jLabel7, gridBagConstraints);

        jLabel8.setText("RMS eixo Z");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jLabel8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(metodoField, gridBagConstraints);

        rmsXField.setColumns(35);
        rmsXField.setName("rmsX"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(rmsXField, gridBagConstraints);

        rmsYField.setColumns(35);
        rmsYField.setName("rmsY"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(rmsYField, gridBagConstraints);

        rmsZField.setColumns(35);
        rmsZField.setName("rmsZ"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(rmsZField, gridBagConstraints);

        add(jPanel2);

        satGeoCB.setText("Marco Geodésico");
        satGeoCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                satGeoCBItemStateChanged(evt);
            }
        });
        jPanel3.add(satGeoCB);

        favoriteCB.setText("Favorito");
        favoriteCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                favoriteCBItemStateChanged(evt);
            }
        });
        jPanel3.add(favoriteCB);

        add(jPanel3);
    }// </editor-fold>//GEN-END:initComponents

    private void satGeoCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_satGeoCBItemStateChanged
        currentGeoPoint.setSatGeo(evt.getStateChange() == ItemEvent.SELECTED);
    }//GEN-LAST:event_satGeoCBItemStateChanged

    private void favoriteCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_favoriteCBItemStateChanged
        currentGeoPoint.setFavorite(evt.getStateChange() == ItemEvent.SELECTED);
    }//GEN-LAST:event_favoriteCBItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField altitudeField;
    private javax.swing.JTextField coordEsteField;
    private javax.swing.JTextField coordNorteField;
    private javax.swing.JCheckBox favoriteCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JComboBox metodoField;
    private javax.swing.JTextField nomeField;
    private javax.swing.JTextField rmsXField;
    private javax.swing.JTextField rmsYField;
    private javax.swing.JTextField rmsZField;
    private javax.swing.JCheckBox satGeoCB;
    // End of variables declaration//GEN-END:variables
    private List<JTextField> pointFields = new ArrayList<JTextField>();
}
