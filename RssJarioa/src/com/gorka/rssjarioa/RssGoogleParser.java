package com.gorka.rssjarioa;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class RssGoogleParser {
    private final static String TAG = "RssGoogleParse";

    public void parseJson(){
        JSONObject json = null;
        try{
            String feed = "http://larrabetzutik.org/feed/";
            URL url = new URL("https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&q="+feed);
            URLConnection connection = url.openConnection();

            Log.e(TAG, connection.toString());
            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }
            json = new JSONObject(builder.toString());
        }catch (MalformedURLException e){
            Log.e(TAG, e.toString());
        }catch (IOException e){
            Log.e(TAG, e.toString());
        }catch (JSONException e){
            Log.e(TAG, e.toString());
        }
        try {
            JSONArray entries = json.getJSONObject("responseData").getJSONObject("feed").getJSONArray("entries");
            for (int i = 0; i < entries.length(); i++) {
                JSONObject c = entries.getJSONObject(i);

                Log.e(TAG, c.getString("title"));
                Log.e(TAG, c.getString("link"));
                Log.e(TAG, c.getString("publishedDate"));
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

}
