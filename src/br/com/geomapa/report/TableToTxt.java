/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.report;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author paulocanedo
 */
public class TableToTxt {

    private static final String separator = ";";

    public static void exportToTxt(AbstractTableModel table, OutputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                sb.append(table.getValueAt(i, j));
                sb.append(separator);
            }
            sb.delete(sb.length() - separator.length(), sb.length());
            sb.append("\r\n");
        }
        
        stream.write(sb.toString().getBytes());
    }
    
    public static void exportToTxt(JTable table, OutputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                sb.append(table.getValueAt(i, j));
                sb.append(separator);
            }
            sb.delete(sb.length() - separator.length(), sb.length());
            sb.append("\r\n");
        }
        
        stream.write(sb.toString().getBytes());
    }
}
