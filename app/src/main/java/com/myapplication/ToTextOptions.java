package com.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ToTextOptions extends AppCompatActivity {

    private Button toSound;
    private Button imageFlashlight;
    private Button morseToText;

    private static final int CAMERA_REQUEST = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_text_options);

        morseToText = (Button) findViewById(R.id.morseToText);
        morseToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ToTextOptions.this, ToTextActivity.class);
                startActivity(intent);
            }
        });

        imageFlashlight = (Button) findViewById(R.id.light_btn);
        imageFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ToTextOptions.this, LightSensor.class);
                startActivity(intent);
            }
        });

        toSound = (Button) findViewById(R.id.sound_btn);
        toSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToTextOptions.this, AudioReceiver.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case CAMERA_REQUEST :
                Toast.makeText(this, "Received camera permission callback", Toast.LENGTH_SHORT).show();
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageFlashlight.setEnabled(true);
                } else {
                    Toast.makeText(this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
