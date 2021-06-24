package com.example.translateapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
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

import java.io.IOException;

public class resim_ceviri extends AppCompatActivity {
    private static final int PICK_IMAGE = 123;
    ImageView importedImageView;
    Uri image_uri;
    TextView OCROutputView;
    public static String xOutput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resim_ceviri);
        importedImageView = findViewById(R.id.iv1);
        OCROutputView = findViewById(R.id.xocropt);
        Button start_translate = findViewById(R.id.xstart_translate);
        Button selectPictureButton = findViewById(R.id.spicture);
        Button takePictureButton = findViewById(R.id.take_picture);
        Button cropImage = findViewById(R.id.xcropimage);
        cropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        start_translate.setOnClickListener(v -> {
            Intent intent = new Intent(resim_ceviri.this, metin_ceviri.class);
            startActivity(intent);
        });
        selectPictureButton.setOnClickListener(v -> selectPic());
        takePictureButton.setOnClickListener(v -> startCameraActivity());
    }

    private void selectPic() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void startCameraActivity() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            image_uri = data.getData();
            importedImageView.setImageURI(image_uri);
        }
        if (requestCode == 0) {
            importedImageView.setImageURI(image_uri);
            Toast.makeText(getApplicationContext(), "Kaydedilen resmin konumu: " + image_uri.getPath(), Toast.LENGTH_LONG).show();
        }
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process() throws IOException {
        InputImage image;
        Context context = getApplicationContext();
        image = InputImage.fromFilePath(context, image_uri);
        TextRecognizer recognizer = TextRecognition.getClient();
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                //OCROutputView.setText(visionText.getText());
                                xOutput = visionText.getText();
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

    static String getxOutput() {
        return xOutput;
    }
}
