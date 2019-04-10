package com.myapplication.networks;

import android.os.AsyncTask;

import com.myapplication.utilities.MorseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TextToMorseAsyncTask extends AsyncTask<String, Void, String> {

    private ConversionListener conversion;

    @Override
    protected String doInBackground(String... params) {
        String searchTerm = params[0];
        String json = params[1];

        if (searchTerm != null) {
            String result = MorseParser.translate(json, searchTerm);
            return result;
        }
        return "Search Failed!";
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
