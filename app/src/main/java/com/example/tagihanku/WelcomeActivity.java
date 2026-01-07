package com.example.tagihanku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvDetected;
    private ImageView ivFlag;
    private TextView btnNext;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // sesuai activity_welcome.xml kamu
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        ivFlag = findViewById(R.id.ivFlag);
        tvDetected = findViewById(R.id.tvDetected);
        btnNext = findViewById(R.id.btnNext);

        btnBack.setOnClickListener(v -> finish());

        // data dari LocationActivity
        String country = getIntent().getStringExtra("country");
        String city = getIntent().getStringExtra("city");

        // 1) Greeting berdasarkan negara saja
        tvTitle.setText(getGreetingByCountry(country));

        // 2) Tetap tampilkan lokasi terdeteksi (boleh ada kota, tapi greeting tetap hanya negara)
        String shownCity = (city == null || city.trim().isEmpty() || city.equals("-")) ? "-" : city;
        String shownCountry = (country == null || country.trim().isEmpty()) ? "-" : country;
        tvDetected.setText("Lokasi Terdeteksi:\n" + shownCity + ", " + shownCountry);

        // 3) Set bendera berdasarkan negara (fallback ke Indonesia kalau tidak ada)
        ivFlag.setImageResource(getFlagRes(country));

        btnNext.setOnClickListener(v -> {
            Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }

    private String getGreetingByCountry(String country) {
        if (country == null) return getString(R.string.welcome_title); // fallback ke "SELAMAT DATANG"

        String key = country.trim().toLowerCase(Locale.ROOT);

        HashMap<String, String> map = new HashMap<>();
        map.put("indonesia", "SELAMAT DATANG");
        map.put("malaysia", "SELAMAT DATANG");
        map.put("italia", "BENVENUTO");
        map.put("italy", "BENVENUTO");
        map.put("usa", "WELCOME");
        map.put("united states", "WELCOME");
        map.put("united states of america", "WELCOME");

        for (String k : map.keySet()) {
            if (key.contains(k)) return map.get(k);
        }

        return getString(R.string.welcome_title);
    }

    private int getFlagRes(String country) {
        // Sesuaikan nama drawable yang kamu punya.
        // Kamu sudah punya: flag_indonesia
        if (country == null) return R.drawable.flag_indonesia;

        String c = country.trim().toLowerCase(Locale.ROOT);

        if (c.contains("indonesia")) return R.drawable.flag_indonesia;
        if (c.contains("malaysia")) return R.drawable.flag_malaysia;
        if (c.contains("italia") || c.contains("italy")) return R.drawable.flag_italia;
        if (c.contains("usa") || c.contains("united states")) return R.drawable.flag_usa;

        // fallback
        return R.drawable.flag_indonesia;
    }
}
