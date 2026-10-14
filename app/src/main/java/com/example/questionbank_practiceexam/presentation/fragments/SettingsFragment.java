package com.example.questionbank_practiceexam.presentation.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.work.WorkManager;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.databinding.FragmentSettingsBinding;
import com.example.questionbank_practiceexam.domain.DailyMissionNotificationScheduler;
import com.example.questionbank_practiceexam.presentation.main.MainActivity;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    // ============================================================
    // SHARED PREFERENCES
    // ============================================================

    private static final String PREF_NAME =
            "app_settings";

    private static final String KEY_DARK_MODE =
            "dark_mode";

    private static final String KEY_MISSION_NOTIFICATION =
            "mission_notification";

    private static final String KEY_SOUND =
            "sound_enabled";

    private static final String KEY_VIBRATION =
            "vibration_enabled";


    private SharedPreferences preferences;


    // ============================================================
    // WORK NAME
    // ============================================================

    private static final String DAILY_MISSION_WORK_NAME =
            "daily_mission_notification";


    // ============================================================
    // CREATE VIEW
    // ============================================================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        binding =
                FragmentSettingsBinding.inflate(
                        inflater,
                        container,
                        false
                );

        return binding.getRoot();
    }


    // ============================================================
    // VIEW CREATED
    // ============================================================

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(
                view,
                savedInstanceState
        );


        preferences =
                requireContext()
                        .getSharedPreferences(
                                PREF_NAME,
                                requireContext().MODE_PRIVATE
                        );


        loadSettings();

        setupListeners();
    }


    // ============================================================
    // LOAD SETTINGS
    // ============================================================

    private void loadSettings() {

        // ========================================================
        // DARK MODE
        // ========================================================

        boolean darkMode =
                preferences.getBoolean(
                        KEY_DARK_MODE,
                        false
                );


        binding.darkModeSwitch.setChecked(
                darkMode
        );


        // ========================================================
        // DAILY MISSION NOTIFICATION
        // ========================================================

        boolean missionNotification =
                preferences.getBoolean(
                        KEY_MISSION_NOTIFICATION,
                        true
                );


        binding.missionNotificationSwitch.setChecked(
                missionNotification
        );


        updateMissionNotificationStatus(
                missionNotification
        );


        // ========================================================
        // SOUND
        // ========================================================




        // ========================================================
        // VIBRATION
        // ========================================================


    }


    // ============================================================
    // LISTENERS
    // ============================================================

    private void setupListeners() {

        // ========================================================
        // DARK MODE
        // ========================================================

        binding.darkModeSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    saveBoolean(
                            KEY_DARK_MODE,
                            isChecked
                    );


                    if (isChecked) {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES
                        );

                    } else {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO
                        );
                    }
                }
        );


        // ========================================================
        // DAILY MISSION NOTIFICATION
        // ========================================================

        binding.missionNotificationSwitch
                .setOnCheckedChangeListener(
                        (buttonView, isChecked) -> {

                            saveBoolean(
                                    KEY_MISSION_NOTIFICATION,
                                    isChecked
                            );


                            updateMissionNotificationStatus(
                                    isChecked
                            );


                            if (isChecked) {

                                enableMissionNotification();

                            } else {

                                disableMissionNotification();
                            }
                        }
                );


        // ========================================================
        // SOUND
        // ========================================================




        // ========================================================
        // PROGRESS & STATISTICS
        // ========================================================

        binding.statisticsSetting.setOnClickListener(
                v -> openProgress()
        );


        // ========================================================
        // CLEAR PROGRESS
        // ========================================================

        binding.clearProgressSetting.setOnClickListener(
                v -> showClearProgressDialog()
        );


        // ========================================================
        // RATE APP
        // ========================================================

        binding.rateAppSetting.setOnClickListener(
                v -> openPlayStore()
        );


        // ========================================================
        // ABOUT
        // ========================================================

        binding.aboutSetting.setOnClickListener(
                v -> showAboutDialog()
        );
    }


    // ============================================================
    // MISSION NOTIFICATION ENABLE
    // ============================================================

    private void enableMissionNotification() {

        DailyMissionNotificationScheduler.schedule(
                requireContext().getApplicationContext()
        );


        Toast.makeText(
                requireContext(),
                "Daily mission reminders enabled",
                Toast.LENGTH_SHORT
        ).show();
    }


    // ============================================================
    // MISSION NOTIFICATION DISABLE
    // ============================================================

    private void disableMissionNotification() {

        WorkManager
                .getInstance(
                        requireContext().getApplicationContext()
                )
                .cancelUniqueWork(
                        DAILY_MISSION_WORK_NAME
                );


        Toast.makeText(
                requireContext(),
                "Daily mission reminders disabled",
                Toast.LENGTH_SHORT
        ).show();
    }


    // ============================================================
    // NOTIFICATION STATUS TEXT
    // ============================================================

    private void updateMissionNotificationStatus(
            boolean enabled
    ) {

        if (enabled) {

            binding.missionNotificationStatus.setText(
                    "Mission reminder notifications are ON"
            );

        } else {

            binding.missionNotificationStatus.setText(
                    "Mission reminder notifications are OFF"
            );
        }
    }


    // ============================================================
    // SAVE BOOLEAN
    // ============================================================

    private void saveBoolean(
            String key,
            boolean value
    ) {

        preferences.edit()
                .putBoolean(
                        key,
                        value
                )
                .apply();
    }


    // ============================================================
    // OPEN PROGRESS
    // ============================================================

    // ============================================================
    // OPEN PROGRESS
    // ============================================================

