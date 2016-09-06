/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CommandListPanel.java
 *
 * Created on 08/12/2011, 17:07:31
 */
package br.com.geomapa.ui.panels;

import br.com.geomapa.controller.CadCommandController;
import br.com.geomapa.controller.command.cad.helper.CadCommandList;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author paulocanedo
 */
public class CommandListPanel extends javax.swing.JPanel {

    /** Creates new form CommandListPanel */
    public CommandListPanel() {
        initComponents();
    }

    private class ListCommandModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return CadCommandList.list.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CadCommand command = CadCommandList.list.get(rowIndex);

            if (columnIndex == 0) {
                return command.toString();
            } else if (columnIndex == 1) {
                return command.getCommandName();
            }
            return "???";
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) {
                return "Nome do comando";
            } else if (column == 1) {
                return "Linha de comando";
            }
            return super.getColumnName(column);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        jTable1.setModel(new ListCommandModel());
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setText("Lista de comandos disponíveis");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                    .add(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
