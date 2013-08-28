package maxpowa.tukmc.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class UrlShortener {

    private static final String BASE_URL = "https://www.googleapis.com/urlshortener/v1/url";
    private static final String apiKey = "AIzaSyDRMkSSz-_FVA4VoAUkRtJ_CxAbab4eoak"; 

    public static final String shorten(String someURL) {
        String response = null;
        if (!someURL.contains("http://") && !someURL.contains("https://") && !someURL.contains("ftp://")) {
            someURL = "http://"+someURL;
        }
        try {
            URL longURL = new URL(someURL);
            String postData = "{\"longUrl\": \"" + longURL.toExternalForm() + "\"}";
            System.out.println("shorten() postData=" + postData);
            final String strGooGlUrl = BASE_URL + "?key=" + apiKey;
            URL gooGlURL = new URL(strGooGlUrl);
            System.out.println("shorten() gooGlURL=" + gooGlURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) gooGlURL.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.addRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Content-Length", "" + Integer.toString(postData.getBytes().length));
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            // Send request
            DataOutputStream wr = new DataOutputStream(
                    httpURLConnection.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();

            // Get response
            response = getResponse(httpURLConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response != null && response.length() < 100) {
            return response;
        }
        return "An error occurred.";

    }
    
    private static String getResponse(HttpURLConnection httpURLConnection) throws IOException {
            InputStream is = null;
            String shortURL = null;
            if (httpURLConnection.getResponseCode() == 200) {
              is = httpURLConnection.getInputStream();
            } else {
              is = httpURLConnection.getErrorStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
              response.append(line);
              response.append('\n');
              if (line.startsWith(" \"id\": \"")) {
                  shortURL = line.substring(8, line.length()-2);
              }
            }
            rd.close();

            httpURLConnection.disconnect();
            
            if (shortURL != null) {
                return shortURL;
            }
            return response.toString();
          }

}
