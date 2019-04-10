package com.myapplication.models;

public class ConversionModel {
    private String input;
    private String output;
    private String textToMorseURL;
    private String morseToTextURL;

    public String getMorseToTextURL() { return morseToTextURL; }

    public void setMorseToTextURL(String morseToTextURL) { this.morseToTextURL = morseToTextURL; }

    public String getTextToMorseURL() { return textToMorseURL; }

    public void setTextToMorseURL(String json) { this.textToMorseURL = json; }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
