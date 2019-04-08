package com.myapplication;

import android.os.AsyncTask;

public class ConversionAsyncTask extends AsyncTask<String, Void, String> {

    private ConversionListener conversion;

    @Override
    protected String doInBackground(String... params) {
        String searchTerm = params[0];

        /******************************************************************
        / Get serialized version of morse.json into string format to pass /
        / as a parameter for the getMorse function.                       /
        /******************************************************************/

        if(searchTerm != null) {
            ConversionModel model = MorseParser.getMorse("morse",searchTerm);
            String response = "";
            for(int i = 0; i < model.getOutput().size(); i++)
                response += model.getOutput().get(i);
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
