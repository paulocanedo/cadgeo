/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geografico.ztest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 *
 * @author paulocanedo
 */
public class RTFPlainTextExtractor {

    private final static char charDelimiter = (char) 0xA6;
    private final static String stringDelimiter = String.valueOf(charDelimiter);

    public static String getText(InputStream rtfStream) throws IOException {
        StringBuilder buffer = new StringBuilder();
        StringBuilder outTextBuffer = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(rtfStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line.replaceAll("\\\\plain", stringDelimiter));
        }

        StringTokenizer tokenizer = new StringTokenizer(buffer.toString(), stringDelimiter);
        tokenizer.nextToken(); //ignore the first one
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            token = token.replaceAll("\\{|\\}", "").replaceAll("\\\\par", "\n").replaceAll("\\\\tab", "\t");

            StringTokenizer tokenizer2 = new StringTokenizer(token, " |\n", true);
            while (tokenizer2.hasMoreTokens()) {
                String token2 = tokenizer2.nextToken();
                if(token2.startsWith("\n|\t")) {
                    outTextBuffer.append(token2);
                } else if (token2.startsWith("\\")) {
//                    System.out.println(token2);
                } else if (token2.contains("\\'")) {
                    outTextBuffer.append(token2);
                } else if (token2.contains("\\")) {
//                    System.out.println(token2);
                } else {
                    outTextBuffer.append(token2);
                }
            }
        }
        
        tokenizer = new StringTokenizer(outTextBuffer.toString(), "\n");
        outTextBuffer = new StringBuilder();
        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            
//            int whiteSpaces = 0;
//            for(int i=0; i<token.length(); i++) {
//                char charAt = token.charAt(i);
//                if(charAt == ' ') {
//                    whiteSpaces++;
//                } else {
//                    break;
//                }
//            }
//            outTextBuffer.append(token.substring(whiteSpaces)).append("\n");
            outTextBuffer.append(token.trim()).append("\n");
        }

        return outTextBuffer.toString();
    }

    private static String fixWord(String word) {
        return word;
    }

    public static void main(String... args) throws FileNotFoundException, IOException {
        long startTime = System.currentTimeMillis();

        FileOutputStream fos = new FileOutputStream("/home/paulocanedo/Desktop/rtf_test.txt");

//        String text = getText(new FileInputStream("/home/paulocanedo/Downloads/georreferenciamento/CALCULO/CADERNETA 01.rtf"));
        String text = getText(new FileInputStream("/home/paulocanedo/Downloads/georreferenciamento/leica/DADOS LOTE 02/AREA/AREA 02.RTF"));
        fos.write(text.getBytes());

        System.out.println(System.currentTimeMillis() - startTime);
    }
}