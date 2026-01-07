package com.example.tagihanku.data;

public class BillItem {
    public long id;
    public String name;
    public int amount;

    // pembayaran
    public boolean isPaid;       // NEW
    public long paidAtMillis;    // 0 kalau belum dibayar

    public BillItem(long id, String name, int amount, boolean isPaid, long paidAtMillis) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.isPaid = isPaid;
        this.paidAtMillis = paidAtMillis;
    }

    // helper: default input = belum dibayar
    public static BillItem newUnpaid(String name, int amount) {
        return new BillItem(System.currentTimeMillis(), name, amount, false, 0L);
    }
}
