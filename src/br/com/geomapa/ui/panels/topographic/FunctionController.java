/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels.topographic;

import java.util.HashMap;

import static br.com.geomapa.ui.panels.topographic.Function.*;

/**
 *
 * @author paulocanedo
 */
public class FunctionController {

    private static final HashMap<String, Function> functionNames = new HashMap<String, Function>();
    private static final HashMap<String, String> aliases = new HashMap<String, String>();

    static {
        for(Function f :Function.values()) {
            functionNames.put(f.name().toLowerCase(), f);
        }

        aliases.put("z", "zoom");
        
        aliases.put("detectar_perimetro", "detect_perimeter");
        aliases.put("dperimetro", "detect_perimeter");
        
        aliases.put("define_poligonal", "define_perimeter");
        aliases.put("dpoligonal", "define_perimeter");
        
        aliases.put("limpar_poligonal", "reset_perimeter");
        aliases.put("lpoligonal", "reset_perimeter");
        
        aliases.put("e", "erase");
        aliases.put("estrada", "road");
        
        aliases.put("azimute_distancia", "table_azimuth_distance");
        aliases.put("area_perimetro", "table_area_perimeter");
        aliases.put("label_coord", "geodesic_point_label_coord");

        aliases.put("m", "move");
        aliases.put("mover", "move");
    }

    public static Function getFunction(String alias) {
        String functionName = aliases.get(alias);
        if (functionName == null) {
            functionName = alias;
        }
        Function get = functionNames.get(functionName.toLowerCase());
        return get == null ? SELECT : get;
    }
}
