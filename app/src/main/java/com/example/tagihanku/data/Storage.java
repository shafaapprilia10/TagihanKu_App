package com.example.tagihanku.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    private static final String PREF = "tagihanku_pref";
    private static final String KEY_LOGIN = "logged_in";
    private static final String KEY_BILLS = "bills_json";

    public static boolean isLoggedIn(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getBoolean(KEY_LOGIN, false);
    }

    public static void setLoggedIn(Context c, boolean v) {
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putBoolean(KEY_LOGIN, v).apply();
    }

    public static void addBill(Context c, BillItem item) {
        List<BillItem> bills = getBills(c);
        bills.add(0, item); // newest first
        saveBills(c, bills);
    }

    public static List<BillItem> getBills(Context c) {
        SharedPreferences sp = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String raw = sp.getString(KEY_BILLS, "[]");
        List<BillItem> out = new ArrayList<>();

        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);

                long id = o.optLong("id");
                String name = o.optString("name");
                int amount = o.optInt("amount");

                // NEW: kompatibel dengan data lama
                boolean isPaid = o.optBoolean("isPaid", false);
                long paidAtMillis = o.optLong("paidAtMillis", 0L);

                out.add(new BillItem(id, name, amount, isPaid, paidAtMillis));
            }
        } catch (Exception ignored) {}

        return out;
    }

    public static void updateBillPaid(Context c, long billId, boolean paid) {
        List<BillItem> bills = getBills(c);
        for (BillItem b : bills) {
            if (b.id == billId) {
                b.isPaid = paid;
                b.paidAtMillis = paid ? System.currentTimeMillis() : 0L;
                break;
            }
        }
        saveBills(c, bills);
    }

    private static void saveBills(Context c, List<BillItem> bills) {
        JSONArray arr = new JSONArray();
        try {
            for (BillItem b : bills) {
                JSONObject o = new JSONObject();
                o.put("id", b.id);
                o.put("name", b.name);
                o.put("amount", b.amount);

                // NEW
                o.put("isPaid", b.isPaid);
                o.put("paidAtMillis", b.paidAtMillis);

                arr.put(o);
            }
        } catch (Exception ignored) {}

        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
                .putString(KEY_BILLS, arr.toString())
                .apply();
    }
}
