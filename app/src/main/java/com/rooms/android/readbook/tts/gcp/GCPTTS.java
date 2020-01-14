package com.rooms.android.readbook.tts.gcp;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rooms.android.readbook.database.DBManager;
import com.rooms.android.readbook.model.AudioData;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2018/6/24.
 */

public class GCPTTS {
    private static final String TAG = GCPTTS.class.getName();

    private List<ISpeakListener> mSpeakListeners = new ArrayList<>();

    Context mContext;
    private GCPVoice mGCPVoice;
    private AudioConfig mAudioConfig;
    private String mMessage;
    private VoiceMessage mVoiceMessage;
    private MediaPlayer mMediaPlayer;

    private int mVoiceLength = -1;

    public GCPTTS(Context context) {
        mContext = context;
    }

//    public GCPTTS(GCPVoice gcpVoice, AudioConfig audioConfig) {
//        mGCPVoice = gcpVoice;
//        mAudioConfig = audioConfig;
//    }

    public void setGCPVoice(GCPVoice gcpVoice) {
        mGCPVoice = gcpVoice;
    }

    public void setAudioConfig(AudioConfig audioConfig) {
        mAudioConfig = audioConfig;
    }

    void start(String text) {
        if (mGCPVoice != null && mAudioConfig != null) {
            mMessage = text;
            mVoiceMessage = new VoiceMessage.Builder()
                    .add(new Input(text))
                    .add(mGCPVoice)
                    .add(mAudioConfig)
                    .build();
            new Thread(runnableSend).start();
        }
    }

    private Runnable runnableSend = new Runnable() {
        @Override
        public void run() {

            ArrayList<AudioData> audios = DBManager.getInstance(mContext).selectAudioData(mMessage);
            if (!audios.isEmpty()) {
                playAudio(audios.get(0).getAudioData());
                return;
            }
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                    mVoiceMessage.toString());
            Log.i(TAG, "RequestBody body = " + mVoiceMessage.toString());
            Request request = new Request.Builder()
                    .url(Config.SYNTHESIZE_ENDPOINT)
                    .addHeader(Config.API_KEY_HEADER, Config.API_KEY)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(body)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    speakFail(e.getMessage(), mMessage);
                    Log.e(TAG, "onFailure error : " + e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response != null) {
                        Log.i(TAG, "onResponse code = " + response.code());
                        Log.i(TAG, "onResponse body = " + response.body().string());
                        if (response.code() == 200) {
                            String text = response.body().string();
                            JsonElement jsonElement = new JsonParser().parse(text);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();

                            if (jsonObject != null) {
                                String json = jsonObject.get("audioContent").toString();
                                json = json.replace("\"", "");
                                DBManager.getInstance(mContext).insertAudioData(mMessage, json);
                                playAudio(json);
                                return;
                            }
                        }
                    }

                    speakFail("get response fail", mMessage);
                }
            });
        }
    };

    private void playAudio(String base64EncodedString) {
        try {
            stopAudio();

            String url = "data:audio/mp3;base64," + base64EncodedString;
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            speakSuccess(mMessage);
        } catch (IOException IoEx) {
            speakFail(IoEx.getMessage(), mMessage);
            Log.e(TAG, "error message : " + IoEx.getMessage());
        }
    }

    void stopAudio() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mVoiceLength = -1;
        }
    }

    void resumeAudio() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying() && mVoiceLength != -1) {
            mMediaPlayer.seekTo(mVoiceLength);
            mMediaPlayer.start();
        }
    }

    void pauseAudio() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mVoiceLength = mMediaPlayer.getCurrentPosition();
        }
    }

    void exit() {
        stopAudio();
        mMediaPlayer = null;
    }

    private void speakSuccess(String speakMessage) {
        for (ISpeakListener speakListener : mSpeakListeners) {
            speakListener.onSuccess(speakMessage);
        }
    }

    private void speakFail(String errorMessage, String speakMessage) {
        for (ISpeakListener speakListener : mSpeakListeners) {
            speakListener.onFailure(errorMessage, speakMessage);
        }
    }

    public void addSpeakListener(ISpeakListener iSpeakListener) {
        mSpeakListeners.add(iSpeakListener);
    }

    public void removeSpeakListener(ISpeakListener iSpeakListener) {
        mSpeakListeners.remove(iSpeakListener);
    }

    public void removeSpeakListener() {
        mSpeakListeners.clear();
    }

    public interface ISpeakListener {
        void onSuccess(String message);
        void onFailure(String errorMessage, String speakMessage);
    }
}
