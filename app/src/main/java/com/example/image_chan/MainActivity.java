package com.example.image_chan;
// CLORO - 2020

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/*
    THIS APP REQUIRES ANDROID 10!

    This app requires Android 10 or newer.
 */
public class MainActivity extends AppCompatActivity {
    private final int SPLASH_DELAY = 1000 * 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, Main.class);

            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}