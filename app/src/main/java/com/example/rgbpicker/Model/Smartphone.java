package com.example.rgbpicker.Model;

import java.util.List;

public class Smartphone {
    private String name;
    private List<String> cameraValues;

    public Smartphone(String name, List<String> cameraValues) {
        this.name = name;
        this.cameraValues = cameraValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCameraValues() {
        return cameraValues;
    }

    public void setCameraValues(List<String> cameraValues) {
        this.cameraValues = cameraValues;
    }
}

