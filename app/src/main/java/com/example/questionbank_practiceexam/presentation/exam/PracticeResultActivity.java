package com.example.questionbank_practiceexam.presentation.exam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
            percentage = Math.round(
                    (correctCount * 100f) / totalQuestions
            );
        }

        // ========================================================
        // ১. রেজাল্ট কার্ডের ভিউ থেকে বিটম্যাপ তৈরি করা
        // ========================================================
        View view = binding.resultCard;

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        android.graphics.Bitmap originalBitmap = android.graphics.Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        try {
            // ========================================================
            // ২. ওয়াটারমার্ক সহ নতুন একটি বিটম্যাপ তৈরি করা (Canvas ব্যবহার করে)
            // ========================================================
            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();

            // ওয়াটারমার্কের জায়গার জন্য নিচে কিছুটা এক্সট্রা হাইট (যেমন: ৬০ পিক্সেল) যোগ করা হলো
            int extraPadding = 60;
            android.graphics.Bitmap watermarkedBitmap = android.graphics.Bitmap.createBitmap(width, height + extraPadding, android.graphics.Bitmap.Config.ARGB_8888);

            android.graphics.Canvas canvas = new android.graphics.Canvas(watermarkedBitmap);

            // কার্ডের ব্যাকগ্রাউন্ড কালার বা হোয়াইট কালার দিয়ে পুরো ক্যানভাস ফিল করা
            canvas.drawColor(android.graphics.Color.parseColor("#121824")); // আপনার অ্যাপের ডার্ক ব্যাকগ্রাউন্ড কালার দিতে পারেন

            // মূল রেজাল্ট কার্ডটি ক্যানভাসে আঁকা
            canvas.drawBitmap(originalBitmap, 0, 0, null);

            // ========================================================
            // ৩. ওয়াটারমার্ক টেক্সট ডিজাইন ও ড্র করা
            // ========================================================
            android.graphics.Paint paint = new android.graphics.Paint();
            paint.setColor(android.graphics.Color.parseColor("#94A3B8")); // ওয়াটারমার্কের টেক্সট কালার (সফট গ্রে)
            paint.setTextSize(28f); // ফন্ট সাইজ
            paint.setAntiAlias(true);
            paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

            String watermarkText = "Powered by Question Bank App";

            // টেক্সট মাঝখানে পজিশন করার হিসাব
            android.graphics.Rect bounds = new android.graphics.Rect();
            paint.getTextBounds(watermarkText, 0, watermarkText.length(), bounds);

            float x = (width - bounds.width()) / 2f;
            float y = height + (extraPadding + bounds.height()) / 2f;

            // ক্যানভাসে ওয়াটারমার্ক টেক্সট বসানো
            canvas.drawText(watermarkText, x, y, paint);

            // ========================================================
            // ৪. ক্যাশ ফোল্ডারে নতুন ওয়াটারমার্কযুক্ত ইমেজটি সেভ করা
            // ========================================================
            java.io.File cachePath = new java.io.File(getCacheDir(), "images");
            cachePath.mkdirs();
            java.io.File file = new java.io.File(cachePath, "practice_result.png");
            java.io.FileOutputStream stream = new java.io.FileOutputStream(file);
            watermarkedBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            // ========================================================
            // ৫. FileProvider দিয়ে শেয়ার ইন্টেন্ট তৈরি করা
            // ========================================================
            android.net.Uri imageUri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);

            String shareText = "My Practice Result - Score: " + correctCount + "/" + totalQuestions + " (" + percentage + "%)";
            intent.putExtra(Intent.EXTRA_TEXT, shareText);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Share Result Image"));

        } catch (Exception e) {
            e.printStackTrace();
            android.widget.Toast.makeText(this, "Failed to share image", android.widget.Toast.LENGTH_SHORT).show();
        }
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