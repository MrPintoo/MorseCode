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

package com.myapplication.utilities.AudioReceiver;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.myapplication.MainAudioTranslation;
import com.myapplication.utilities.StopWatch;

public class RecordingThread {
    private static final String LOG_TAG = RecordingThread.class.getSimpleName();
    private static final int SAMPLE_RATE = 44100;

    private float threshold = 0;

    public RecordingThread(AudioDataReceivedListener listener) {
        mListener = listener;
    }

    private boolean mShouldContinue;
    private AudioDataReceivedListener mListener;
    private Thread mThread;
    private MainAudioTranslation main = MainAudioTranslation.getContext();

    public boolean recording() {
        return mThread != null;
    }

    public void startRecording() {
        if (mThread != null)
            return;

        mShouldContinue = true;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                main.setMorseValue("");
                boolean executed = false;
                record(executed);
            }
        });
        mThread.start();
    }

    public void stopRecording() {
        if (mThread == null)
            return;

        mShouldContinue = false;
        mThread = null;
    }

    private void record(boolean executed) {
        Log.v(LOG_TAG, "Start");
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // buffer size in bytes
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        short[] audioBuffer = new short[bufferSize / 2];

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!");
            return;
        }
        record.startRecording();

        Log.v(LOG_TAG, "Start recording");

        long shortsRead = 0;

        // Calibration Variables
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        StopWatch calibratingStopWatch = new StopWatch();
        calibratingStopWatch.start();
        int [] maxFreqArray = new int[bufferSize / 2];
        int index = 0;

        // Processing Variables
        boolean highFreqProcessing = false;
        boolean lowFreqProcessing = false;
        boolean hasStarted = false;
        float waitTime;
        float stopTime = 0;
        String morse = "";

        /*****************************/
        /** Begin Recording Process **/
        while (mShouldContinue) {
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
            shortsRead += numberOfShort;

            int avg = 0;
            for(int i = 0; i < audioBuffer.length; i++)
                avg += Math.abs(audioBuffer[i]);

            avg = avg /audioBuffer.length;
            main.setFreqValue(String.valueOf(avg));

            /********************************************************/
            /** After calibration, begin listening for frequencies **/
            if(calibratingStopWatch.getElapsedTime() > 5000) {
                waitTime = 5000;

                if(! executed) {
                    int calibrateAvg = 0;
                    for (int i = 0; i < maxFreqArray.length; i++)
                        calibrateAvg += maxFreqArray[i];

                    threshold = calibrateAvg / maxFreqArray.length;
                    executed = true;
                }

                // If it is too quiet for longer than 5 seconds, quit.
                if(stopTime > 2000 && avg < threshold && (calibratingStopWatch.getElapsedTime() - waitTime) > 5000 ) {
                    stopWatch.stop();
                    break;
                }

                /************************************************/
                /** Calculates time that it is above threshold **/
                else if (avg > threshold )  { // Timing of audio to determine morse characters.
                    if(hasStarted == false)
                        hasStarted = true;
                    if(stopWatch.isRunning() == true && lowFreqProcessing == true){
                        stopWatch.stop();
                        stopTime = stopWatch.getElapsedTime();

                        if(stopTime < 1650 && stopTime > 1450) {
                            morse += "/";
                        }
                        else if(stopTime < 1450 && stopTime > 1000) {
                            morse += " ";
                        }
                        main.setMorseValue("space = " + String.valueOf(stopTime));

                        lowFreqProcessing = false;
                    }

                    if(!highFreqProcessing) {
                        stopWatch.start();
                        stopWatch.setRunning(true);
                        highFreqProcessing = true;
                    }
                }
                /************************************************/

                /************************************************/
                /** Calculates time that it is below threshold **/
                else { // Timing of audio to determine morse characters.
                    if(hasStarted == true) {
                        if (stopWatch.isRunning() == true && highFreqProcessing == true) {
                            stopWatch.stop();

                            stopTime = stopWatch.getElapsedTime();
                            if (stopTime < 700 && stopTime > 500) {
                                morse += "-";
                            } else if (stopTime < 450 && stopTime > 250) {
                                morse += ".";
                            }
                            highFreqProcessing = false;
                        }

                        if (!lowFreqProcessing) {
                            stopWatch.start();
                            stopWatch.setRunning(true);
                            lowFreqProcessing = true;
                        }
                    }

                }
                /************************************************/
                main.setMorseValue(String.valueOf(threshold) + " " + String.valueOf(stopTime) + " morse = " + morse);


            } else {
                main.setFreqValue("Give 5 seconds for calibration...");
                int calibrateAvgFrame = 0;
                for(int i = 0; i < audioBuffer.length; i++)
                    calibrateAvgFrame += Math.abs(audioBuffer[i]);
                calibrateAvgFrame = calibrateAvgFrame / audioBuffer.length;

                maxFreqArray[index] = calibrateAvgFrame;
                index++;
            }
            // Notify waveform
            mListener.onAudioDataReceived(audioBuffer);
        }
        main.setMorse(morse);

        record.stop();
        record.release();

        Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
    }
}
