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

import com.example.questionbank_practiceexam.presentation.fragments.DailyMissionFragment;
import com.example.questionbank_practiceexam.presentation.fragments.SettingsFragment;
import com.example.questionbank_practiceexam.presentation.fragments.ProgressFragment;
import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;
import com.example.questionbank_practiceexam.databinding.ActivityMainBinding;
import com.example.questionbank_practiceexam.presentation.home.HomeFragment;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DatabaseHelper dbHelper;
    private long backPressedTime = 0;


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

            loadFragment(
                    new HomeFragment()
            );
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
                    selectedFragment = new ProgressFragment();
                } else if (itemId == R.id.nav_wronglist) {
                   // selectedFragment = new WronglistFragment();
                    selectedFragment = new DailyMissionFragment();
                } else if (itemId == R.id.nav_setting) {
                   // selectedFragment = new SettingFragment();
                    selectedFragment = new SettingsFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });


        // আধুনিক OnBackPressedDispatcher ব্যবহার করে ব্যাক প্রেস হ্যান্ডেল করা
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                // যদি ইউজার ইতিমধ্যে HomeFragment-এ না থাকে, তবে আগে HomeFragment-এ নিয়ে যাবো
                if (!(currentFragment instanceof HomeFragment)) {
                    loadFragment(new HomeFragment());

                    // বটম নেভিগেশন বারকে Home আইকনে সিলেক্ট করে দেওয়া
                    if (binding != null && binding.bottomNavigation != null) {
                        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
                    }
                } else {
                    // যদি ইউজার ইতিমধ্যে HomeFragment-েই থাকে, তবে কলার রিমুভ করে সিস্টেমের ডিফল্ট ব্যাক অ্যাকশন (অ্যাপ বন্ধ হওয়া) ঘটাবে
                    // ইউজার যদি ইতিমধ্যে HomeFragment-েই থাকে, তবে ডাবল প্রেস চেক করব
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        // ২ সেকেন্ডের মধ্যে দ্বিতীয়বার প্রেস করলে অ্যাপ বন্ধ হবে
                        setEnabled(false);
                        onBackPressed();
                    } else {
                        // প্রথমবার প্রেস করলে টোস্ট দেখাবে
                        android.widget.Toast.makeText(MainActivity.this, "Press again to exit", android.widget.Toast.LENGTH_SHORT).show();
                    }
                    backPressedTime = System.currentTimeMillis();
                }
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

    public void setSelectedBottomNavigationItem(int menuItemId) {
        if (binding != null && binding.bottomNavigation != null) {
            binding.bottomNavigation.setSelectedItemId(menuItemId);
        }
    }
}