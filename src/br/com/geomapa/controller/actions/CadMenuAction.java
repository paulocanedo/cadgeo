/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.actions;

import br.com.geomapa.controller.command.cad.impl.AzimuthDistanceCommand;
import br.com.geomapa.controller.command.cad.impl.BorderingNameCommand;
import br.com.geomapa.controller.command.cad.impl.BorderingSeparatorCommand;
import br.com.geomapa.controller.command.cad.impl.CreateGeoPointCommand;
import br.com.geomapa.controller.command.cad.impl.DefinePerimeterCommand;
import br.com.geomapa.controller.command.cad.impl.PortionCreateCommand;
import br.com.geomapa.controller.command.cad.impl.BorderingDefineCommand;
import br.com.geomapa.controller.command.cad.impl.DashedLineBetweenOffsetPointCommand;
import br.com.geomapa.controller.command.cad.impl.DxfOverlayCommand;
import br.com.geomapa.controller.command.cad.impl.GeodesicPointEditCommand;
import br.com.geomapa.controller.command.cad.impl.GridCoordCommand;
import br.com.geomapa.controller.command.cad.impl.IdCommand;
import br.com.geomapa.controller.command.cad.impl.LabelCoordCommand;
import br.com.geomapa.controller.command.cad.impl.LabelGeoPointCommand;
import br.com.geomapa.controller.command.cad.impl.PaperCommand;
import br.com.geomapa.controller.command.cad.impl.PortionInfoCommand;
import br.com.geomapa.controller.command.cad.impl.PortionLabelCommand;
import br.com.geomapa.controller.command.cad.impl.ProjectLineCommand;
import br.com.geomapa.controller.command.cad.impl.ResetPortionCommand;
import br.com.geomapa.controller.command.cad.impl.TableAreaPerimeterCommand;
import br.com.geomapa.controller.command.cad.impl.TableAzimuthDistanceCommand;
import br.com.geomapa.controller.ToolBarController;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 *
 * @author paulocanedo
 */
public class CadMenuAction extends AbstractAction {

    private final JPopupMenu menuGeo = new JPopupMenu();
    private final JMenu pontosMenu = new JMenu("Pontos");
    private final JMenu confrontantesMenu = new JMenu("Confrontantes");
    private final JMenu tabelasMenu = new JMenu("Tabelas");
    private final JMenu parcelaMenu = new JMenu("Parcelas");
    private final JMenu relatoriosMenu = new JMenu("Relatórios");
    private final JMenu miscelaneaMenu = new JMenu("Miscelânea");

    public CadMenuAction() {
        super("Comandos Geo");

        initMenuGeo();
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

        relatoriosMenu.add("Memorial");
        relatoriosMenu.add("Cálculo de Área");
        relatoriosMenu.add("Dados Cartográficos");
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
        separator.setPreferredSize(new Dimension(148, 2));
        return separator;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractButton src = (AbstractButton) e.getSource();

        menuGeo.show(src, 0, src.getHeight());
    }
}
