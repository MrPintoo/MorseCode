/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.myapplication;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.myapplication.models.ConversionModel;
import com.myapplication.networks.ConversionAsyncTask;
import com.myapplication.utilities.AudioReceiver.AudioDataReceivedListener;
import com.myapplication.utilities.AudioReceiver.RecordingThread;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainAudioTranslation extends AppCompatActivity {

    ConversionModel model = new ConversionModel();

    private TextView avgFreq;
    private TextView morseTextView;
    private WaveformView mRealtimeWaveformView;
    private RecordingThread mRecordingThread;
    private static MainAudioTranslation mContext;

    // private PlaybackThread mPlaybackThread;
    private static final int REQUEST_RECORD_AUDIO = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wave_form);

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

        mRealtimeWaveformView = (WaveformView) findViewById(R.id.waveformView);

        /** Recording Thread **/
        mRecordingThread = new RecordingThread(new AudioDataReceivedListener() {
            @Override
            public void onAudioDataReceived(short[] data) {
                mRealtimeWaveformView.setSamples(data);
            }
        });

        /** Record Button **/
        avgFreq = (TextView) findViewById(R.id.avgFreq);
        morseTextView = (TextView) findViewById(R.id.morseCode);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mRecordingThread.recording()) {
                    startAudioRecordingSafe();
                } else {
                    mRecordingThread.stopRecording();
                }
            }
        });
    }

    public void setFreqValue(final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                avgFreq.setText(String.valueOf(value));
            }
        });
    }

    public void setMorseValue(final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                morseTextView.setText(String.valueOf(value));
            }
        });
    }

    public void setMorse(String morse) {
        ConversionAsyncTask task = new ConversionAsyncTask();
        task.setConversionListener(new ConversionAsyncTask.ConversionListener() {
            @Override
            public void onConversionCallback(String response) {
                model.setOutput(response);
                morseTextView.setText(model.getOutput());
            }
        });
        model.setInput(morse);
        task.execute(model.getInput(), model.getMorseToTextURL());
    }

    public static MainAudioTranslation getContext() {
        return mContext;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRecordingThread.stopRecording();
    }

    private void startAudioRecordingSafe() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            mRecordingThread.startRecording();
        } else {
            requestMicrophonePermission();
        }
    }

    private void requestMicrophonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
            // Show dialog explaining why we need record audio
            Snackbar.make(mRealtimeWaveformView, "Microphone access is required in order to record audio",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainAudioTranslation.this, new String[]{
                            android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(MainAudioTranslation.this, new String[]{
                    android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mRecordingThread.stopRecording();
        }
    }
}
