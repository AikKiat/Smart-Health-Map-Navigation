package com.example.smarthealth.activities;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smarthealth.MedicalCentreFinder.MapPageNavigation.MapClinicFinder;
import com.example.smarthealth.R;

public class BaseActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        setNavbarListeners();
    }

    private void setNavbarListeners() {
        View navbarHomeLayout = findViewById(R.id.navbarHomeLayout);
        View navbarInventoryLayout = findViewById(R.id.navbarInventoryLayout);
        View navbarMedbotLayout = findViewById(R.id.navbarMedbotLayout);
        View navbarUserLayout = findViewById(R.id.navbarUserLayout);

        //Button
        View navbarHospitalButton = findViewById(R.id.navbarHospitalBtn);

        navbarHomeLayout.setOnClickListener(v -> loadFragment(new HomeFragment()));
        navbarInventoryLayout.setOnClickListener(v -> loadFragment(new HomeFragment()));
        navbarMedbotLayout.setOnClickListener(v -> loadFragment(new HomeFragment()));
        navbarUserLayout.setOnClickListener(v -> loadFragment(new HomeFragment()));
        navbarHospitalButton.setOnClickListener(view -> {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!lm.isLocationEnabled()) {
                    Toast.makeText(this, "Please Enable Location Services First", Toast.LENGTH_SHORT).show();

                } else {
                    loadFragment(new MapClinicFinder());
                }
            }
        });
    }

                // TODO 1: highlight current page icon.
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

}