// ============================================================
    // OPEN PROGRESS
    // ============================================================

// ============================================================
    // OPEN PROGRESS
    // ============================================================

    // ============================================================
    // OPEN PROGRESS
    // ============================================================

    private void openProgress() {
        // ১. MainActivity-র মাধ্যমে বটম নেভিগেশনের আইকন সিলেক্ট করা
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity())
                    .setSelectedBottomNavigationItem(R.id.nav_graph);
        }

        // ২. ফ্রেগমেন্ট লোড করা (এখানে .addToBackStack(null) বাদ দেওয়া হয়েছে)
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container,
                        new ProgressFragment()
                )
                .commit();
    }

    // ============================================================
    // CLEAR PROGRESS DIALOG
    // ============================================================

    private void showClearProgressDialog() {

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle(
                        "Clear Progress?"
                )
                .setMessage(
                        "Are you sure you want to clear your progress and statistics? This action cannot be undone."
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Clear",
                        (dialog, which) -> {

                            clearProgress();

                        }
                )
                .show();
    }


    // ============================================================
    // CLEAR PROGRESS
    // ============================================================

    private void clearProgress() {

        /*
         * এখানে তোমার actual progress repositories-এর
         * clear/reset methods বসবে।
         *
         * উদাহরণ:
         *
         * progressRepository.clearAll();
         * practiceRepository.clearAll();
         * mockRepository.clearAll();
         */


        Toast.makeText(
                requireContext(),
                "Progress cleared",
                Toast.LENGTH_SHORT
        ).show();
    }


    // ============================================================
    // RATE APP
    // ============================================================

    private void openPlayStore() {

        String packageName =
                requireContext()
                        .getPackageName();


        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                    "market://details?id="
                                            + packageName
                            )
                    );


            startActivity(intent);

        } catch (Exception e) {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                    "https://play.google.com/store/apps/details?id="
                                            + packageName
                            )
                    );


            startActivity(intent);
        }
    }


    // ============================================================
    // ABOUT DIALOG
    // ============================================================

    private void showAboutDialog() {

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle(
                        "Question Bank Practice Exam"
                )
                .setMessage(
                        "Practice questions, mock tests and track your daily progress.\n\n"
                                + "Version 1.0"
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }


    // ============================================================
    // ON RESUME
    // ============================================================

    @Override
    public void onResume() {

        super.onResume();


        if (binding != null) {

            loadSettings();
        }
    }


    // ============================================================
    // DESTROY VIEW
    // ============================================================

    @Override
    public void onDestroyView() {

        binding = null;

        super.onDestroyView();
    }
}