package com.example.tagihanku;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
    public interface OnPick { void onPick(int pos); }
    private final OnPick cb;

    public SimpleItemSelectedListener(OnPick cb) { this.cb = cb; }

    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cb.onPick(position);
    }
    @Override public void onNothingSelected(AdapterView<?> parent) {}
}
