package com.example.questionbank_practiceexam.presentation.mocktest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;
import com.example.questionbank_practiceexam.data.local.entity.Question;
import com.example.questionbank_practiceexam.data.local.repository.QuestionRepository;
import com.example.questionbank_practiceexam.databinding.ActivityMockTestReviewBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockTestReviewActivity extends AppCompatActivity {

    private ActivityMockTestReviewBinding binding;

    // ============================================================
    // DATABASE
    // ============================================================

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private QuestionRepository questionRepository;


    // ============================================================
    // INTENT DATA
    // ============================================================

    private int[] subjectIds;

    private int unitId;
    private String unitName;

    private int yearId;
    private String yearTitle;


    // ============================================================
    // MOCK TEST SETTINGS
    // ============================================================

    /*
     * আপাতত তোমার XML অনুযায়ী:
     *
     * 60 Questions
     * 60 Minutes
     * 1 mark per question
     * 0.25 negative marking
     */

    private int questionCount = 60;

    private int testTimeMinutes = 60;

    private double marksPerQuestion = 1.0;

    private double negativeMark = 0.25;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);


        // ========================================================
        // BINDING
        // ========================================================

        binding =
                ActivityMockTestReviewBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(
                binding.getRoot()
        );


        // ========================================================
        // WINDOW INSETS
        // ========================================================

        ViewCompat.setOnApplyWindowInsetsListener(
                binding.main,
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );


        // ========================================================
        // GET INTENT DATA
        // ========================================================

        getIntentData();


        // ========================================================
        // VALIDATE
        // ========================================================

        if (!validateData()) {

            finish();

            return;
        }


        // ========================================================
        // DATABASE
        // ========================================================

        dbHelper =
                new DatabaseHelper(
                        getApplicationContext()
                );


        try {

            dbHelper.createDatabase();

            db =
                    dbHelper.openDatabase();

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Database error",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // ========================================================
        // QUESTION REPOSITORY
        // ========================================================

        questionRepository =
                new QuestionRepository(
                        getApplicationContext()
                );


        // ========================================================
        // LOAD REVIEW DATA
        // ========================================================

        loadReviewData();


        // ========================================================
        // TOOLBAR
        // ========================================================

        binding.toolbar.setNavigationOnClickListener(
                v -> finish()
        );


        // ========================================================
        // START MOCK TEST
        // ========================================================

        binding.startMockTestButton.setOnClickListener(
                v -> startMockTest()
        );
    }


    // ============================================================
    // GET INTENT DATA
    // ============================================================

    private void getIntentData() {

        // --------------------------------------------------------
        // SUBJECT IDS
        // --------------------------------------------------------

        subjectIds =
                getIntent().getIntArrayExtra(
                        "subject_ids"
                );


        // --------------------------------------------------------
        // UNIT
        // --------------------------------------------------------

        unitId =
                getIntent().getIntExtra(
                        "unit_id",
                        -1
                );


        unitName =
                getIntent().getStringExtra(
                        "unit_name"
                );


        // --------------------------------------------------------
        // YEAR
        // --------------------------------------------------------

        yearId =
                getIntent().getIntExtra(
                        "year_id",
                        -1
                );


        yearTitle =
                getIntent().getStringExtra(
                        "year_title"
                );
    }


    // ============================================================
    // VALIDATE DATA
    // ============================================================

    private boolean validateData() {

        if (subjectIds == null ||
                subjectIds.length == 0) {

            Toast.makeText(
                    this,
                    "No subject selected",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }


        if (unitId == -1) {

            Toast.makeText(
                    this,
                    "Invalid unit",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }


        if (yearId == -1) {

            Toast.makeText(
                    this,
                    "Invalid year",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }


        return true;
    }


    // ============================================================
    // LOAD REVIEW DATA
    // ============================================================

    private void loadReviewData() {

        // ========================================================
        // UNIVERSITY
        // ========================================================

        String universityName =
                getUniversityName();


        if (universityName == null ||
                universityName.trim().isEmpty()) {

            universityName =
                    "University";
        }


        binding.universityName.setText(
                universityName
        );


        // ========================================================
        // TEST NAME
        // ========================================================

        String testName;


        if (unitName != null &&
                !unitName.trim().isEmpty()) {

            testName =
                    unitName
                            + " Mock Test";

        } else {

            testName =
                    "Mock Test";
        }


        binding.testName.setText(
                testName
        );


        // ========================================================
        // QUESTION COUNT
        // ========================================================

        int availableQuestions =
                getAvailableQuestionCount();


        /*
         * যদি available question 60-এর কম হয়,
         * তাহলে available সংখ্যাই নেওয়া হবে।
         */

        if (availableQuestions > 0 &&
                availableQuestions < questionCount) {

            questionCount =
                    availableQuestions;
        }


        binding.questionCount.setText(
                toBanglaNumber(
                        questionCount
                )
                        + " টি"
        );


        // ========================================================
        // TIME
        // ========================================================

        /*
         * আপাতত 1 question = 1 minute
         */

        testTimeMinutes =
                questionCount;


        binding.testTime.setText(
                toBanglaNumber(
                        testTimeMinutes
                )
                        + " মিনিট"
        );


        // ========================================================
        // FULL MARKS
        // ========================================================

        double fullMarks =
                questionCount
                        * marksPerQuestion;


        binding.fullMarks.setText(
                formatNumber(
                        fullMarks
                )
        );


        // ========================================================
        // NEGATIVE MARK
        // ========================================================

        binding.marksPerQuestion.setText(
                formatNumber(
                        negativeMark
                )
        );


        // ========================================================
        // QUESTION TYPE
        // ========================================================

        binding.questionType.setText(
                "MCQ"
        );


        // ========================================================
        // SYLLABUS
        // ========================================================

        binding.syllabusText.setText(
                "Question Bank"
        );


        // ========================================================
        // INSTRUCTION
        // ========================================================

        String instruction =
                "• সময় শেষ হলে পরীক্ষা স্বয়ংক্রিয়ভাবে জমা হবে।\n"
                        + "• প্রতিটি ভুল উত্তরের জন্য "
                        + formatNumber(negativeMark)
                        + " নম্বর কাটা যাবে।\n"
                        + "• মকটেস্ট শুরু করার পর বন্ধ করা যাবে না।\n"
                        + "• প্রতিটি প্রশ্নের জন্য নির্ধারিত সময় থাকবে।\n"
                        + "• পরীক্ষা শেষে ফলাফল দেখা যাবে।";


        binding.instructionText.setText(
                instruction
        );
    }


    // ============================================================
    // GET UNIVERSITY NAME
    // ============================================================

    private String getUniversityName() {

        if (db == null ||
                !db.isOpen()) {

            return null;
        }


        String universityName = null;


        /*
         * year_id
         *     ↓
         * years.university_id
         *     ↓
         * universities.name
         */

        Cursor cursor = null;


        try {

            cursor =
                    db.rawQuery(
                            "SELECT u.name " +
                                    "FROM years y " +
                                    "INNER JOIN universities u " +
                                    "ON y.university_id = u.id " +
                                    "WHERE y.id = ? " +
                                    "LIMIT 1",

                            new String[]{
                                    String.valueOf(yearId)
                            }
                    );


            if (cursor.moveToFirst()) {

                universityName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "name"
                                )
                        );
            }


        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null) {

                cursor.close();
            }
        }


        return universityName;
    }


    // ============================================================
    // GET AVAILABLE QUESTION COUNT
    // ============================================================

    private int getAvailableQuestionCount() {

        if (questionRepository == null) {

            return 0;
        }


        Set<Integer> selectedSubjectSet =
                new HashSet<>();


        for (int id : subjectIds) {

            selectedSubjectSet.add(id);
        }


        List<Question> questions =
                questionRepository
                        .getQuestionsBySubjects(
                                selectedSubjectSet
                        );


        if (questions == null) {

            return 0;
        }


        return questions.size();
    }


    // ============================================================
    // START MOCK TEST
    // ============================================================

    private void startMockTest() {

        if (subjectIds == null ||
                subjectIds.length == 0) {

            Toast.makeText(
                    this,
                    "No subject selected",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // CHECK QUESTIONS
        // ========================================================

        int availableQuestions =
                getAvailableQuestionCount();


        if (availableQuestions <= 0) {

            Toast.makeText(
                    this,
                    "No questions available",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // ========================================================
        // START QUESTION ACTIVITY
        // ========================================================

        Intent intent =
                new Intent(
                        MockTestReviewActivity.this,
                        MockTestQuestionActivity.class
                );


        // ========================================================
        // SUBJECT IDS
        // ========================================================

        intent.putExtra(
                "subject_ids",
                subjectIds
        );


        // ========================================================
        // UNIT
        // ========================================================

        intent.putExtra(
                "unit_id",
                unitId
        );


        intent.putExtra(
                "unit_name",
                unitName
        );


        // ========================================================
        // YEAR
        // ========================================================

        intent.putExtra(
                "year_id",
                yearId
        );


        intent.putExtra(
                "year_title",
                yearTitle
        );


        // ========================================================
        // QUESTION SETTINGS
        // ========================================================

        intent.putExtra(
                "question_count",
                questionCount
        );


        intent.putExtra(
                "test_time_minutes",
                testTimeMinutes
        );


        intent.putExtra(
                "marks_per_question",
                marksPerQuestion
        );


        intent.putExtra(
                "negative_mark",
                negativeMark
        );


        // ========================================================
        // START
        // ========================================================

        startActivity(
                intent
        );


        finish();
    }


    // ============================================================
    // FORMAT NUMBER
    // ============================================================

    private String formatNumber(
            double number
    ) {

        if (number == (long) number) {

            return String.valueOf(
                    (long) number
            );
        }


        return String.format(
                java.util.Locale.US,
                "%.2f",
                number
        );
    }


    // ============================================================
    // BANGLA NUMBER
    // ============================================================

    private String toBanglaNumber(
            int number
    ) {

        String english =
                String.valueOf(number);


        return english
                .replace("0", "০")
                .replace("1", "১")
                .replace("2", "২")
                .replace("3", "৩")
                .replace("4", "৪")
                .replace("5", "৫")
                .replace("6", "৬")
                .replace("7", "৭")
                .replace("8", "৮")
                .replace("9", "৯");
    }


    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        if (questionRepository != null) {

            questionRepository.close();

            questionRepository = null;
        }


        if (dbHelper != null) {

            dbHelper.close();

            dbHelper = null;

            db = null;
        }


        binding = null;


        super.onDestroy();
    }
}