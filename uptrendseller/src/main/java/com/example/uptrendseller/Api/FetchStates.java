package com.example.uptrendseller.Api;

import android.os.AsyncTask;
import android.util.Log;

import com.example.adapteranddatamodel.StateDataListener;

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

import DataModel.StateData;

public class FetchStates extends AsyncTask<Void, Void, String> {
    private StateDataListener stateDataListener;

    public FetchStates(StateDataListener listener) {
        this.stateDataListener = listener;
    }
    @Override
    protected String doInBackground(Void... voids) {
        try {
            String API_KEY = "ODVmc25MZEFyTnI3OVZKY20yZWV4V0poMnNQUUVsamJtcXNVTkl3Ng==";
            String API_URL = "https://api.countrystatecity.in/v1/countries/IN/states";

            URL url = new URL(API_URL);
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
            Log.e("FetchStatesTask", "Error fetching states", e);
            return null;
        }

    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                ArrayList<StateData> stateDataList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject stateObject = jsonArray.getJSONObject(i);
                    String stateName = stateObject.getString("name");
                    String isoCode = stateObject.getString("iso2");

                    Log.d("StateName", stateName);
                    Log.d("ISOCode", isoCode);

                    // Add state data to the list
                    stateDataList.add(new StateData(stateName, isoCode));
                }

                // Notify the listener with the state data
                if (stateDataListener != null) {
                    stateDataListener.onStateDataReceived(stateDataList);
                }

            } catch (JSONException e) {
                Log.e("FetchStatesTask", "Error parsing JSON", e);
            }
        } else {
            Log.e("FetchStatesTask", "Failed to fetch states");
        }
    }

}
