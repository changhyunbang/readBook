package com.rooms.android.readbook.model;

public class AudioData {
    String text;
    String audioData;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAudioData() {
        return audioData;
    }

    public void setAudioData(String audioData) {
        this.audioData = audioData;
    }

    public String toString() {

        return String.format("[text] : %s [audioData] : %s" + text, audioData);
    }
}
