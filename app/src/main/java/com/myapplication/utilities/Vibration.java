package com.myapplication.utilities;

import android.content.Context;
import android.os.Vibrator;

public class Vibration {

    private static Vibrator vibrator;

    public static void vibrate(Context context, String morse) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        for(int i = 0; i < morse.length(); i++) {
            if(morse.charAt(i) == '.')
                shortVibration();
            else if(morse.charAt(i) == '-')
                longVibration();
            else
                pauseVibration();
        }
    }

    public static void shortVibration() { vibrator.vibrate(50); }

    public static void longVibration() { vibrator.vibrate(500); }

    public static void pauseVibration() { vibrator.vibrate(0); }

}
