package com.colordetect;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.colordetect.models.ConversionModel;
import com.colordetect.networks.ConversionAsyncTask;
import com.colordetect.utilities.StopWatch;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;

public class FlashReceiver extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = "OCVSample:Activity";
    int threshold = 0;
    long stopTime = 0;
    boolean hasStarted = false;
    boolean lowFreqProcessing = false;
    boolean highFreqProcessing = false;
    boolean isCalibration = true;
    boolean isStart = false;
    StopWatch calibrationStopWatch = new StopWatch();
    StopWatch stopWatch = new StopWatch();
    int calibrationAvg = 0;
    int noOfCalibrationAvg = 0;
    String morse = "";

    CameraBridgeViewBase cameraBridgeViewBase;
    ConversionModel model = new ConversionModel();


    TextView stopTextView;
    TextView textView;
    TextView morseView;
    TextView thresholdView;
    TextView guide;
    private Button start;
    private Button stop;
    private Button reset;
    BaseLoaderCallback baseLoaderCallback;
    private Mat mat;
    private Mat mRgba;
    private Mat mRgbaF;
    private Mat mRgbaT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiver_flash);

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

        guide = (TextView) findViewById(R.id.guide);
        textView = (TextView) findViewById(R.id.textView);
        morseView = (TextView) findViewById(R.id.morse);
        stopTextView = (TextView) findViewById(R.id.stopTime);
        thresholdView = (TextView) findViewById(R.id.threshold);
        start = (Button) findViewById(R.id.start_btn);
        stop = (Button) findViewById(R.id.stop_btn);
        reset = (Button) findViewById(R.id.reset_btn);

        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.cameraview);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);


        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status){
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.i(TAG, "OpenCV loaded successfully");
                        cameraBridgeViewBase.enableView();
                    }break;
                    default:
                    {
                        super.onManagerConnected(status);
                    }break;
                }
            }
        };
