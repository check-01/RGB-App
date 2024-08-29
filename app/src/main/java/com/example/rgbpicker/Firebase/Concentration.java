package com.example.rgbpicker.Firebase;

public class Concentration {
    private int red;
    private int green;
    private int blue;
    private double concentration;

    public Concentration() {
        // Default constructor required for calls to DataSnapshot.getValue(Concentration.class)
    }

    public Concentration(int red, int green, int blue, double concentration) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.concentration = concentration;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public double getConcentration() {
        return concentration;
    }
}
