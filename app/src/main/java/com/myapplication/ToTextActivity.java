package com.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.models.ConversionModel;
import com.myapplication.networks.ConversionAsyncTask;
import com.myapplication.networks.HTTPAsyncTask;
import com.myapplication.utilities.Flashlight;
import com.myapplication.utilities.Sound;
import com.myapplication.utilities.NoiseDetection.SoundRunnable;

public class ToTextActivity extends AppCompatActivity {


    private Button toTextButton;
    private Button toSound;
    private Button buttonEnable;
    private Button imageFlashlight;
    private EditText inputToConvert;
    private TextView convertedText;

    ConversionModel model;
    Flashlight flashlight = new Flashlight();

    private static final int CAMERA_REQUEST = 50;


    private static ToTextActivity mContext;

    int RECORD_AUDIO = 0;

    private Handler mHandler = new Handler();

    /* References to view elements */
    private TextView mStatusView,tv_noice;
    private Button listen;

    /* sound data source */
    ProgressBar bar;

    SoundRunnable soundRunnable;
//    Timer timer = new Timer();



    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_text);

        mContext = this;
//        timer.startTime();

        final CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        final boolean hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        /************************************************/
        /** Retrieve text_to_morse JSON with HTTP call **/
        HTTPAsyncTask task = new HTTPAsyncTask();
        task.setHTTPListener(new HTTPAsyncTask.HTTPListener() {
            @Override
            public void onHTTPCallback(ConversionModel response) {
                model = response;
            }
        });
        task.execute(getString(R.string.textToMorseAPI), getString(R.string.morseToTextAPI));
        /************************************************/


        /*********************************************************************************/
        /**                               Conversion Process                            **/
        inputToConvert = (EditText) findViewById(R.id.input_editText);
        convertedText = (TextView) findViewById(R.id.converted_text);

        imageFlashlight = (Button) findViewById(R.id.light_btn);
        buttonEnable = (Button) findViewById(R.id.buttonEnable);


        boolean isEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;


        buttonEnable.setEnabled(!isEnabled);
        imageFlashlight.setEnabled(isEnabled);
        buttonEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(ToTextActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
            }
        });

        imageFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConversionAsyncTask task = new ConversionAsyncTask();
                task.setConversionListener(new ConversionAsyncTask.ConversionListener() {
                    @Override
                    public void onConversionCallback(String response) {
                        if (hasCameraFlash) {
                            flashlight.flash(cameraManager, response);
                        } else {
                            Toast.makeText(ToTextActivity.this, "No flash available on your device", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                model.setInput(inputToConvert.getText().toString());
                task.execute(model.getInput(), model.getMorseToTextURL());
            }
        });

        toTextButton = (Button) findViewById(R.id.toText);
        toTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversionAsyncTask task = new ConversionAsyncTask();
                task.setConversionListener(new ConversionAsyncTask.ConversionListener() {
                    @Override
                    public void onConversionCallback(String response) {
                        model.setOutput(response);
                        convertedText.setText(model.getOutput());
                    }
                });
                model.setInput(inputToConvert.getText().toString());
                task.execute(model.getInput(), model.getMorseToTextURL());
            }
        });

        toSound = (Button) findViewById(R.id.sound_btn);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.beepsound);
        final MediaPlayer noSound = MediaPlayer.create(this, R.raw.nosound);
        toSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversionAsyncTask task = new ConversionAsyncTask();
                task.setConversionListener(new ConversionAsyncTask.ConversionListener() {
                    @Override
                    public void onConversionCallback(String response) {
                        try{
                            Sound.sound(mediaPlayer, noSound, response);
                        } catch (Exception e){
                            Log.e("Sound", "onConversionCallback");
                        }
                    }
                });
                model.setInput(inputToConvert.getText().toString());
                task.execute(model.getInput(), model.getMorseToTextURL());
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
        }

        mStatusView = (TextView) findViewById(R.id.status);
        tv_noice = (TextView) findViewById(R.id.tv_noice);
        bar = (ProgressBar) findViewById(R.id.progressBar1);

        // Used to record voice
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        soundRunnable = new SoundRunnable(pm, mStatusView, tv_noice, bar);

        listen = (Button) findViewById(R.id.listen);
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                timer.pause();
//                Log.d("MILLISECONDS", String.valueOf(timer.getMilliSeconds()));
//                Log.d("SECONDS", String.valueOf(timer.getSeconds()));
//                Log.d("MINUTES", String.valueOf(timer.getMinutes()));
//                timer.reset();
//                timer.startTime();
                mHandler.postDelayed(soundRunnable.mPollTask, 50);
                soundRunnable.run();
            }
        });


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case CAMERA_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buttonEnable.setEnabled(false);
                    buttonEnable.setText("Camera Enabled!!");
                    imageFlashlight.setEnabled(true);
                } else {
                    Toast.makeText(this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public static ToTextActivity getContext() {
        return mContext;
    }




    /****************** Define runnable thread again and again detect noise *********/

//    public Runnable mSleepTask = new Runnable() {
//        public void run() {
//            //Log.i("Noise", "runnable mSleepTask");
//            start();
//        }
//    };
//
//    // Create runnable thread to Monitor Voice
//    public Runnable mPollTask = new Runnable() {
//        public void run() {
//            double amp = mSensor.getAmplitude();
//            //Log.i("Noise", "runnable mPollTask");
//            updateDisplay("Monitoring Voice...", amp);
//
//            if ((amp > mThreshold)) {
//                callForHelp(amp);
//                //Log.i("Noise", "==== onCreate ===");
//            }
//            // Runnable(mPollTask) will again execute after POLL_INTERVAL
//            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
//        }
//    };
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        //Log.i("Noise", "==== onResume ===");
//
//        initializeApplicationConstants();
//        if (!mRunning) {
//            mRunning = true;
//            start();
//        }
//    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        // Log.i("Noise", "==== onStop ===");
//        //Stop noise monitoring
//        stop();
//    }
//    private void start() {
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
//        }
//
//        //Log.i("Noise", "==== start ===");
//        mSensor.start();
//        if (!mWakeLock.isHeld()) {
//            mWakeLock.acquire();
//        }
//        //Noise monitoring start
//        // Runnable(mPollTask) will execute after POLL_INTERVAL
//        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
//    }
//    private void stop() {
//        Log.d("Noise", "==== Stop Noise Monitoring===");
//        if (mWakeLock.isHeld()) {
//            mWakeLock.release();
//        }
//        mHandler.removeCallbacks(mSleepTask);
//        mHandler.removeCallbacks(mPollTask);
//        mSensor.stop();
//        bar.setProgress(0);
//        updateDisplay("stopped...", 0.0);
//        mRunning = false;
//
//    }
//
//
//    private void initializeApplicationConstants() {
//        // Set Noise Threshold
//        mThreshold = 1;
//
//    }
//
//    private void updateDisplay(String status, double signalEMA) {
//        mStatusView.setText(status);
//        //
//        bar.setProgress((int)signalEMA);
//        Log.d("SOUND", String.valueOf(signalEMA));
//        tv_noice.setText(signalEMA+"dB");
//    }
//
//
//    private void callForHelp(double signalEMA) {
//
////        stop();
//
//        // Show alert when noise thersold crossed
//        Toast.makeText(getApplicationContext(), "Noise Thresold Crossed, do your stuff here.",
//                Toast.LENGTH_LONG).show();
//        Log.d("SOUND", String.valueOf(signalEMA));
//        tv_noice.setText(signalEMA+"dB");
//    }



}
