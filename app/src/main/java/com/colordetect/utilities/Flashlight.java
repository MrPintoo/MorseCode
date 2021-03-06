package com.colordetect.utilities;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;



public class Flashlight extends AppCompatActivity {
    public void flash(CameraManager cameraManager, String morse) {
        for(int i = 0; i < morse.length(); i++){
            if(morse.charAt(i) == '.'){
                flashLightOn(cameraManager);
                delay(300);
                flashLightOff(cameraManager);
                delay(300);
            }
            else if (morse.charAt(i) == '-'){
                flashLightOn(cameraManager);
                delay(500);
                flashLightOff(cameraManager);
                delay(300);
            }
            else if (morse.charAt(i) == ' '){
                flashLightOff(cameraManager);
                delay(500);
            }
            else{
                flashLightOff(cameraManager);
                delay(800);
            }
        }
    }


    private void delay(int time){
        try{
            Thread.sleep(time);
        }catch(InterruptedException e){

        }
    }

    @TargetApi(23)
    private void flashLightOn(CameraManager cameraManager) {

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.getStackTrace();
        }
    }

    @TargetApi(23)
    private void flashLightOff(CameraManager cameraManager) {

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.getReason();
        }
    }

}

