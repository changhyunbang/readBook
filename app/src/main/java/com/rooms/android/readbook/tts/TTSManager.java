package com.rooms.android.readbook.tts;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rooms.android.readbook.PreferenceManager;
import com.rooms.android.readbook.tts.android.AndroidTTS;
import com.rooms.android.readbook.tts.android.AndroidTTSAdapter;
import com.rooms.android.readbook.tts.android.AndroidVoice;
import com.rooms.android.readbook.tts.gcp.AudioConfig;
import com.rooms.android.readbook.tts.gcp.EAudioEncoding;
import com.rooms.android.readbook.tts.gcp.ESSMLlVoiceGender;
import com.rooms.android.readbook.tts.gcp.GCPTTS;
import com.rooms.android.readbook.tts.gcp.GCPTTSAdapter;
import com.rooms.android.readbook.tts.gcp.GCPVoice;
import com.rooms.android.readbook.tts.gcp.VoiceCollection;
import com.rooms.android.readbook.tts.gcp.VoiceList;

import java.util.Locale;

public class TTSManager {

    final String DEFAULT_LANGUAGECODE = "";
    final String DEFAULT_STYLE = "";

    static final String TAG = TTSManager.class.getSimpleName();

    private Context selfContext;
    static TTSManager instance;
    private TextToSpeechManger mTextToSpeechManger;
    private AndroidTTS mAndroidTTS;
    private GCPTTS mGCPTTS;

    public static TTSManager getInstance(Context context) {
        if(instance == null) {
            synchronized (TTSManager.class) {
                if(instance == null) {
                    instance = new TTSManager(context);
                }
            }
        }
        return instance;
    }

    public TTSManager(Context context) {
        initGCPTTS(context);
        initAndroidTTS(context);
    }

    public void speak(String srcText) {

        String languageCode = PreferenceManager.getTtsLanguageCode(selfContext);
        String name = PreferenceManager.getTtsName(selfContext);
        float pitch = PreferenceManager.getTtsPitch(selfContext);
        float speakRate = PreferenceManager.getTtsRate(selfContext);

        mTextToSpeechManger = loadGCPTTS(languageCode, name, pitch, speakRate);
        if (mTextToSpeechManger != null) {
            mTextToSpeechManger.stop();
            mTextToSpeechManger.speak(srcText);
        }
    }

    public void pause() {
        if (mTextToSpeechManger != null) {
            mTextToSpeechManger.pause();
        }
    }

    public void resume() {
        if (mTextToSpeechManger != null) {
            mTextToSpeechManger.resume();
        }
    }

    public void release() {
        if (mTextToSpeechManger != null) {
            mTextToSpeechManger.exit();
            mTextToSpeechManger = null;
        }

        if (mGCPTTS != null) {
            mGCPTTS.removeSpeakListener();
            mGCPTTS = null;
        }

        if (mAndroidTTS != null) {
            mAndroidTTS.removeSpeakListener();
            mAndroidTTS = null;
        }
    }

