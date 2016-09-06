/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.project;

import br.com.geomapa.export.Exporter;
import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.InvalidGeodesicPointException;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.PolygonalMetadata;
import br.com.geomapa.geodesic.datum.Datum;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.importer.pcgeocad.VisualObjectParser;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.importer.CSVPointImporter;
import br.com.geomapa.importer.PointImporter;
import br.com.geomapa.importer.pcgeocad.VisualObjectParserException;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.ui.model.GeoPointTableModel;
import br.com.geomapa.util.DirectoryFileFilter;
import br.com.geomapa.util.FileFilterExtension;
import br.com.geomapa.util.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 *
 * @author paulocanedo
 */
public class ProjectUtils {

    public final static String metadataDirName = "metadados";
    public final static String metadataFileName = "project.properties";
    public final static String pointsFileName = "points.csv";
    public final static String pointsOtherZoneFileName = "points_other_zones.csv";
    public final static String pointsMetadataFileName = "points_metadata.properties";
    public final static String polygonalsDirName = "polygonals";
    public final static String mainPolygonalFileName = "0";
    public final static String rinexDirName = "rinex";

    private ProjectUtils() {
    }

    public static List<ProjectMetadata> listProjects(File baseDir) throws IOException {
        List<ProjectMetadata> list = new ArrayList<ProjectMetadata>();
        if (baseDir.exists() == false) {
            return Collections.emptyList();
        }

        File[] listFiles = baseDir.listFiles(DirectoryFileFilter.FILE_FILTER);
        for (File f : listFiles) {
            if (isProjectFolder(f)) {
                list.add(openProject(f));
            } else if (f.isDirectory()) {
                list.addAll(listProjects(f));
            }
        }

        return list;
    }

