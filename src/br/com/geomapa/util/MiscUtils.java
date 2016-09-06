/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author paulocanedo
 */
public class MiscUtils {

    private static MessageDigest md;
    private static final String hexDigitChars = "0123456789abcdef";

    static {
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MiscUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String currentClipboardValue() {
        String clipboardValue = null;
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null)
                && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                clipboardValue = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception ex) {
                return null;
            }
        }
        return clipboardValue;
    }

    public static Date currentDateTimeFromWeb() throws IOException {
        try {
            URL url = new URL("http://www.paulocanedo.com.br/geografico/timestamp.php");
            URLConnection connection = url.openConnection();
            InputStream input = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            long timemilis = Long.parseLong(reader.readLine());
            return new Date(timemilis * 1000);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MiscUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String sha1(File file) throws FileNotFoundException, IOException {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);

            byte[] buffer = new byte[10 * 1024];
            int readed;

            md.reset();
            while ((readed = stream.read(buffer)) >= 0) {
                md.update(buffer, 0, readed);
            }

            return byteArrayToHex(md.digest());
        } finally {
            try {
                stream.close();
            } catch (Exception ex) {
            }
        }
    }

    public static String sha1(String input) {
        md.reset();
        byte[] result = md.digest(input.getBytes());

        return byteArrayToHex(result);
    }

    public static String byteArrayToHex(byte[] a) {
        int hn, ln, cx;
        StringBuilder buf = new StringBuilder(a.length * 2);
        for (cx = 0; cx < a.length; cx++) {
            hn = ((int) (a[cx]) & 0x00ff) / 16;
            ln = ((int) (a[cx]) & 0x000f);
            buf.append(hexDigitChars.charAt(hn));
            buf.append(hexDigitChars.charAt(ln));
        }
        return buf.toString().toUpperCase();
    }

    public static void extractZipFile(File input, File output) throws IOException {
        if (!output.isDirectory()) {
            throw new IOException(String.format("O caminho %s não é um diretório válido.", output));
        }

        int buffer = 1024 * 4;
        BufferedOutputStream dest = null;
        FileInputStream fis = new FileInputStream(input);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            int count;
            byte data[] = new byte[buffer];
            // write the files to the disk

            File fout = new File(output, entry.getName());
            FileOutputStream fos = new FileOutputStream(fout);
            dest = new BufferedOutputStream(fos, buffer);
            while ((count = zis.read(data, 0, buffer))
                    != -1) {
                dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
        }
        zis.close();
    }

}
