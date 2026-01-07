package com.example.tagihanku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tagihanku.data.BillItem;
import com.example.tagihanku.data.Storage;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RiwayatActivity extends AppCompatActivity {

    private LinearLayout listContainer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        ImageView btnBack = findViewById(R.id.btnBack);
        TextView btnSave = findViewById(R.id.btnSave); // "Simpan TagihanKu"
        TextView btnNext = findViewById(R.id.btnNext); // "Next" / "Lanjut"
        listContainer = findViewById(R.id.listContainer);

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
        );

        btnNext.setOnClickListener(v ->
                startActivity(new Intent(this, ProfilActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderList(); // refresh setiap balik ke halaman ini
    }

    private void renderList() {
        List<BillItem> bills = Storage.getBills(this);
        listContainer.removeAllViews();

        if (bills == null || bills.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText(getString(R.string.no_history));
            empty.setTextColor(getResources().getColor(android.R.color.white));
            empty.setAlpha(0.85f);
            empty.setPadding(0, 12, 0, 0);
            listContainer.addView(empty);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        // Format uang mengikuti lokasi (ID => Rp)
        NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        money.setMaximumFractionDigits(0);

        boolean isEnglish = Locale.getDefault().getLanguage().equals("en");
        SimpleDateFormat df = isEnglish
                ? new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
                : new SimpleDateFormat("d MMM yyyy", new Locale("id", "ID"));

        for (BillItem b : bills) {
            // Pakai layout item card kamu (kalau sudah dibuat)
            // Kalau kamu belum punya item_bill_card.xml, bilang ya—aku buatin versi yang match.
            android.view.View row = inflater.inflate(R.layout.item_bill_card, listContainer, false);

            TextView tvName = row.findViewById(R.id.tvName);
            TextView tvAmount = row.findViewById(R.id.tvAmount);
            TextView tvPaidAt = row.findViewById(R.id.tvPaidAt);

            tvName.setText(statusPrefix(b.isPaid) + b.name);
            tvAmount.setText(money.format(b.amount));

            String dateStr = b.isPaid && b.paidAtMillis > 0
                    ? df.format(new Date(b.paidAtMillis))
                    : "-";

            tvPaidAt.setText(getString(R.string.paid_at_format, dateStr));

            // Klik card untuk toggle paid/unpaid
            row.setOnClickListener(v -> {
                boolean newPaid = !b.isPaid;
                Storage.updateBillPaid(this, b.id, newPaid);
                renderList(); // refresh tampilan
            });

            listContainer.addView(row);
        }
    }

    private String statusPrefix(boolean isPaid) {
        return isPaid ? "✅ " : "⏳ ";
    }
}
