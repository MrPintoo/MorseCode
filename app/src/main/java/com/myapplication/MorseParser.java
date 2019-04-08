package com.myapplication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MorseParser {
    private static final String TAG = "MorseParser";

    public static ConversionModel getMorse(String json, String input) {

        ConversionModel model = new ConversionModel();
        List<String> morseText = new ArrayList<String>();

        char letter;
        String morse;
        try {
            JSONObject morseObject = new JSONObject(json);
            for (int i = 0; i < input.length(); i++) {
                letter = input.charAt(i);
                morse = Character.toString(letter);
                morse = morseObject.getString(morse);

                morseText.add(morseObject.getString(morse));
            }
        } catch(JSONException e){
                Log.e(TAG, "getMorse: error parsing JSON", e);
        }
        model.setOutput(morseText);
        return model;
    }
}

//    private static final String FILE = "/Users/student/Desktop/Repositories/MorseCode/app/src/main/java/com/myapplication/test.json";
//    private static final String FILE = "C:\\Users\\student\\Desktop\\morse.json";
//
//    JsonParser jsonParser = new JsonParser();
//    File file = new File(FILE);
//
//
//    @SuppressWarnings("unchecked")
//    public static String get_json() {
//        JsonParser parser = new JsonParser();
//        String output = "c";
//
//        try {
//            Object obj = parser.parse(new FileReader("morse.json"));
//            JSONObject jsonObject = (JSONObject) obj;
//                JsonObject obj = (JsonObject) o;
//            public static String responding() {
//                try {
//                    String response = (String) jsonObject.get("a");
//                    return response;
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
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
//
//        } catch (IOException e) {
//           e.printStackTrace();
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return output;
//    }
//
//
//


