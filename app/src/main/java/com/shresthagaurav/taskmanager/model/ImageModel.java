package com.shresthagaurav.taskmanager.model;

public class ImageModel {
    String filename;
    String destination;


    public ImageModel(String filename, String destination) {
        this.filename = filename;
        this.destination = destination;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
