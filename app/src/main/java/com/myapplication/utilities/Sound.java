package com.myapplication.utilities;

import android.media.MediaPlayer;

public class Sound {

    public static void sound(MediaPlayer mediaPlayer, MediaPlayer noSound, String  morse){

        for(int i = 0; i < morse.length(); i++){
            if(morse.charAt(i) == '.'){
                soundLoop(mediaPlayer, noSound, 250);
            }
            else if(morse.charAt(i) == '-'){
                soundLoop(mediaPlayer, noSound, 500);
            }
            else if(morse.charAt(i) == ' '){
                soundLoopForSpace(noSound, 250);
            }
            else{
                soundLoopForSpace(noSound, 500);

            }
        }
    }

    public static void soundLoop(MediaPlayer mediaPlayer, MediaPlayer noSound, int time){
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
        while(true){
            if(mediaPlayer.getCurrentPosition() > time)
                break;
        }
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        innerCharLoop(noSound);
    }

    public static void soundLoopForSpace(MediaPlayer noSound, int time){
        noSound.seekTo(0);
        noSound.start();
        while(true){
            if(noSound.getCurrentPosition() > time)
                break;
        }
        noSound.pause();
        noSound.seekTo(0);
    }

    public static void innerCharLoop(MediaPlayer noSound){
        noSound.seekTo(0);
        noSound.start();
        while(true){
            if(noSound.getCurrentPosition() > 100)
                break;
        }
        noSound.pause();
        noSound.seekTo(0);
    }
}
