package com.example.questionbank_practiceexam.presentation.exam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.questionbank_practiceexam.data.local.entity.Subject;
import com.example.questionbank_practiceexam.data.local.repository.SubjectRepository;
import com.example.questionbank_practiceexam.databinding.ActivityReviewStartBinding;

import java.util.ArrayList;
import java.util.List;

public class ReviewStartActivity extends AppCompatActivity {

    private ActivityReviewStartBinding binding;

    private SubjectRepository subjectRepository;

    // ============================================================
    // DATA
    // ============================================================

    private int[] subjectIds;

    private int unitId;
    private String unitName;

    private int yearId;
    private String yearTitle;

    // -1 = ALL
    private int questionCount = -1;

    private boolean timePerQuestionEnabled = false;

    private boolean showExplanation = true;


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
                ActivityReviewStartBinding.inflate(
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
        // REPOSITORY
        // ========================================================

        subjectRepository =
                new SubjectRepository(
                        getApplicationContext()
                );


        // ========================================================
        // GET DATA
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
        // SHOW DATA
        // ========================================================

        showSelectedData();


        // ========================================================
        // TOOLBAR
        // ========================================================

        binding.toolbar.setNavigationOnClickListener(
                v -> finish()
        );


        // ========================================================
        // START PRACTICE
        // ========================================================

        binding.startPracticeButton.setOnClickListener(
                v -> startPractice()
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


        // --------------------------------------------------------
        // QUESTION COUNT
        // --------------------------------------------------------

        questionCount =
                getIntent().getIntExtra(
                        "question_count",
                        -1
                );


        // --------------------------------------------------------
        // TIME
        // --------------------------------------------------------

        timePerQuestionEnabled =
                getIntent().getBooleanExtra(
                        "time_per_question_enabled",
                        false
                );


        // --------------------------------------------------------
        // EXPLANATION
        // --------------------------------------------------------

        showExplanation =
                getIntent().getBooleanExtra(
                        "show_explanation",
                        true
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

        return true;
    }


    // ============================================================
    // SHOW SELECTED DATA
    // ============================================================

    private void showSelectedData() {

        // --------------------------------------------------------
        // SUBJECTS
        // --------------------------------------------------------

        String subjectText =
                getSubjectNames();


        binding.selectedSubjectsText.setText(
                subjectText
        );


        // --------------------------------------------------------
        // QUESTION COUNT
        // --------------------------------------------------------

        if (questionCount == -1) {

            binding.selectedQuestionCount.setText(
                    "All"
            );

        } else {

            binding.selectedQuestionCount.setText(
                    String.valueOf(questionCount)
            );
        }


        // --------------------------------------------------------
        // TIME
        // --------------------------------------------------------

        if (timePerQuestionEnabled) {

            binding.selectedTime.setText(
                    "Enabled"
            );

        } else {

            binding.selectedTime.setText(
                    "Not Set"
            );
        }


        // --------------------------------------------------------
        // EXPLANATION
        // --------------------------------------------------------

        if (showExplanation) {

            binding.selectedExplanation.setText(
                    "Yes"
            );

        } else {

            binding.selectedExplanation.setText(
                    "No"
            );
        }


        // --------------------------------------------------------
        // INFO TEXT
        // --------------------------------------------------------

        String infoText;

        if (questionCount == -1) {

            infoText =
                    "You will get all available random MCQ based on your selection.";

        } else {

            infoText =
                    "You will get "
                            + questionCount
                            + " random MCQ based on your selection.";
        }


        binding.infoText.setText(
                infoText
        );
    }


    // ============================================================
    // GET SUBJECT NAMES
    // ============================================================

    private String getSubjectNames() {

        if (subjectIds == null ||
                subjectIds.length == 0) {

            return "No subject";
        }


        List<String> names =
                new ArrayList<>();


        List<Subject> subjects =
                subjectRepository.getSubjectsByUnit(
                        unitId
                );


        if (subjects != null) {

            for (Subject subject : subjects) {

                for (int id : subjectIds) {

                    if (subject.getId() == id) {

                        names.add(
                                subject.getName()
                        );

                        break;
                    }
                }
            }
        }


        // --------------------------------------------------------
        // FALLBACK
        // --------------------------------------------------------

        if (names.isEmpty()) {

            return subjectIds.length
                    + " subject(s) selected";
        }


        // --------------------------------------------------------
        // BUILD TEXT
        // --------------------------------------------------------

        StringBuilder builder =
                new StringBuilder();


        for (int i = 0;
             i < names.size();
             i++) {

            builder.append(
                    names.get(i)
            );

            if (i < names.size() - 1) {

                builder.append(", ");
            }
        }


        return builder.toString();
    }


    // ============================================================
    // START PRACTICE
    // ============================================================

    /*private void startPractice() {

        // --------------------------------------------------------
        // TEST
        // --------------------------------------------------------

        String message;

        if (questionCount == -1) {

            message =
                    "Starting all available questions";

        } else {

            message =
                    "Starting "
                            + questionCount
                            + " questions";
        }


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();


        // ========================================================
        // NEXT ACTIVITY
        // ========================================================



        Intent intent =
                new Intent(
                        ReviewStartActivity.this,
                        PracticeQuestionActivity.class
                );

        intent.putExtra(
                "subject_ids",
                subjectIds
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
        //
        // পরে তোমার actual MCQ Activity এখানে বসাবে।
        //
        // Example:
        //
        // Intent intent =
        //         new Intent(
        //                 ReviewStartActivity.this,
        //                 MCQActivity.class
        //         );
        //
        // intent.putExtra("subject_ids", subjectIds);
        // intent.putExtra("unit_id", unitId);
        // intent.putExtra("unit_name", unitName);
        // intent.putExtra("year_id", yearId);
        // intent.putExtra("year_title", yearTitle);
        // intent.putExtra("question_count", questionCount);
        // intent.putExtra(
        //         "time_per_question_enabled",
        //         timePerQuestionEnabled
        // );
        // intent.putExtra(
        //         "show_explanation",
        //         showExplanation
        // );
        //
        // startActivity(intent);
        //
        // ========================================================
    }*/

    // ============================================================
// START PRACTICE
// ============================================================

    private void startPractice() {

        // --------------------------------------------------------
        // SUBJECT CHECK
        // --------------------------------------------------------

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
        // GET SUBJECT NAMES
        // ========================================================

        ArrayList<String> subjectNames =
                new ArrayList<>();


        List<Subject> subjects =
                subjectRepository.getSubjectsByUnit(
                        unitId
                );


        if (subjects != null &&
                !subjects.isEmpty()) {

            // subjectIds-এর order অনুযায়ী name নেওয়া হবে

            for (int selectedId : subjectIds) {

                for (Subject subject : subjects) {

                    if (subject.getId() == selectedId) {

                        subjectNames.add(
                                subject.getName()
                        );

                        break;
                    }
                }
            }
        }


        // ========================================================
        // VALIDATION
        // ========================================================

        if (subjectNames.size() != subjectIds.length) {

            Toast.makeText(
                    this,
                    "Unable to load subject names",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // TEST MESSAGE
        // ========================================================

        String message;

        if (questionCount == -1) {

            message =
                    "Starting all available questions";

        } else {

            message =
                    "Starting "
                            + questionCount
                            + " questions";
        }


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();


        // ========================================================
        // OPEN PRACTICE QUESTION ACTIVITY
        // ========================================================

        Intent intent =
                new Intent(
                        ReviewStartActivity.this,
                        PracticeQuestionActivity.class
                );


        // ========================================================
        // SUBJECT IDS
        // ========================================================

        intent.putExtra(
                "subject_ids",
                subjectIds
        );


        // ========================================================
        // SUBJECT NAMES
        // ========================================================

        intent.putStringArrayListExtra(
                "subject_names",
                subjectNames
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
        // QUESTION COUNT
        // ========================================================

        intent.putExtra(
                "question_count",
                questionCount
        );


        // ========================================================
        // TIME
        // ========================================================

        intent.putExtra(
                "time_per_question_enabled",
                timePerQuestionEnabled
        );


        // ========================================================
        // EXPLANATION
        // ========================================================

        intent.putExtra(
                "show_explanation",
                showExplanation
        );


        startActivity(intent);
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

        binding = null;
    }
}