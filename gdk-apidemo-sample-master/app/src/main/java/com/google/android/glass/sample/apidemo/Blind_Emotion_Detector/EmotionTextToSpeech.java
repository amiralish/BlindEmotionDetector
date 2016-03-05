package com.google.android.glass.sample.apidemo.Blind_Emotion_Detector;
import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.Locale;
/**
 * Created by Samaneh on 3/5/16.
 */



public class EmotionTextToSpeech extends Activity
        implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private boolean initialized = false;
    private String queuedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = new TextView(this);
        view.setText("Tap Me");
        setContentView(view);
        tts = new TextToSpeech(this /* context ​*/, this /*​ listener */);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            initialized = true;
            tts.setLanguage(Locale.ENGLISH);

            if (queuedText != null) {
                speak(queuedText);
            }
        }
    }

    public void speak(String text) {
        // If not yet initialized, queue up the text.
        if (!initialized) {
            queuedText = text;
            return;
        }
        queuedText = null;
        // Before speaking the current text, stop any ongoing speech.
        tts.stop();
        // Speak the text.
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // On any motion event (including touchpad tap), say 'Hello Glass'
        speak("Hello Glass");
        return true;
    }
}