//        calibrationStopWatch.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(), "There is a problem in opencv", Toast.LENGTH_LONG).show();
        }
        else{
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null)
            cameraBridgeViewBase.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) { mat = new Mat(height, width, CvType.CV_8UC4);
        mat = new Mat(height, width, CvType.CV_8UC4);
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mat.release();
        mRgba.release();
        mRgbaF.release();
        mRgbaT.release();
    }


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        if(!isStart){
            runOnUiThread(new Runnable() {
                public void run() {
                    guide.setText("Focus the flash source inside the square and Hit start");
                }
            });

        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        guide.setText("Hold Still...");
//                    }
//                });
                calibrationStopWatch.start();
                isStart = true;
                start.setEnabled(false);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        guide.setText("Processing stopped...");
                    }
                });
                isStart = false;
                onPause();
                model.setOutput(morse);
                setMorse(model.getOutput());
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onPause();
                runOnUiThread(new Runnable() {
                    public void run() {
                        guide.setText("Focus the flash source inside the square and Hit start");
                    }
                });
                start.setEnabled(true);
                morse = "";
                threshold = 0;
                stopTime = 0;
                calibrationStopWatch.stop();
                onResume();
                calibrationStopWatch.start();
                isCalibration = true;
            }
        });

        System.gc();
        mat = inputFrame.rgba();

        int matWidth = mRgbaF.width();
        int matHeight = mRgbaF.height();
        Size size1   = new Size(matWidth, matHeight);
        Core.transpose(mat, mRgbaT);

        Imgproc.resize(mRgbaT, mRgbaF, size1, 0,0,0);
        Core.flip(mRgbaF, mat, 1);


        int width = mat.width();
        int height = mat.height();


        /**********Draw Rectangle**********/
        Imgproc.rectangle(mat, new Point(width * 3 / 7, height * 3 / 7), new Point(
                width * 4 / 7, height * 4 /  7 ), new Scalar( 255, 0, 0 ), 5
        );


        /************************************************/
        /**********Calculates the average pixel**********/
        Deque<double[]> pixelStack = new ArrayDeque<double[]>();
        int oneSeventhRow = mat.rows()/7;
        int oneSeventhCol = mat.cols()/7;

        for(int row = oneSeventhRow*3; row < oneSeventhRow*4; row+=2){    //store all the pixel in that area in the stack
            for(int col = oneSeventhCol*3; col  < oneSeventhCol*4; col+=2){
                double [] pixelValue = mat.get(row, col);
                pixelStack.push(pixelValue);
            }
        }
        double redChannelValue=0;
        double greenChannelValue=0;
        double blueChannelValue=0;
        int sizeOfPixelStack = pixelStack.size();
        double avgPixel = 0;

        while(!pixelStack.isEmpty()){
            System.gc();
            redChannelValue = pixelStack.getLast()[0];
            greenChannelValue = pixelStack.getLast()[1];
            blueChannelValue = pixelStack.getLast()[2];
            avgPixel += (redChannelValue + blueChannelValue + greenChannelValue)/3;
            pixelStack.removeLast();
        }


        avgPixel = avgPixel/(sizeOfPixelStack);
        final double averagePixel = avgPixel;
        runOnUiThread(new Runnable() {
            public void run() {
                textView.setText(String.valueOf(averagePixel));
            }
        });
        Log.i(TAG, "red channel value: "  + redChannelValue);
        Log.i(TAG, "green channel value: "  + greenChannelValue);
        Log.i(TAG, "blue channel value: "  + blueChannelValue);

        runOnUiThread(new Runnable() {
            public void run() {
                textView.setText(String.valueOf(averagePixel));
            }
        });
        /************************************************/

        if(isStart){
            if(isCalibration && calibrationStopWatch.getElapsedTime() < 7000){
                calibrationAvg += averagePixel;
                noOfCalibrationAvg ++;
                runOnUiThread(new Runnable() {
                    public void run() {
                        guide.setText("Hold Still...");
                    }
                });
                runOnUiThread(new Runnable() {
                    public void run() {
                        thresholdView.setText("threshold: " + String.valueOf(threshold));
                    }
                });
                runOnUiThread(new Runnable() {
                    public void run() {
                        stopTextView.setText("intervals: " + String.valueOf(threshold));
                    }
                });

            }
            else if(isCalibration && calibrationStopWatch.getElapsedTime() > 7000){
                runOnUiThread(new Runnable() {
                    public void run() {
                        guide.setText("");
                    }
                });
                if(noOfCalibrationAvg!=0)
                    threshold = calibrationAvg/noOfCalibrationAvg + 30;
                runOnUiThread(new Runnable() {
                    public void run() {
                        thresholdView.setText("threshold: " + String.valueOf(threshold));
                    }
                });
                isCalibration = false;
            }
            else {
                /************************************************/
                /** Calculates time that it is above threshold **/
                runOnUiThread(new Runnable() {
                    public void run() {
                        guide.setText("Processing...");
                    }
                });
                if (averagePixel > threshold) { // Timing of video to determine morse characters.

                    if (hasStarted == false)
                        hasStarted = true;
                    if (stopWatch.isRunning() == true && lowFreqProcessing == true) {
                        stopWatch.stop();
                        stopTime = stopWatch.getElapsedTime();

                        if (stopTime < 1300 && stopTime > 900) {
                            morse += "/";
                        } else if (stopTime < 900 && stopTime > 450) {
                            morse += " ";
                        }
                        final long stopstopTime = stopTime;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                stopTextView.setText("intervals: " + String.valueOf(stopstopTime));
                            }
                        });
                        lowFreqProcessing = false;
                    }
                    if (!highFreqProcessing) {
                        stopWatch.start();
                        stopWatch.setRunning(true);
                        highFreqProcessing = true;
                    }
                }
                /************************************************/
                /************************************************/
                /** Calculates time that it is below threshold **/
                else { // Timing of video to determine morse characters.
                    if (hasStarted == true) {
                        if (stopWatch.isRunning() == true && highFreqProcessing == true) {
                            stopWatch.stop();
                            stopTime = stopWatch.getElapsedTime();
                            if (stopTime < 480 && stopTime > 100) {
                                morse += ".";
                            } else if (stopTime < 600 && stopTime > 480) {
                                morse += "-";
                            }
                            final long stopstopTime = stopTime;
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    stopTextView.setText("intervals: " + String.valueOf(stopstopTime));
                                }
                            });
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
            }
            final String morseAns = morse;
            runOnUiThread(new Runnable() {
                public void run() {
                    morseView.setText( "Input: " +  morseAns);
                }
            });
        }
        return mat;
    }

    public void setMorse(String morse) {
        ConversionAsyncTask task = new ConversionAsyncTask();
        task.setConversionListener(new ConversionAsyncTask.ConversionListener() {
            @Override
            public void onConversionCallback(String response) {
                model.setOutput(response);
                morseView.setText( "Output: " +  model.getOutput());
            }
        });
        model.setInput(morse);
        task.execute(model.getInput(), model.getMorseToTextURL());
    }
}
