package com.example.tagihanku;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLanguage(this);
        super.onCreate(savedInstanceState);

        // Redirect ke Splash supaya alur app kamu mulai dari Splash
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}
