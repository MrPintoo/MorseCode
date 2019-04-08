package com.myapplication;

import android.os.AsyncTask;

public class ConversionAsyncTask extends AsyncTask<String, Void, String> {

    private ConversionListener conversion;

    @Override
    protected String doInBackground(String... params) {
        String searchTerm = params[0];

        if(searchTerm != null) {
            String response = MorseParser.get_json();
            return "Morse: " + response;
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
