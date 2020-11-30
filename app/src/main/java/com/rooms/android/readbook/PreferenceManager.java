package com.rooms.android.readbook;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    public static final String PREFERENCES_NAME = "readbook_preference";

    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;

    private static SharedPreferences preferences;

    private static SharedPreferences preferences(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    public static void put(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = preferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void put(Context context, String key, int value) {
        SharedPreferences.Editor editor = preferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void put(Context context, String key, float value) {
        SharedPreferences.Editor editor = preferences(context).edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void put(Context context, String key, String value) {
        SharedPreferences.Editor editor = preferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        return preferences(context).getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return preferences(context).getBoolean(key, defaultValue);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return preferences(context).getInt(key, defaultValue);
    }

    public static int getInt(Context context, String key) {
        return preferences(context).getInt(key, 0);
    }

    public static float getFloat(Context context, String key) {
        return preferences(context).getFloat(key, 0);
    }

    public static String getTtsLanguageCode(Context context) {
        return preferences(context).getString("tts_languageCode", "en-US");
    }

    public static void setTtsLanguageCode(Context context, String ttsLanguageCode) {
        put(context, "tts_languageCode", ttsLanguageCode);
    }

    public static String getTtsName(Context context) {
        return preferences(context).getString("tts_name", "en-US-Wavenet-F");
    }

    public static void setTtsName(Context context, String ttsName) {
        put(context, "tts_name", ttsName);
    }

    public static float getTtsPitch(Context context) {
        return preferences(context).getFloat("tts_pitch", 0.0f);
    }

    public static void setTtsPitch(Context context, float ttsPitch) {
        put(context, "tts_pitch", ttsPitch);
    }

    public static float getTtsRate(Context context) {
        return preferences(context).getFloat("tts_rate", 1.0f);
    }

    public static void setTtsRate(Context context, float ttsRate) {
        put(context, "tts_rate", ttsRate);
    }

}
