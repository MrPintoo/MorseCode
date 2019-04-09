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
    private static final String baseApiUrl = "http://myjson.com/18ukd0";

    public static String httpCall(String input) throws IOException {

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder builder = HttpUrl.parse(baseApiUrl).newBuilder();

        builder.addQueryParameter("q",input);

        String url = builder.build().toString();

        Request request = new Request().url(url).build();

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

//         given
//        HttpGet request = new HttpGet("https://api.myjson.com/bins/18ukd0");
//        request.add("Accept", "application/json");
//        request.add("Accept-Language", "en-US");
//        request.add("Accept-Charset", "US-ASCII");
//
//
//        // when
//        HttpResponse response = HttpClientBuilder.create().build().execute(request);
//
//        // then
//        HttpEntity entity = response.getEntity();
//        String jsonString = EntityUtils.toString(entity);
//
//        // and if the response is
//        // {
//        //     "status": "OK"
//        // }
//        // Then we can assert it with
//        assertThat(jsonString, hasJsonPath("$.status", is("OK")));
    }
}