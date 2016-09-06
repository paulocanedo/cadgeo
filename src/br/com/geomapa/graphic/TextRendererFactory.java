/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic;

import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author paulocanedo
 */
public class TextRendererFactory {
    
    private Map<String, TextRenderer> map;
    private static TextRendererFactory instance = new TextRendererFactory();

    private TextRendererFactory() {
        map = new HashMap<String, TextRenderer>();
    }
    
    public static TextRenderer buildTextRenderer(String font, int style, float size) {
        String key = font + style + size;
        TextRenderer get = instance.map.get(key);
        
        if(get == null) {
            get = createTextRenderer(font, style, size * 10);
            instance.map.put(key, get);
        }
        return get;
    }
    
    public static TextRenderer buildTextRenderer(float size) {
        return buildTextRenderer(Font.SERIF, Font.PLAIN, size);
    }
    
    public static TextRenderer buildTextRenderer(int style, float size) {
        return buildTextRenderer(Font.SERIF, style, size);
    }
    
    private static TextRenderer createTextRenderer(String font, int style, float size) {
        Font f = Font.decode(font).deriveFont(style, size);
        return new TextRenderer(f, true, true);
    }
    
    
    
}
