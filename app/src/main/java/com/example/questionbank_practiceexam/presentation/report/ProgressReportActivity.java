package com.example.questionbank_practiceexam.presentation.report;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.questionbank_practiceexam.data.local.repository.ProgressDailyRepository;
import com.example.questionbank_practiceexam.databinding.ActivityProgressReportBinding;

import java.util.Locale;

public class ProgressReportActivity extends AppCompatActivity {

    private ActivityProgressReportBinding binding;

    private ProgressDailyRepository progressRepository;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =
                ActivityProgressReportBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(
                binding.getRoot()
        );


        // ========================================================
        // REPOSITORY
        // ========================================================

        progressRepository =
                new ProgressDailyRepository(
                        getApplicationContext()
                );


        // ========================================================
        // LOAD TODAY
        // ========================================================

        loadProgress();
    }


    // ============================================================
    // LOAD PROGRESS
    // ============================================================

    private void loadProgress() {

        ProgressDailyRepository.ProgressDaily progress =
                progressRepository.getTodayProgress();


        if (progress == null) {

            showEmpty();

            return;
        }


        showProgress(progress);
    }


    // ============================================================
    // SHOW PROGRESS
    // ============================================================

    private void showProgress(
            ProgressDailyRepository.ProgressDaily progress
    ) {

        // ========================================================
        // DATE
        // ========================================================

        binding.dateText.setText(
                "Today's Progress"
        );


        // ========================================================
        // READ
        // ========================================================

        binding.readCountText.setText(
                String.valueOf(
                        progress.getReadCount()
                )
        );


        // ========================================================
        // PRACTICE TEST COUNT
        // ========================================================

        binding.practiceCountText.setText(
                String.valueOf(
                        progress.getPracticeCount()
                )
        );


        // ========================================================
        // PRACTICE QUESTIONS
        // ========================================================

        binding.practiceQuestionText.setText(
                "Questions: " +
                        progress.getPracticeTotal()
        );


        // ========================================================
        // PRACTICE SUCCESS RATE
        // ========================================================

        binding.practiceSuccessRateText.setText(
                "Success Rate: " +
                        formatPercentage(
                                progress.getPracticeSuccessRate()
                        )
        );


        // ========================================================
        // PRACTICE PROGRESS BAR
        // ========================================================

        binding.practiceProgress.setProgress(
                (int) Math.round(
                        progress.getPracticeSuccessRate()
                )
        );


        // ========================================================
        // MOCK TEST COUNT
        // ========================================================

        binding.mockTestCountText.setText(
                progress.getMockTestCount() +
                        " Tests"
        );


        // ========================================================
        // MOCK SUCCESS RATE
        // ========================================================

        binding.mockSuccessRateText.setText(
                "Success Rate: " +
                        formatPercentage(
                                progress.getMockSuccessRate()
                        )
        );


        // ========================================================
        // MOCK PROGRESS BAR
        // ========================================================

        binding.mockProgress.setProgress(
                (int) Math.round(
                        progress.getMockSuccessRate()
                )
        );


        // ========================================================
        // TODAY'S STATISTICS
        // ========================================================

        int totalCorrect =
                progress.getPracticeCorrect()
                        + progress.getMockCorrect();


        int totalWrong =
                progress.getPracticeWrong()
                        + progress.getMockWrong();


        int totalSkipped =
                progress.getPracticeSkipped()
                        + progress.getMockSkipped();


        binding.correctText.setText(
                "Correct\n" +
                        totalCorrect
        );


        binding.wrongText.setText(
                "Wrong\n" +
                        totalWrong
        );


        binding.skippedText.setText(
                "Skipped\n" +
                        totalSkipped
        );


        // ========================================================
        // SHOW DATA
        // ========================================================

        binding.emptyProgressLayout.setVisibility(
                android.view.View.GONE
        );
    }


    // ============================================================
    // EMPTY
    // ============================================================

    private void showEmpty() {

        binding.readCountText.setText("0");

        binding.practiceCountText.setText("0");

        binding.practiceQuestionText.setText(
                "Questions: 0"
        );

        binding.practiceSuccessRateText.setText(
                "Success Rate: 0%"
        );

        binding.practiceProgress.setProgress(0);


        binding.mockTestCountText.setText(
                "0 Tests"
        );

        binding.mockSuccessRateText.setText(
                "Success Rate: 0%"
        );

        binding.mockProgress.setProgress(0);


        binding.correctText.setText(
                "Correct\n0"
        );

        binding.wrongText.setText(
                "Wrong\n0"
        );

        binding.skippedText.setText(
                "Skipped\n0"
        );


        binding.emptyProgressLayout.setVisibility(
                android.view.View.VISIBLE
        );
    }


    // ============================================================
    // FORMAT PERCENTAGE
    // ============================================================

    private String formatPercentage(
            double percentage
    ) {

        return String.format(
                Locale.getDefault(),
                "%.2f%%",
                percentage
        );
    }


    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        if (progressRepository != null) {

            progressRepository.close();

            progressRepository = null;
        }

        binding = null;

        super.onDestroy();
    }
}