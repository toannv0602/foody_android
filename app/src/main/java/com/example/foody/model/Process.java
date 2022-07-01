package com.example.foody.model;

public class Process  {
    private int step;
    private String action;

    public Process() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Process(int step, String action) {
        this.step = step;
        this.action = action;
    }
}
