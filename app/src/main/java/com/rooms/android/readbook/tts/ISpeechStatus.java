package com.rooms.android.readbook.tts;

public interface ISpeechStatus {
    public void onStart();
    public void onError();
    public void onFinish();
}
