package com.example.tagihanku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tagihanku.data.BillItem;
import com.example.tagihanku.data.Storage;

public class InputTagihanActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLanguage(this);
        super.onCreate(savedInstanceState);

        if (!Storage.isLoggedIn(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_input_tagihan);

        ImageView btnBack = findViewById(R.id.btnBack);
        EditText etBillName = findViewById(R.id.etBillName); // pastikan id sama dengan XML kamu
        EditText etAmount = findViewById(R.id.etAmount);     // pastikan id sama
        TextView btnSave = findViewById(R.id.btnSave);   // contoh: tombol "Simpan TagihanKu"
        TextView btnNext = findViewById(R.id.btnNext);       // tombol "Lanjut"

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String name = etBillName.getText().toString().trim();
            String a = etAmount.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(a)) {
                Toast.makeText(this, getString(R.string.fill_required), Toast.LENGTH_SHORT).show();
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(a);
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.amount_must_number), Toast.LENGTH_SHORT).show();
                return;
            }

            Storage.addBill(this, BillItem.newUnpaid(name, amount));
            etBillName.setText("");
            etAmount.setText("");

            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        });

        btnNext.setOnClickListener(v -> startActivity(new Intent(this, RiwayatActivity.class)));
    }
}
