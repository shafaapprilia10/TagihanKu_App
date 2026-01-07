package com.example.tagihanku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_LANG = "app_lang";

    /**
     * Dipanggil dari attachBaseContext() di setiap Activity.
     * Contoh:
     *  @Override
     *  protected void attachBaseContext(Context newBase) {
     *      super.attachBaseContext(LocaleHelper.onAttach(newBase));
     *  }
     */
    public static Context onAttach(Context context) {
        String lang = getSavedLanguage(context);
        return setLocale(context, lang);
    }

    /** Simpan pilihan bahasa (misal "en" / "id") */
    public static void saveLanguage(Context context, String languageCode) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_LANG, languageCode).apply();
    }

    /** Ambil bahasa tersimpan (default: "id") */
    public static String getSavedLanguage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_LANG, "id");
    }

    /**
     * Apply bahasa + simpan (dipanggil dari LanguageActivity saat user klik Next)
     * Ini akan mengubah configuration context untuk activity itu.
     */
    public static Context setLanguage(Context context, String languageCode) {
        saveLanguage(context, languageCode);
        return setLocale(context, languageCode);
    }

    /**
     * Kalau kamu masih mau pakai di onCreate (opsional),
     * cukup panggil ini sebelum setContentView.
     */
    public static void applySavedLanguage(Context context) {
        setLocale(context, getSavedLanguage(context));
    }

    // ===== Internal =====

    @SuppressLint("ObsoleteSdkInt")
    private static Context setLocale(Context context, String languageCode) {
        if (languageCode == null || languageCode.trim().isEmpty()) languageCode = "id";

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7+
            return context.createConfigurationContext(config);
        } else {
            // Android 6 ke bawah
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }
    }
}
