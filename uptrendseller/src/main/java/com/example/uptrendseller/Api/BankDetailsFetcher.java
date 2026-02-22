package com.example.uptrendseller.Api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BankDetailsFetcher extends AsyncTask<String, Void, String> {

    private static final String API_URL = "https://ifsc.razorpay.com/";

    private OnBankDetailsFetchedListener listener;

    public BankDetailsFetcher(OnBankDetailsFetchedListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String ifscCode = params[0];
        String apiUrl = API_URL + ifscCode;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            Log.e("BankDetailsFetcher", "Error fetching bank details", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String bankName = jsonObject.optString("BANK");
                String branch=jsonObject.optString("BRANCH");
                String address=jsonObject.optString("ADDRESS");
                String contact=jsonObject.optString("CONTACT");
                String city=jsonObject.optString("CITY");
                String state=jsonObject.optString("STATE");



                if (bankName.isEmpty()) {
                    listener.onFailure("Bank details not found");
                } else {
                    listener.onSuccess(bankName,branch,address,contact,city,state);
                }
            } catch (JSONException e) {
                Log.e("BankDetailsFetcher", "Error parsing JSON", e);
                listener.onFailure("Error parsing JSON");
            }
        } else {
            listener.onFailure("Error fetching bank details");
        }
    }

    public interface OnBankDetailsFetchedListener {
        void onSuccess(String bankName,String branch,String address,String contact,String city,String  state);
        void onFailure(String errorMessage);
    }
}
