package com.rooms.android.readbook.tts;

/**
 * Created by USER on 2018/6/24.
 */

public interface ISpeech {
    void start(String text);

    void resume();

    void pause();

    void stop();

    void exit();
}
