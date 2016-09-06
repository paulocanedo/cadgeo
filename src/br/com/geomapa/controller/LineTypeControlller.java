/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller;

import br.com.geomapa.graphic.cad.linetype.LineType;
import br.com.geomapa.graphic.cad.spec.AbstractVisualObject;

/**
 *
 * @author paulocanedo
 */
public final class LineTypeControlller {

    public static LineType getLineTypeInstance(String name) {
        if (name.equalsIgnoreCase("by_layer")) {
            return null;
        } else if (name.equalsIgnoreCase("dashed")) {
            return AbstractVisualObject.DASHED_LINE_TYPE;
        }
        return AbstractVisualObject.CONTINUOUS_LINE_TYPE;
    }
}
