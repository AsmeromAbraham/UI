package com.example.android.cse594project;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public  class TTS extends Service implements TextToSpeech.OnInitListener{
    private TextToSpeech mTts;
    private String spokenText;
    public static String NOTIFICATION = "notification";

    @Override
    public void onStart(Intent intent, int startId) {
        if(intent != null){
            spokenText = intent.getStringExtra(NOTIFICATION);
            mTts = new TextToSpeech(this, this);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                mTts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}