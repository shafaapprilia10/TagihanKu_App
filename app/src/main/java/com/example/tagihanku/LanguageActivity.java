package com.example.tagihanku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LanguageActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        ImageView btnBack = findViewById(R.id.btnBack);
        TextView btnNext = findViewById(R.id.btnNext);
        RadioGroup rg = findViewById(R.id.rgLanguage);

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            int checkedId = rg.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, getString(R.string.choose_first), Toast.LENGTH_SHORT).show();
                return;
            }

            String langCode = (checkedId == R.id.rbEnglish) ? "en" : "id";
            LocaleHelper.setLanguage(LanguageActivity.this, langCode);

            // ✅ jangan clear task, biar tombol back dari Location balik ke Language
            Intent i = new Intent(LanguageActivity.this, LocationActivity.class);
            startActivity(i);

            // optional: kalau kamu mau user gak bisa balik ke Language pakai tombol back,
            // baru pakai finish(); tapi karena kamu MAU bisa balik, kita jangan finish
            // finish();
        });
    }
}
