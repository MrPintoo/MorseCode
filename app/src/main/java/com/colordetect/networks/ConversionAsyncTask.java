package com.colordetect.networks;

import android.os.AsyncTask;

import com.colordetect.utilities.MorseParser;

public class ConversionAsyncTask extends AsyncTask<String, Void, String> {

    private ConversionListener conversion;

    @Override
    protected String doInBackground(String... params) {
        String searchTerm = params[0];
        String json = params[1];

        if (searchTerm != null) {
            String result = MorseParser.translate(json, searchTerm);
            return result;
        }
        return "ConversionAsyncTask: Error in resolving search term";
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
