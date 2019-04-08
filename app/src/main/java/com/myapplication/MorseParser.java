package com.myapplication;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.*;



public class MorseParser {
//    private static final String FILE = "/Users/student/Desktop/Repositories/MorseCode/app/src/main/java/com/myapplication/test.json";
//    private static final String FILE = "C:\\Users\\student\\Desktop\\morse.json";

    //JsonParser jsonParser = new JsonParser();
    //File file = new File(FILE);


//    @SuppressWarnings("unchecked")
    public static String get_json() {
        JsonParser parser = new JsonParser();
        String output = "c";

        try {
            Object obj = parser.parse(new FileReader("morse.json"));
            JSONObject jsonObject = (JSONObject) obj;
//                JsonObject obj = (JsonObject) o;
            public static String responding() {
                try {
                    String response = (String) jsonObject.get("a");
                    return response;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
//            Object obj = parser.parse(new FileReader(("morse.json")));
//            output = "e";
//            JSONObject jsonObject = (JSONObject) obj;
//            try {
//                output = (String) jsonObject.get("a");
//                output = "d";
//            }
//            catch (JSONException ignored){
//                ignored.printStackTrace();
//            }
//            return output;

        } catch (IOException e) {
           e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return output;
    }


    public static String getMorse(String json) {
        for (int i = 0; i < json.length(); i++)
            json.charAt(i);
        return " ";
    }
}

