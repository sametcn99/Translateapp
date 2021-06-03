package com.example.translateapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ceviri_gecmisi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceviri_gecmisi);
        List<String> gecmis = metin_ceviri.getGecmis();
        ListView lv = findViewById(R.id.ceviri_gecmisi);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gecmis);
        lv.setAdapter(arrayAdapter);
        Button gecmisTemizle = findViewById(R.id.gecmis_temizle);
        gecmisTemizle.setOnClickListener(v -> {
            gecmis.clear();
            setContentView(R.layout.activity_ceviri_gecmisi);
        });
    }
}