    public static boolean isProjectFolder(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            File file = new File(dir, metadataDirName);
            return file.exists();
        }
        return false;
    }

    private static Properties openPropertiesFile(File rootFolder) throws IOException {
        File metadataFile = new File(rootFolder, metadataDirName + "/" + metadataFileName);

        Properties prop = new Properties();
        prop.load(new InputStreamReader(new FileInputStream(metadataFile), "utf-8"));

        return prop;
    }

    public static void unloadCurrentProject() {
        Main instance = Main.getInstance();
        instance.setProjectInfo(null);
        MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();

        DataManagement.getGeoPointTableModel().clear();
        mainPolygonal.clearAllSecondaryPolygonals();
        mainPolygonal.clearAll();
        LineDivision.getAllLineDivisions().clear();
        instance.refreshAll();
    }

    public static void loadProject(ProjectMetadata project) throws FileNotFoundException, IOException, VisualObjectParserException, InvalidGeodesicPointException {
        loadProject(project, null);
    }
    
    public static void loadProject(ProjectMetadata project, ProjectLoaderObserver plo) throws FileNotFoundException, IOException, VisualObjectParserException, InvalidGeodesicPointException {
        Main mainInstance = Main.getInstance();
        mainInstance.setProjectInfo(project);
        float version = project.getVersion();
        VisualObjectParser parser = VisualObjectParser.getInstance(version);
        
        File dir = new File(project.getMetadataFolder(), ProjectUtils.polygonalsDirName);
        File[] listFiles = dir.listFiles(new FileFilterExtension("properties"));
        
        if(plo != null) {
            plo.start(listFiles.length + 1);
        }

        ProjectUtils.loadPointsFile(project.getMetadataFolder(), DataManagement.getGeoPointTableModel());
        ProjectUtils.loadPointsMetadataFile(project.getMetadataFolder());
        ProjectUtils.loadPolygonals(project.getMetadataFolder(), parser);
        Exporter.prepareTemplates(project);
        mainInstance.refreshAll();
    }

    public static ProjectMetadata openProject(File rootFolder) throws IOException {
        ProjectMetadata pmdata = new ProjectMetadata(openPropertiesFile(rootFolder));
        pmdata.setRootFolder(rootFolder);

        return pmdata;
    }

    public static void loadPointsMetadataFile(File rootFolder) throws IOException {
        try {
            File pointsMetadataFile = new File(rootFolder, ProjectUtils.pointsMetadataFileName);
            Properties prop = new Properties();
            prop.load(new FileInputStream(pointsMetadataFile));
            String favoritos = prop.getProperty("favoritos");
            String sat = prop.getProperty("sat");

            DataManagement.setFavoritePoints(favoritos, true);
            DataManagement.setSatGeoPoints(sat, true);
        } catch (FileNotFoundException ex) {
        }
    }

    public static void loadPointsFile(File rootFolder, GeoPointTableModel model) throws FileNotFoundException, IOException, InvalidGeodesicPointException {
        Main main = Main.getInstance();
        ProjectMetadata projectInfo = main.getProjectInfo();
        Integer zonaUtm = projectInfo.getZonaUtm();
        Hemisphere hemisferio = projectInfo.getHemisferio();
        Datum datum = projectInfo.getDatum();

        File pointsFile = new File(rootFolder, ProjectUtils.pointsFileName);
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(pointsFile);
            CSVPointImporter pointImporter = new CSVPointImporter(model, stream, PointImporter.projectSequence, zonaUtm, hemisferio, datum);
            pointImporter.importData();
        } finally {
            stream.close();
        }
    }

    public static void loadPolygonals(File rootFolder, VisualObjectParser parser) throws FileNotFoundException, IOException, VisualObjectParserException {
        File dir = new File(rootFolder, ProjectUtils.polygonalsDirName);
        loadPolygonal(new File(dir, mainPolygonalFileName + ".properties"), parser);

        for (File file : dir.listFiles(new FileFilterExtension("properties"))) {
            if ((mainPolygonalFileName + ".properties").equalsIgnoreCase(file.getName())) {
                continue;
            }
            loadPolygonal(file, parser);
        }
    }

    private static Polygonal loadPolygonal(File file, VisualObjectParser parser) throws FileNotFoundException, IOException, VisualObjectParserException {
        Polygonal polygonal;
        MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();

        Properties prop = new Properties();
        prop.load(new InputStreamReader(new FileInputStream(file), "utf-8"));
        PolygonalMetadata polygonalMetadata = new PolygonalMetadata(prop);
        if ((mainPolygonalFileName + ".properties").equalsIgnoreCase(file.getName())) {
            polygonal = mainPolygonal;
        } else {
            polygonal = mainPolygonal.createOrGetPolygonal(polygonalMetadata.getNome());
        }
//        String nome = polygonalMetadata.getNome();
//        if (nome.contains(" - ")) {//TODO remover
//            String[] split = nome.split(" - ");
//            polygonal.setName(split[0]);
//            polygonalMetadata.setNome(split[0]);
//            polygonalMetadata.setDescricao(split[1]);
//        }
        polygonalMetadata.setFile(file);
        polygonal.setMetadata(polygonalMetadata);

        String orderPoints = file.getAbsolutePath().replace(".properties", "");
        String vo = orderPoints + ".vo";

        loadOrderPoints(polygonal, new File(orderPoints));
        loadVisualObjects(polygonal, new File(vo), parser);


        return polygonal;
    }

    private static void loadOrderPoints(Polygonal polygonal, File file) throws FileNotFoundException, IOException {
        String result = IOUtils.readEntireFile(file);
        if (!result.isEmpty()) {
            StringTokenizer st = new StringTokenizer(result, ",");

            while (st.hasMoreTokens()) {
                String token = st.nextToken();

                GeodesicPoint findPoint = DataManagement.findPoint(token);
                if (findPoint == null) {
                    polygonal.resetPerimeter();
                    break;
                }
                polygonal.addElement(findPoint);
            }
            polygonal.forceClose();
        }
    }

    private static void loadVisualObjects(Polygonal polygonal, File file, VisualObjectParser parser) throws FileNotFoundException, VisualObjectParserException {
        Collection<VisualObject> objects = parser.parse(new FileInputStream(file), polygonal.getMetadata().getEscala(), 1);

        for (VisualObject vo : objects) {
            polygonal.addToVisualObjects(vo);
        }
    }
}
