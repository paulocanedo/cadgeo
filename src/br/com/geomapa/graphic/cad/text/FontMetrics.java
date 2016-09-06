/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.text;

import br.com.geomapa.main.Bus;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author paulocanedo
 */
public class FontMetrics {

    private static Map<Character, Float> map = new HashMap<Character, Float>();

    public static void setFontWidth(Character c, Float width) {
        map.put(c, width);
    }

    public static Float getFontWidth(Character c) {
        return map.get(c);
    }

    public static Float getScaledFontWidth(Character c) {
        return getFontWidth(c) * Bus.getScale();
    }

    public static Float getStringWidth(String text) {
        if (text == null || text.isEmpty()) {
            return 0f;
        }
        
        Float sum = 0f;
        for (char c : text.toCharArray()) {
            Float fontWidth = getFontWidth(c);
            sum += Math.max(0.8f, fontWidth == null ? 1f : fontWidth);
        }
        return sum;
    }

    public static Float getScaledStringWidth(String text) {
        return getStringWidth(text) * Bus.getScale();
    }
}
