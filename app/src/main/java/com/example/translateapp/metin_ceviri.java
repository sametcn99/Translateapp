package com.example.translateapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.Translation;
import com.ibm.watson.language_translator.v3.model.TranslationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.translateapp.apiKey.getIBM_CLOUD_API_KEY;

public class metin_ceviri extends AppCompatActivity {
    TextToSpeech textToSpeech;
    TextView translatedTextView;
    EditText TextInputView;
    String apiKey = getIBM_CLOUD_API_KEY();
    //IamAuthenticator authenticator = new IamAuthenticator("kfeXzCurZ6WEW05HkkEPszdlITvXGtrtSFmn9Z9Qc3CD");
    IamAuthenticator authenticator = new IamAuthenticator(apiKey);
    LanguageTranslator languageTranslator = new LanguageTranslator("2018-05-01", authenticator);
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    static String xTranslatedOutput = "";
    public static List<String> gecmis = new ArrayList<>();
    static List<String> getGecmis() {
        return gecmis;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metin_ceviri);
        Button bTextToSpeech = findViewById(R.id.b_text_to_speech);
        Button start_translate = findViewById(R.id.start_translate);
        Button gecmisGoruntule = findViewById(R.id.gecmisGoruntule);
        TextInputView = findViewById(R.id.t_input);
        String xoutput = com.example.translateapp.resim_ceviri.getxOutput();
        if (xoutput != null) {
            TextInputView.setText(xoutput);
        }
        bTextToSpeech.setOnClickListener(v -> speak());
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.getDefault());
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {
                    bTextToSpeech.setEnabled(true);
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
        gecmisGoruntule.setOnClickListener(v -> {
            Intent intent = new Intent(metin_ceviri.this, ceviri_gecmisi.class);
            startActivity(intent);
        });
        translatedTextView = findViewById(R.id.translated_text);
        StrictMode.setThreadPolicy(policy);
        languageTranslator.setServiceUrl("https://api.eu-gb.language-translator.watson.cloud.ibm.com/instances/f8f5988b-a894-4321-b2e5-e655c1164df5");
        translatedTextView.setMovementMethod(new ScrollingMovementMethod());
        start_translate.setOnClickListener(v -> translate());
    }

    @SuppressLint("SetTextI18n")
    public void translate() {
        String textInput = String.valueOf(TextInputView.getText());
        TranslateOptions translateOptions = new TranslateOptions.Builder().addText(textInput).modelId("en-tr").build();
        TranslationResult result = languageTranslator.translate(translateOptions).execute().getResult();
        List<Translation> textOutput = result.getTranslations();
        xTranslatedOutput = textOutput.toString();
        xTranslatedOutput = xTranslatedOutput.replace("[{", "");
        xTranslatedOutput = xTranslatedOutput.replace("}]", "");
        xTranslatedOutput = xTranslatedOutput.replace((char) 34 + "translation" + (char) 34 + ":", "");
        translatedTextView.setText(xTranslatedOutput);
        gecmis.add(xTranslatedOutput);
    }

    private void speak() {
        String text = translatedTextView.getText().toString();
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}