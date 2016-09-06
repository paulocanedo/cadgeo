/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller;

import br.com.geomapa.controller.actions.AboutAction;
import br.com.geomapa.controller.actions.CopyToClipboardAction;
import br.com.geomapa.controller.actions.ExportEverythingAction;
import br.com.geomapa.controller.actions.ManagerProjectAction;
import br.com.geomapa.controller.actions.ManualAction;
import br.com.geomapa.controller.actions.NewProjectAction;
import br.com.geomapa.controller.actions.OpenProjectAction;
import br.com.geomapa.controller.actions.PasteFromClipboardAction;
import br.com.geomapa.controller.actions.PreferencesAction;
import br.com.geomapa.controller.actions.QuitAction;
import br.com.geomapa.controller.actions.SaveProjectAction;
import br.com.geomapa.controller.actions.UndoAction;
import br.com.geomapa.controller.command.cad.impl.AzimuthDistanceCommand;
import br.com.geomapa.controller.command.cad.impl.BorderingDefineCommand;
import br.com.geomapa.controller.command.cad.impl.BorderingNameCommand;
import br.com.geomapa.controller.command.cad.impl.BorderingSeparatorCommand;
import br.com.geomapa.controller.command.cad.impl.CalculationAreaCommand;
import br.com.geomapa.controller.command.cad.impl.CartographicDataCommand;
import br.com.geomapa.controller.command.cad.impl.CommandListCommand;
import br.com.geomapa.controller.command.cad.impl.CreateGeoPointCommand;
import br.com.geomapa.controller.command.cad.impl.DashedLineBetweenOffsetPointCommand;
import br.com.geomapa.controller.command.cad.impl.DefinePerimeterCommand;
import br.com.geomapa.controller.command.cad.impl.DxfOverlayCommand;
import br.com.geomapa.controller.command.cad.impl.ExportCommand;
import br.com.geomapa.controller.command.cad.impl.GeodesicPointEditCommand;
import br.com.geomapa.controller.command.cad.impl.GridCoordCommand;
import br.com.geomapa.controller.command.cad.impl.IdCommand;
import br.com.geomapa.controller.command.cad.impl.LabelCoordCommand;
import br.com.geomapa.controller.command.cad.impl.LabelGeoPointCommand;
import br.com.geomapa.controller.command.cad.impl.MemorialCommand;
import br.com.geomapa.controller.command.cad.impl.PaperCommand;
import br.com.geomapa.controller.command.cad.impl.PortionCreateCommand;
import br.com.geomapa.controller.command.cad.impl.PortionInfoCommand;
import br.com.geomapa.controller.command.cad.impl.PortionLabelCommand;
import br.com.geomapa.controller.command.cad.impl.ProjectLineCommand;
import br.com.geomapa.controller.command.cad.impl.ResetPortionCommand;
import br.com.geomapa.controller.command.cad.impl.TableAreaPerimeterCommand;
import br.com.geomapa.controller.command.cad.impl.TableAzimuthDistanceCommand;
import br.com.geomapa.util.UserInterfaceUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

/**
 *
 * @author paulocanedo
 */
public class MenuController {

    private List<Object> singleMenuList = new ArrayList<Object>();
    private List<JMenu> classicMenuList = new ArrayList<JMenu>();
    //--------------
    private final JMenu arquivoMenu = new JMenu("Arquivo");
    private final JMenu menuGeo = new JMenu("Desenho Geo");
    private final JMenu pontosMenu = new JMenu("Pontos");
    private final JMenu confrontantesMenu = new JMenu("Confrontantes");
    private final JMenu tabelasMenu = new JMenu("Tabelas");
    private final JMenu parcelaMenu = new JMenu("Parcelas");
    private final JMenu relatoriosMenu = new JMenu("Relatórios");
    private final JMenu miscelaneaMenu = new JMenu("Miscelânea");
    private final JMenu importMenu = new JMenu("Importar");
    private final JMenu editMenu = new JMenu("Editar");
    private final JMenu ajudaSubmenu = new JMenu("Ajuda");
    //--------------
    private static MenuController instance;

    private MenuController() {
        initMenu();
    }

