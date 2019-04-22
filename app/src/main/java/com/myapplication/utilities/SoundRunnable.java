package com.myapplication.utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.DetectNoise;
import com.myapplication.ToTextActivity;

public class SoundRunnable implements Runnable {

    /* constants */
    private static final int POLL_INTERVAL = 300;

    /** running state **/
    private boolean mRunning = false;

    /** config state **/
    private int mThreshold;

    int RECORD_AUDIO = 0;

    private Handler mHandler = new Handler();

    /* References to view elements */
    private TextView mStatusView,tv_noice;
    private Button listen;

    /* sound data source */
    private DetectNoise mSensor = new DetectNoise();
    ;
    ProgressBar bar;

    PowerManager pm;
    private PowerManager.WakeLock mWakeLock;// = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert");


    public SoundRunnable(PowerManager powerManager, TextView status, TextView db, ProgressBar progressBar) {
        pm = powerManager;
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert");
        mStatusView = status;
        tv_noice = db;
        bar = progressBar;
    }


//    public Runnable mSleepTask = new Runnable() {
        public void run() {
            //Log.i("Noise", "runnable mSleepTask");

            start();
        }
//    };

    // Create runnable thread to Monitor Voice
    public Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            //Log.i("Noise", "runnable mPollTask");
            updateDisplay("Monitoring Voice...", amp);

            if ((amp > mThreshold)) {
                callForHelp(amp);
                //Log.i("Noise", "==== onCreate ===");
            }
            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };

//    @Override
    public void onResume() {
//        super.onResume();
        onResume();
        //Log.i("Noise", "==== onResume ===");

        initializeApplicationConstants();
        if (!mRunning) {
            mRunning = true;
            start();
        }
    }
//    @Override
    public void onStop() {
//        super.onStop();
        onStop();
        // Log.i("Noise", "==== onStop ===");
        //Stop noise monitoring
        stop();
    }
    private void start() {
        //Log.i("Noise", "==== start ===");
        if (ActivityCompat.checkSelfPermission(ToTextActivity.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ToTextActivity.getContext(), new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
        }
        mSensor.start();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }
    private void stop() {
        Log.d("Noise", "==== Stop Noise Monitoring===");
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
//        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        bar.setProgress(0);
        updateDisplay("stopped...", 0.0);
        mRunning = false;

    }


    private void initializeApplicationConstants() {
        // Set Noise Threshold
        mThreshold = 1;

    }

    private void updateDisplay(String status, double signalEMA) {
        mStatusView.setText(status);
        //
        bar.setProgress((int)signalEMA);
        Log.d("SOUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA+"dB");
    }


    private void callForHelp(double signalEMA) {

//        stop();

        // Show alert when noise thersold crossed

        Toast.makeText(ToTextActivity.getContext(), "Noise Thresold Crossed, do your stuff here.", Toast.LENGTH_LONG).show();
        Log.d("SOUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA+"dB");
    }
}
