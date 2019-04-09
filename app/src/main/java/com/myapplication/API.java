package com.myapplication;
// request MorseCode API

import android.util.Log;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class API {

    private static final String TAG = "RecipeSearchHelper";
    private static final String baseApiUrl = "https://api.myjson.com/bins/18p7gk";

    public static String httpCall() throws IOException {

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder builder = HttpUrl.parse(baseApiUrl).newBuilder();

        String url = builder.build().toString();

        Request request = new Request.Builder().url(url).build();

        try {
            // ask the server for a response
            Response response = client.newCall(request).execute();
            if (response != null) {
                // response also contains metadata: success vs fail, travel time, etc.
                // only need the search result here
                return response.body().string();
            }
        } catch (IOException e) {
            // log the error to the console window (logcat)
            Log.e(TAG, "searchRecipes: ", e);
        }
        return null;
    }
}