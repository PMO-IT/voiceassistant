package de.pmoit.voiceassistant.utils.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Class for handling JSOn Requestresult.
 *
 */
public class JSONService {
    public String getJSONRequestResult(String jsonUrl) {
        try {
            URL url = new URL(jsonUrl);
            HttpURLConnection conn = ( HttpURLConnection ) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                return "Fehler : HTTP error code : " + conn.getResponseCode();
            }
            String output = readJsonStream(conn);
            conn.disconnect();
            return output;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Fehler, keine Antwort erhalten";
    }

    private String readJsonStream(HttpURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader( ( conn.getInputStream() )));
        String output = "";
        String readLineResult;
        while ( ( readLineResult = br.readLine() ) != null) {
            output += readLineResult;
        }
        return output;
    }
}
