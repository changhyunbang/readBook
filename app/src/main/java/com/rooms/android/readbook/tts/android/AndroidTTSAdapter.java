package com.rooms.android.readbook.tts.android;

import com.rooms.android.readbook.tts.ISpeech;

/**
 * Created by USER on 2018/6/24.
 */

public class AndroidTTSAdapter implements ISpeech {
    private AndroidTTS mAndroidTTS;

    public AndroidTTSAdapter(AndroidTTS androidTTS) {
        mAndroidTTS = androidTTS;
    }

    @Override
    public void start(String text) {
        mAndroidTTS.speak(text);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {
        mAndroidTTS.stop();
    }

    @Override
    public void exit() {
        mAndroidTTS.exit();
    }
}
