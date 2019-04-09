package com.myapplication;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

public class ConversionAsyncTask extends AsyncTask<String, Void, String> {

    private ConversionListener conversion;

    @Override
    protected String doInBackground(String... params) {
        String searchTerm = params[0];

        try {
            String json = API.httpCall();
            if(searchTerm != null) {
                String result = MorseParser.getMorse(json, searchTerm);
                return result;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return "Search Failed";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        conversion.onConversionCallback(result);
    }

    public interface ConversionListener {
        void onConversionCallback(String response);
    }

    public void setConversionListener(ConversionListener listener) {
        this.conversion = listener;
    }

}
