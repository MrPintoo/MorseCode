package com.myapplication;

import java.util.ArrayList;
import java.util.List;

public class ConversionModel {
    private String input;
    private List<String> output = new ArrayList<String>();

    public List<String> getOutput() {
        return output;
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
