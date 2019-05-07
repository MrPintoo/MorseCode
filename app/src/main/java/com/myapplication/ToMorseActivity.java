package com.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.models.ConversionModel;
import com.myapplication.networks.ConversionAsyncTask;
import com.myapplication.utilities.Flashlight;
import com.myapplication.utilities.Sound;
import com.myapplication.utilities.Vibration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ToMorseActivity extends AppCompatActivity {

    private Button toMorseButton;
    private Button toVibrate;
    private Button toSound;
    private Button imageFlashlight;
    private EditText inputToConvert;
    private TextView convertedText;
    private FloatingActionButton index;
    ImageView imageView;
    boolean isVisible = false;

    Vibration vibration;
    ConversionModel model = new ConversionModel();
    Flashlight flashlight = new Flashlight();

    private static final int CAMERA_REQUEST = 50;

    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_morse);

        final CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        final boolean hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        /************************************************/
        /** Retrieve text_to_morse JSON from JSON file **/
        String textToMorse = "";
        try {
            String line;
            InputStream ins = getResources().openRawResource(R.raw.text_to_morse);
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins));
            if (ins != null) {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                    stringBuffer.append("\n");
                }
            }
            textToMorse = stringBuffer.toString();
            ins.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        String morseToText = "";
        try {
            String line;
            InputStream ins = getResources().openRawResource(R.raw.morse_to_text);
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins));
            if (ins != null) {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                    stringBuffer.append("\n");
                }
            }
            morseToText = stringBuffer.toString();
            ins.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        model.setTextToMorseURL(textToMorse);
        model.setMorseToTextURL(morseToText);
        /************************************************/


        /*********************************************************************************/
        /**                               Conversion Process                            **/
        inputToConvert = (EditText) findViewById(R.id.input_editText);
        convertedText = (TextView) findViewById(R.id.converted_text);

        imageFlashlight = (Button) findViewById(R.id.light_btn);
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
                            Toast.makeText(ToMorseActivity.this, "No flash available on your device", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                model.setInput(inputToConvert.getText().toString());
                task.execute(model.getInput(), model.getTextToMorseURL());
            }
        });

        toMorseButton = (Button) findViewById(R.id.toMorse);
        toMorseButton.setOnClickListener(new View.OnClickListener() {
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
                task.execute(model.getInput(), model.getTextToMorseURL());
            }
        });

        toVibrate = (Button) findViewById(R.id.vibrate_btn);
        toVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversionAsyncTask task = new ConversionAsyncTask();
                task.setConversionListener(new ConversionAsyncTask.ConversionListener() {
                    @Override
                    public void onConversionCallback(String response) {
                        try {
                            vibration.vibrate(getBaseContext(), response);
                        } catch(Exception e) {
                            Log.e("Vibration", "onConversionCallback");
                        }
                    }
                });
                model.setInput(inputToConvert.getText().toString());
                task.execute(model.getInput(),model.getTextToMorseURL());
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
                task.execute(model.getInput(), model.getTextToMorseURL());
            }
        });
//        index = (FloatingActionButton) findViewById(R.id.index);
//        imageView = (ImageView)findViewById(R.id.imageView);
//        imageView.setVisibility(View.INVISIBLE);
//        index.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isVisible == false) {
//                    imageView.setVisibility(View.VISIBLE);
//                    isVisible = true;
//                }
//                else {
//                    imageView.setVisibility(View.INVISIBLE);
//                    isVisible = false;
//                }
//            }
//        });


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case CAMERA_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageFlashlight.setEnabled(true);
                } else {
                    Toast.makeText(this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}
