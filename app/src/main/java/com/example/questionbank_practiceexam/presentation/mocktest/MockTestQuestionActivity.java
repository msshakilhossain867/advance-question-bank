package com.example.questionbank_practiceexam.presentation.mocktest;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;
import com.example.questionbank_practiceexam.data.local.entity.Question;
import com.example.questionbank_practiceexam.data.local.repository.QuestionRepository;
import com.example.questionbank_practiceexam.data.local.repository.StudyTimeRepository;
import com.example.questionbank_practiceexam.databinding.ActivityMockTestQuestionBinding;
import com.example.questionbank_practiceexam.databinding.BottomSheetQuestionListBinding;
import com.example.questionbank_practiceexam.databinding.ItemQuestionNumberBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * Mock Test Question Activity
 *
 * STATE:
 *
 * NORMAL
 *   ↓
 * ANSWERED → Green → locked
 *
 * ANSWERED
 *   ↓ Review
 * REVIEW → Orange → answer change allowed
 *
 * NORMAL
 *   ↓ Skip
 * SKIPPED → cannot answer anymore
 *
 *
 * MARK:
 *
 * Correct = +1
 * Wrong   = -0.25
 * Skip    = 0
 */
public class MockTestQuestionActivity
        extends AppCompatActivity {


    // ============================================================
    // BINDING
    // ============================================================

    private ActivityMockTestQuestionBinding binding;


    // ============================================================
    // DATABASE
    // ============================================================

    private QuestionRepository questionRepository;

    private DatabaseHelper databaseHelper;

    private SQLiteDatabase database;
    private TopQuestionNumberAdapter topQuestionNumberAdapter;


    // ============================================================
    // QUESTIONS
    // ============================================================

    private final List<Question> questionList =
            new ArrayList<>();

    private int currentPosition = 0;


    // ============================================================
    // INTENT DATA
    // ============================================================

    private int unitId = -1;

    private int yearId = -1;

    private String unitName = "";

    private String yearTitle = "";

    private int[] selectedSubjectIds;
    private long mockTestStartTime = 0L;


    // ============================================================
    // ANSWER MAP
    // ============================================================

    /*
     * questionId -> selected option
     *
     * Example:
     *
     * 101 -> a
     * 102 -> c
     */
    private final Map<Integer, String>
            answerMap =
            new HashMap<>();


    // ============================================================
    // SKIPPED
    // ============================================================

    /*
     * Skip করা question এখানে থাকবে।
     *
     * Skip করা question কখনো answer করা যাবে না।
     */
    private final Set<Integer>
            skippedQuestionIds =
            new HashSet<>();


    // ============================================================
    // REVIEW
    // ============================================================

    /*
     * Review করা question এখানে থাকবে।
     *
     * Review থাকলে answer change করা যাবে।
     */
    private final Set<Integer>
            reviewQuestionIds =
            new HashSet<>();


    private final Set<Integer> lockedQuestionIds =
            new HashSet<>();





    // ============================================================
    // TIMER
    // ============================================================

    private CountDownTimer testTimer;


    private long totalTestTime = 0L;


    // ============================================================
    // QUESTION LIST DIALOG
    // ============================================================

    private BottomSheetDialog questionListDialog;


    // ============================================================
    // COLORS
    // ============================================================

    private static final int COLOR_PURPLE =
            Color.rgb(98, 84, 232);

    private static final int COLOR_GREEN =
            Color.rgb(27, 154, 82);

    private static final int COLOR_GREEN_BG =
            Color.rgb(221, 247, 231);

    private static final int COLOR_ORANGE =
            Color.rgb(229, 138, 0);

    private static final int COLOR_ORANGE_BG =
            Color.rgb(255, 240, 217);

    private static final int COLOR_NORMAL =
            Color.rgb(242, 243, 246);

    private static final int COLOR_NORMAL_TEXT =
            Color.rgb(69, 74, 85);


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);


        binding =
                ActivityMockTestQuestionBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(
                binding.getRoot()
        );


        // ========================================================
        // READ INTENT
        // ========================================================

        readIntentData();


        // ========================================================
        // VALIDATION
        // ========================================================

        if (selectedSubjectIds == null ||
                selectedSubjectIds.length == 0) {

            Toast.makeText(
                    this,
                    "কোনো Subject নির্বাচন করা হয়নি",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // ========================================================
        // DATABASE
        // ========================================================

        questionRepository =
                new QuestionRepository(
                        getApplicationContext()
                );


        databaseHelper =
                new DatabaseHelper(
                        getApplicationContext()
                );


        database =
                databaseHelper.openDatabase();

        mockTestStartTime =
                System.currentTimeMillis();


        // ========================================================
        // UNIVERSITY + UNIT
        // ========================================================

        loadUniversityAndUnit();


        // ========================================================
        // QUESTIONS
        // ========================================================

        loadQuestions();


        // ========================================================
        // BUTTONS
        // ========================================================

        setupButtons();
    }


    // ============================================================
    // READ INTENT DATA
    // ============================================================

    private void readIntentData() {

        selectedSubjectIds =
                getIntent().getIntArrayExtra(
                        "subject_ids"
                );


        unitId =
                getIntent().getIntExtra(
                        "unit_id",
                        -1
                );


        yearId =
                getIntent().getIntExtra(
                        "year_id",
                        -1
                );


        unitName =
                getIntent().getStringExtra(
                        "unit_name"
                );


        yearTitle =
                getIntent().getStringExtra(
                        "year_title"
                );


        if (unitName == null) {
            unitName = "";
        }


        if (yearTitle == null) {
            yearTitle = "";
        }
    }


    // ============================================================
    // LOAD UNIVERSITY + UNIT
    // ============================================================

    private void loadUniversityAndUnit() {

        String universityName = "";

        String databaseUnitName = "";

        Cursor cursor = null;


        try {

            cursor =
                    database.rawQuery(
                            "SELECT " +
                                    "u.unit_name AS unit_name, " +
                                    "un.name AS university_name " +
                                    "FROM units u " +
                                    "INNER JOIN years y " +
                                    "ON u.year_id = y.id " +
                                    "INNER JOIN universities un " +
                                    "ON y.university_id = un.id " +
                                    "WHERE u.id = ? " +
                                    "LIMIT 1",
                            new String[]{
                                    String.valueOf(unitId)
                            }
                    );


            if (cursor != null &&
                    cursor.moveToFirst()) {

                int unitIndex =
                        cursor.getColumnIndex(
                                "unit_name"
                        );


                int universityIndex =
                        cursor.getColumnIndex(
                                "university_name"
                        );


                if (unitIndex >= 0) {

                    databaseUnitName =
                            cursor.getString(
                                    unitIndex
                            );
                }


                if (universityIndex >= 0) {

                    universityName =
                            cursor.getString(
                                    universityIndex
                            );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }


        if (databaseUnitName == null ||
                databaseUnitName.trim().isEmpty()) {

            databaseUnitName =
                    unitName;
        }


        if (universityName == null ||
                universityName.trim().isEmpty()) {

            universityName =
                    "University";
        }


        if (!databaseUnitName.trim().isEmpty()) {

            binding.testName.setText(
                    universityName
                            + " • "
                            + databaseUnitName
            );

        } else {

            binding.testName.setText(
                    universityName
            );
        }
    }


    // ============================================================
    // LOAD QUESTIONS
    // ============================================================

    private void loadQuestions() {

        Set<Integer> subjectIdSet =
                new HashSet<>();


        for (int subjectId :
                selectedSubjectIds) {

            subjectIdSet.add(
                    subjectId
            );
        }


        List<Question> questions =
                questionRepository
                        .getQuestionsBySubjects(
                                subjectIdSet
                        );


        if (questions == null ||
                questions.isEmpty()) {

            Toast.makeText(
                    this,
                    "এই Subject-এ কোনো প্রশ্ন পাওয়া যায়নি",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        // ========================================================
        // RANDOM
        // ========================================================

        Collections.shuffle(
                questions
        );


        questionList.clear();

        questionList.addAll(
                questions
        );


        // ========================================================
        // TIMER
        // ========================================================

        /*
         * মোট প্রশ্ন যতটি,
         * মোট সময় তত মিনিট।
         *
         * Example:
         *
         * 20 প্রশ্ন = 20 মিনিট
         * 50 প্রশ্ন = 50 মিনিট
         * 100 প্রশ্ন = 100 মিনিট
         */

        totalTestTime =
                questionList.size()
                        * 60L
                        * 1000L;


        startTestTimer();


        // ========================================================
        // QUESTION NUMBER RECYCLER
        // ========================================================

        setupTopQuestionNumbers();


        // ========================================================
        // FIRST QUESTION
        // ========================================================

        currentPosition = 0;

        showQuestion();
    }





    private void setupTopQuestionNumbers() {

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                ) {

                    @Override
                    public boolean canScrollHorizontally() {
                        return false;
                    }

                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };


        binding.questionNumberRecycler.setLayoutManager(
                layoutManager
        );


        binding.questionNumberRecycler.setOverScrollMode(
                View.OVER_SCROLL_NEVER
        );


        binding.questionNumberRecycler.setNestedScrollingEnabled(
                false
        );


        topQuestionNumberAdapter =
                new TopQuestionNumberAdapter();


        binding.questionNumberRecycler.setAdapter(
                topQuestionNumberAdapter
        );


        binding.questionNumberRecycler.post(
                () -> topQuestionNumberAdapter.recalculate()
        );
    }


    // ============================================================
    // SHOW QUESTION
    // ============================================================

    private void showQuestion() {

        if (questionList.isEmpty()) {
            return;
        }


        if (currentPosition < 0) {
            currentPosition = 0;
        }


        if (currentPosition >=
                questionList.size()) {

            currentPosition =
                    questionList.size() - 1;
        }


        Question question =
                questionList.get(
                        currentPosition
                );


        int questionId =
                question.getId();


        // ========================================================
        // QUESTION
        // ========================================================

        binding.questionText.setText(
                (currentPosition + 1)
                        + ". "
                        + question.getQuestionText()
        );


        // ========================================================
        // OPTIONS
        // ========================================================

        setOptions(
                question
        );


        // ========================================================
        // RESET UI
        // ========================================================

        resetOptionUI();


        // ========================================================
        // PREVIOUS
        // ========================================================

        binding.previousButton.setEnabled(
                currentPosition > 0
        );


        binding.previousButton.setAlpha(
                currentPosition > 0
                        ? 1f
                        : 0.45f
        );


        // ========================================================
        // STATE
        // ========================================================

        if (skippedQuestionIds.contains(
                questionId
        )) {

            /*
             * SKIPPED
             *
             * আর answer করা যাবে না।
             */

            showSkippedState();

        } else if (
                answerMap.containsKey(
                        questionId
                )
        ) {

            /*
             * ANSWERED / REVIEW
             */

            String selected =
                    answerMap.get(
                            questionId
                    );


            selectOptionUI(
                    selected
            );


            /*
             * Review হলে editable।
             *
             * Review না হলে locked।
             */

            if (reviewQuestionIds.contains(
                    questionId
            )) {

                enableOptions(true);

                setActionButton(true);

            } else {

                enableOptions(false);

                setActionButton(true);
            }

        } else {

            /*
             * NORMAL
             */

            enableOptions(true);

            setActionButton(false);
        }


        updateQuestionNumberAdapters();
    }


    // ============================================================
    // SET OPTIONS
    // ============================================================

    private void setOptions(
            Question question
    ) {

        try {

            JSONObject options =
                    new JSONObject(
                            question.getOptions()
                    );


            binding.optionAText.setText(
                    options.optString("a")
            );


            binding.optionBText.setText(
                    options.optString("b")
            );


            binding.optionCText.setText(
                    options.optString("c")
            );


            binding.optionDText.setText(
                    options.optString("d")
            );

        } catch (Exception e) {

            binding.optionAText.setText("");

            binding.optionBText.setText("");

            binding.optionCText.setText("");

            binding.optionDText.setText("");
        }
    }


    // ============================================================
    // RESET OPTION UI
    // ============================================================

    private void resetOptionUI() {

        binding.optionA.setBackgroundResource(
                R.drawable.bg_mock_option
        );

        binding.optionB.setBackgroundResource(
                R.drawable.bg_mock_option
        );

        binding.optionC.setBackgroundResource(
                R.drawable.bg_mock_option
        );

        binding.optionD.setBackgroundResource(
                R.drawable.bg_mock_option
        );


        binding.optionALabel.setText("A");

        binding.optionBLabel.setText("B");

        binding.optionCLabel.setText("C");

        binding.optionDLabel.setText("D");


        binding.optionALabel.setTextColor(
                Color.parseColor("#687080")
        );

        binding.optionBLabel.setTextColor(
                Color.parseColor("#687080")
        );

        binding.optionCLabel.setTextColor(
                Color.parseColor("#687080")
        );

        binding.optionDLabel.setTextColor(
                Color.parseColor("#687080")
        );


        binding.optionALabel.setBackgroundResource(
                R.drawable.bg_mock_option_letter
        );

        binding.optionBLabel.setBackgroundResource(
                R.drawable.bg_mock_option_letter
        );

        binding.optionCLabel.setBackgroundResource(
                R.drawable.bg_mock_option_letter
        );

        binding.optionDLabel.setBackgroundResource(
                R.drawable.bg_mock_option_letter
        );


        binding.optionACheck.setVisibility(
                View.GONE
        );

        binding.optionBCheck.setVisibility(
                View.GONE
        );

        binding.optionCCheck.setVisibility(
                View.GONE
        );

        binding.optionDCheck.setVisibility(
                View.GONE
        );


        enableOptions(true);
    }


    // ============================================================
    // ENABLE / DISABLE OPTIONS
    // ============================================================

    private void enableOptions(
            boolean enabled
    ) {

        binding.optionA.setEnabled(
                enabled
        );

        binding.optionB.setEnabled(
                enabled
        );

        binding.optionC.setEnabled(
                enabled
        );

        binding.optionD.setEnabled(
                enabled
        );


        float alpha =
                enabled
                        ? 1f
                        : 0.65f;


        binding.optionA.setAlpha(alpha);

        binding.optionB.setAlpha(alpha);

        binding.optionC.setAlpha(alpha);

        binding.optionD.setAlpha(alpha);
    }


    // ============================================================
    // SELECT OPTION
    // ============================================================

    private void selectOption(String option) {

        Question question =
                questionList.get(currentPosition);

        int questionId =
                question.getId();


        // ========================================================
        // SKIPPED
        // ========================================================

        if (skippedQuestionIds.contains(questionId)) {

            Toast.makeText(
                    this,
                    "এই প্রশ্নটি Skip করা হয়েছে",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // FINAL / LOCKED QUESTION
        // ========================================================

        /*
         * Green question।
         *
         * একবার Next করে Final হলে
         * আর answer change করা যাবে না।
         */
        if (lockedQuestionIds.contains(questionId)) {

            Toast.makeText(
                    this,
                    "এই প্রশ্নের উত্তর পরিবর্তন করা যাবে না",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // SAVE / CHANGE ANSWER
        // ========================================================

        /*
         * প্রথমবার answer দিলে save হবে।
         *
         * Review question হলে answer change-ও হবে।
         */
        answerMap.put(
                questionId,
                option.toLowerCase(Locale.ROOT)
        );


        // ========================================================
        // SHOW SELECTED OPTION
        // ========================================================

        selectOptionUI(
                option
        );


        // ========================================================
        // NEXT BUTTON
        // ========================================================

        setActionButton(
                true
        );


        // ========================================================
        // UPDATE NUMBER
        // ========================================================

        updateQuestionNumberAdapters();
    }


    // ============================================================
    // SELECT OPTION UI
    // ============================================================

    private void selectOptionUI(
            String option
    ) {

        resetOptionUI();


        if (option == null) {
            return;
        }


        switch (
                option.toLowerCase(
                        Locale.ROOT
                )
        ) {

            case "a":

                binding.optionA.setBackgroundResource(
                        R.drawable.bg_mock_option_selected
                );

                binding.optionALabel.setText("✓");

                binding.optionALabel.setTextColor(
                        Color.WHITE
                );

                binding.optionALabel.setBackgroundResource(
                        R.drawable.bg_mock_option_letter_selected
                );

                binding.optionACheck.setVisibility(
                        View.VISIBLE
                );

                break;


            case "b":

                binding.optionB.setBackgroundResource(
                        R.drawable.bg_mock_option_selected
                );

                binding.optionBLabel.setText("✓");

                binding.optionBLabel.setTextColor(
                        Color.WHITE
                );

                binding.optionBLabel.setBackgroundResource(
                        R.drawable.bg_mock_option_letter_selected
                );

                binding.optionBCheck.setVisibility(
                        View.VISIBLE
                );

                break;


            case "c":

                binding.optionC.setBackgroundResource(
                        R.drawable.bg_mock_option_selected
                );

                binding.optionCLabel.setText("✓");

                binding.optionCLabel.setTextColor(
                        Color.WHITE
                );

                binding.optionCLabel.setBackgroundResource(
                        R.drawable.bg_mock_option_letter_selected
                );

                binding.optionCCheck.setVisibility(
                        View.VISIBLE
                );

                break;


            case "d":

                binding.optionD.setBackgroundResource(
                        R.drawable.bg_mock_option_selected
                );

                binding.optionDLabel.setText("✓");

                binding.optionDLabel.setTextColor(
                        Color.WHITE
                );

                binding.optionDLabel.setBackgroundResource(
                        R.drawable.bg_mock_option_letter_selected
                );

                binding.optionDCheck.setVisibility(
                        View.VISIBLE
                );

                break;
        }
    }


    // ============================================================
    // SKIPPED STATE
    // ============================================================

    private void showSkippedState() {

        enableOptions(false);


        binding.skipNextButton.setText(
                "পরের প্রশ্ন ›"
        );


        binding.skipNextButton.setEnabled(
                true
        );


        binding.skipNextButton.setAlpha(
                1f
        );
    }


    // ============================================================
    // ACTION BUTTON
    // ============================================================

    private void setActionButton(
            boolean answered
    ) {

        Question question =
                questionList.get(
                        currentPosition
                );


        int questionId =
                question.getId();


        if (skippedQuestionIds.contains(
                questionId
        )) {

            binding.skipNextButton.setText(
                    "পরের প্রশ্ন ›"
            );

            return;
        }


        if (answered) {

            binding.skipNextButton.setText(
                    "পরের প্রশ্ন ›"
            );

        } else {

            binding.skipNextButton.setText(
                    "Skip ›"
            );
        }


        binding.skipNextButton
                .setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.parseColor(
                                        "#F0EEFF"
                                )
                        )
                );
    }


    // ============================================================
    // SETUP BUTTONS
    // ============================================================

    private void setupButtons() {

        // ========================================================
        // OPTION A
        // ========================================================

        binding.optionA.setOnClickListener(
                v -> selectOption("a")
        );


        // ========================================================
        // OPTION B
        // ========================================================

        binding.optionB.setOnClickListener(
                v -> selectOption("b")
        );


        // ========================================================
        // OPTION C
        // ========================================================

        binding.optionC.setOnClickListener(
                v -> selectOption("c")
        );


        // ========================================================
        // OPTION D
        // ========================================================

        binding.optionD.setOnClickListener(
                v -> selectOption("d")
        );


        // ========================================================
        // PREVIOUS
        // ========================================================

        binding.previousButton.setOnClickListener(
                v -> previousQuestion()
        );


        // ========================================================
        // NEXT / SKIP
        // ========================================================

        binding.skipNextButton.setOnClickListener(
                v -> handleNextOrSkip()
        );


        // ========================================================
        // QUESTION LIST
        // ========================================================

        binding.questionListButton.setOnClickListener(
                v -> showQuestionListBottomSheet()
        );


        // ========================================================
        // REVIEW
        // ========================================================

        binding.reviewButton.setOnClickListener(
                v -> toggleReview()
        );


        // ========================================================
        // SUBMIT
        // ========================================================

        binding.submitButton.setOnClickListener(
                v -> submitMockTest()
        );
    }


    // ============================================================
    // HANDLE NEXT / SKIP
    // ============================================================

    private void handleNextOrSkip() {

        Question question =
                questionList.get(
                        currentPosition
                );


        int questionId =
                question.getId();


        // ========================================================
        // SKIPPED
        // ========================================================

        if (skippedQuestionIds.contains(
                questionId
        )) {

            nextQuestion();

            return;
        }


        // ========================================================
        // ANSWERED
        // ========================================================

        if (answerMap.containsKey(
                questionId
        )) {

            nextQuestion();

            return;
        }


        // ========================================================
        // NOT ANSWERED → SKIP
        // ========================================================

        skipCurrentQuestion();
    }


    // ============================================================
    // SKIP
    // ============================================================

    private void skipCurrentQuestion() {

        Question question =
                questionList.get(
                        currentPosition
                );


        int questionId =
                question.getId();


        // ========================================================
        // ALREADY ANSWERED
        // ========================================================

        if (answerMap.containsKey(
                questionId
        )) {

            nextQuestion();

            return;
        }


        // ========================================================
        // PERMANENT SKIP
        // ========================================================

        skippedQuestionIds.add(
                questionId
        );


        /*
         * Review থাকলে remove।
         */
        reviewQuestionIds.remove(
                questionId
        );


        updateQuestionNumberAdapters();


        nextQuestion();
    }


    // ============================================================
    // NEXT QUESTION
    // ============================================================

// ============================================================
// NEXT QUESTION
// ============================================================

    private void nextQuestion() {

        if (questionList.isEmpty()) {
            return;
        }


        Question question =
                questionList.get(
                        currentPosition
                );

        int questionId =
                question.getId();


        // ========================================================
        // SKIPPED
        // ========================================================

        if (skippedQuestionIds.contains(
                questionId
        )) {

            goToNextQuestion();

            return;
        }


        // ========================================================
        // ANSWER REQUIRED
        // ========================================================

        if (!answerMap.containsKey(
                questionId
        )) {

            Toast.makeText(
                    this,
                    "আগে উত্তর নির্বাচন করুন অথবা Skip করুন",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // FINAL ANSWER
        // ========================================================

        /*
         * Next চাপার সাথে সাথে প্রশ্নটি Final হয়ে যাবে।
         *
         * Review থাকলেও remove হবে।
         *
         * তারপর locked হবে।
         */
        reviewQuestionIds.remove(
                questionId
        );


        lockedQuestionIds.add(
                questionId
        );


        // ========================================================
        // UPDATE UI
        // ========================================================

        updateQuestionNumberAdapters();


        // ========================================================
        // NEXT
        // ========================================================

        goToNextQuestion();
    }

    private void goToNextQuestion() {

        if (currentPosition <
                questionList.size() - 1) {

            currentPosition++;

            showQuestion();

        } else {

            updateQuestionNumberAdapters();

            showQuestion();

            Toast.makeText(
                    this,
                    "এটাই শেষ প্রশ্ন",
                    Toast.LENGTH_SHORT
            ).show();
            
            submitMockTest();
        }
    }


    // ============================================================
    // PREVIOUS QUESTION
    // ============================================================

    private void previousQuestion() {

        if (currentPosition > 0) {

            currentPosition--;

            showQuestion();
        }
    }


    // ============================================================
    // TOGGLE REVIEW
    // ============================================================

    // ============================================================
// REVIEW
// ============================================================
    private void toggleReview() {

        Question question =
                questionList.get(currentPosition);

        int questionId =
                question.getId();


        // ========================================================
        // SKIPPED
        // ========================================================

        if (skippedQuestionIds.contains(
                questionId
        )) {

            Toast.makeText(
                    this,
                    "Skip করা প্রশ্ন Review করা যাবে না",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // FINAL / LOCKED
        // ========================================================

        /*
         * Green / Final question আবার Review করা যাবে না।
         */
        if (lockedQuestionIds.contains(
                questionId
        )) {

            Toast.makeText(
                    this,
                    "এই প্রশ্নটি ইতোমধ্যে Final Answer করা হয়েছে",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // ANSWER REQUIRED
        // ========================================================

        if (!answerMap.containsKey(
                questionId
        )) {

            Toast.makeText(
                    this,
                    "আগে উত্তর নির্বাচন করুন",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // ADD REVIEW
        // ========================================================

        reviewQuestionIds.add(
                questionId
        );


        // ========================================================
        // UPDATE UI
        // ========================================================

        updateQuestionNumberAdapters();


        // ========================================================
        // GO NEXT
        // ========================================================

        if (currentPosition <
                questionList.size() - 1) {

            currentPosition++;

            showQuestion();

        } else {

            Toast.makeText(
                    this,
                    "Review-এ রাখা হয়েছে। এটাই শেষ প্রশ্ন।",
                    Toast.LENGTH_SHORT
            ).show();

            showQuestion();
        }
    }

    // ============================================================
    // QUESTION LIST BOTTOM SHEET
    // ============================================================

    private void showQuestionListBottomSheet() {

        questionListDialog =
                new BottomSheetDialog(
                        this
                );


        BottomSheetQuestionListBinding sheetBinding =
                BottomSheetQuestionListBinding.inflate(
                        LayoutInflater.from(this)
                );


        questionListDialog.setContentView(
                sheetBinding.getRoot()
        );


        sheetBinding.questionListTitle.setText(
                "প্রশ্ন লিস্ট"
        );


        // ========================================================
        // GRID
        // ========================================================

        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(
                        this,
                        6
                );


        sheetBinding.questionGridRecycler
                .setLayoutManager(
                        gridLayoutManager
                );


        sheetBinding.questionGridRecycler
                .setAdapter(
                        new BottomQuestionNumberAdapter(
                                position -> {

                                    currentPosition =
                                            position;


                                    showQuestion();


                                    if (questionListDialog != null) {

                                        questionListDialog.dismiss();
                                    }
                                }
                        )
                );


        // ========================================================
        // CLOSE
        // ========================================================

        sheetBinding.closeQuestionListButton
                .setOnClickListener(
                        v -> {

                            if (questionListDialog != null) {

                                questionListDialog.dismiss();
                            }
                        }
                );


        questionListDialog.show();
    }


    // ============================================================
    // UPDATE QUESTION NUMBER ADAPTERS
    // ============================================================

    private void updateQuestionNumberAdapters() {

        RecyclerView.Adapter topAdapter =
                binding.questionNumberRecycler
                        .getAdapter();


        if (topAdapter != null) {

            topAdapter.notifyDataSetChanged();
        }
    }


    // ============================================================
    // TOP QUESTION NUMBER ADAPTER
    // ============================================================




    private class TopQuestionNumberAdapter
            extends RecyclerView.Adapter<
            TopQuestionNumberAdapter.ViewHolder> {


        private final List<Integer> visiblePositions =
                new ArrayList<>();


        private boolean calculated = false;


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType
        ) {

            ItemQuestionNumberBinding itemBinding =
                    ItemQuestionNumberBinding.inflate(
                            LayoutInflater.from(
                                    parent.getContext()
                            ),
                            parent,
                            false
                    );


            return new ViewHolder(
                    itemBinding
            );
        }


        @Override
        public void onBindViewHolder(
                @NonNull ViewHolder holder,
                int position
        ) {

            if (position < 0 ||
                    position >= visiblePositions.size()) {

                return;
            }


            int realPosition =
                    visiblePositions.get(
                            position
                    );


            // ========================================================
            // ELLIPSIS
            // ========================================================

            if (realPosition == -1) {

                holder.binding.questionNumberText.setText(
                        "..."
                );


                holder.binding.questionNumberCard
                        .setCardBackgroundColor(
                                Color.TRANSPARENT
                        );


                holder.binding.questionNumberText
                        .setTextColor(
                                COLOR_NORMAL_TEXT
                        );


                holder.binding.questionNumberCard
                        .setClickable(false);


                holder.binding.questionNumberCard
                        .setFocusable(false);


                return;
            }


            // ========================================================
            // QUESTION NUMBER
            // ========================================================

            holder.binding.questionNumberText.setText(
                    String.valueOf(
                            realPosition + 1
                    )
            );


            // ========================================================
            // STATE
            // ========================================================

            applyQuestionNumberState(
                    holder.binding,
                    realPosition
            );


            // ========================================================
            // CLICK
            // ========================================================

            holder.binding.questionNumberCard
                    .setClickable(true);


            holder.binding.questionNumberCard
                    .setFocusable(true);


            holder.binding.questionNumberCard
                    .setOnClickListener(
                            v -> {

                                /*
                                 * IMPORTANT:
                                 *
                                 * এখানে আর সরাসরি
                                 * showQuestion() হবে না।
                                 *
                                 * Bottom Sheet খুলবে।
                                 */

                                showQuestionListBottomSheet();
                            }
                    );
        }


        @Override
        public int getItemCount() {

            return visiblePositions.size();
        }


        // ============================================================
        // CALCULATE VISIBLE NUMBERS
        // ============================================================

        private void calculateVisiblePositions(
                int availableWidth
        ) {

            visiblePositions.clear();


            int totalQuestions =
                    questionList.size();


            if (totalQuestions <= 0) {
                return;
            }


            /*
             * প্রতিটি question number-এর আনুমানিক width।
             *
             * 42dp card + gap ধরে।
             */
            float density =
                    getResources()
                            .getDisplayMetrics()
                            .density;


            int itemWidth =
                    (int) (
                            42 * density
                    );


            int possibleCount =
                    Math.max(
                            1,
                            availableWidth / itemWidth
                    );


            // ========================================================
            // ALL QUESTIONS FIT
            // ========================================================

            if (totalQuestions <= possibleCount) {

                for (int i = 0;
                     i < totalQuestions;
                     i++) {

                    visiblePositions.add(i);
                }

                return;
            }


            // ========================================================
            // NEED ELLIPSIS
            // ========================================================

            /*
             * Example:
             *
             * 1 2 3 4 5 ... 50
             *
             * তাই শেষের জন্য 1টা
             * এবং ... এর জন্য 1টা রাখছি।
             */

            int normalCount =
                    Math.max(
                            2,
                            possibleCount - 1
                    );


            /*
             * Current question যদি প্রথম অংশের বাইরে থাকে,
             * তাহলে current question-টাকেও দেখানোর চেষ্টা করবো।
             */

            int firstCount =
                    Math.max(
                            1,
                            normalCount - 1
                    );


            // ========================================================
            // FIRST QUESTIONS
            // ========================================================

            for (int i = 0;
                 i < firstCount &&
                         i < totalQuestions - 1;
                 i++) {

                visiblePositions.add(i);
            }


            // ========================================================
            // CURRENT QUESTION
            // ========================================================

            if (currentPosition >= firstCount &&
                    currentPosition <
                            totalQuestions - 1) {

                visiblePositions.add(
                        currentPosition
                );
            }


            // ========================================================
            // ELLIPSIS
            // ========================================================

            visiblePositions.add(-1);


            // ========================================================
            // LAST QUESTION
            // ========================================================

            visiblePositions.add(
                    totalQuestions - 1
            );


            /*
             * Duplicate remove করছি।
             */
            List<Integer> cleanList =
                    new ArrayList<>();


            Set<Integer> alreadyAdded =
                    new HashSet<>();


            for (Integer value :
                    visiblePositions) {

                if (value == -1) {

                    /*
                     * Ellipsis একবারই থাকবে।
                     */
                    if (!alreadyAdded.contains(-1)) {

                        cleanList.add(-1);

                        alreadyAdded.add(-1);
                    }

                } else {

                    if (!alreadyAdded.contains(value)) {

                        cleanList.add(value);

                        alreadyAdded.add(value);
                    }
                }
            }


            visiblePositions.clear();

            visiblePositions.addAll(
                    cleanList
            );
        }


        // ============================================================
        // RECALCULATE
        // ============================================================

        private void recalculate() {

            RecyclerView recyclerView =
                    binding.questionNumberRecycler;


            int width =
                    recyclerView.getWidth();


            if (width <= 0) {

                recyclerView.post(
                        this::recalculate
                );

                return;
            }


            calculateVisiblePositions(
                    width
            );


            calculated = true;


            notifyDataSetChanged();
        }


        class ViewHolder
                extends RecyclerView.ViewHolder {

            ItemQuestionNumberBinding binding;


            ViewHolder(
                    ItemQuestionNumberBinding binding
            ) {

                super(
                        binding.getRoot()
                );

                this.binding =
                        binding;
            }
        }
    }


    // ============================================================
    // BOTTOM QUESTION NUMBER ADAPTER
    // ============================================================

    private class BottomQuestionNumberAdapter
            extends RecyclerView.Adapter<
            BottomQuestionNumberAdapter.ViewHolder> {


        private final OnQuestionClickListener listener;


        BottomQuestionNumberAdapter(
                OnQuestionClickListener listener
        ) {

            this.listener =
                    listener;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType
        ) {

            ItemQuestionNumberBinding itemBinding =
                    ItemQuestionNumberBinding.inflate(
                            LayoutInflater.from(
                                    parent.getContext()
                            ),
                            parent,
                            false
                    );


            return new ViewHolder(
                    itemBinding
            );
        }


        @Override
        public void onBindViewHolder(
                @NonNull ViewHolder holder,
                int position
        ) {

            holder.binding.questionNumberText
                    .setText(
                            String.valueOf(
                                    position + 1
                            )
                    );


            applyQuestionNumberState(
                    holder.binding,
                    position
            );


            holder.binding.questionNumberCard
                    .setOnClickListener(
                            v -> {

                                if (listener != null) {

                                    listener.onQuestionClick(
                                            position
                                    );
                                }
                            }
                    );
        }


        @Override
        public int getItemCount() {

            return questionList.size();
        }


        class ViewHolder
                extends RecyclerView.ViewHolder {

            ItemQuestionNumberBinding binding;


            ViewHolder(
                    ItemQuestionNumberBinding binding
            ) {

                super(
                        binding.getRoot()
                );

                this.binding =
                        binding;
            }
        }
    }


    // ============================================================
    // QUESTION NUMBER STATE
    // ============================================================

    private void applyQuestionNumberState(
            ItemQuestionNumberBinding itemBinding,
            int position
    ) {

        if (position < 0 ||
                position >= questionList.size()) {

            return;
        }


        Question question =
                questionList.get(
                        position
                );


        int questionId =
                question.getId();


        // ========================================================
        // CURRENT
        // ========================================================

        if (position == currentPosition) {

            itemBinding.questionNumberCard
                    .setCardBackgroundColor(
                            COLOR_PURPLE
                    );


            itemBinding.questionNumberText
                    .setTextColor(
                            Color.WHITE
                    );

            return;
        }


        // ========================================================
        // SKIPPED
        // ========================================================

        if (skippedQuestionIds.contains(
                questionId
        )) {

            itemBinding.questionNumberCard
                    .setCardBackgroundColor(
                            COLOR_NORMAL
                    );


            itemBinding.questionNumberText
                    .setTextColor(
                            COLOR_NORMAL_TEXT
                    );

            return;
        }


        // ========================================================
        // REVIEW - ORANGE
        // ========================================================

        if (reviewQuestionIds.contains(
                questionId
        )) {

            itemBinding.questionNumberCard
                    .setCardBackgroundColor(
                            COLOR_ORANGE_BG
                    );


            itemBinding.questionNumberText
                    .setTextColor(
                            COLOR_ORANGE
                    );

            return;
        }


        // ========================================================
        // FINAL ANSWER - GREEN
        // ========================================================

        /*
         * শুধু locked question-ই Green হবে।
         *
         * answerMap.containsKey() ব্যবহার করা হচ্ছে না।
         *
         * কারণ:
         *
         * Answer দেওয়া + Review
         *       = Orange
         *
         * Answer দেওয়া + Next
         *       = Green / Locked
         */

        if (lockedQuestionIds.contains(
                questionId
        )) {

            itemBinding.questionNumberCard
                    .setCardBackgroundColor(
                            COLOR_GREEN_BG
                    );


            itemBinding.questionNumberText
                    .setTextColor(
                            COLOR_GREEN
                    );

            return;
        }


        // ========================================================
        // NORMAL / UNANSWERED
        // ========================================================

        itemBinding.questionNumberCard
                .setCardBackgroundColor(
                        COLOR_NORMAL
                );


        itemBinding.questionNumberText
                .setTextColor(
                        COLOR_NORMAL_TEXT
                );
    }


    // ============================================================
    // CLICK LISTENER
    // ============================================================

    private interface OnQuestionClickListener {

        void onQuestionClick(
                int position
        );
    }


    // ============================================================
    // SUBMIT
    // ============================================================

  /*  private void submitMockTest() {

        stopTestTimer();


        int total =
                questionList.size();


        int correct = 0;

        int wrong = 0;

        int skipped = 0;


        // ========================================================
        // CALCULATE
        // ========================================================

        for (Question question :
                questionList) {

            int questionId =
                    question.getId();


            // ====================================================
            // SKIPPED
            // ====================================================

            if (skippedQuestionIds.contains(
                    questionId
            )) {

                skipped++;

                continue;
            }


            // ====================================================
            // UNANSWERED
            // ====================================================

            if (!answerMap.containsKey(
                    questionId
            )) {

                skipped++;

                continue;
            }


            String selected =
                    answerMap.get(
                            questionId
                    );


            String correctOption =
                    question.getCorrectOption();


            if (selected != null &&
                    correctOption != null &&
                    selected.trim()
                            .equalsIgnoreCase(
                                    correctOption.trim()
                            )) {

                correct++;

            } else {

                wrong++;
            }
        }


        // ========================================================
        // MARK
        // ========================================================

        *//*
         * Correct = +1
         * Wrong   = -0.25
         * Skip    = 0
         *//*

        double totalMark =
                correct
                        - (wrong * 0.25);


        *//*
         * Negative mark 0-এর নিচে যাবে না।
         *//*
        if (totalMark < 0) {

            totalMark = 0;
        }


        // ========================================================
        // RESULT INTENT
        // ========================================================

        *//*
         * তোমার Result Activity-এর নাম অনুযায়ী
         * এখানে class বসাবে।
         *//*
        Intent intent =
                new Intent(
                       // MockTestQuestionActivity.this,
                        //MockTestResultActivity.class
                );


        intent.putExtra(
                "total_questions",
                total
        );


        intent.putExtra(
                "correct_count",
                correct
        );


        intent.putExtra(
                "wrong_count",
                wrong
        );


        intent.putExtra(
                "skipped_count",
                skipped
        );


        intent.putExtra(
                "total_mark",
                totalMark
        );


        intent.putExtra(
                "unit_id",
                unitId
        );


        intent.putExtra(
                "year_id",
                yearId
        );


        intent.putExtra(
                "unit_name",
                unitName
        );


        intent.putExtra(
                "year_title",
                yearTitle
        );


        intent.putExtra(
                "subject_ids",
                selectedSubjectIds
        );


        startActivity(
                intent
        );


        finish();
    }*/





    private void submitMockTest() {

        /*
         * Timer চলমান থাকলে confirmation dialog দেখাবে।
         *
         * Timer শেষ হয়ে গেলে সরাসরি submit হবে।
         */
        if ( testTimer != null) {

            showSubmitConfirmDialog();

        } else {

            submitMockTestFinal();
        }
    }
    private void submitMockTestFinal() {

        stopTestTimer();
        saveMockTestStudyTime();

        int total = questionList.size();

        int correct = 0;
        int wrong = 0;
        int skipped = 0;

        /*
         * Question-wise review data
         */
        org.json.JSONArray reviewArray =
                new org.json.JSONArray();


        // ========================================================
        // CALCULATE RESULT + CREATE REVIEW DATA
        // ========================================================

        for (Question question : questionList) {

            int questionId =
                    question.getId();

            boolean isSkipped =
                    skippedQuestionIds.contains(
                            questionId
                    );

            String selectedAnswer =
                    answerMap.get(
                            questionId
                    );

            String correctAnswer =
                    question.getCorrectOption();


            // ====================================================
            // RESULT CALCULATION
            // ====================================================

            if (isSkipped) {

                skipped++;

            } else if (!answerMap.containsKey(questionId)) {

                /*
                 * Answer না দিয়ে শেষ করলে এটাও skipped
                 */
                skipped++;

            } else {

                if (selectedAnswer != null &&
                        correctAnswer != null &&
                        selectedAnswer.equalsIgnoreCase(
                                correctAnswer.trim()
                        )) {

                    correct++;

                } else {

                    wrong++;
                }
            }


            // ====================================================
            // CREATE QUESTION REVIEW JSON
            // ====================================================

            try {

                JSONObject object =
                        new JSONObject();

                object.put(
                        "question_id",
                        questionId
                );


                object.put(
                        "question",
                        question.getQuestionText()
                );


                object.put(
                        "options",
                        question.getOptions()
                );


                object.put(
                        "selected_answer",
                        selectedAnswer == null
                                ? ""
                                : selectedAnswer
                );


                object.put(
                        "correct_answer",
                        correctAnswer == null
                                ? ""
                                : correctAnswer
                );


                object.put(
                        "skipped",
                        isSkipped ||
                                !answerMap.containsKey(
                                        questionId
                                )
                );


                /*
                 * Explanation থাকলে পাঠাবে।
                 *
                 * Question class-এ getExplanation()
                 * থাকলে এটা ব্যবহার করবে।
                 */
                try {

                    object.put(
                            "explanation",
                            question.getExplanation() == null
                                    ? ""
                                    : question.getExplanation()
                    );

                } catch (Exception ignored) {

                    object.put(
                            "explanation",
                            ""
                    );
                }


                reviewArray.put(
                        object
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        }


        // ========================================================
        // MARK
        // ========================================================

        double mark =
                correct -
                        (wrong * 0.25);


        /*
         * Negative mark zero-এর নিচে যাবে না।
         */
        if (mark < 0) {

            mark = 0;
        }


        // ========================================================
        // RESULT INTENT
        // ========================================================

        Intent intent =
                new Intent(
                        MockTestQuestionActivity.this,
                        MockTestResultActivity.class
                );


        intent.putExtra(
                "total_questions",
                total
        );


        intent.putExtra(
                "correct_count",
                correct
        );


        intent.putExtra(
                "wrong_count",
                wrong
        );


        intent.putExtra(
                "skipped_count",
                skipped
        );


        intent.putExtra(
                "total_mark",
                mark
        );


        intent.putExtra(
                "unit_id",
                unitId
        );


        intent.putExtra(
                "year_id",
                yearId
        );


        intent.putExtra(
                "unit_name",
                unitName
        );


        intent.putExtra(
                "year_title",
                yearTitle
        );


        intent.putExtra(
                "subject_ids",
                selectedSubjectIds
        );


        /*
         * Question-wise review data
         */
        intent.putExtra(
                "review_data",
                reviewArray.toString()
        );


        startActivity(
                intent
        );


        finish();
    }


    private void saveMockTestStudyTime() {

        if (mockTestStartTime <= 0) {
            return;
        }

        long completedAt =
                System.currentTimeMillis();

        long durationSeconds =
                (completedAt - mockTestStartTime)
                        / 1000;

        StudyTimeRepository repository =
                new StudyTimeRepository(this);

        repository.saveStudySession(
                "MOCK_TEST",
                0,
                durationSeconds,
                mockTestStartTime,
                completedAt
        );

        repository.close();

        mockTestStartTime = 0L;
    }




  /*  private void submitMockTestFinal() {

        stopTestTimer();


        int total =
                questionList.size();


        int correct = 0;

        int wrong = 0;

        int skipped = 0;


        // ========================================================
        // CALCULATE RESULT
        // ========================================================

        for (Question question : questionList) {

            int questionId =
                    question.getId();


            // ====================================================
            // SKIPPED
            // ====================================================

            if (skippedQuestionIds.contains(questionId)) {

                skipped++;

                continue;
            }


            // ====================================================
            // UNANSWERED
            // ====================================================

            if (!answerMap.containsKey(questionId)) {

                skipped++;

                continue;
            }


            String selected =
                    answerMap.get(questionId);


            String correctOption =
                    question.getCorrectOption();


            if (selected != null &&
                    correctOption != null &&
                    selected.equalsIgnoreCase(
                            correctOption.trim()
                    )) {

                correct++;

            } else {

                wrong++;
            }
        }


        // ========================================================
        // MARK
        // ========================================================

        double mark =
                correct - (wrong * 0.25);


        *//*
         * Negative mark zero-এর নিচে যাবে না।
         *//*
        if (mark < 0) {

            mark = 0;
        }


        // ========================================================
        // RESULT
        // ========================================================

        Intent intent =
                new Intent(
                        MockTestQuestionActivity.this,
                       MockTestResultActivity.class
                );

        intent.putExtra(
                "total_questions",
                total
        );


        intent.putExtra(
                "correct_count",
                correct
        );


        intent.putExtra(
                "wrong_count",
                wrong
        );


        intent.putExtra(
                "skipped_count",
                skipped
        );


        intent.putExtra(
                "total_mark",
                mark
        );


        intent.putExtra(
                "unit_id",
                unitId
        );


        intent.putExtra(
                "year_id",
                yearId
        );


        intent.putExtra(
                "unit_name",
                unitName
        );


        intent.putExtra(
                "year_title",
                yearTitle
        );


        intent.putExtra(
                "subject_ids",
                selectedSubjectIds
        );


        startActivity(intent);


        finish();
    }*/

    private void showSubmitConfirmDialog() {

        if (isFinishing() || isDestroyed()) {
            return;
        }


        final android.app.Dialog dialog =
                new android.app.Dialog(this);


        dialog.requestWindowFeature(
                android.view.Window.FEATURE_NO_TITLE
        );


        View view =
                getLayoutInflater().inflate(
                        R.layout.dialog_submit_exam,
                        null
                );


        dialog.setContentView(view);


        /*
         * Dialog background transparent
         */
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(
                            Color.TRANSPARENT
                    )
            );

            dialog.getWindow().setDimAmount(0.45f);

            android.view.WindowManager.LayoutParams params =
                    dialog.getWindow().getAttributes();

            params.width =
                    (int) (
                            getResources()
                                    .getDisplayMetrics()
                                    .widthPixels
                                    * 0.88f
                    );

            params.height =
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setAttributes(params);
        }


        // ========================================================
        // BUTTONS
        // ========================================================

        View submitButton =
                view.findViewById(
                        R.id.confirmSubmitButton
                );


        View continueButton =
                view.findViewById(
                        R.id.cancelSubmitButton
                );


        // ========================================================
        // YES - SUBMIT
        // ========================================================

        submitButton.setOnClickListener(
                v -> {

                    dialog.dismiss();

                    submitMockTestFinal();
                }
        );


        // ========================================================
        // NO - CONTINUE
        // ========================================================

        continueButton.setOnClickListener(
                v -> dialog.dismiss()
        );


        /*
         * বাইরে click করলে dialog বন্ধ হবে না।
         */
        dialog.setCancelable(false);


        dialog.show();
    }


    // ============================================================
    // TIMER
    // ============================================================

    private void startTestTimer() {

        stopTestTimer();


        testTimer =
                new CountDownTimer(
                        totalTestTime,
                        1000
                ) {

                    @Override
                    public void onTick(
                            long millisUntilFinished
                    ) {

                        long totalSeconds =
                                millisUntilFinished
                                        / 1000;


                        long hours =
                                totalSeconds / 3600;


                        long minutes =
                                (totalSeconds % 3600)
                                        / 60;


                        long seconds =
                                totalSeconds % 60;


                        binding.timerText.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "%02d:%02d:%02d",
                                        hours,
                                        minutes,
                                        seconds
                                )
                        );
                    }


                    @Override
                    public void onFinish() {

                        binding.timerText.setText(
                                "00:00:00"
                        );


                        Toast.makeText(
                                MockTestQuestionActivity.this,
                                "সময় শেষ হয়েছে",
                                Toast.LENGTH_LONG
                        ).show();


                        submitMockTestFinal();
                    }
                };


        testTimer.start();
    }


    // ============================================================
    // STOP TIMER
    // ============================================================

    private void stopTestTimer() {

        if (testTimer != null) {

            testTimer.cancel();

            testTimer = null;
        }
    }


    // ============================================================
    // ON DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        stopTestTimer();
        saveMockTestStudyTime();


        if (questionRepository != null) {

            questionRepository.close();

            questionRepository = null;
        }


        if (databaseHelper != null) {

            databaseHelper.close();

            databaseHelper = null;

            database = null;
        }


        binding = null;


        super.onDestroy();
    }
}