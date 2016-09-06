/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.export;

import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.main.Main;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.report.ReportGenerator;
import br.com.geomapa.ui.panels.options.ResponsavelTecnico;
import br.com.geomapa.util.unit.impl.HectareUnit;
import br.com.geomapa.util.unit.impl.Meter;
import br.com.geomapa.util.unit.specs.AreaUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author paulocanedo
 */
public class TechnicalReport {

    private static final HashMap<String, Object> headerValues = new HashMap<String, Object>();
    public static final String FILE_NAME = "relatorio_tecnico.odt";

    public static void export(Polygonal polygonal) throws IOException {
        ProjectMetadata projectMetadata = Main.getInstance().getProjectInfo();
        File outfolder = projectMetadata.getTechnicalReport();
        outfolder.mkdir();

        File outfile = new File(outfolder, polygonal.getName() + ".odt");
        export(polygonal, projectMetadata, outfile);
    }

    public static void export(Polygonal polygonal, ProjectMetadata projectMetadata, File outfile) throws IOException {
        DistanceUnit dUnit = new Meter();
        AreaUnit aUnit = new HectareUnit();
        ResponsavelTecnico rt = projectMetadata.getResponsavelTecnico();

        headerValues.clear();
        headerValues.put("area", aUnit.toString(polygonal.area(), 4));
        headerValues.put("perimetro", dUnit.toString(polygonal.perimeter(), 2));
        headerValues.put("info", projectMetadata);
        headerValues.put("parcela", polygonal.getMetadata());
        headerValues.put("marcos_ocupados", PolygonalUtils.countOccurrences(polygonal, GeodesicPointType.M));
        headerValues.put("pontos_ocupados", PolygonalUtils.countOccurrences(polygonal, GeodesicPointType.P));
        headerValues.put("pontos_offset", PolygonalUtils.countOccurrences(polygonal, GeodesicPointType.O));
        headerValues.put("rt_nome", rt == null ? "" : rt.getNome());
        headerValues.put("rt_crea", rt == null ? "" : rt.getCodigoCrea());
        headerValues.put("rt_codigo_credenciamento", rt == null ? "" : rt.getCodigoIncra());
        headerValues.put("rt_profissao", rt == null ? "" : rt.getProfissao());

        ReportGenerator generator = new ReportGenerator(Exporter.getStream(projectMetadata, Exporter.ExporterId.TECHNICAL_REPORT));

        generator.generate(outfile, headerValues);
    }
}
