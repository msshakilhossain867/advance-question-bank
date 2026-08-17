package com.example.questionbank_practiceexam.presentation.exam;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.repository.PracticeResultRepository;
import com.example.questionbank_practiceexam.databinding.ActivityPracticeResultBinding;
import com.example.questionbank_practiceexam.presentation.adapters.SubjectPerformanceAdapter;
import com.example.questionbank_practiceexam.presentation.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class PracticeResultActivity extends AppCompatActivity {

    private ActivityPracticeResultBinding binding;

    // ============================================================
    // RESULT DATA
    // ============================================================

    private int correctCount = 0;
    private int wrongCount = 0;
    private int skippedCount = 0;
    private int totalQuestions = 0;

    // ============================================================
    // SUBJECT PERFORMANCE
    // ============================================================

    private ArrayList<String> subjectNames =
            new ArrayList<>();

    private ArrayList<Integer> subjectCorrect =
            new ArrayList<>();

    private ArrayList<Integer> subjectTotal =
            new ArrayList<>();


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding =
                ActivityPracticeResultBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(
                binding.getRoot()
        );

        // ========================================================
        // GET RESULT
        // ========================================================

        getResultData();

        savePracticeResult();

        // ========================================================
        // TOOLBAR
        // ========================================================

        setupToolbar();

        // ========================================================
        // SHOW MAIN RESULT
        // ========================================================

        showResult();

        // ========================================================
        // SUBJECT PERFORMANCE
        // ========================================================

        setupSubjectPerformance();

        // ========================================================
        // BUTTONS
        // ========================================================

        setupButtons();
    }


    // ============================================================
    // GET RESULT DATA
    // ============================================================

    private void getResultData() {

        // ========================================================
        // MAIN RESULT
        // ========================================================

        correctCount =
                getIntent().getIntExtra(
                        "correct_count",
                        0
                );

        wrongCount =
                getIntent().getIntExtra(
                        "wrong_count",
                        0
                );

        skippedCount =
                getIntent().getIntExtra(
                        "skipped_count",
                        0
                );

        totalQuestions =
                getIntent().getIntExtra(
                        "total_questions",
                        0
                );


        // ========================================================
        // SUBJECT NAMES
        // ========================================================

        ArrayList<String> names =
                getIntent().getStringArrayListExtra(
                        "subject_names"
                );

        if (names != null) {

            subjectNames.clear();

            subjectNames.addAll(
                    names
            );
        }


        // ========================================================
        // SUBJECT CORRECT
        // ========================================================

        ArrayList<Integer> correct =
                getIntent().getIntegerArrayListExtra(
                        "subject_correct"
                );

        if (correct != null) {

            subjectCorrect.clear();

            subjectCorrect.addAll(
                    correct
            );
        }


        // ========================================================
        // SUBJECT TOTAL
        // ========================================================

        ArrayList<Integer> totals =
                getIntent().getIntegerArrayListExtra(
                        "subject_total"
                );

        if (totals != null) {

            subjectTotal.clear();

            subjectTotal.addAll(
                    totals
            );
        }


        // ========================================================
        // SAFETY
        // ========================================================

        if (totalQuestions <= 0) {

            totalQuestions =
                    correctCount
                            + wrongCount
                            + skippedCount;
        }
    }



    private void savePracticeResult() {

        PracticeResultRepository repository =
                new PracticeResultRepository(
                        getApplicationContext()
                );

        String subjectData = "";

        try {

            org.json.JSONArray array =
                    new org.json.JSONArray();

            for (int i = 0; i < subjectNames.size(); i++) {

                org.json.JSONObject object =
                        new org.json.JSONObject();

                object.put(
                        "subject_name",
                        subjectNames.get(i)
                );

                object.put(
                        "correct",
                        i < subjectCorrect.size()
                                ? subjectCorrect.get(i)
                                : 0
                );

                object.put(
                        "total",
                        i < subjectTotal.size()
                                ? subjectTotal.get(i)
                                : 0
                );

                array.put(object);
            }

            subjectData =
                    array.toString();

        } catch (Exception e) {

            e.printStackTrace();
        }

        repository.saveResult(
                totalQuestions,
                correctCount,
                wrongCount,
                skippedCount,
                subjectData
        );

        repository.close();
    }


    // ============================================================
    // TOOLBAR
    // ============================================================

    private void setupToolbar() {

        binding.toolbar.setNavigationOnClickListener(
                v -> finish()
        );


        binding.toolbar.setOnMenuItemClickListener(
                item -> {

                    if (item.getItemId() ==
                            R.id.action_share) {

                        shareResult();

                        return true;
                    }

                    return false;
                }
        );
    }


    // ============================================================
    // SHOW RESULT
    // ============================================================

    private void showResult() {

        // ========================================================
        // PERCENTAGE
        // ========================================================

        int percentage = 0;

        if (totalQuestions > 0) {

            percentage =
                    Math.round(
                            (correctCount * 100f)
                                    / totalQuestions
                    );
        }


        // ========================================================
        // RESULT TITLE
        // ========================================================

        if (percentage >= 80) {

            binding.resultTitle.setText(
                    "Great Job! 🎉"
            );

            binding.resultSubtitle.setText(
                    "Excellent performance!"
            );

        } else if (percentage >= 50) {

            binding.resultTitle.setText(
                    "Good Job! 👍"
            );

            binding.resultSubtitle.setText(
                    "Keep practicing to improve."
            );

        } else {

            binding.resultTitle.setText(
                    "Keep Practicing 💪"
            );

            binding.resultSubtitle.setText(
                    "Don't give up. Try again!"
            );
        }


        // ========================================================
        // SCORE
        // ========================================================

        binding.scoreText.setText(
                String.valueOf(correctCount)
        );

        binding.totalScoreText.setText(
                "/ " + totalQuestions
        );

        binding.percentageText.setText(
                percentage + "%"
        );


        // ========================================================
        // STATISTICS
        // ========================================================

        binding.correctCount.setText(
                String.valueOf(correctCount)
        );

        binding.wrongCount.setText(
                String.valueOf(wrongCount)
        );

        binding.skippedCount.setText(
                String.valueOf(skippedCount)
        );


        // ========================================================
        // SCORE RING
        // ========================================================

        /*
         * তোমার ScoreRingView-তে setScore() নেই।
         *
         * সেখানে setPercentage() আছে।
         */

        binding.scoreRing.setPercentage(
                percentage
        );
    }


    // ============================================================
    // SUBJECT PERFORMANCE
    // ============================================================

    private void setupSubjectPerformance() {

        // ========================================================
        // CHECK DATA
        // ========================================================

        if (subjectNames == null ||
                subjectNames.isEmpty()) {

            binding.subjectPerformanceRecycler
                    .setAdapter(null);

            return;
        }


        // ========================================================
        // GRID 2 COLUMNS
        // ========================================================

        GridLayoutManager layoutManager =
                new GridLayoutManager(
                        this,
                        2
                );


        binding.subjectPerformanceRecycler
                .setLayoutManager(
                        layoutManager
                );


        binding.subjectPerformanceRecycler
                .setNestedScrollingEnabled(
                        false
                );


        // ========================================================
        // PERFORMANCE LIST
        // ========================================================

        List<SubjectPerformance> performanceList =
                new ArrayList<>();


        int size =
                subjectNames.size();


        for (int i = 0; i < size; i++) {

            String name =
                    subjectNames.get(i);


            int correct = 0;

            int total = 0;


            // ====================================================
            // CORRECT
            // ====================================================

            if (i < subjectCorrect.size()) {

                Integer value =
                        subjectCorrect.get(i);

                if (value != null) {

                    correct = value;
                }
            }


            // ====================================================
            // TOTAL
            // ====================================================

            if (i < subjectTotal.size()) {

                Integer value =
                        subjectTotal.get(i);

                if (value != null) {

                    total = value;
                }
            }


            // ====================================================
            // ADD
            // ====================================================

            performanceList.add(
                    new SubjectPerformance(
                            name,
                            correct,
                            total
                    )
            );
        }


        // ========================================================
        // ADAPTER
        // ========================================================

        SubjectPerformanceAdapter adapter =
                new SubjectPerformanceAdapter(
                        performanceList
                );


        binding.subjectPerformanceRecycler
                .setAdapter(
                        adapter
                );
    }


    // ============================================================
    // BUTTONS
    // ============================================================

    private void setupButtons() {

        // ========================================================
        // Back Home
        // ========================================================

        binding.backHomesButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PracticeResultActivity.this,
                            MainActivity.class
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);

            finish();
        });


        // ========================================================
        // PRACTICE AGAIN
        // ========================================================

        binding.practiceAgainButton.setOnClickListener(
                v -> {

                    finish();

                }
        );
    }


    // ============================================================
    // SHARE RESULT
    // ============================================================

    private void shareResult() {

        int percentage = 0;

        if (totalQuestions > 0) {

            percentage =
                    Math.round(
                            (correctCount * 100f)
                                    / totalQuestions
                    );
        }


        String shareText =
                "Practice Result\n\n"
                        + "Score: "
                        + correctCount
                        + " / "
                        + totalQuestions
                        + "\n"
                        + "Percentage: "
                        + percentage
                        + "%\n"
                        + "Correct: "
                        + correctCount
                        + "\n"
                        + "Wrong: "
                        + wrongCount
                        + "\n"
                        + "Skipped: "
                        + skippedCount;


        Intent intent =
                new Intent(
                        Intent.ACTION_SEND
                );


        intent.setType(
                "text/plain"
        );


        intent.putExtra(
                Intent.EXTRA_TEXT,
                shareText
        );


        startActivity(
                Intent.createChooser(
                        intent,
                        "Share Result"
                )
        );
    }


    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        binding = null;
    }


    // ============================================================
    // SUBJECT PERFORMANCE MODEL
    // ============================================================

    public static class SubjectPerformance {

        private final String subjectName;

        private final int correct;

        private final int total;


        public SubjectPerformance(
                String subjectName,
                int correct,
                int total
        ) {

            this.subjectName =
                    subjectName;

            this.correct =
                    correct;

            this.total =
                    total;
        }


        // ========================================================
        // GET SUBJECT NAME
        // ========================================================

        public String getSubjectName() {

            return subjectName;
        }


        // ========================================================
        // GET CORRECT
        // ========================================================

        public int getCorrect() {

            return correct;
        }


        // ========================================================
        // GET TOTAL
        // ========================================================

        public int getTotal() {

            return total;
        }


        // ========================================================
        // GET PERCENTAGE
        // ========================================================

        public int getPercentage() {

            if (total <= 0) {

                return 0;
            }

            return Math.round(
                    (correct * 100f)
                            / total
            );
        }
    }
}