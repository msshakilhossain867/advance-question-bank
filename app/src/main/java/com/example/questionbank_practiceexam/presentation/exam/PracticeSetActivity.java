package com.example.questionbank_practiceexam.presentation.exam;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.questionbank_practiceexam.data.local.entity.Subject;
import com.example.questionbank_practiceexam.data.local.repository.QuestionRepository;
import com.example.questionbank_practiceexam.data.local.repository.SubjectRepository;
import com.example.questionbank_practiceexam.databinding.ActivityPracticeSetBinding;
import com.example.questionbank_practiceexam.presentation.adapters.PracticeSubjectAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Set;

public class PracticeSetActivity extends AppCompatActivity {

    private ActivityPracticeSetBinding binding;

    private SubjectRepository subjectRepository;
    private QuestionRepository questionRepository;

    private PracticeSubjectAdapter subjectAdapter;

    private int unitId;
    private String unitName;

    private int yearId;
    private String yearTitle;

    // ============================================================
    // SELECTED SUBJECTS
    // ============================================================

    private Set<Integer> selectedSubjectIds;

    // ============================================================
    // QUESTION COUNT
    //
    // 0  = nothing selected
    // -1 = ALL
    // ============================================================

    private int questionCount = 0;

    // Selected subjects-এর মোট available questions
    private int availableQuestionCount = 0;

    // ============================================================
    // OTHER OPTIONS
    // ============================================================

    private boolean timePerQuestionEnabled = false;

    private boolean showExplanation = true;


    // ============================================================
    // COLORS
    // ============================================================

    private static final int COLOR_PRIMARY =
            Color.rgb(91, 77, 224);

    private static final int COLOR_WHITE =
            Color.WHITE;

    private static final int COLOR_TEXT =
            Color.rgb(51, 51, 51);

    private static final int COLOR_STROKE =
            Color.rgb(217, 220, 227);

