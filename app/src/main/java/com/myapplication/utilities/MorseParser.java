package com.myapplication.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static java.lang.Character.toLowerCase;


public class MorseParser {
    private static final String TAG = "MorseParser";

    public static String translate(String json, String input) {

        try {
            JSONObject morseObject = new JSONObject(json);
            JSONArray matches = morseObject.getJSONArray("symbols");
            JSONObject obj = matches.getJSONObject(0);
            String type = obj.getString("type").toString();
            if("textToMorse".equals(type)) {
                String result = getMorse(obj, input);
                return result;
            }else{
                String result = getText(obj, input);
                return result;
            }
        } catch(JSONException e){
                Log.e(TAG, "translate: error parsing JSON", e);
        }
        return "Failed!.";
    }

    public static String getMorse(JSONObject obj, String input){

        boolean isSpace = false;

        try {
            char letter;
            String inputLetters;
            String morse = "";
            for (int i = 0; i < input.length(); i++) {
                letter = toLowerCase(input.charAt(i));
                inputLetters = Character.toString(letter);
                if(letter == ' ' ) {
                    morse += obj.getString(inputLetters);
                    isSpace = true;
                }
                else {
                    if(isSpace) {
                        morse += obj.getString(inputLetters);
                        isSpace = false;
                    } else
                    morse += " " + obj.getString(inputLetters);
                }
            }
            return morse;
        } catch (JSONException e) {
            Log.e(TAG, "getMorse: error parsing JSON");
        }
        return "Failed!";
    }

    public static String getText(JSONObject obj, String input) {

        try {
            char letter;
            String MorseChar = "";
            String result = "";
            for(int i = 0; i < input.length(); i++) {
                letter = input.charAt(i);
                if(letter == ' ' || letter == '/' || letter == '\n') {
                    if(MorseChar != "")
                        result += obj.getString(MorseChar);
                    MorseChar = "";
                    if(letter == '/')
                        result += obj.getString(Character.toString(letter));

                }
                else {
                    MorseChar += Character.toString(letter);
                }
                if(i + 1 == input.length())
                    result += obj.getString(MorseChar);
            }
            return result;
        } catch (JSONException e) {
            Log.e(TAG, "getText: error parsing JSON");
        }
        return "Failed!";
    }

}