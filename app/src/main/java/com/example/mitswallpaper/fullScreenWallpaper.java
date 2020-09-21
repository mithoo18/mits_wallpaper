package com.example.mitswallpaper;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class fullScreenWallpaper extends AppCompatActivity {
String originalUrl="";
PhotoView photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_wallpaper);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        originalUrl = intent.getStringExtra("originalUrl");
        photoView = findViewById(R.id.photoView);
        Glide.with(this).load(originalUrl).into(photoView);
    }
}