    private void initMenu() {
        initMenuGeo();

        NewProjectAction newProjectAction = new NewProjectAction();
        OpenProjectAction openProjectAction = new OpenProjectAction();
        SaveProjectAction saveProjectAction = new SaveProjectAction();
        ManagerProjectAction managerProjectAction = new ManagerProjectAction();
        UndoAction undoAction = new UndoAction();
        CopyToClipboardAction copyToClipboardAction = new CopyToClipboardAction();
        PasteFromClipboardAction pasteFromClipboardAction = new PasteFromClipboardAction();
        PreferencesAction preferencesAction = new PreferencesAction();
        ManualAction manualAction = new ManualAction();
        AboutAction aboutAction = new AboutAction();
        QuitAction quitAction = new QuitAction();

        for (JMenuItem mitem : UserInterfaceUtil.importItemsMenu()) {
            importMenu.add(mitem);
        }

        editMenu.add(undoAction);
        editMenu.add(copyToClipboardAction);
        editMenu.add(pasteFromClipboardAction);
        editMenu.add(preferencesAction);

        ajudaSubmenu.add(manualAction);
        ajudaSubmenu.add(ToolBarController.createCadAction(CommandListCommand.class));
        ajudaSubmenu.add(aboutAction);

        singleMenuList.add(newProjectAction);
        singleMenuList.add(openProjectAction);
        singleMenuList.add(saveProjectAction);
        singleMenuList.add(editMenu);
        singleMenuList.add(separator());
        singleMenuList.add(managerProjectAction);
        singleMenuList.add(menuGeo);
        singleMenuList.add(separator());
        singleMenuList.add(importMenu);
        singleMenuList.add(ToolBarController.createCadAction(ExportCommand.class));
        singleMenuList.add(separator());
        singleMenuList.add(ajudaSubmenu);
        singleMenuList.add(quitAction);

        arquivoMenu.add(newProjectAction);
        arquivoMenu.add(openProjectAction);
        arquivoMenu.add(saveProjectAction);
        arquivoMenu.add(importMenu);
        arquivoMenu.add(ToolBarController.createCadAction(ExportCommand.class));
        arquivoMenu.add(quitAction);

        classicMenuList.add(arquivoMenu);
        classicMenuList.add(editMenu);
        classicMenuList.add(menuGeo);
        classicMenuList.add(ajudaSubmenu);
    }

    private void initMenuGeo() {
        pontosMenu.add(ToolBarController.createCadAction(CreateGeoPointCommand.class));
        pontosMenu.add(ToolBarController.createCadAction(GeodesicPointEditCommand.class));
        pontosMenu.add(ToolBarController.createCadAction(LabelGeoPointCommand.class));
        pontosMenu.add(ToolBarController.createCadAction(LabelCoordCommand.class));
        menuGeo.add(pontosMenu);

        confrontantesMenu.add(ToolBarController.createCadAction(BorderingNameCommand.class));
        confrontantesMenu.add(ToolBarController.createCadAction(BorderingSeparatorCommand.class));
        confrontantesMenu.add(ToolBarController.createCadAction(BorderingDefineCommand.class));
        menuGeo.add(confrontantesMenu);

        parcelaMenu.add(ToolBarController.createCadAction(PortionLabelCommand.class));
        parcelaMenu.add(ToolBarController.createCadAction(PortionInfoCommand.class));
        parcelaMenu.add(ToolBarController.createCadAction(PortionCreateCommand.class));
        parcelaMenu.add(ToolBarController.createCadAction(DefinePerimeterCommand.class));
        parcelaMenu.add(ToolBarController.createCadAction(ResetPortionCommand.class));
        menuGeo.add(parcelaMenu);

        tabelasMenu.add(ToolBarController.createCadAction(TableAreaPerimeterCommand.class));
        tabelasMenu.add(ToolBarController.createCadAction(TableAzimuthDistanceCommand.class));
        menuGeo.add(tabelasMenu);

        relatoriosMenu.add(ToolBarController.createCadAction(MemorialCommand.class));
        relatoriosMenu.add(ToolBarController.createCadAction(CalculationAreaCommand.class));
        relatoriosMenu.add(ToolBarController.createCadAction(CartographicDataCommand.class));
        menuGeo.add(relatoriosMenu);

        miscelaneaMenu.add(ToolBarController.createCadAction(DxfOverlayCommand.class));
        miscelaneaMenu.add(ToolBarController.createCadAction(DashedLineBetweenOffsetPointCommand.class));
        menuGeo.add(miscelaneaMenu);

        menuGeo.add(ToolBarController.createCadAction(AzimuthDistanceCommand.class));
        menuGeo.add(ToolBarController.createCadAction(GridCoordCommand.class));
        menuGeo.add(ToolBarController.createCadAction(PaperCommand.class));
        menuGeo.add(ToolBarController.createCadAction(ProjectLineCommand.class));
        menuGeo.add(ToolBarController.createCadAction(IdCommand.class));
    }

    private JSeparator separator() {
        JSeparator separator = new JSeparator();
        return separator;
    }

    private static MenuController getInstance() {
        if (instance == null) {
            instance = new MenuController();
        }
        return instance;
    }

    public static List<Object> getSingleMenuList() {
        MenuController controller = getInstance();

        return controller.singleMenuList;
    }

    public static List<JMenu> getClassicMenuList() {
        MenuController controller = getInstance();

        return controller.classicMenuList;
    }
}
