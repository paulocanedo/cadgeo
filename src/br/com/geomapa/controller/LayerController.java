/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller;

import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.graphic.CustomLayer;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.SelectionLayer;
import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;
import java.awt.Color;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author paulocanedo
 */
public final class LayerController {

    private static final Collection<Layer> layers = new TreeSet<Layer>();
    private static Layer currentLayer;
    private static Color currentColor;
    private static LineType currentLineType;
    public static final Layer DEFAULT_LAYER = new CustomLayer("0", "default layer", Color.WHITE);
    private static final Layer BORDER = new CustomLayer("Divisas", "Divisa entre parcelas", Color.WHITE);
    private static final Layer BORDER_NAME = new CustomLayer("Confrontantes", "Nomes de confrontantes e linhas de marcação", Color.WHITE, AbstractVisualObject.DASHED_LINE_TYPE);
    private static final Layer ROAD = new CustomLayer("Estradas", "Estrada", Color.YELLOW);
    private static final Layer WATER = new CustomLayer("Curso_DAgua", "Hidrografia", Color.BLUE);
    private static final Layer POINT_M = new CustomLayer("Vertices_Tipo_M", "Ponto tipo Marco", Color.RED);
    private static final Layer POINT_P = new CustomLayer("Vertices_Tipo_P", "Ponto comum", Color.GRAY);
    private static final Layer POINT_O = new CustomLayer("Vertices_Tipo_O", "Ponto tipo Offset", Color.WHITE);
    private static final Layer POINT_V = new CustomLayer("Vertices_Tipo_V", "Ponto tipo Virtual", Color.GRAY);
    private static final Layer POINT_X = new CustomLayer("Ponto", "Ponto", Color.GRAY);
    private static final Layer POINT_M_TEXT = new CustomLayer("Vertices_Tipo_M_Texto", "Ponto tipo Marco", Color.WHITE);
    private static final Layer POINT_P_TEXT = new CustomLayer("Vertices_Tipo_P_Texto", "Ponto comum", Color.GRAY);
    private static final Layer POINT_O_TEXT = new CustomLayer("Vertices_Tipo_O_Texto", "Ponto tipo Offset", Color.WHITE);
    private static final Layer POINT_V_TEXT = new CustomLayer("Vertices_Tipo_V_Texto", "Ponto tipo Virtual", Color.GRAY);
    private static final Layer POINT_X_TEXT = new CustomLayer("Ponto", "Ponto", Color.GRAY);
    private static final Layer LEGEND = new CustomLayer("Legenda", "Legenda", Color.MAGENTA);
    private static final Layer GRID = new CustomLayer("Grid", "Grid", Color.GREEN);
    private static final Layer CARTOGRAPHIC_INFO = new CustomLayer("Informacoes_Cartograficas", "Informações cartográficas", Color.GREEN);
    public static final Layer SELECTION_LAYER = new SelectionLayer();
    private static final Collection<Layer> predefinedLayers = new ArrayList<Layer>();
    private static DefaultComboBoxModel model = new DefaultComboBoxModel();

    static {
        setCurrentLayer(DEFAULT_LAYER);
        predefinedLayers.addAll(Arrays.asList(
                new Layer[]{
                    DEFAULT_LAYER, BORDER, BORDER_NAME, ROAD, WATER,
                    POINT_M, POINT_P, POINT_O, POINT_V, POINT_X,
                    POINT_M_TEXT, POINT_P_TEXT, POINT_O_TEXT, POINT_V_TEXT, POINT_X_TEXT,
                    LEGEND, GRID, CARTOGRAPHIC_INFO
                }));
        addAll(predefinedLayers);
    }

    public static ComboBoxModel getModel() {
        return model;
    }

    public static void resetToDefaultLayer() {
        setCurrentLayer(DEFAULT_LAYER);
    }

    public static void setCurrentLayer(Layer layer) {
        currentLayer = layer;
    }

    public static Layer getCurrentLayer() {
        return currentLayer;
    }
    
    public static void setCurrentColor(Color color) {
        currentColor = color;
    }
    
    public static Color getCurrentColor() {
        return currentColor;
    }
    
    public static void setCurrentLineType(LineType lineType) {
        currentLineType = lineType;
    }
    
