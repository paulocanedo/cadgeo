/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.autoupdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class PC9Downloader implements Runnable {

    private List<Pc9DownloaderListener> listeners = new ArrayList<Pc9DownloaderListener>();
    private Throwable t;
    private boolean canceled = false;
    private File output;
    private URL inputUrl;

    public PC9Downloader(File output, URL input) {
        this.output = output;
        this.inputUrl = input;
    }

    public static String downloadWithSameThread(URL url) {
        InputStream inputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            long total = connection.getContentLength();

            inputStream = connection.getInputStream();

            byte[] buffer = new byte[1024 * 4];
            int readed;
            StringBuilder sb = new StringBuilder();
            while ((readed = inputStream.read(buffer)) >= 0) {
                sb.append(new String(buffer, 0, readed));
            }
            return sb.toString().trim();
        } catch (Throwable ex) {
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (Exception ex) {
            }
        }
    }
    
    public static void downloadWithSameThread(URL url, File out) throws IOException {
        InputStream inputStream = null;
        FileOutputStream ostream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            inputStream = connection.getInputStream();
            ostream = new FileOutputStream(out);

            byte[] buffer = new byte[1024 * 4];
            int readed;
            while ((readed = inputStream.read(buffer)) >= 0) {
                ostream.write(buffer, 0, readed);
            }
        }
        finally {
            try {
                inputStream.close();
            } catch (Exception ex) {
            }
            try {
                ostream.close();
            } catch (Exception ex) {
            }
            
        }
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        FileOutputStream outStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) inputUrl.openConnection();
            System.out.println("a");
            long total = connection.getContentLength();
            System.out.println("b");

            inputStream = connection.getInputStream();
            System.out.println("c");
            outStream = new FileOutputStream(output);
            System.out.println("d");

            byte[] buffer = new byte[1024 * 4];
            int readed;
            long totalDownloaded = 0;
            while (canceled == false && (readed = inputStream.read(buffer)) >= 0) {
                totalDownloaded += readed;
                fireProgressChangedListeners(totalDownloaded, total);

                outStream.write(buffer, 0, readed);
            }
            System.out.println("e");
            fireDownloadFinishedListeners(true);
        } catch (Throwable ex) {
            t = ex;
            System.out.println("erro: " + ex.getMessage() + " - " + ex.getClass().getName());
            ex.printStackTrace();
            fireDownloadFinishedListeners(false);
        } finally {
            try {
                inputStream.close();
            } catch (Exception ex) {
            }
            try {
                outStream.close();
            } catch (Exception ex) {
            }
        }
    }

    public Throwable getError() {
        return t;
    }

    public void cancel() {
        this.canceled = true;
        fireDownloadFinishedListeners(false);
    }

    private void fireProgressChangedListeners(long partial, long total) {
        for (Pc9DownloaderListener listener : listeners) {
            listener.progressChanged(partial, total, partial / (double) total * 100);
        }
    }

    private void fireDownloadFinishedListeners(boolean success) {
        for (Pc9DownloaderListener listener : listeners) {
            listener.downloadFinished(success);
        }
    }

    public void addListenner(Pc9DownloaderListener listener) {
        listeners.add(listener);
    }

    public void removeListener(Pc9DownloaderListener listener) {
        listeners.remove(listener);
    }
}
