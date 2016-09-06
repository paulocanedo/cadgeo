/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util;

import br.com.geomapa.controller.actions.ImportAction;
import br.com.geomapa.controller.actions.NewQuotaFromCalcAreaAction;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.importer.rinex.RinexImporterAction;
import br.com.geomapa.main.Main;
import br.com.geomapa.ui.panels.CalculationAreaPanel;
import br.com.geomapa.ui.panels.CartograficDataPanel;
import br.com.geomapa.ui.panels.CommandListPanel;
import br.com.geomapa.ui.panels.DefineBorderingPanel;
import br.com.geomapa.ui.panels.ExportPanel;
import br.com.geomapa.ui.panels.GeodesicPanel;
import br.com.geomapa.ui.panels.MemorialPanel;
import br.com.geomapa.ui.panels.PointManagerPanel;
import br.com.geomapa.ui.panels.PortionDataPanel;
import br.com.geomapa.ui.panels.ProjectManagerPanel;
import br.com.geomapa.ui.panels.RinexManagerPanel;
import br.com.geomapa.ui.panels.StatisticsPanel;
import br.com.geomapa.ui.panels.options.OptionsPanel;
import br.com.geomapa.ui.panels.project.OpenProjectPanel;
import br.com.geomapa.ui.panels.project.ProjectDataPanel;
import br.com.geomapa.ui.panels.topographic.TopographicPanel;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

/**
 *
 * @author paulocanedo
 */
public class UserInterfaceUtil {

    public static final String PROJECT_MANAGER_PANEL = "projectManagerPanel";
    public static final String PROJECT_DATA = "projectData";
    public static final String PORTION_DATA = "quoteData";
    public static final String POINT_MANAGER = "pointManager";
    public static final String STATISTICS = "statistics";
    public static final String DEFINE_PERIMETER = "definePerimeter";
    public static final String DEFINE_BORDERING = "defineBordering";
    public static final String TOPOGRAPHIC = "topographic";
    public static final String MEMORIAL = "memorial";
    public static final String CALCULATION_AREA = "calculation";
    public static final String CARTOGRAFIC_DATA = "cartograficData";
    public static final String RINEX_DATA = "rinexData";
    public static final String OPEN_PROJECT = "openProject";
    public static final String OPTIONS = "options";
    public static final String EXPORT = "export";
    public static final String COMMAND_LIST = "commandList";
    /**************************************************************************/
    private static final JPopupMenu addMenuPopup = new JPopupMenu("Inserir");

    public static JPopupMenu createInsertPopupMenu() {
        if (addMenuPopup.getComponentCount() == 0) {
            addMenuPopup.add(new NewQuotaFromCalcAreaAction());
            addMenuPopup.add(new JSeparator());
            for (JMenuItem mitem : importItemsMenu()) {
                addMenuPopup.add(mitem);
            }
        }
        return addMenuPopup;
    }

    public static JMenuItem[] importItemsMenu() {
        return new JMenuItem[]{
                    new JMenuItem(new RinexImporterAction("Importar Rinex")),
                    new JMenuItem(new ImportAction("Importar CSV", "Importar pontos de um arquivo em formato CSV", "csv")),
                    new JMenuItem(new ImportAction("Importar do Astek", "Importar pontos do software Astek por arquivo rtf", "rtf")),
                    new JMenuItem(new ImportAction("Importar do Topcon Plus", "Importar pontos do software Topcon Plus por arquivo docx", "docx"))
                };
    }

    public static HashMap<String, JPanel> createPanels() {
        if (panels.isEmpty()) {
            panels.put(POINT_MANAGER, new PointManagerPanel());
            panels.put(PORTION_DATA, new PortionDataPanel());
            panels.put(STATISTICS, new StatisticsPanel());
            panels.put(DEFINE_BORDERING, new DefineBorderingPanel());
            panels.put(TOPOGRAPHIC, topographicPanel);
            panels.put(MEMORIAL, new MemorialPanel());
            panels.put(CALCULATION_AREA, new CalculationAreaPanel());
            panels.put(CARTOGRAFIC_DATA, new CartograficDataPanel());
            panels.put(RINEX_DATA, new RinexManagerPanel());
            panels.put(OPEN_PROJECT, new OpenProjectPanel());
            panels.put(OPTIONS, new OptionsPanel());
            panels.put(PROJECT_DATA, projectDataPanel);
            panels.put(PROJECT_MANAGER_PANEL, projectManagerPanel);
            panels.put(EXPORT, new ExportPanel());
            panels.put(COMMAND_LIST, new CommandListPanel());
        }
        return panels;
    }

    public static JPanel get(String what) {
        HashMap<String, JPanel> map = createPanels();

        return map.get(what);
    }

    public static GeodesicPanel getAsGeodesicPanel(String what) {
        HashMap<String, JPanel> map = createPanels();

        JPanel panel = map.get(what);
        return panel instanceof GeodesicPanel ? ((GeodesicPanel) panel) : null;
    }

    public static void showDialog(String what) {
        showDialog(what, null);
    }

    public static void showDialog(String what, Polygonal polygonal) {
        JPanel panel = panels.get(what);
        if (panel instanceof GeodesicPanel) {
            if (polygonal != null) {
                ((GeodesicPanel) panel).setPolygonal(polygonal);
            }
            
            ((GeodesicPanel) panel).refresh();
        }

        if (dialog == null) {
            dialog = new JDialog(Main.getInstance(), panel.getName(), true);
            dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
            dialog.getRootPane().getActionMap().put("escape", new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    hideCurrent();
                }
            });
        }
        if (currentPanel != null) {
            dialog.remove(currentPanel);
        }
        dialog.add(currentPanel = panel);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setBounds((screenSize.width - dialogWidth) / 2, (screenSize.height - dialogHeight) / 2, dialogWidth, dialogHeight);
        panel.setVisible(true);
        dialog.setVisible(true);
    }

    public static void hideCurrent() {
        dialog.setVisible(false);
    }

    public static void refreshActive(Container container, Polygonal currentPolygonal) {
        Component c = catchActiveComponent(container);
        if (c == null) {
            return;
        }

        ((GeodesicPanel) c).setPolygonal(currentPolygonal);
        ((GeodesicPanel) c).refresh();
    }

    public static Component catchActiveComponent(Container container) {
        for (Component c : container.getComponents()) {
            if (c.isVisible() && c instanceof GeodesicPanel) {
                return c;
            }
        }
        return null;
    }

    public static TopographicPanel getTopographicPanel() {
        return topographicPanel;
    }
    private static JPanel currentPanel;
    private static JDialog dialog;
    private static final int dialogWidth = 1050;
    private static final int dialogHeight = 590;
    private static final HashMap<String, JPanel> panels = new HashMap<String, JPanel>();
    public static final ProjectDataPanel projectDataPanel = new ProjectDataPanel();
    public static TopographicPanel topographicPanel = new TopographicPanel();
    private static final ProjectManagerPanel projectManagerPanel = new ProjectManagerPanel();
}
