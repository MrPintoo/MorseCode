package com.colordetect;

import android.annotation.TargetApi;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.colordetect.utilities.StopWatch;


public class LightSensor extends AppCompatActivity implements SensorEventListener {

    TextView textView;
    TextView outputView;
    TextView timeView;
    SensorManager sensorManager;
    Sensor sensor;
    StopWatch stopWatch = new StopWatch();
    boolean startDecoding = false;
    private String flashToMorse = "";

    private int value = 1;
    private float threshold = 0;

    @TargetApi(15)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_light);

        textView = (TextView) findViewById(R.id.textView);
        outputView = (TextView) findViewById(R.id.output_view);
        timeView = (TextView) findViewById(R.id.timeView);
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor = ((SensorManager) sensorManager).getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            textView.setText(""+event.values[0]);
            if(value > 0){        //threshold gets the first average light of the surrounding
                threshold = event.values[0];
                value = value -1;
            }
            if(event.values[0]> threshold){
                if(stopWatch.isRunning() == true){
                    stopWatch.stop();
                    float time = stopWatch.getElapsedTime();
                    if(time > 100 && time <400){
                        //outputView.setText(" ");
                        flashToMorse += " ";
                    }
                    else if (time > 500 && time < 900){
                        //outputView.setText("/");
                        flashToMorse += "/";
                    }
                }
                stopWatch.start();
                if(startDecoding == false)
                    startDecoding = true;
                //outputView.setText("> " + threshold);
            }else if(event.values[0] == threshold){
                if(startDecoding == true) {
                    if (stopWatch.isRunning() == true) {
                        stopWatch.stop();
                        float time = stopWatch.getElapsedTime();
                        timeView.setText("" + time);
                        if (time > 100 && time < 400) {
                            //outputView.setText(".");
                            flashToMorse += ".";

                        } else if (time > 500 && time < 900) {
                            //outputView.setText("-");
                            flashToMorse += "-";

                        } else ;
                        //outputView.setText("< " + threshold);
                    }
                    stopWatch.start();
                }
            }
            else{
                //outputView.setText("< " + threshold);
            }
        }

        outputView.setText(flashToMorse);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
