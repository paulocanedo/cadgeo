/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geografico.ztest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

/**
 *
 * @author paulocanedo
 */
public class CreateRandomPointCSV {
    
    private static int numeroPontos = 10000;
    
    public static void main(String... args) throws IOException {
        File file = new File("/Users/paulocanedo/Desktop/random.csv");
        FileWriter writer = new FileWriter(file);
        
        double minE = 679000;
        double sizeE = 200000;
        double minN = 8950000;
        double sizeN = 150000;
        double minH = 0;
        double sizeH = 500;
        
        Random random = new Random();
        for(int i=0; i<numeroPontos; i++) {
            double e = random.nextDouble() * sizeE + minE;
            double n = random.nextDouble() * sizeN + minN;
            double h = random.nextDouble() * sizeH + minH;
            double qx = random.nextDouble();
            double qy = random.nextDouble();
            double qz = random.nextDouble();
            
            String line = String.format(Locale.ENGLISH, "ABC-M-%4d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f", i, e, qx, n, qy, h, qz).replace(" ", "0");
            writer.write(line + "\n");
        }
        
        writer.flush();
        writer.close();
    }
    
}
