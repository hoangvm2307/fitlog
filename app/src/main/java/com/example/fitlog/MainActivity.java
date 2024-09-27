package com.example.fitlog;

import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set StartWorkoutSession as the default screen
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new StartWorkoutSession())
                .commit();
        }

        // Set the Start Workout item as the default selected item
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_start_workout);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }
}