    private static final int COLOR_DISABLED =
            Color.rgb(235, 237, 242);


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
                ActivityPracticeSetBinding.inflate(
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
        // TOOLBAR BACK
        // ========================================================

        binding.toolbar.setNavigationOnClickListener(
                v -> finish()
        );


        // ========================================================
        // GET INTENT DATA
        // ========================================================

        unitId =
                getIntent().getIntExtra(
                        "unit_id",
                        -1
                );

        unitName =
                getIntent().getStringExtra(
                        "unit_name"
                );

        yearId =
                getIntent().getIntExtra(
                        "year_id",
                        -1
                );

        yearTitle =
                getIntent().getStringExtra(
                        "year_title"
                );


        // ========================================================
        // VALIDATION
        // ========================================================

        if (unitId == -1) {

            Toast.makeText(
                    this,
                    "Invalid unit",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // ========================================================
        // REPOSITORIES
        // ========================================================

        subjectRepository =
                new SubjectRepository(
                        getApplicationContext()
                );

        questionRepository =
                new QuestionRepository(
                        getApplicationContext()
                );


        // ========================================================
        // DEFAULT
        // ========================================================

        selectedSubjectIds = null;

        availableQuestionCount = 0;

        // IMPORTANT:
        // শুরুতে কোনো question count selected থাকবে না
        questionCount = 0;


        // ========================================================
        // LOAD SUBJECTS
        // ========================================================

        loadSubjects();


        // ========================================================
        // QUESTION COUNT
        // ========================================================

        setupQuestionCount();


        // ========================================================
        // TIME
        // ========================================================

        setupTimeSwitch();


        // ========================================================
        // EXPLANATION
        // ========================================================

        setupExplanationSwitch();


        // ========================================================
        // START PRACTICE
        // ========================================================

        binding.startPracticeButton.setOnClickListener(
                v -> openReviewScreen()
        );


        // ========================================================
        // INITIAL COUNT UI
        // ========================================================

        updateQuestionCountButtons();
    }


    // ============================================================
    // LOAD SUBJECTS
    // ============================================================

    private void loadSubjects() {

        List<Subject> subjects =
                subjectRepository.getSubjectsByUnit(
                        unitId
                );


        if (subjects == null ||
                subjects.isEmpty()) {

            Toast.makeText(
                    this,
                    "No subject found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        subjectAdapter =
                new PracticeSubjectAdapter(
                        subjects,
                        selectedIds -> {

                            // ====================================
                            // SAVE SELECTED SUBJECTS
                            // ====================================

                            selectedSubjectIds =
                                    selectedIds;


                            // ====================================
                            // RECALCULATE QUESTIONS
                            // ====================================

                            updateAvailableQuestionCount();
                        }
                );


        // ========================================================
        // GRID
        // ========================================================

        binding.subjectRecyclerView.setLayoutManager(
                new GridLayoutManager(
                        this,
                        2
                )
        );


        binding.subjectRecyclerView.setAdapter(
                subjectAdapter
        );
    }


    // ============================================================
    // QUESTION COUNT SETUP
    // ============================================================

    private void setupQuestionCount() {

        // ========================================================
        // ALL
        // ========================================================

        binding.countAll.setOnClickListener(
                v -> selectQuestionCount(-1)
        );


        // ========================================================
        // 10
        // ========================================================

        binding.count10.setOnClickListener(
                v -> selectQuestionCount(10)
        );


        // ========================================================
        // 20
        // ========================================================

        binding.count20.setOnClickListener(
                v -> selectQuestionCount(20)
        );


        // ========================================================
        // 30
        // ========================================================

        binding.count30.setOnClickListener(
                v -> selectQuestionCount(30)
        );


        // ========================================================
        // 40
        // ========================================================

        binding.count40.setOnClickListener(
                v -> selectQuestionCount(40)
        );


        // ========================================================
        // 50
        // ========================================================

        binding.count50.setOnClickListener(
                v -> selectQuestionCount(50)
        );


        // ========================================================
        // 60
        // ========================================================

        binding.count60.setOnClickListener(
                v -> selectQuestionCount(60)
        );
    }


    // ============================================================
    // SELECT QUESTION COUNT
    // ============================================================

    private void selectQuestionCount(int count) {

        // ========================================================
        // SUBJECT CHECK
        // ========================================================

        if (selectedSubjectIds == null ||
                selectedSubjectIds.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select subject first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // ALL
        // ========================================================

        if (count == -1) {

            if (availableQuestionCount <= 0) {

                Toast.makeText(
                        this,
                        "No questions available",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            questionCount = -1;

            updateQuestionCountButtons();

            return;
        }


        // ========================================================
        // AVAILABLE QUESTION CHECK
        // ========================================================

        if (availableQuestionCount < count) {

            Toast.makeText(
                    this,
                    "Only "
                            + availableQuestionCount
                            + " questions available",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // SELECT
        // ========================================================

        questionCount = count;

        updateQuestionCountButtons();
    }


    // ============================================================
    // UPDATE AVAILABLE QUESTION COUNT
    // ============================================================

    private void updateAvailableQuestionCount() {

        // ========================================================
        // NO SUBJECT
        // ========================================================

        if (selectedSubjectIds == null ||
                selectedSubjectIds.isEmpty()) {

            availableQuestionCount = 0;

            // কোনো count selected থাকবে না
            questionCount = 0;

            updateQuestionCountButtons();

            return;
        }


        // ========================================================
        // GET QUESTION COUNT FROM DATABASE
        // ========================================================

        availableQuestionCount =
                questionRepository
                        .getQuestionCountBySubjects(
                                selectedSubjectIds
                        );


        // ========================================================
        // DEBUG
        // ========================================================

        Log.d(
                "QUESTION_DEBUG",
                "Selected Subjects = "
                        + selectedSubjectIds
                        + " | Available Questions = "
                        + availableQuestionCount
        );


        // ========================================================
        // PREVIOUS SELECTION VALIDATION
        // ========================================================

        // ========================================================
// FIRST SUBJECT SELECTED
// ========================================================

// যদি প্রথমবার subject select করা হয়
// তাহলে ALL automatically selected হবে
        if (availableQuestionCount > 0 &&
                questionCount == 0) {

            questionCount = -1;
        }


// ========================================================
// PREVIOUS COUNT NO LONGER AVAILABLE
// ========================================================

        if (questionCount > 0 &&
                questionCount > availableQuestionCount) {

            // আগের selected count আর possible না হলে
            // ALL automatically selected হবে
            questionCount = -1;
        }


// ========================================================
// ALL VALIDATION
// ========================================================

        if (questionCount == -1 &&
                availableQuestionCount <= 0) {

            questionCount = 0;
        }


// ========================================================
// UPDATE UI
// ========================================================


        // ========================================================
        // UPDATE BUTTONS
        // ========================================================

        updateQuestionCountButtons();
    }


    // ============================================================
    // UPDATE ALL QUESTION BUTTON UI
    // ============================================================

    private void updateQuestionCountButtons() {

        boolean hasSubject =
                selectedSubjectIds != null
                        && !selectedSubjectIds.isEmpty();


        // ========================================================
        // NO SUBJECT
        // ========================================================

        if (!hasSubject) {

            setCountButtonState(
                    binding.countAll,
                    false,
                    false
            );

            setCountButtonState(
                    binding.count10,
                    false,
                    false
            );

            setCountButtonState(
                    binding.count20,
                    false,
                    false
            );

            setCountButtonState(
                    binding.count30,
                    false,
                    false
            );

            setCountButtonState(
                    binding.count40,
                    false,
                    false
            );

            setCountButtonState(
                    binding.count50,
                    false,
                    false
            );

            setCountButtonState(
                    binding.count60,
                    false,
                    false
            );

            return;
        }


        // ========================================================
        // HAS SUBJECT
        // ========================================================

        setCountButtonState(
                binding.countAll,
                availableQuestionCount > 0,
                questionCount == -1
        );


        setCountButtonState(
                binding.count10,
                availableQuestionCount >= 10,
                questionCount == 10
        );


        setCountButtonState(
                binding.count20,
                availableQuestionCount >= 20,
                questionCount == 20
        );


        setCountButtonState(
                binding.count30,
                availableQuestionCount >= 30,
                questionCount == 30
        );


        setCountButtonState(
                binding.count40,
                availableQuestionCount >= 40,
                questionCount == 40
        );


        setCountButtonState(
                binding.count50,
                availableQuestionCount >= 50,
                questionCount == 50
        );


        setCountButtonState(
                binding.count60,
                availableQuestionCount >= 60,
                questionCount == 60
        );
    }


    // ============================================================
    // BUTTON STATE + UI
    // ============================================================

    private void setCountButtonState(
            MaterialButton button,
            boolean enabled,
            boolean selected
    ) {

        // ========================================================
        // ENABLE / DISABLE
        // ========================================================

        button.setEnabled(enabled);


        // ========================================================
        // SELECTED
        // ========================================================

        if (selected && enabled) {

            button.setSelected(true);

            button.setAlpha(1.0f);

            button.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            COLOR_PRIMARY
                    )
            );

            button.setStrokeColor(
                    android.content.res.ColorStateList.valueOf(
                            COLOR_STROKE
                    )
            );

            button.setTextColor(
                    COLOR_WHITE
            );

            return;
        }


        // ========================================================
        // DISABLED
        // ========================================================

        if (!enabled) {

            button.setSelected(false);

            button.setAlpha(0.45f);

            button.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            COLOR_DISABLED
                    )
            );

            button.setStrokeColor(
                    android.content.res.ColorStateList.valueOf(
                            COLOR_STROKE
                    )
            );

            button.setTextColor(
                    Color.rgb(
                            150,
                            150,
                            150
                    )
            );

            return;
        }


        // ========================================================
        // ENABLED + NOT SELECTED
        // ========================================================

        button.setSelected(false);

        button.setAlpha(1.0f);

        button.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        COLOR_WHITE
                )
        );

        button.setStrokeColor(
                android.content.res.ColorStateList.valueOf(
                        COLOR_STROKE
                )
        );

        button.setTextColor(
                COLOR_TEXT
        );
    }


    // ============================================================
    // TIME SWITCH
    // ============================================================

    private void setupTimeSwitch() {

        binding.timeSwitch.setChecked(false);

        timePerQuestionEnabled = false;


        binding.timeSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    timePerQuestionEnabled =
                            isChecked;
                }
        );
    }


    // ============================================================
    // EXPLANATION SWITCH
    // ============================================================

    private void setupExplanationSwitch() {

        binding.explanationSwitch.setChecked(true);

        showExplanation = true;


        binding.explanationSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    showExplanation =
                            isChecked;
                }
        );
    }


    // ============================================================
    // OPEN REVIEW
    // ============================================================

    private void openReviewScreen() {

        // ========================================================
        // SUBJECT CHECK
        // ========================================================

        if (selectedSubjectIds == null ||
                selectedSubjectIds.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select at least one subject",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // QUESTION COUNT CHECK
        // ========================================================

        if (questionCount == 0) {

            Toast.makeText(
                    this,
                    "Please select number of questions",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // VALIDATE COUNT
        // ========================================================

        if (questionCount > 0 &&
                questionCount > availableQuestionCount) {

            Toast.makeText(
                    this,
                    "Not enough questions available",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // SUBJECT IDS
        // ========================================================

        int[] subjectIds =
                new int[selectedSubjectIds.size()];


        int index = 0;


        for (Integer id : selectedSubjectIds) {

            subjectIds[index++] = id;
        }


        // ========================================================
        // REVIEW ACTIVITY
        // ========================================================

        Intent intent =
                new Intent(
                        PracticeSetActivity.this,
                        ReviewStartActivity.class
                );

        intent.putExtra(
                "subject_ids",
                subjectIds
        );

        intent.putExtra(
                "unit_id",
                unitId
        );

        intent.putExtra(
                "unit_name",
                unitName
        );



        intent.putExtra(
                "year_id",
                yearId
        );

        intent.putExtra(
                "year_title",
                yearTitle
        );

        intent.putExtra(
                "question_count",
                questionCount
        );

        intent.putExtra(
                "time_per_question_enabled",
                timePerQuestionEnabled
        );

        intent.putExtra(
                "show_explanation",
                showExplanation
        );

        startActivity(intent);


        // ========================================================
        // TEMPORARY TEST
        // ========================================================

        String message;

        if (questionCount == -1) {

            message =
                    "All "
                            + availableQuestionCount
                            + " questions selected";

        } else {

            message =
                    questionCount
                            + " questions selected";
        }


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }


    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();


        if (subjectRepository != null) {

            subjectRepository.close();

            subjectRepository = null;
        }


        if (questionRepository != null) {

            questionRepository.close();

            questionRepository = null;
        }


        binding = null;
    }
}