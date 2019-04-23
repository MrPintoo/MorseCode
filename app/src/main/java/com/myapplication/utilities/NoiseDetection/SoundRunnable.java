package com.myapplication.utilities.NoiseDetection;

import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.ToTextActivity;

public class SoundRunnable implements Runnable {

    private static final int POLL_INTERVAL = 100;
    private boolean mRunning = false;
    private int mThreshold;

    /* References to view elements */
    private TextView mStatusView,tv_noice;
    ProgressBar bar;

    /* sound data source */
    private DetectNoise mSensor = new DetectNoise();
    private Timer timer = new Timer();
    private PowerManager.WakeLock mWakeLock;
    private Handler mHandler = new Handler();
    PowerManager pm;


    public SoundRunnable(PowerManager powerManager, TextView status, TextView db, ProgressBar progressBar) {
        pm = powerManager;
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert");
        mStatusView = status;
        tv_noice = db;
        bar = progressBar;
    }


    public void run() {
        timer.startTime();
        initializeApplicationConstants();
        start();
    }

    // Create runnable thread to Monitor Voice
    public Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            updateDisplay("Monitoring Voice...", amp);
            amp = 0;
            if(amp > mThreshold)
                timer.startTime();
            while (amp > mThreshold) {
                callForHelp(amp);
                amp = mSensor.getAmplitude();
            }
//            timer.pause();
            timer.reset();
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };


    private void start() {
        mSensor.start();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }


    private void initializeApplicationConstants() {
        // Set Noise Threshold
        mThreshold = -5;
    }


    private void updateDisplay(String status, double signalEMA) {
        mStatusView.setText(status);
        bar.setProgress((int)signalEMA);
//        Log.d("SOUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA+"dB");
    }


    private void callForHelp(double signalEMA) {
//        timer.startTime();
//        while(signalEMA > mThreshold) {
//            signalEMA = mSensor.getAmplitude();
//        }
//        timer.pause();
//        Log.d("MILLISECONDS", String.valueOf(timer.getMilliSeconds()));
//        Log.d("SECONDS", String.valueOf(timer.getSeconds()));
//        Log.d("MINUTES", String.valueOf(timer.getMinutes()));
//        timer.reset();

        Toast.makeText(ToTextActivity.getContext(), "Noise Thresold Crossed, do your stuff here.", Toast.LENGTH_LONG).show();
        Log.d("SOUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA+"dB");
    }


//        public void onResume() {
//        initializeApplicationConstants();
//        if (!mRunning) {
//            mRunning = true;
//            start();
//        }
//        try {
//            mPollTask.wait();
//        } catch(InterruptedException e) {
//            e.getStackTrace();
//        }
//    }

//        private void stop() {
//        Log.d("Noise", "==== Stop Noise Monitoring===");
//        if (mWakeLock.isHeld()) {
//            mWakeLock.release();
//        }
//        mHandler.removeCallbacks(mPollTask);
//        mSensor.stop();
//        bar.setProgress(0);
//        updateDisplay("stopped...", 0.0);
//        mRunning = false;
//    }

}
