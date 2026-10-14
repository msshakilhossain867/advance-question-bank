package com.example.questionbank_practiceexam.presentation.home;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.NotificationPrefs;
import com.example.questionbank_practiceexam.data.local.repository.DailyMissionRepository;
import com.example.questionbank_practiceexam.domain.DailyMissionNotificationScheduler;
import com.example.questionbank_practiceexam.presentation.adapters.NotificationAdapter;
import com.example.questionbank_practiceexam.presentation.report.ProgressReportActivity;
import com.example.questionbank_practiceexam.data.local.entity.University;
import com.example.questionbank_practiceexam.data.local.repository.BookmarkRepository;
import com.example.questionbank_practiceexam.data.local.repository.MockTestResultRepository;
import com.example.questionbank_practiceexam.data.local.repository.PracticeResultRepository;
import com.example.questionbank_practiceexam.data.local.repository.ProgressDailyRepository;
import com.example.questionbank_practiceexam.data.local.repository.RecentLearningRepository;
import com.example.questionbank_practiceexam.data.local.repository.StudyTimeRepository;
import com.example.questionbank_practiceexam.data.local.repository.UniversityRepository;
import com.example.questionbank_practiceexam.databinding.FragmentHomeBinding;
import com.example.questionbank_practiceexam.presentation.bookmarks.BookmarkActivity;
import com.example.questionbank_practiceexam.presentation.questionview.QuestionsActivity;
import com.example.questionbank_practiceexam.presentation.unlist.UniversityListActivity;
import com.example.questionbank_practiceexam.presentation.years.YearsActivity;

