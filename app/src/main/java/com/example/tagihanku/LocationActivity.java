package com.example.tagihanku;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private static final int REQ_LOCATION = 101;

    private Spinner spCountry;
    private TextView btnNext;
    private View btnDetect;

    private String[] options;
    private FusedLocationProviderClient fused;

    private String detectedCity = null;
    private String detectedCountry = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        ImageView btnBack = findViewById(R.id.btnBack);
        spCountry = findViewById(R.id.spCountry);
        btnDetect = findViewById(R.id.btnDetect);
        btnNext = findViewById(R.id.btnNext);

        fused = LocationServices.getFusedLocationProviderClient(this);

        btnBack.setOnClickListener(v -> finish());

        options = new String[]{
                getString(R.string.country_placeholder),
                "Indonesia",
                "Italia",
                "Malaysia",
                "USA"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spCountry.setAdapter(adapter);

        // OPTIONAL: kalau user memilih manual, reset detected supaya gak nyangkut
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // manual pilih negara
                    detectedCountry = null;
                    detectedCity = null;
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnDetect.setOnClickListener(v -> detectCityCountry());
        btnNext.setOnClickListener(v -> goNext());
    }

    private void goNext() {
        int pos = spCountry.getSelectedItemPosition();

        if (pos <= 0 && detectedCountry == null) {
            Toast.makeText(this, getString(R.string.choose_country_first), Toast.LENGTH_SHORT).show();
            return;
        }

        boolean manual = (pos > 0);

        String countryToSend = manual ? options[pos] : detectedCountry;

        // PENTING: kalau manual, kota jangan pakai hasil deteksi lama
        String cityToSend = manual ? "-" :
                ((detectedCity == null || detectedCity.trim().isEmpty()) ? "-" : detectedCity);

        Intent i = new Intent(LocationActivity.this, WelcomeActivity.class);
        i.putExtra("country", countryToSend);
        i.putExtra("city", cityToSend);
        startActivity(i);
    }

    private void detectCityCountry() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_LOCATION
            );
            return;
        }

        Toast.makeText(this, getString(R.string.detecting_location), Toast.LENGTH_SHORT).show();

        // Cara utama: getCurrentLocation (lebih real-time)
        CancellationTokenSource cts = new CancellationTokenSource();
        fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        resolveLocation(location);
                    } else {
                        // kalau null, paksa update sekali (stabil untuk emulator)
                        requestSingleUpdate();
                    }
                })
                .addOnFailureListener(e -> requestSingleUpdate());
    }

    // Paksa update lokasi sekali (buat emulator sering lebih ampuh)
    private void requestSingleUpdate() {
        LocationRequest req = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMaxUpdates(1)
                .build();

        LocationCallback cb = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                fused.removeLocationUpdates(this);
                Location loc = result.getLastLocation();
                if (loc != null) resolveLocation(loc);
                else pickFromDeviceLocaleOnly();
            }
        };

        fused.requestLocationUpdates(req, cb, Looper.getMainLooper());
    }

    private void resolveLocation(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );

            if (addresses == null || addresses.isEmpty()) {
                pickFromDeviceLocaleOnly();
                return;
            }

            Address a = addresses.get(0);

            String country = a.getCountryName();
            String city = a.getLocality();
            if (city == null || city.trim().isEmpty()) city = a.getSubAdminArea();
            if (city == null || city.trim().isEmpty()) city = a.getAdminArea();

            if (country == null || country.trim().isEmpty()) {
                pickFromDeviceLocaleOnly();
                return;
            }

            detectedCountry = mapCountryName(country);
            detectedCity = city;

            selectCountryInSpinner(detectedCountry);

            String shownCity = (detectedCity == null || detectedCity.trim().isEmpty()) ? "-" : detectedCity;
            Toast.makeText(
                    this,
                    getString(R.string.detected_format, shownCity, detectedCountry),
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {
            pickFromDeviceLocaleOnly();
        }
    }

    private void pickFromDeviceLocaleOnly() {
        String countryCode = Locale.getDefault().getCountry();
        String countryName = new Locale("", countryCode).getDisplayCountry();

        if (countryName == null || countryName.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.detect_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        detectedCountry = mapCountryName(countryName);
        detectedCity = null;

        selectCountryInSpinner(detectedCountry);

        Toast.makeText(this, getString(R.string.detected_country_only, detectedCountry), Toast.LENGTH_SHORT).show();
    }

    private String mapCountryName(String country) {
        if (country == null) return null;

        String c = country.toLowerCase(Locale.ROOT).trim();

        // USA (English + Indonesian)
        if (c.contains("united states") || c.contains("amerika serikat") || c.equals("us") || c.equals("usa")) {
            return "USA";
        }

        // Italia (English + Indonesian)
        if (c.contains("italy") || c.contains("italia")) return "Italia";

        // Malaysia
        if (c.contains("malaysia")) return "Malaysia";

        // Indonesia
        if (c.contains("indonesia")) return "Indonesia";

        return country;
    }


    private void selectCountryInSpinner(String country) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equalsIgnoreCase(country)) {
                spCountry.setSelection(i);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_LOCATION) {
            boolean granted = true;
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) { granted = false; break; }
            }
            if (granted) detectCityCountry();
            else Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }
}
