package com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess;

import android.app.Activity;
import android.util.Log;

import com.example.smarthealth.MedicalCentreFinder.MapPageNavigation.MapClinicFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

abstract class MapExploration {

    protected MapClinicFinder mapClinicFinder;

    protected boolean bhasFieldMask = true;
    protected String fieldMask;

    protected String placeName;

    protected Double latitude;

    protected Double longitude;

    protected String key;

    protected String googlePlaceData;

    protected JSONArray jsonArray;

    MapExploration(String fieldMask, MapClinicFinder mapClinicFinder)
    {
        this.fieldMask = fieldMask;
        this.mapClinicFinder = mapClinicFinder;
    }

    public void setPlaceName(String placeName)
    {
        this.placeName = placeName;
    }

    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }

    public void setSearchKey(String key)
    {
        this.key = key;
    }


    public abstract String getRequestBody();

    public abstract String getURL();

    public abstract List<?> getExplorationResults() throws JSONException;

    public void readTheUrl() throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection httpUrlConnection = null;

        try {
            Log.d("here2", "here2");
            // Open a connection to the URL (POST request)
            URL url = new URL(this.getURL());
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            Log.d("here3", "here3");
            httpUrlConnection.setRequestMethod("POST");
            Log.d("here4", "here4");
            httpUrlConnection.setRequestProperty("Content-Type", "application/json");
            Log.d("here5", "here5");
            if (bhasFieldMask)
            {
                Log.d("here6", fieldMask);
                //"places.displayName,places.formattedAddress,places.location"
                httpUrlConnection.setRequestProperty("X-Goog-FieldMask", fieldMask);
            }
            Log.d("here7", "here7");
            httpUrlConnection.setDoOutput(true); // Allow writing to the connection

            // Write the request body (JSON body)
            OutputStream os = httpUrlConnection.getOutputStream();
            os.write(getRequestBody().getBytes());
            os.flush();
            os.close();

            // Read the response from the input stream
            inputStream = httpUrlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();
            // Log the response for debugging
            Log.d("API Response", data);

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }
        }

        googlePlaceData = data;
        Log.d("googleplacedata",googlePlaceData);
    }

    private void parseDetails() throws JSONException {
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;
        jsonObject = new JSONObject(googlePlaceData);
        jsonArray = jsonObject.getJSONArray(key);

        this.jsonArray = jsonArray;
    }

    public List<? extends Object> search() {
        try{
            readTheUrl();
            parseDetails();
            return getExplorationResults();
        }catch (IOException|JSONException e)
        {
            Log.e("Error","No file found", e);
        }
        return java.util.Collections.emptyList();
    }
}