import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private UniversityRepository universityRepository;
    private DailyMissionRepository dailyMissionRepository;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 2001;


    // ============================================================
    // CREATE VIEW
    // ============================================================

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHomeBinding.inflate(
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

        super.onViewCreated(view, savedInstanceState);

        // --------------------------------------------------------
        // University Repository
        // --------------------------------------------------------

        universityRepository =
                new UniversityRepository(requireContext());


        // --------------------------------------------------------
        // Load Home Data
        // --------------------------------------------------------
        dailyMissionRepository =
                new DailyMissionRepository(
                        requireContext()
                );


        // ============================================================
        // DAILY MISSION NOTIFICATION
        // ============================================================




        // ============================================================
        // NOTIFICATION PERMISSION - ANDROID 13+
        // ============================================================


setupDailyMissionNotification() ;

        updateDailyMissionStatus();

        loadUniversity();
        loadTodayProgress();
        practiceRecent();
        todayBookmarkList();
        recentMockTest();
        loadRecentLearning();



        // --------------------------------------------------------
        // Click Listeners
        // --------------------------------------------------------

        setupClickListeners();
        updateNotificationDot();
    }


    // ============================================================
    // LOAD TODAY'S PROGRESS
    // ============================================================

    private void loadTodayProgress() {

        ProgressDailyRepository repository =
                new ProgressDailyRepository(requireContext());


        ProgressDailyRepository.ProgressDaily progress =
                repository.getTodayProgress();


        // Default values
        binding.studyTimeText.setText("—");
        binding.solvedText.setText("0");
        binding.accuracyText.setText("0%");
        binding.mockTestsText.setText("0");


        if (progress == null) {

            repository.close();
            return;
        }


        // --------------------------------------------------------
        // Total Solved
        // --------------------------------------------------------

        int practiceTotal =
                progress.getPracticeTotal();

        int mockTotal =
                progress.getMockTotal();


        int solved =
                practiceTotal + mockTotal;


        // --------------------------------------------------------
        // Correct
        // --------------------------------------------------------

        int practiceCorrect =
                progress.getPracticeCorrect();

        int mockCorrect =
                progress.getMockCorrect();


        int correct =
                practiceCorrect + mockCorrect;


        // --------------------------------------------------------
        // Skipped
        // --------------------------------------------------------

        int practiceSkipped =
                progress.getPracticeSkipped();

        int mockSkipped =
                progress.getMockSkipped();


        // --------------------------------------------------------
        // Attempted
        // --------------------------------------------------------

        int attempted =
                (practiceTotal - practiceSkipped)
                        +
                        (mockTotal - mockSkipped);


        // --------------------------------------------------------
        // Accuracy
        // --------------------------------------------------------

        double accuracy = 0;


        if (attempted > 0) {

            accuracy =
                    (correct * 100.0) / attempted;
        }


        // --------------------------------------------------------
        // Update UI
        // --------------------------------------------------------

        binding.solvedText.setText(
                String.valueOf(solved)
        );


        binding.accuracyText.setText(
                String.format(
                        Locale.getDefault(),
                        "%.0f%%",
                        accuracy
                )
        );


        binding.mockTestsText.setText(
                String.valueOf(
                        progress.getMockTestCount()
                )
        );


        /*
         * এখনো Study Time database থেকে calculate করার
         * logic দেওয়া হয়নি।
         *
         * তাই আপাতত —
         */

       loadStudyTime();


        repository.close();
    }






    private void setupDailyMissionNotification() {

        // ============================================================
        // ANDROID 13+
        // ============================================================

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                    requireContext().checkSelfPermission(
                            android.Manifest.permission.POST_NOTIFICATIONS
                    )
                            != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(
                        new String[]{
                                android.Manifest.permission.POST_NOTIFICATIONS
                        },
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );

                // Permission না পাওয়া পর্যন্ত
                // scheduler চালাবো না

                return;
            }
        }


        // ============================================================
        // ANDROID 12 OR BELOW
        // OR PERMISSION ALREADY GRANTED
        // ============================================================

        DailyMissionNotificationScheduler.schedule(
                requireContext()
                        .getApplicationContext()
        );
    }


    // ============================================================
    // LOAD TARGET UNIVERSITY
    // ============================================================

    private void loadUniversity() {

        if (universityRepository == null) {
            return;
        }


        University university =
                universityRepository.getTargetUniversity();


        if (university == null) {

            // কোনো target university না থাকলে
            binding.unName.setText("No University");
            binding.unShortName.setText("Select Target University");

            return;
        }


        // --------------------------------------------------------
        // University Name
        // --------------------------------------------------------

        binding.unName.setText(
                university.getFullName()
        );


        // --------------------------------------------------------
        // University Short Name
        // --------------------------------------------------------

        binding.unShortName.setText(
                university.getName() + " All Unit"
        );


        // --------------------------------------------------------
        // University Card Click
        // --------------------------------------------------------

        binding.btnMoveYearActivity.setOnClickListener(v -> {

            int universityId =
                    university.getId();


            String universityName =
                    university.getName();


            Intent intent =
                    new Intent(
                            requireContext(),
                            YearsActivity.class
                    );


            intent.putExtra(
                    "university_id",
                    universityId
            );


            intent.putExtra(
                    "university_name",
                    universityName
            );


            startActivity(intent);
        });
    }
    private void updateDailyMissionStatus() {

        if (binding == null) {
            return;
        }


        if (dailyMissionRepository == null) {

            dailyMissionRepository =
                    new DailyMissionRepository(
                            requireContext()
                    );
        }


        // ============================================================
        // GET TODAY'S MISSION DATA
        // ============================================================

        int completed =
                dailyMissionRepository
                        .getCompletedMissionCount();


        int total =
                dailyMissionRepository
                        .getTotalMissionCount();


        // ============================================================
        // CALCULATE PERCENTAGE
        // ============================================================

        int percentage = 0;


        if (total > 0) {

            percentage =
                    (completed * 100) / total;
        }


        // ============================================================
        // UPDATE UI
        // ============================================================

        binding.dailyMissionProgressBar.setProgress(
                percentage
        );


        binding.dailyMissionPercentText.setText(
                percentage + "%"
        );


        binding.dailyMissionProgressText.setText(
                completed
                        + " of "
                        + total
                        + " missions completed"
        );


        // ============================================================
        // ALL COMPLETED
        // ============================================================

        if (
                total > 0
                        && completed == total
        ) {

            binding.dailyMissionCompletedText.setVisibility(
                    View.VISIBLE
            );

            binding.dailyMissionProgressText.setText(
                    "Great job! Today's goal is complete."
            );

        } else {

            binding.dailyMissionCompletedText.setVisibility(
                    View.GONE
            );
        }
    }


    // ============================================================
    // CLICK LISTENERS
    // ============================================================

    private void setupClickListeners() {


        // --------------------------------------------------------
        // Menu
        // --------------------------------------------------------

        binding.btnMenu.setOnClickListener(v -> {

            /*
             * এখানে পরে Navigation Drawer open করবে।
             */
        });


        // --------------------------------------------------------
        // Notification
        // --------------------------------------------------------

        binding.btnNotification.setOnClickListener(v -> {

            /*
             * পরে NotificationActivity / Dialog
             * এখানে connect করতে পারবে।
             */

            showNotificationDialog();
        });

        binding.txtStartPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), UniversityListActivity.class));
            }
        });


        // ========================================================
        // READ QUESTIONS
        // ========================================================

        binding.nextVersityList.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            UniversityListActivity.class
                    );


            intent.putExtra(
                    "mode",
                    "read"
            );


            startActivity(intent);
        });


        // ========================================================
        // PRACTICE MCQ
        // ========================================================

        binding.practiceMcqCard.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            UniversityListActivity.class
                    );


            intent.putExtra(
                    "mode",
                    "practice"
            );


            startActivity(intent);
        });


        // ========================================================
        // MOCK TEST
        // ========================================================

        binding.mockTestCard.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            UniversityListActivity.class
                    );


            intent.putExtra(
                    "mode",
                    "mock_test"
            );


            startActivity(intent);
        });


        // ========================================================
        // BOOKMARKS
        // ========================================================

        binding.bookmarkCard.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            BookmarkActivity.class
                    );


            startActivity(intent);
        });


        // ========================================================
        // PROGRESS REPORT
        // ========================================================

        binding.progressReportCard.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            ProgressReportActivity.class
                    );


            startActivity(intent);
        });


        // ========================================================
        // TARGET UNIVERSITY - VIEW ALL
        // ========================================================

        binding.btnViewAllUni.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            UniversityListActivity.class
                    );


            startActivity(intent);
        });


        // ========================================================
        // RECENT ACTIVITY - VIEW ALL
        // ========================================================

        binding.btnViewAllRecent.setOnClickListener(v -> {

            /*
             * এখনো RecentActivity screen তৈরি করা হয়নি।
             *
             * Screen তৈরি হলে এখানে:
             *
             * startActivity(...)
             *
             * করবে।
             */
        });
    }


    private void showNotificationDialog() {

        Dialog dialog =
                new Dialog(requireContext());


        dialog.requestWindowFeature(
                Window.FEATURE_NO_TITLE
        );


        dialog.setContentView(
                R.layout.dialog_notifications
        );


        if (dialog.getWindow() != null) {

            dialog.getWindow()
                    .setBackgroundDrawableResource(
                            android.R.color.transparent
                    );

            dialog.getWindow()
                    .setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
        }


        RecyclerView recyclerView =
                dialog.findViewById(
                        R.id.notificationRecyclerView
                );


        TextView emptyText =
                dialog.findViewById(
                        R.id.emptyNotificationText
                );


        TextView markAllRead =
                dialog.findViewById(
                        R.id.markAllReadText
                );


        NotificationPrefs prefs =
                new NotificationPrefs(
                        requireContext()
                );


        List<NotificationPrefs.NotificationItem> list =
                prefs.getNotifications();


        // ============================================================
        // EMPTY
        // ============================================================

        if (list.isEmpty()) {

            emptyText.setVisibility(
                    View.VISIBLE
            );

            recyclerView.setVisibility(
                    View.GONE
            );

        } else {

            emptyText.setVisibility(
                    View.GONE
            );

            recyclerView.setVisibility(
                    View.VISIBLE
            );
        }


        // ============================================================
        // RECYCLER
        // ============================================================

        NotificationAdapter adapter =
                new NotificationAdapter(
                        list,
                        notificationId -> {

                            prefs.markAsRead(
                                    notificationId
                            );


                            // Reload dialog
                            List<NotificationPrefs.NotificationItem>
                                    updatedList =
                                    prefs.getNotifications();


                            NotificationAdapter newAdapter =
                                    new NotificationAdapter(
                                            updatedList,
                                            id -> {

                                                prefs.markAsRead(
                                                        id
                                                );

                                                showNotificationDialog();

                                            }
                                    );


                            recyclerView.setAdapter(
                                    newAdapter
                            );


                            updateNotificationDot();
                        }
                );


        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );


        recyclerView.setAdapter(
                adapter
        );


        // ============================================================
        // MARK ALL READ
        // ============================================================

        markAllRead.setOnClickListener(v -> {

            prefs.markAllAsRead();


            dialog.dismiss();


            updateNotificationDot();
        });


        dialog.setOnDismissListener(
                d -> updateNotificationDot()
        );


        dialog.show();


        if (dialog.getWindow() != null) {

            dialog.getWindow()
                    .setLayout(
                            (int) (
                                    getResources()
                                            .getDisplayMetrics()
                                            .widthPixels
                                            * 0.94
                            ),
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
        }
    }


    private void updateNotificationDot() {

        NotificationPrefs prefs =
                new NotificationPrefs(
                        requireContext()
                );


        if (prefs.hasUnreadNotifications()) {

            binding.notificationUnreadDot.setVisibility(
                    View.VISIBLE
            );

        } else {

            binding.notificationUnreadDot.setVisibility(
                    View.GONE
            );
        }
    }


    private void practiceRecent() {

        PracticeResultRepository repository =
                new PracticeResultRepository(requireContext());

        PracticeResultRepository.PracticeResult latestResult =
                repository.getLatestResult();

        if (latestResult != null) {

            int questions =
                    latestResult.getTotalQuestions();

            double accuracy =
                    latestResult.getSuccessRate();

            binding.tvPracticeSummary.setText(
                    questions
                            + " Questions • "
                            + String.format(
                            Locale.getDefault(),
                            "%.0f%% Accuracy",
                            accuracy
                    )
            );

            binding.tvPracticeTime.setText(
                    getTimeAgo(
                            latestResult.getCompletedAt()
                    )
            );

        } else {

            binding.tvPracticeSummary.setText(
                    "No Practice Test Yet"
            );

            binding.tvPracticeTime.setText("—");
        }
    }


    private void todayBookmarkList() {

        BookmarkRepository bookmarkRepository =
                new BookmarkRepository(requireContext());

        int todayBookmarks =
                bookmarkRepository.getTodayBookmarkCount();

        long latestBookmarkTime =
                bookmarkRepository.getLatestBookmarkTime();


        // ============================================================
        // TODAY BOOKMARK COUNT
        // ============================================================

        if (todayBookmarks == 0) {

            binding.tvBookmarkSummary.setText(
                    "No questions saved today"
            );

        } else {

            binding.tvBookmarkSummary.setText(
                    todayBookmarks
                            + " New Questions Today"
            );
        }


        // ============================================================
        // LAST BOOKMARK TIME
        // ============================================================

        if (latestBookmarkTime > 0) {

            binding.tvBookmarkTime.setText(
                    getTimeAgo(
                            latestBookmarkTime
                    )
            );

        } else {

            binding.tvBookmarkTime.setText("—");
        }
    }

    private void recentMockTest(){
        MockTestResultRepository mockTestRepository =
                new MockTestResultRepository(requireContext());

        MockTestResultRepository.MockTestResult latestResult =
                mockTestRepository.getLatestResult();

        if (latestResult != null) {

            int totalQuestions =
                    latestResult.getTotalQuestions();

            double successRate =
                    latestResult.getSuccessRate();

            binding.tvMockTestSummary.setText(
                    "Score: "
                            + latestResult.getTotalMark()
                            + "/"
                            + totalQuestions
                            + " • "
                            + String.format(
                            Locale.getDefault(),
                            "%.0f%% Accuracy",
                            successRate
                    )
            );

            binding.tvMockTestTime.setText(
                    getTimeAgo(
                            latestResult.getCompletedAt()
                    )
            );

        } else {

            binding.tvMockTestSummary.setText(
                    "No Mock Test Taken"
            );

            binding.tvMockTestTime.setText("—");
        }
    }

    private void loadStudyTime() {

        StudyTimeRepository repository =
                new StudyTimeRepository(requireContext());

        long totalSeconds =
                repository.getTotalStudySeconds();

        binding.studyTimeText.setText(
                formatStudyTime(totalSeconds)
        );
    }

    private String formatStudyTime(long totalSeconds) {

        long totalMinutes =
                totalSeconds / 60;

        long hours =
                totalMinutes / 60;

        long minutes =
                totalMinutes % 60;

        if (hours > 0) {

            if (minutes > 0) {

                return hours + "h " + minutes + "m";

            } else {

                return hours + "h";
            }
        }

        return minutes + "m";
    }


    private String getTimeAgo(long timestamp) {

        long difference =
                System.currentTimeMillis() - timestamp;

        long minutes =
                difference / (60 * 1000);

        long hours =
                difference / (60 * 60 * 1000);

        long days =
                difference / (24 * 60 * 60 * 1000);

        if (minutes < 1) {

            return "Just Now";

        } else if (minutes < 60) {

            return minutes + " Min Ago";

        } else if (hours < 24) {

            return hours + " Hours Ago";

        } else if (days == 1) {

            return "Yesterday";

        } else {

            return days + " Days Ago";
        }
    }

    private void loadRecentLearning() {

        RecentLearningRepository repository =
                new RecentLearningRepository(requireContext());

        RecentLearningRepository.RecentLearning recent =
                repository.getRecentLearning();


        if (recent != null) {

            binding.tvRecentSubject.setText(
                    recent.getSubjectName()
            );

            binding.tvRecentChapter.setText(
                    recent.getChapterName()
            );

            binding.recentProgressBar.setProgress(
                    recent.getProgressPercent()
            );

            binding.tvRecentProgress.setText(
                    recent.getProgressPercent() + "%"
            );


            binding.tvResumeReading.setVisibility(
                    View.VISIBLE
            );


            binding.tvResumeReading.setOnClickListener(
                    v -> {

                        Intent intent =
                                new Intent(
                                        requireContext(),
                                        QuestionsActivity.class
                                );


                        intent.putExtra(
                                "subject_id",
                                recent.getSubjectId()
                        );


                        intent.putExtra(
                                "subject_name",
                                recent.getSubjectName()
                        );


                        intent.putExtra(
                                "content_position",
                                recent.getContentPosition()
                        );


                        startActivity(intent);
                    }
            );

        } else {

            binding.tvRecentSubject.setText(
                    "Start Learning"
            );

            binding.tvRecentChapter.setText(
                    "Choose a subject to begin"
            );

            binding.recentProgressBar.setProgress(0);

            binding.tvRecentProgress.setText(
                    "0%"
            );

            binding.tvResumeReading.setVisibility(
                    View.GONE
            );
        }


        repository.close();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (
                requestCode ==
                        NOTIFICATION_PERMISSION_REQUEST_CODE
        ) {

            if (
                    grantResults.length > 0
                            &&
                            grantResults[0] ==
                                    PackageManager.PERMISSION_GRANTED
            ) {

                // Permission পাওয়া গেছে
                // এখন scheduler চালু হবে

                DailyMissionNotificationScheduler.schedule(
                        requireContext()
                                .getApplicationContext()
                );
            }
        }
    }


    // ============================================================
    // ON RESUME
    // ============================================================

    @Override
    public void onResume() {

        super.onResume();

        /*
         * Practice / Mock Test থেকে Home-এ ফিরে এলে
         * Today's Study Overview যেন নতুন data দেখায়।
         */

        if (binding != null) {

            loadTodayProgress();
            loadUniversity();
            loadStudyTime();
            practiceRecent();
            todayBookmarkList();
            recentMockTest();
            updateNotificationDot();
             loadRecentLearning();

            updateDailyMissionStatus();
        }
    }


    // ============================================================
    // DESTROY VIEW
    // ============================================================

    @Override
    public void onDestroyView() {

        super.onDestroyView();


        if (universityRepository != null) {

            universityRepository.close();

            universityRepository = null;
        }

        if (dailyMissionRepository != null) {

            dailyMissionRepository.close();

            dailyMissionRepository = null;
        }


        binding = null;
    }
}