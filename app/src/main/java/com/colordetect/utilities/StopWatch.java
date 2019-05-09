package com.colordetect.utilities;

public class StopWatch {
    private long startTime = 0;
    private long stopTime = 0;
    private boolean running = false;


    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    public void stop() {
        this.stopTime = System.currentTimeMillis();
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean var) {
        running = var;
    }

    // elaspsed time in milliseconds
    public long getElapsedTime() {
        long elapsed;
        if (running) {
            elapsed = (System.currentTimeMillis() - startTime);
        } else {
            elapsed = (stopTime - startTime);
        }
        return elapsed;
    }

    // elaspsed time in seconds
    public long getElapsedTimeSecs() {
        return getElapsedTime() / 1000;
    }
}