    private void initGCPTTS(Context context) {

        selfContext = context;
        VoiceList voiceList = new VoiceList();
        voiceList.addVoiceListener(new VoiceList.IVoiceListener() {
            @Override
            public void onResponse(String text) {
                JsonElement jsonElement = new JsonParser().parse(text);
                if (jsonElement == null || jsonElement.getAsJsonObject() == null ||
                        jsonElement.getAsJsonObject().get("voices").getAsJsonArray() == null) {
                    Log.e(TAG, "get error json");
                    return;
                }

                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonArray jsonArray = jsonObject.get("voices").getAsJsonArray();
                final VoiceCollection voiceCollection = new VoiceCollection();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonArray jsonArrayLanguage = jsonArray.get(i)
                            .getAsJsonObject().get("languageCodes")
                            .getAsJsonArray();

                    if (jsonArrayLanguage.get(0) != null) {
                        String language = jsonArrayLanguage.get(0).toString().replace("\"", "");
                        String name = jsonArray.get(i).getAsJsonObject().get("name").toString().replace("\"", "");
                        String ssmlGender = jsonArray.get(i).getAsJsonObject().get("ssmlGender").toString().replace("\"", "");
                        ESSMLlVoiceGender essmLlVoiceGender = ESSMLlVoiceGender.convert(ssmlGender);
                        int naturalSampleRateHertz = jsonArray.get(i).getAsJsonObject().get("naturalSampleRateHertz").getAsInt();

                        GCPVoice gcpVoice = new GCPVoice(language, name, essmLlVoiceGender, naturalSampleRateHertz);
                        voiceCollection.add(language, gcpVoice);
                    }
                }

                final ArrayAdapter<String> adapterLanguage;
                adapterLanguage = new ArrayAdapter<String>(selfContext,
                        android.R.layout.simple_spinner_item,
                        voiceCollection.getLanguage());
                adapterLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                mSpinnerLanguage.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mSpinnerLanguage.setAdapter(adapterLanguage);
//                    }
//                });
//                mSpinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        final ArrayAdapter<String> adapterStyle;
//                        adapterStyle = new ArrayAdapter<String>(selfContext,
//                                android.R.layout.simple_spinner_item,
//                                voiceCollection.getNames(mSpinnerLanguage.getSelectedItem().toString()));
//                        adapterStyle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        mSpinnerStyle.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mSpinnerStyle.setAdapter(adapterStyle);
//                            }
//                        });
//                        mSpinnerStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                GCPVoice gcpVoice = voiceCollection.getGCPVoice(mSpinnerLanguage.getSelectedItem().toString(),
//                                        mSpinnerStyle.getSelectedItem().toString());
//                                if (gcpVoice != null) {
//                                    mTextViewGender.setText(gcpVoice.getESSMLlGender().toString());
//                                    mTextViewSampleRate.setText(String.valueOf(gcpVoice.getNaturalSampleRateHertz()));
//                                }
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                    }
//                });

                mGCPTTS = new GCPTTS(selfContext);
                mGCPTTS.addSpeakListener(new GCPTTS.ISpeakListener() {
                    @Override
                    public void onSuccess(String message) {
                        Log.i(TAG, message);
                    }

                    @Override
                    public void onFailure(String errorMessage, String speakMessage) {
//                        Message message = mHandler.obtainMessage(EUiHandlerStatus.SHOW_TOAST.ordinal(), errorMessage);
//                        message.sendToTarget();

                        Log.e(TAG, "speak fail : " + errorMessage);
                        mTextToSpeechManger = loadAndroidTTS();
                        if (mTextToSpeechManger != null) {
                            mTextToSpeechManger.speak(speakMessage);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String error) {
//                Message message = mHandler.obtainMessage(EUiHandlerStatus.UPDATE_SPINNER.ordinal(), null);
//                message.sendToTarget();

                mGCPTTS = null;
                Log.e(TAG, "Loading Voice List Error, error code : " + error);
            }
        });
        voiceList.start();
    }

    private void initAndroidTTS(Context context) {

        mAndroidTTS = new AndroidTTS(context);
        mAndroidTTS.addSpeakListener(new AndroidTTS.ISpeakListener() {
            @Override
            public void onSuccess(String message) {
                Log.i(TAG, message);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "speak fail : " + errorMessage);
            }
        });
    }

    private TextToSpeechManger loadGCPTTS(String languageCode, String name, float pitchValue, float speakRateValue) {
        if (mGCPTTS == null) {
            return null;
        }

//        float pitch = ((float) (pitchValue - 2000) / 100);
//        float speakRate = ((float) (speakRateValue + 25) / 100);

        GCPVoice gcpVoice = new GCPVoice(languageCode, name);
        AudioConfig audioConfig = new AudioConfig.Builder()
                .addAudioEncoding(EAudioEncoding.MP3)
                .addSpeakingRate(speakRateValue)
                .addPitch(pitchValue)
                .build();

        mGCPTTS.setGCPVoice(gcpVoice);
        mGCPTTS.setAudioConfig(audioConfig);
        GCPTTSAdapter gcpttsAdapter = new GCPTTSAdapter(mGCPTTS);

        return new TextToSpeechManger(gcpttsAdapter);
    }

    private TextToSpeechManger loadAndroidTTS() {
        if (mAndroidTTS == null) {
            return null;
        }

        AndroidVoice androidVoice = new AndroidVoice.Builder()
                .addLanguage(Locale.ENGLISH)
                .addPitch(1.0f)
                .addSpeakingRate(1.0f)
                .build();

        mAndroidTTS.setAndroidVoice(androidVoice);
        AndroidTTSAdapter androidTTSAdapter = new AndroidTTSAdapter(mAndroidTTS);
        return new TextToSpeechManger(androidTTSAdapter);
    }

}
