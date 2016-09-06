/**
 * TODO: Construir cabeçalho dos arquivos de código fonte
 */
package br.com.geomapa.main;

import br.com.geomapa.controller.ToolBarController;
import javax.swing.JMenu;
import br.com.geomapa.controller.MenuController;
import br.com.pc9.pswing.util.SystemUtilsOS;
import br.com.geomapa.controller.actions.OpenProjectAction;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import br.com.geomapa.controller.actions.SaveProjectAction;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.geodesic.InvalidPolygonalException;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.ui.panels.RegistrationPanel;
import br.com.geomapa.util.SecurityUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import static br.com.geomapa.util.UserInterfaceUtil.*;

/**
 *
 * @author paulocanedo
 */
public class Main extends JFrame {

    private final String softwareName = "pcGeoCad";

    private Main() {
        initComponents();

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                onReady();
                centerPanel.grabFocus();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(Main.this, "Deseja salvar as alterações antes de fechar o programa?");

                if (result == JOptionPane.YES_OPTION) {
                    try {
                        new SaveProjectAction().save();
                        System.exit(0);
                    } catch (Throwable ex) {
                        JOptionPane.showMessageDialog(Main.this, ex.getMessage());
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        leftPanel.setPreferredSize(new Dimension(32, 400));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - frameWidth) / 2, (screenSize.height - frameHeight) / 2, frameWidth, frameHeight);
        createPanels();

        initTopPanel();

        setTitle(softwareName);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        Action action = new OpenProjectAction();
        KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "open");
        getRootPane().getActionMap().put("open", action);

        action = new SaveProjectAction();
        keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "save");
        getRootPane().getActionMap().put("save", action);
    }

    private void onReady() {
        setExtendedState(MAXIMIZED_BOTH);
        refreshAll();
        createBufferStrategy(2);

        if (SystemUtilsOS.isMac()) {
            JMenuBar menuBar = new JMenuBar();
            for (JMenu menu : MenuController.getClassicMenuList()) {
                menuBar.add(menu);
            }

            setJMenuBar(menuBar);
        }
    }

    private void initTopPanel() {
        topPanel.add(ToolBarController.getInstance().getToolBarComponent());
    }

    public ProjectMetadata getProjectInfo() {
        return projectDataPanel.getProjectInfo();
    }

    public void setProjectInfo(ProjectMetadata projectInfo) {
        projectDataPanel.setAndLabelProjectMetadata(projectInfo);
    }

    public synchronized void refreshAll() {
        refreshAll(DataManagement.getMainPolygonal());
    }

    public synchronized void refreshAll(Polygonal polygonal) {
        try {
            refreshActive(centerPanel, polygonal);
            String projectName = getProjectInfo().getNome();
            setTitle(softwareName + (projectName.isEmpty() ? "" : " - ") + projectName);
        } catch (InvalidPolygonalException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    public synchronized static Main getInstance() {
        if (SecurityUtils.gmIsKeyValid() || RegistrationPanel.canOpen()) {
            return SingletonHolder.instance;
        }
        return null;
    }

    private static class SingletonHolder {

        public static final Main instance = new Main();
    }
    private final JPanel centerPanel = getTopographicPanel();
    private final JPanel topPanel = new JPanel(new GridLayout());
    private final JPanel leftPanel = new JPanel(new GridLayout());
    private final JPanel bottomPanel = new JPanel(new GridLayout());
    private int frameWidth = 1280;
    private int frameHeight = 720;
}
