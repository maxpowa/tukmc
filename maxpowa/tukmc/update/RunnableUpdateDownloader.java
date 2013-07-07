package maxpowa.tukmc.update;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class RunnableUpdateDownloader implements Runnable {
    @Override
    public void run() {
        download("http://www.download.com/random.file",
                "C:\randomlocation\random.file");
    }

    public static void download(String URL, String localStore) {
        try {
            /*
             * Get a connection to the URL and start up a buffered reader.
             */
            long startTime = System.currentTimeMillis();

            System.out.println("Connecting to " + URL + " ...");

            URL url = new URL(URL);
            url.openConnection();
            InputStream reader = url.openStream();

            /*
             * Setup a buffered file writer to write out what we read from the
             * website.
             */
            FileOutputStream writer = new FileOutputStream(localStore);
            byte[] buffer = new byte[153600];
            int totalBytesRead = 0;
            int bytesRead = 0;

            System.out
                    .println("Reading ZIP file 150KB blocks at a time. This shouldn't be required, but for scaleability");

            while ((bytesRead = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, bytesRead);
                buffer = new byte[153600];
                totalBytesRead += bytesRead;
            }

            long endTime = System.currentTimeMillis();

            System.out.println("Done. "
                    + new Double(totalBytesRead / 1024).toString()
                    + " kilobytes downloaded and saved in "
                    + new Long((endTime - startTime) / 1000).toString()
                    + " seconds.");
            writer.close();
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
