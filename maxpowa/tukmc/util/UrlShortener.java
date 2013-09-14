package maxpowa.tukmc.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class UrlShortener {


    private static final String BASE_URL = "https://www.googleapis.com/urlshortener/v1/url"; 
    // This is my Google API key, please if you insist on copying my code, get your own API key.
    private static final String apiKey = "AIzaSyDRMkSSz-_FVA4VoAUkRtJ_CxAbab4eoak";

    public static final String shorten(String someURL) {
        String response = null;
        if (!someURL.contains("http://") && !someURL.contains("https://") && !someURL.contains("ftp://")) {
            someURL = "http://"+someURL;
        }
        try {
            URL longURL = new URL(someURL);
            String postData = "{\"longUrl\": \"" + longURL.toExternalForm() + "\"}";
            final String strGooGlUrl = BASE_URL + "?key=" + apiKey;
            URL gooGlURL = new URL(strGooGlUrl);
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
            response = getShortURL(httpURLConnection);
        } catch (UnknownHostException e) {
            response = "Check your DNS servers.";
        } catch (MalformedURLException e) {
            response = "That's not a URL, silly.";
        } catch (IOException e) {
            response = "Check your internet connection.";
        }
        if (response != null && response.length() < 100) {
            return response;
        }
        return "That's not a URL, silly.";


    }

    private static String getShortURL(HttpURLConnection httpURLConnection) throws IOException {
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

    public static String expand(String shortUrl) {
        String response = null;
        if (!shortUrl.contains("goo.gl") || shortUrl.equalsIgnoreCase("http://goo.gl/") || shortUrl.equalsIgnoreCase("goo.gl") || shortUrl.equalsIgnoreCase("goo.gl/")) {
            return "blank";
        }
        try {
            String strGooGlUrl = BASE_URL + "?shortUrl=" + shortUrl;
            URL gooGlURL;
            gooGlURL = new URL(strGooGlUrl);

            HttpURLConnection httpURLConnection = (HttpURLConnection) gooGlURL.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();

            response = getLongURL(httpURLConnection);

            httpURLConnection.disconnect();

        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        }
        if (response.contains(" \"error\": {"))
            return "blank";
        return response;
    }

    private static String getLongURL(HttpURLConnection httpURLConnection) throws IOException {
        InputStream is = null;
        String longURL = null;
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
            if (line.startsWith(" \"longUrl\": \"")) {
                longURL = line.substring(13, line.length()-2);
            }
        }
        rd.close();


        httpURLConnection.disconnect();

        if (longURL != null) {
            return longURL;
        }
        return response.toString();
    }

}

