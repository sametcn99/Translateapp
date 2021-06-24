package com.example.translateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.Translation;
import com.ibm.watson.language_translator.v3.model.TranslationResult;

import java.io.IOException;

import static com.example.translateapp.apiKey.getIBM_CLOUD_API_KEY;

public class newLayout extends AppCompatActivity {
    TextToSpeech textToSpeech;
    TextView translatedTextView;
    EditText TextInputView;
    String apiKey = getIBM_CLOUD_API_KEY();
    IamAuthenticator authenticator = new IamAuthenticator(apiKey);
    LanguageTranslator languageTranslator = new LanguageTranslator("2018-05-01", authenticator);
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    static String xTranslatedOutput = "";
    public static List<String> gecmis = new ArrayList<>();
    private static final int PICK_IMAGE = 123;
    ImageView importedImageView;
    Uri image_uri;
    TextView OCROutputView;
    public static String xOutput = "";

    static List<String> getGecmis() {
        return gecmis;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_layout);
        StrictMode.setThreadPolicy(policy);
        languageTranslator.setServiceUrl("https://api.eu-gb.language-translator.watson.cloud.ibm.com/instances/f8f5988b-a894-4321-b2e5-e655c1164df5");
        Button bTextToSpeech = findViewById(R.id.b_text_to_speech);
        Button start_translate = findViewById(R.id.start_translate);
        Button gecmisGoruntule = findViewById(R.id.gecmisGoruntule);
        translatedTextView = findViewById(R.id.translated_text);
        translatedTextView.setMovementMethod(new ScrollingMovementMethod());
        Button selectPictureButton = findViewById(R.id.spicture);
        Button takePictureButton = findViewById(R.id.take_picture);
        importedImageView = findViewById(R.id.iv1);
        OCROutputView = findViewById(R.id.xocropt);
        Button cropImage = findViewById(R.id.xcropimage);
        TextInputView = findViewById(R.id.t_input);
        start_translate.setOnClickListener(v -> translate());
        selectPictureButton.setOnClickListener(v -> selectPic());
        takePictureButton.setOnClickListener(v -> startCameraActivity());
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
            Intent intent = new Intent(newLayout.this, ceviri_gecmisi.class);
            startActivity(intent);
        });
    }
    public void selectPic() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
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

    public void speak() {
        String text = translatedTextView.getText().toString();
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void startCameraActivity() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            image_uri = data.getData();
        }
        if (requestCode == 0) {
            Toast.makeText(getApplicationContext(), "Kaydedilen resmin konumu: " + image_uri.getPath(), Toast.LENGTH_LONG).show();
        }
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process() throws IOException {
        InputImage image;
        Context context = getApplicationContext();
        image = InputImage.fromFilePath(context, image_uri);
        TextRecognizer recognizer = TextRecognition.getClient();
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                xOutput = visionText.getText();
                                TextInputView.setText(visionText.getText());
                                translate();
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Something went wrong!.. ", Toast.LENGTH_LONG).show();
                                    }
                                });
    }
}