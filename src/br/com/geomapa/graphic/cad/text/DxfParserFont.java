/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paulocanedo
 */
public class DxfParserFont {

    private Map<String, String> map = new HashMap<String, String>();
    private File file;

    public DxfParserFont(File file) {
        this.file = file;
    }

    private InputStream getStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public void parse() throws IOException {
        InputStream stream = getStream();
        StringBuilder sb = new StringBuilder();
        boolean isPolyline = false;
        boolean vertexOpened = false;
        String currentChar = "not_initialized";

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.contains("POLYLINE")) {
                isPolyline = true;
                vertexOpened = false;
            } else if (line.contains("VERTEX")) {
                vertexOpened = true;
            } else if (line.contains("SEQEND")) {
                isPolyline = false;
                vertexOpened = false;

                String get = map.get(currentChar);
                if (get == null) {
                    get = "";
                }
                map.put(currentChar, get + sb.toString() + "\n");
                sb.delete(0, sb.length());
            }

            if (line.trim().equals("8")) {
                currentChar = reader.readLine().trim();
            }

            if (line.trim().equals("10")) {
                String x = reader.readLine();
                reader.readLine(); //20
                String y = reader.readLine();

                if (isPolyline && vertexOpened) {
                    int charInt = Integer.parseInt(currentChar);
                    double xvalue = Double.parseDouble(x);
//                    double xvalue = Double.parseDouble(x) - (charInt - 33) * 10;
                    double yvalue = Double.parseDouble(y);
                    sb.append(String.format(Locale.ENGLISH, "%.2f", xvalue)).append(",").append(String.format(Locale.ENGLISH, "%.2f", yvalue)).append(" ");
                }
            }
        }
        String[] keySet = map.keySet().toArray(new String[0]);
        Arrays.sort(keySet);
        for (String key : keySet) {
            System.out.println(String.format("character=%s", key));
            System.out.println(map.get(key));
            System.out.println("endchar\n");
        }
    }

    public static void main(String... args) {
        DxfParserFont dxfParserFont = new DxfParserFont(new File("/Users/paulocanedo/Desktop/especialchars3.dxf"));
        try {
            dxfParserFont.parse();
        } catch (IOException ex) {
            Logger.getLogger(DxfParserFont.class.getName()).log(Level.SEVERE, null, ex);
        }
//        char c = 'Ã';
//        System.out.println("" + (int)c + ": " + c);
//        c = 'ã';
//        System.out.println("" + (int)c + ": " + c);
//        c = 'Ê';
//        System.out.println("" + (int)c + ": " + c);
//        c = 'Ô';
//        System.out.println("" + (int)c + ": " + c);
//        c = 'ê';
//        System.out.println("" + (int)c + ": " + c);
//        c = 'ô';
//        System.out.println("" + (int)c + ": " + c);
//        for (int i = 0x20; i <= 0x7e; i++) {
//            System.out.println("" + (int) i + ": " + (char) i);
//        }
//        
//        for (int i = 0xa1; i <= 0xff; i++) {
//            System.out.println("" + (int) i + ": " + (char) i);
//        }
    }

    private static String newLayer(String name) {
        String s = "";
        s += "LAYER\n";
        s += "  2\n";
        s += name + "\n";
        s += " 70\n";
        s += "     0\n";
        s += " 62\n";
        s += "     7\n";
        s += "  6\n";
        s += "CONTINUOUS\n";
        s += "  0";

        return s;
    }
}
