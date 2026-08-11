package com.example.questionbank_practiceexam.presentation.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;
import com.example.questionbank_practiceexam.databinding.ActivityMainBinding;
import com.example.questionbank_practiceexam.presentation.home.HomeFragment;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // ডেটাবেজ প্রসেস
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // ডিফল্টভাবে HomeFragment লোড হবে
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Navigation Selection Listener
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_graph) {
                   // selectedFragment = new GraphFragment();
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_wronglist) {
                   // selectedFragment = new WronglistFragment();
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_setting) {
                   // selectedFragment = new SettingFragment();
                    selectedFragment = new HomeFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    // Fragment replace করার হেল্পার মেথড
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

    }
}