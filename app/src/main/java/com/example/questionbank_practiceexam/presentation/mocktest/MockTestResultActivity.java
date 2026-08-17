package com.example.questionbank_practiceexam.presentation.mocktest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.questionbank_practiceexam.data.local.repository.MockTestResultRepository;
import com.example.questionbank_practiceexam.databinding.ActivityMockTestResultBinding;
import com.example.questionbank_practiceexam.presentation.main.MainActivity;

import java.util.Locale;

public class MockTestResultActivity
        extends AppCompatActivity {

    // ============================================================
    // VIEW BINDING
    // ============================================================

    private ActivityMockTestResultBinding binding;


    // ============================================================
    // RESULT DATA
    // ============================================================

    private int totalQuestions = 0;

    private int correctCount = 0;

    private int wrongCount = 0;

    private int skippedCount = 0;

    private double totalMark = 0.0;


    // ============================================================
    // EXTRA DATA
    // ============================================================

    private int unitId = -1;

    private int yearId = -1;

    private String unitName = "";

    private String yearTitle = "";

    private int[] selectedSubjectIds;


    /*
     * Question-wise review data
     */
    private String reviewData = "";


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);


        binding =
                ActivityMockTestResultBinding.inflate(
                        getLayoutInflater()
                );


        setContentView(
                binding.getRoot()
        );


        // ========================================================
        // READ RESULT
        // ========================================================

        readResultData();

        saveMockTestResult();


        // ========================================================
        // SHOW RESULT
        // ========================================================

        showResult();


        // ========================================================
        // BUTTONS
        // ========================================================

        setupButtons();
    }


    // ============================================================
    // READ RESULT DATA
    // ============================================================

    private void readResultData() {

        Intent intent =
                getIntent();


        if (intent == null) {

            return;
        }


        totalQuestions =
                intent.getIntExtra(
                        "total_questions",
                        0
                );


        correctCount =
                intent.getIntExtra(
                        "correct_count",
                        0
                );


        wrongCount =
                intent.getIntExtra(
                        "wrong_count",
                        0
                );


        skippedCount =
                intent.getIntExtra(
                        "skipped_count",
                        0
                );


        totalMark =
                intent.getDoubleExtra(
                        "total_mark",
                        0.0
                );


        unitId =
                intent.getIntExtra(
                        "unit_id",
                        -1
                );


        yearId =
                intent.getIntExtra(
                        "year_id",
                        -1
                );


        unitName =
                intent.getStringExtra(
                        "unit_name"
                );


        yearTitle =
                intent.getStringExtra(
                        "year_title"
                );


        selectedSubjectIds =
                intent.getIntArrayExtra(
                        "subject_ids"
                );


        reviewData =
                intent.getStringExtra(
                        "review_data"
                );


        if (unitName == null) {

            unitName = "";
        }


        if (yearTitle == null) {

            yearTitle = "";
        }


        if (reviewData == null) {

            reviewData = "";
        }
    }


    // ============================================================
    // SHOW RESULT
    // ============================================================

    private void showResult() {

        // ========================================================
        // CORRECT
        // ========================================================

        binding.correctText.setText(
                String.valueOf(
                        correctCount
                )
        );


        // ========================================================
        // WRONG
        // ========================================================

        binding.wrongText.setText(
                String.valueOf(
                        wrongCount
                )
        );


        // ========================================================
        // ATTEMPTED
        // ========================================================

        binding.totalQuestionText.setText(
                String.format(
                        Locale.getDefault(),
                        "%d/%d",
                        totalQuestions - skippedCount,
                        totalQuestions
                )
        );


        // ========================================================
        // NEGATIVE MARK
        // ========================================================

        double negativeMark =
                wrongCount * 0.25;


        binding.negativeMarkText.setText(
                String.format(
                        Locale.getDefault(),
                        "-%.2f",
                        negativeMark
                )
        );


        // ========================================================
        // SCORE
        // ========================================================

        binding.scoreText.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f",
                        totalMark
                )
        );


        // ========================================================
        // TOTAL SCORE
        // ========================================================

        binding.scoreTotalText.setText(
                String.format(
                        Locale.getDefault(),
                        "/%d",
                        totalQuestions
                )
        );


        // ========================================================
        // SCORE PROGRESS
        // ========================================================

        int progress = 0;


        if (totalQuestions > 0) {

            progress =
                    (int) Math.round(
                            (totalMark /
                                    totalQuestions)
                                    * 100
                    );
        }


        progress =
                Math.max(
                        0,
                        Math.min(
                                100,
                                progress
                        )
                );


        binding.scoreProgress.setProgress(
                progress
        );


        // ========================================================
        // RESULT MESSAGE
        // ========================================================

        setResultMessage();
    }


    // ============================================================
    // RESULT MESSAGE
    // ============================================================

    private void setResultMessage() {

        if (totalQuestions <= 0) {

            binding.congratulationText.setText(
                    "পরীক্ষা সম্পন্ন"
            );


            binding.resultMessage.setText(
                    "কোনো প্রশ্ন পাওয়া যায়নি।"
            );


            return;
        }


        double percentage =
                (totalMark /
                        totalQuestions)
                        * 100;


        if (percentage >= 80) {

            binding.congratulationText.setText(
                    "অভিনন্দন! 🎉"
            );


            binding.resultMessage.setText(
                    "দারুণ ফলাফল! আপনার প্রস্তুতি খুব ভালো।"
            );

        } else if (percentage >= 60) {

            binding.congratulationText.setText(
                    "খুব ভালো! 👏"
            );


            binding.resultMessage.setText(
                    "আপনার ফলাফল ভালো হয়েছে। আরও একটু অনুশীলন করুন।"
            );

        } else if (percentage >= 40) {

            binding.congratulationText.setText(
                    "ভালো চেষ্টা! 👍"
            );


            binding.resultMessage.setText(
                    "আরও অনুশীলন করলে ফলাফল আরও ভালো হবে।"
            );

        } else {

            binding.congratulationText.setText(
                    "চেষ্টা চালিয়ে যান 💪"
            );


            binding.resultMessage.setText(
                    "ভুলগুলো দেখে আবার অনুশীলন করুন।"
            );
        }
    }


    // ============================================================
    // BUTTONS
    // ============================================================

    private void setupButtons() {

        // ========================================================
        // HOME
        // ========================================================

        binding.homeButton.setOnClickListener(
                v -> goHome()
        );


        // ========================================================
        // CHECK ANSWERS
        // ========================================================

        binding.checkAnswersButton.setOnClickListener(
                v -> checkAnswers()
        );


        // ========================================================
        // TOOLBAR BACK
        // ========================================================

        binding.toolbar.setNavigationOnClickListener(
                v -> goHome()
        );
    }


    // ============================================================
    // HOME
    // ============================================================

    private void goHome() {

        Intent intent =
                new Intent(
                        MockTestResultActivity.this,
                        MainActivity.class
                );


        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );


        startActivity(
                intent
        );


        finish();
    }


    // ============================================================
    // CHECK ANSWERS
    // ============================================================

    private void checkAnswers() {

        if (reviewData == null ||
                reviewData.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "উত্তরের তথ্য পাওয়া যায়নি",
                    Toast.LENGTH_SHORT
            ).show();


            return;
        }


        Intent intent =
                new Intent(
                        MockTestResultActivity.this,
                        MockTestAnswerReviewActivity.class
                );


        intent.putExtra(
                "review_data",
                reviewData
        );


        startActivity(
                intent
        );
    }


    private void saveMockTestResult() {

        MockTestResultRepository repository =
                new MockTestResultRepository(
                        getApplicationContext()
                );

        int attemptedCount =
                correctCount + wrongCount;

        double negativeMark =
                wrongCount * 0.25;

        double successRate = 0;

        if (totalQuestions > 0) {

            successRate =
                    (correctCount * 100.0)
                            / totalQuestions;
        }

        String subjectIds = "";

        if (selectedSubjectIds != null &&
                selectedSubjectIds.length > 0) {

            StringBuilder builder =
                    new StringBuilder();

            for (int i = 0;
                 i < selectedSubjectIds.length;
                 i++) {

                if (i > 0) {
                    builder.append(",");
                }

                builder.append(
                        selectedSubjectIds[i]
                );
            }

            subjectIds =
                    builder.toString();
        }

        repository.saveResult(
                unitId,
                yearId,
                unitName,
                yearTitle,
                totalQuestions,
                correctCount,
                wrongCount,
                skippedCount,
                attemptedCount,
                totalMark,
                negativeMark,
                successRate,
                subjectIds
        );

        repository.close();
    }


    // ============================================================
    // BACK PRESSED
    // ============================================================

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        goHome();
    }




    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        binding = null;

        super.onDestroy();
    }
}