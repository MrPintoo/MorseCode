package com.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.models.ConversionModel;
import com.myapplication.networks.ConversionAsyncTask;
import com.myapplication.networks.HTTPAsyncTask;
import com.myapplication.utilities.Flashlight;
import com.myapplication.utilities.Sound;

import java.io.IOException;

public class ToTextActivity extends AppCompatActivity {

    private static final String TAG = "ToTextActivity";
    private static ToTextActivity mContext;

    private Button toTextButton;
    private Button toSound;
    //private Button buttonEnable;
    private Button imageFlashlight;
    private EditText inputToConvert;
    private TextView convertedText;

    ConversionModel model;
    Flashlight flashlight = new Flashlight();

    private static final int CAMERA_REQUEST = 50;
    private static final int RECORD_AUDIO = 0;


    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_text);

        mContext = this;

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
        //buttonEnable = (Button) findViewById(R.id.buttonEnable);


        final boolean isCameraEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;


        //buttonEnable.setEnabled(!isEnabled);
//        imageFlashlight.setEnabled(isEnabled);
//        buttonEnable.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityCompat.requestPermissions(ToTextActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
//            }
//        });

        imageFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: light translate");
                if (!isCameraEnabled)
                    ActivityCompat.requestPermissions(ToTextActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
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
//        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.beepsound);
//        final MediaPlayer noSound = MediaPlayer.create(this, R.raw.nosound);
        toSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToTextActivity.this, MainAudioTranslation.class);
                startActivity(intent);
//                ConversionAsyncTask task = new ConversionAsyncTask();
//                task.setConversionListener(new ConversionAsyncTask.ConversionListener() {
//                    @Override
//                    public void onConversionCallback(String response) {
//                        try{
//                            Sound.sound(mediaPlayer, noSound, response);
//                        } catch (Exception e){
//                            Log.e("Sound", "onConversionCallback");
//                        }
//                    }
//                });
//                model.setInput(inputToConvert.getText().toString());
//                task.execute(model.getInput(), model.getMorseToTextURL());
            }
        });

//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
//        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case CAMERA_REQUEST :
                Toast.makeText(this, "Received camera permission callback", Toast.LENGTH_SHORT).show();
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //buttonEnable.setEnabled(false);
                    //buttonEnable.setText("Camera Enabled!!");
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

}
