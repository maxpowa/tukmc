package maxpowa.tukmc.update;
 
import java.net.*;
import java.io.*;
 
public class UpdateDownloader
{
  public static void main(String[] args)
  {
     try
     {
        /*
         * Get a connection to the URL and start up
         * a buffered reader.
         */
        long startTime = System.currentTimeMillis();
 
        System.out.println("Connecting to Mura site...\n");
 
        URL url = new URL("http://www.getmura.com/currentversion/");
        url.openConnection();
        InputStream reader = url.openStream();
 
        /*
         * Setup a buffered file writer to write
         * out what we read from the website.
         */
        FileOutputStream writer = new FileOutputStream("C:/mura-newest.zip");
        byte[] buffer = new byte[153600];
        int totalBytesRead = 0;
        int bytesRead = 0;
 
        System.out.println("Reading ZIP file 150KB blocks at a time.\n");
 
        while ((bytesRead = reader.read(buffer)) > 0)
        {  
           writer.write(buffer, 0, bytesRead);
           buffer = new byte[153600];
           totalBytesRead += bytesRead;
        }
 
        long endTime = System.currentTimeMillis();
 
        System.out.println("Done. " + (new Integer(totalBytesRead).toString()) + " bytes read (" + (new Long(endTime - startTime).toString()) + " millseconds).\n");
        writer.close();
        reader.close();
     }
     catch (MalformedURLException e)
     {
        e.printStackTrace();
     }
     catch (IOException e)
     {
        e.printStackTrace();
     }
 
  }
 
}
