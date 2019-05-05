package com.myapplication;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.models.ConversionModel;
import com.myapplication.networks.ConversionAsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ToTextActivity extends AppCompatActivity {

    private static final String TAG = "ToTextActivity";
    private static ToTextActivity mContext;

    private Button dot;
    private Button dash;
    private Button charSpace;
    private Button wordSpace;
    private Button delete;

    private Button toTextButton;
    private Button toSound;
    private Button imageFlashlight;
    private TextView inputToConvert;
    private TextView convertedText;
    private FloatingActionButton index;
    ImageView imageView;
    boolean isVisible = false;

    ConversionModel model = new ConversionModel();

    String morse = "";

    private static final int CAMERA_REQUEST = 50;


    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_text);

        mContext = this;

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

        /************************************************/
        /**               Morse Keyboard               **/
        dot = findViewById(R.id.dot);
        dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morse += ".";
                inputToConvert.setText(morse);
            }
        });

        dash = findViewById(R.id.dash);
        dash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morse += "-";
                inputToConvert.setText(morse);
            }
        });

        charSpace = findViewById(R.id.char_space);
        charSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morse += " ";
                inputToConvert.setText(morse);
            }
        });

        wordSpace = findViewById(R.id.word_space);
        wordSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morse += "/";
                inputToConvert.setText(morse);
            }
        });

        delete = findViewById(R.id.del);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMorse = "";
                for(int i = 0; i < morse.length() - 1; i++)
                    newMorse += morse.charAt(i);
                morse = newMorse;
                inputToConvert.setText(morse);
            }
        });
        /************************************************/


        /*********************************************************************************/
        /**                               Conversion Process                            **/
        inputToConvert = (TextView) findViewById(R.id.input_editText);
        convertedText = (TextView) findViewById(R.id.converted_text);

        imageFlashlight = (Button) findViewById(R.id.light_btn);
        imageFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ToTextActivity.this, LightSensor.class);
                startActivity(intent);
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
                morse = "";
                task.execute(model.getInput(), model.getMorseToTextURL());
            }
        });

        toSound = (Button) findViewById(R.id.sound_btn);
        toSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToTextActivity.this, MainAudioTranslation.class);
                startActivity(intent);
            }
        });

        index = (FloatingActionButton) findViewById(R.id.index);
        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE);
        index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible == false) {
                    imageView.setVisibility(View.VISIBLE);
                    isVisible = true;
                }
                else {
                    imageView.setVisibility(View.INVISIBLE);
                    isVisible = false;
                }
            }
        });
        /*********************************************************************************/

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


    public static ToTextActivity getContext() {
        return mContext;
    }

}
