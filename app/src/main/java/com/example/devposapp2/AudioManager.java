package com.example.devposapp2;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
public class AudioManager implements TextToSpeech.OnInitListener {
    private static AudioManager INSTANCE = null;
    private static Object mutex = new Object();
    private final TextToSpeech tts;

    private boolean ttsOk; // The constructor will create a TextToSpeech instance.
    private AudioManager(Context context) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                    ttsOk = true;
                }
            }
        });
    }

    public static AudioManager getInstance(Context context) {
        synchronized (mutex) {
            if (INSTANCE == null) {
                INSTANCE = new AudioManager(context);
            }
        }
        return(INSTANCE);
    }

    @Override // OnInitListener method to receive the TTS engine
    public void onInit(int status) {
        if (status != TextToSpeech.ERROR) {
            tts.setLanguage(Locale.UK);
            ttsOk = true;
        }
    } // A method to speak something
    @SuppressWarnings("deprecation") // Support older API levels too.
    public void speak(String text) {
        if (ttsOk) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    } // Other code goes here...
}