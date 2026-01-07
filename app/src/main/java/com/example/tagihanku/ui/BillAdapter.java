package com.example.tagihanku;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tagihanku.data.BillItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.H> {

    private final List<BillItem> items = new ArrayList<>();

    public void setItems(List<BillItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill_row, parent, false);
        return new H(v);
    }

    @Override
    public void onBindViewHolder(@NonNull H h, int position) {
        BillItem b = items.get(position);

        h.tvName.setText(b.name == null ? "-" : b.name);

        String formatted = NumberFormat.getNumberInstance(Locale.getDefault())
                .format(b.amount);
        h.tvAmount.setText("Rp" + formatted);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class H extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount;

        H(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
