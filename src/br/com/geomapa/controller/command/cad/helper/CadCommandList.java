package br.com.geomapa.controller.command.cad.helper;

import br.com.geomapa.controller.command.cad.impl.*;
import br.com.geomapa.controller.command.cad.spec.CadCommand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  THIS FILE IS GENERATED AUTOMATICALLY BY CLASS ClassCommandCreator, DO NOT EDIT!
 */
public class CadCommandList {

	public static final List<CadCommand> list = new ArrayList<CadCommand>();

	static {
		list.add(new AzimuthDistanceCommand());
		list.add(new BorderingDefineCommand());
		list.add(new BorderingNameCommand());
		list.add(new BorderingSeparatorCommand());
		list.add(new CalculationAreaCommand());
		list.add(new CartographicDataCommand());
		list.add(new CircleCommand());
		list.add(new CopyCommand());
		list.add(new CreateGeoPointCommand());
		list.add(new DashedLineBetweenOffsetPointCommand());
		list.add(new DefinePerimeterCommand());
		list.add(new DxfOverlayCommand());
		list.add(new EraseCommand());
		list.add(new ExportCommand());
		list.add(new GeodesicPointEditCommand());
		list.add(new GoToMainPolygonalCommand());
		list.add(new GridCoordCommand());
		list.add(new IdCommand());
		list.add(new LabelCoordCommand());
		list.add(new LabelGeoPointCommand());
		list.add(new LineArrowCommand());
		list.add(new LineCommand());
		list.add(new LineDivisionCommand());
		list.add(new MatchPropertiesCommand());
		list.add(new MemorialCommand());
		list.add(new MoveCommand());
		list.add(new PaperCommand());
		list.add(new PortionCreateCommand());
		list.add(new PortionInfoCommand());
		list.add(new PortionLabelCommand());
		list.add(new ProjectLineCommand());
		list.add(new RectangleCommand());
		list.add(new RedrawCommand());
		list.add(new ResetPortionCommand());
		list.add(new RotateCommand());
		list.add(new TableAreaPerimeterCommand());
		list.add(new TableAzimuthDistanceCommand());
		list.add(new TextCommand());
		list.add(new TextEditCommand());
		list.add(new UndoCommand());
		list.add(new ZoomCommand());
		Collections.sort(list, new CadCommandComparator());
	}

	private CadCommandList() {
	}
}
