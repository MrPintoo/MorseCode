package com.myapplication.utilities;

import android.content.Context;
import android.os.Vibrator;

import java.util.ArrayList;

public class Vibration {

    private static Vibrator vibrator;

    public static void vibrate(Context context, String morse) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] textToVibrationPattern = new long[2*morse.length()+1];
        textToVibrationPattern[0] = 0;

            for(int i = 0; i < morse.length(); i++) {
                if(morse.charAt(i) == '.') {
                    textToVibrationPattern[i * 2 + 1] = 100; // dit: 1 unit
                    textToVibrationPattern[i * 2 + 2] = 100;
                }
                else if(morse.charAt(i) == '-') {
                    textToVibrationPattern[i * 2 + 1] = 300; //dah: 3 units
                    textToVibrationPattern[i * 2 + 2]= 100;
                }
                else if(morse.charAt(i) == ' ') {
                    textToVibrationPattern[i * 2 + 1] = 0;
                    textToVibrationPattern[i * 2 + 2] = 300; //inter-character space = 3 unit
                }
                else {//if(morse.charAt(i) == '/')
                    textToVibrationPattern[i * 2 + 1] = 0;
                    textToVibrationPattern[i * 2 + 2] = 700; //word character space
                }

            }

        vibrator.vibrate(textToVibrationPattern, -1);
    }


    public static void shortVibration() { vibrator.vibrate(50); }
    public static void longVibration() { vibrator.vibrate(2000); }
    public static void pauseVibration() { vibrator.vibrate(0); }

}
