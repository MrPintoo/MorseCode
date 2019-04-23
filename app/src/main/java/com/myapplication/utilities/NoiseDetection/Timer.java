package com.myapplication.utilities.NoiseDetection;

import android.os.Handler;
import android.os.SystemClock;

public class Timer {
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler = new Handler();
    int Seconds, Minutes, MilliSeconds ;

    public void startTime() {
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }

    public void pause() {
        TimeBuff += MillisecondTime;
        handler.removeCallbacks(runnable);
    }


    public void reset() {
        MillisecondTime = 0L;
        StartTime = 0L;
        TimeBuff = 0L;
        UpdateTime = 0L;
        Seconds = 0;
        Minutes = 0;
        MilliSeconds = 0;
    }

    public int getMinutes() { return Minutes; }
    public int getSeconds() { return Seconds; }
    public int getMilliSeconds() { return MilliSeconds; }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

//            timer.setText("" + Minutes + ":"
//                    + String.format("%02d", Seconds) + ":"
//                    + String.format("%03d", MilliSeconds));

            handler.postDelayed(this, 0);
        }

    };
}