    public static LineType getCurrentLineType() {
        return currentLineType;
    }

    public static void restoreCurrentLayer() {
        model.setSelectedItem(currentLayer);
    }

    private LayerController() {
    }

    public static void addAll(Collection<Layer> layer) {
        for (Layer l : layer) {
            add(l);
        }

    }

    public static void add(Layer layer) {
        if (layer.getName().toLowerCase().startsWith("BA7")) {
            throw new RuntimeException("crazy layer?");
        }

        if (layers.add(layer)) {
            model.removeAllElements();

            for (Layer l : layers) {
                model.addElement(l);
            }
        }
    }

    public static void remove(Layer layer) {
        if (layers.remove(layer)) {
            model.removeElement(layer);
        }
    }

    public static Layer find(String name) {
        for (Layer l : layers) {
            if (l.getName().equalsIgnoreCase(name)) {
                return l;
            }
        }
        for (Layer l : predefinedLayers) {
            if (l.getName().equalsIgnoreCase(name)) {
                add(l);
                return l;
            }
        }
        return null;
    }

    public static Layer createOrGet(String name) {
        return createOrGet(name, false);
    }

    public static Layer createOrGet(String name, boolean polygonal) {
        Layer found = find(name);
        if (found != null) {
            return found;
        }

        Color color;
        if (polygonal) {
            color = random_colors[++color_control];
        } else {
            color = Color.WHITE;
        }

        if (color_control == random_colors.length - 1) {
            color_control = -1;

        }

        CustomLayer l = new CustomLayer(name, "", color);
        l.setDxfVisible(false);
        add(l);
        return l;
    }

    public static Layer getLayerByPointType(GeodesicPointType type) {
        switch (type) {
            case M:
                return (LayerController.POINT_M);
            case O:
                return (LayerController.POINT_O);
            case P:
                return (LayerController.POINT_P);
            case V:
                return (LayerController.POINT_V);
            case X:
                return (LayerController.POINT_X);
        }
        return DEFAULT_LAYER;
    }

    public static Layer getTextLayerByPointType(GeodesicPointType type) {
        switch (type) {
            case M:
                return (LayerController.POINT_M_TEXT);
            case O:
                return (LayerController.POINT_O_TEXT);
            case P:
                return (LayerController.POINT_P_TEXT);
            case V:
                return (LayerController.POINT_V_TEXT);
            case X:
                return (LayerController.POINT_X_TEXT);
        }
        return DEFAULT_LAYER;
    }

    public static Collection<Layer> getAllLayers() {
        return Collections.EMPTY_LIST;
//        return layers;
    }
    private static int color_control = -1;
    private static final Color[] random_colors = {
        new Color(0, 191, 255),
        new Color(173, 216, 230),
        new Color(255, 255, 224),
        new Color(255, 222, 173),
        new Color(255, 248, 220)};

    public static void writeDxf(PrintStream stream) {
        stream.println("0");
        stream.println("TABLE");
        stream.println("2");
        stream.println("LAYER");
        stream.println("70");
        stream.println("2");

        for (Layer layer : layers) {
            int color = layer.isDxfVisible() ? getDxfColor(layer.getColor()) : -7;
            stream.println("0");
            stream.println("LAYER");
            stream.println("2");
            stream.println(layer.getName());
            stream.println("70");
            stream.println("0");
            stream.println("62");
            stream.println(color);
            stream.println("6");
            stream.println(layer.getLineType().getDxfName());
        }

        stream.println("0");
        stream.println("ENDTAB");
    }

    public static int getDxfColor(Color color) {
        if (Color.RED.equals(color)) {
            return 1;
        } else if (Color.YELLOW.equals(color)) {
            return 2;
        } else if (Color.GREEN.equals(color)) {
            return 3;
        } else if (Color.CYAN.equals(color)) {
            return 4;
        } else if (Color.BLUE.equals(color)) {
            return 5;
        } else if (Color.MAGENTA.equals(color)) {
            return 6;
        } else if (Color.DARK_GRAY.equals(color)) {
            return 8;
        } else if (Color.GRAY.equals(color)) {
            return 9;
        }
        return 7;
    }
}
