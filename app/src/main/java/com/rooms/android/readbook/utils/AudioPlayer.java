package com.rooms.android.readbook.utils;

import android.media.MediaPlayer;

import java.io.IOException;

public class AudioPlayer extends MediaPlayer {

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        super.setOnPreparedListener(listener);
    }
}
