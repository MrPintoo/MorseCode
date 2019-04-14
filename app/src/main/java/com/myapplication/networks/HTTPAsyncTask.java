package com.myapplication.networks;

import android.os.AsyncTask;

import com.myapplication.models.ConversionModel;

import java.io.IOException;


public class HTTPAsyncTask extends AsyncTask<String, Void, ConversionModel> {

    private ConversionModel model = new ConversionModel();
    private HTTPListener http;

    @Override
    protected ConversionModel doInBackground(String... params) {

        String textToMorse = params[0];
        String morseToText = params[1];

        try {
            textToMorse = API.httpCall(textToMorse);
            morseToText = API.httpCall(morseToText);
            model.setTextToMorseURL(textToMorse);
            model.setMorseToTextURL(morseToText);
            return model;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ConversionModel result) {
        super.onPostExecute(result);
        http.onHTTPCallback(result);
    }

    public interface HTTPListener {
        void onHTTPCallback(ConversionModel response);
    }

    public void setHTTPListener(HTTPListener listener) { this.http = listener; }

}
