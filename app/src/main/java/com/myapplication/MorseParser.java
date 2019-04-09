package com.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Character.toLowerCase;


public class MorseParser {
    private static final String TAG = "MorseParser";

    public static String getMorse(String json, String input) {

        char letter;
        String inputLetters;
        String morse = "";
        try {
            JSONObject morseObject = new JSONObject(json);
            JSONArray matches = morseObject.getJSONArray("symbols");
            JSONObject obj = matches.getJSONObject(0);
            for (int i = 0; i < input.length(); i++) {
                letter = toLowerCase(input.charAt(i));
                inputLetters = Character.toString(letter);
                morse += " " + obj.getString(inputLetters);
            }
            return morse;
        } catch(JSONException e){
                Log.e(TAG, "getMorse: error parsing JSON", e);
        }
        return " ";
    }
}