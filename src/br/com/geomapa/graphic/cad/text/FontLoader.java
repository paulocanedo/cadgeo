/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author paulocanedo
 */
public class FontLoader {

    private final static String characterVarName = "character=";
    private final String resource = "/br/com/geomapa/resources/templates/txt.font";

    public InputStream getStream() {
        return getClass().getResourceAsStream(resource);
    }

    public void loadVertex() throws IOException {
        InputStream stream = getStream();
        double fontWidth = 0d;
        char currentChar = 1;
        Float lastX = null, lastY = null;

        List<Float> buffer = new ArrayList<Float>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("SPACE") || line.startsWith("endchar")) { //TODO remover
                fontWidth = 10;
                continue;
            }

            if (line.startsWith(characterVarName)) {
                if (!buffer.isEmpty() && currentChar != 1) { //have a buffer to commit
                    saveBuffer(currentChar, buffer);
                }

                lastX = lastY = null;
                buffer.clear();
                currentChar = (char) Integer.parseInt(line.substring(characterVarName.length()));

                continue;
            }

            StringTokenizer st = new StringTokenizer(line);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                String[] split = token.split(",");
                float x = Float.parseFloat(split[0]);
                float y = Float.parseFloat(split[1]);
                fontWidth = Math.max(fontWidth, x);

                if (lastX != null && lastY != null) {
                    buffer.add(lastX);
                    buffer.add(lastY);
                    buffer.add(x);
                    buffer.add(y);
                }
                lastX = x;
                lastY = y;
            }
            lastX = lastY = null;
        }
        saveBuffer(currentChar, buffer);
    }

    private void saveBuffer(Character currentChar, Collection<Float> buffer) {
        FloatBuffer fbuffer = FloatBuffer.allocate(buffer.size());
        for (Float f : buffer) {
            fbuffer.put(f);
        }
        charVertex.put(currentChar, fbuffer);
    }
    
    public static Map<Character, FloatBuffer> reloadCharVertex() throws IOException {
        FontLoader fontLoader = new FontLoader();
        fontLoader.loadVertex();
        return fontLoader.charVertex;
    }
    
    private HashMap<Character, FloatBuffer> charVertex = new HashMap<Character, FloatBuffer>();
}
