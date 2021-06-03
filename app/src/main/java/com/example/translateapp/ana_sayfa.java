package com.example.translateapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

public class ana_sayfa extends AppCompatActivity {
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);
        Button bTextTranslation = findViewById(R.id.TextTranslate);
        Button xPictureButton = findViewById(R.id.PictureTranslate);
        xPictureButton.setOnClickListener(v -> {
            Intent intent = new Intent(ana_sayfa.this, resim_ceviri.class);
            startActivity(intent);
        });
        bTextTranslation.setOnClickListener(v -> {
            Intent intent = new Intent(ana_sayfa.this, metin_ceviri.class);
            startActivity(intent);
        });
        appPermissions();
    }

    private void appPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, 0);
            }

        }
    }
}