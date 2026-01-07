package com.example.tagihanku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tagihanku.data.BillItem;
import com.example.tagihanku.data.Storage;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfilActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        ImageView btnBack = findViewById(R.id.btnBack);
        TextView tvTotal = findViewById(R.id.tvTotal);
        TextView tvPaid = findViewById(R.id.tvPaid);

        TextView row1 = findViewById(R.id.row1);
        TextView row2 = findViewById(R.id.row2);
        TextView row3 = findViewById(R.id.row3);

        TextView btnCheck = findViewById(R.id.btnCheck);
        TextView btnLogout = findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(v -> finish());

        btnCheck.setOnClickListener(v ->
                startActivity(new Intent(this, RiwayatActivity.class))
        );

        btnLogout.setOnClickListener(v -> {
            Storage.setLoggedIn(this, false);
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });

        // ==== HITUNG DATA ====
        List<BillItem> bills = Storage.getBills(this);

        int total = 0;
        int paid = 0;

        // kalau kamu mau "bulan ini", perlu tanggal & filter.
        // untuk simple dulu: hitung semua.
        for (BillItem b : bills) {
            total += b.amount;
            if (b.isPaid) paid += b.amount;
        }
        int unpaid = total - paid;

        NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        tvTotal.setText(rupiah.format(total));
        tvPaid.setText(rupiah.format(paid));
        // SISA belum dibayar: kalau kamu punya tvUnpaid di XML, set juga:
        // tvUnpaid.setText(rupiah.format(unpaid));

        // ==== ISI ROW 1-3 (prioritas: yang belum dibayar) ====
        List<String> rows = new ArrayList<>();

        for (BillItem b : bills) {
            if (!b.isPaid) rows.add("⏳ " + b.name);
            if (rows.size() == 3) break;
        }
        if (rows.size() < 3) {
            for (BillItem b : bills) {
                if (b.isPaid) rows.add("✅ " + b.name);
                if (rows.size() == 3) break;
            }
        }

        setRow(row1, rows, 0);
        setRow(row2, rows, 1);
        setRow(row3, rows, 2);
    }

    private void setRow(TextView tv, List<String> rows, int idx) {
        if (idx < rows.size()) {
            tv.setText(rows.get(idx));
        } else {
            tv.setText("-");
        }
    }
}
