package com.example.uptrendseller.Api;

import android.os.AsyncTask;
import android.util.Log;

import com.example.adapteranddatamodel.CityDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FetchCities extends AsyncTask<String, Void, String> {
    private CityDataListener cityDataListener;
    public FetchCities(CityDataListener cityDataListener){
        this.cityDataListener=cityDataListener;
    }
    private static final String API_KEY = "ODVmc25MZEFyTnI3OVZKY20yZWV4V0poMnNQUUVsamJtcXNVTkl3Ng==";
    @Override
    protected String doInBackground(String... urls) {
        String apiUrl = urls[0];
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("X-CSCAPI-KEY", API_KEY);

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        } catch (IOException e) {
            Log.e("FetchCitiesTask", "Error fetching cities", e);
            return null;
        }
    }
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                ArrayList<String> cityData = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject stateObject = jsonArray.getJSONObject(i);
                    String cityName = stateObject.getString("name");

                    // Add state data to the list
                    cityData.add(cityName);
                }

                // Notify the listener with the state data
                if (cityDataListener != null) {
                    cityDataListener.onCityDataReceived(cityData);
                }

            } catch (JSONException e) {
                Log.e("FetchStatesTask", "Error parsing JSON", e);
            }
            // Handle the response containing city data
            Log.d("FetchCitiesTask", "Cities: " + result);
        } else {
            Log.e("FetchCitiesTask", "Failed to fetch cities");
        }
    }
}
