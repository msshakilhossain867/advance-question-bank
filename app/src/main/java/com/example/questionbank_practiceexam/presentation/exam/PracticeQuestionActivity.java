package com.example.questionbank_practiceexam.presentation.exam;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.Question;
import com.example.questionbank_practiceexam.data.local.repository.QuestionRepository;
import com.example.questionbank_practiceexam.data.local.repository.StudyTimeRepository;
import com.example.questionbank_practiceexam.databinding.ActivityPracticeQuestionBinding;
import com.example.questionbank_practiceexam.databinding.BottomSheetReportBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PracticeQuestionActivity extends AppCompatActivity {

    private ActivityPracticeQuestionBinding binding;

    private QuestionRepository questionRepository;

    private List<Question> questionList =
            new ArrayList<>();

    private int currentPosition = 0;

    private int questionCount = -1;


    // ============================================================
    // PRACTICE TIMER
    // ============================================================

    private CountDownTimer practiceTimer;

    private boolean practiceTimeEnabled = false;

    private long totalPracticeTime = 0L;


    // ============================================================
    // SETTINGS
    // ============================================================

    private boolean showExplanation = true;


    // ============================================================
    // ANSWER
    // ============================================================

    private boolean answered = false;

    private String selectedOption = null;


    // ============================================================
    // MAIN RESULT
    // ============================================================

    private int correctCount = 0;

    private int wrongCount = 0;

    private int skippedCount = 0;


    // ============================================================
    // SKIP
    // ============================================================

    private int skippedQuestionCount = 0;

    private static final int MAX_SKIP_COUNT = 3;


    // ============================================================
    // SUBJECT PERFORMANCE
    // ============================================================

    /*
     * subjectId -> total questions
     */
    private final LinkedHashMap<Integer, Integer>
            subjectTotalMap =
            new LinkedHashMap<>();


    /*
     * subjectId -> correct questions
     */
    private final LinkedHashMap<Integer, Integer>
            subjectCorrectMap =
            new LinkedHashMap<>();


    /*
     * Subject names যদি আগের Activity থেকে আসে,
     * এখানে রাখা হবে।
     */
    private ArrayList<String> incomingSubjectNames =
            new ArrayList<>();


    /*
     * subject ID order
     */
    private ArrayList<Integer> selectedSubjectIds =
            new ArrayList<>();


    // ============================================================
    // REPORT EMAIL
    // ============================================================

    private static final String REPORT_EMAIL =
            "yourmail@gmail.com";

    private long practiceStartTime = 0L;


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
                ActivityPracticeQuestionBinding.inflate(
                        getLayoutInflater()
                );


        setContentView(
                binding.getRoot()
        );


        // ========================================================
        // GET INTENT DATA
        // ========================================================

        questionCount =
                getIntent().getIntExtra(
                        "question_count",
                        -1
                );


        showExplanation =
                getIntent().getBooleanExtra(
                        "show_explanation",
                        true
                );


        practiceTimeEnabled =
                getIntent().getBooleanExtra(
                        "time_per_question_enabled",
                        false
                );


        int[] subjectIds =
                getIntent().getIntArrayExtra(
                        "subject_ids"
                );

        practiceStartTime =
                System.currentTimeMillis();


        // ========================================================
        // SUBJECT NAMES
        // ========================================================

        ArrayList<String> names =
                getIntent().getStringArrayListExtra(
                        "subject_names"
                );


        if (names != null) {

            incomingSubjectNames.addAll(
                    names
            );

            android.util.Log.d(
                    "SUBJECT_DEBUG",
                    "Subject Names = " + names
            );
        }


        // ========================================================
        // VALIDATION
        // ========================================================

        if (subjectIds == null ||
                subjectIds.length == 0) {

            Toast.makeText(
                    this,
                    "No subject selected",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // ========================================================
        // STORE SUBJECT IDS
        // ========================================================

        for (int id : subjectIds) {

            if (!selectedSubjectIds.contains(id)) {

                selectedSubjectIds.add(id);
            }
        }


        // ========================================================
        // REPOSITORY
        // ========================================================

        questionRepository =
                new QuestionRepository(
                        getApplicationContext()
                );


        // ========================================================
        // LOAD QUESTIONS
        // ========================================================

        loadQuestions(subjectIds);


        // ========================================================
        // BUTTONS
        // ========================================================

        setupButtons();
    }


    // ============================================================
    // LOAD QUESTIONS
    // ============================================================

    private void loadQuestions(
            int[] subjectIds
    ) {

        Set<Integer> subjectIdSet =
                new HashSet<>();


        for (int id : subjectIds) {

            subjectIdSet.add(id);
        }


        List<Question> allQuestions =
                questionRepository
                        .getQuestionsBySubjects(
                                subjectIdSet
                        );


        if (allQuestions == null ||
                allQuestions.isEmpty()) {

            Toast.makeText(
                    this,
                    "No questions found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // ========================================================
        // RANDOM QUESTIONS
        // ========================================================

        Collections.shuffle(
                allQuestions
        );


        // ========================================================
        // QUESTION COUNT
        // ========================================================

        if (questionCount > 0 &&
                questionCount < allQuestions.size()) {

            questionList =
                    new ArrayList<>(
                            allQuestions.subList(
                                    0,
                                    questionCount
                            )
                    );

        } else {

            questionList =
                    new ArrayList<>(
                            allQuestions
                    );
        }


        currentPosition = 0;


        // ========================================================
        // BUILD SUBJECT TOTAL
        // ========================================================

        buildSubjectTotals();


        // ========================================================
        // START TOTAL PRACTICE TIMER
        // ========================================================

        if (practiceTimeEnabled) {

            /*
             * প্রতি প্রশ্ন = 60 seconds
             *
             * 20 প্রশ্ন = 20 minutes
             */

            totalPracticeTime =
                    questionList.size()
                            * 60L
                            * 1000L;


            startPracticeTimer();

        } else {

            binding.timerIcon.setVisibility(
                    View.GONE
            );

            binding.timerText.setVisibility(
                    View.GONE
            );
        }


        showQuestion();
    }


    private void savePracticeStudyTime() {

        if (practiceStartTime <= 0) {
            return;
        }

        long completedAt =
                System.currentTimeMillis();

        long durationSeconds =
                (completedAt - practiceStartTime)
                        / 1000;

        StudyTimeRepository repository =
                new StudyTimeRepository(this);

        repository.saveStudySession(
                "PRACTICE",
                0,
                durationSeconds,
                practiceStartTime,
                completedAt
        );

        repository.close();

        practiceStartTime = 0L;
    }


    // ============================================================
    // BUILD SUBJECT TOTAL
    // ============================================================

    private void buildSubjectTotals() {

        subjectTotalMap.clear();

        subjectCorrectMap.clear();


        for (Question question : questionList) {

            int subjectId =
                    question.getSubjectId();


            // ====================================================
            // TOTAL
            // ====================================================

            int oldTotal =
                    subjectTotalMap.containsKey(
                            subjectId
                    )
                            ? subjectTotalMap.get(
                            subjectId
                    )
                            : 0;


            subjectTotalMap.put(
                    subjectId,
                    oldTotal + 1
            );


            // ====================================================
            // INITIAL CORRECT = 0
            // ====================================================

            if (!subjectCorrectMap.containsKey(
                    subjectId
            )) {

                subjectCorrectMap.put(
                        subjectId,
                        0
                );
            }
        }
    }


    // ============================================================
    // SHOW QUESTION
    // ============================================================

    private void showQuestion() {

        if (questionList == null ||
                questionList.isEmpty()) {

            return;
        }


        Question question =
                questionList.get(
                        currentPosition
                );


        // ========================================================
        // RESET ANSWER STATE
        // ========================================================

        answered = false;

        selectedOption = null;


        // ========================================================
        // QUESTION NUMBER
        // ========================================================

        binding.questionNumber.setText(
                (currentPosition + 1)
                        + " / "
                        + questionList.size()
        );


        // ========================================================
        // QUESTION
        // ========================================================

        binding.questionText.setText(
                question.getQuestionText()
        );


        // ========================================================
        // OPTIONS
        // ========================================================

        setOptions(
                question
        );


        // ========================================================
        // RESET OPTION UI
        // ========================================================

        resetOptions();


        // ========================================================
        // NEXT DISABLED
        // ========================================================

        setNextButtonEnabled(
                false
        );


        // ========================================================
        // EXPLANATION HIDDEN
        // ========================================================

        binding.explanationLayout.setVisibility(
                View.GONE
        );
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
    // RESET OPTIONS
    // ============================================================

    private void resetOptions() {

        // ========================================================
        // A
        // ========================================================

        binding.optionA.setBackgroundResource(
                R.drawable.bg_option_normal
        );

        binding.optionALabel.setText("A");

        binding.optionALabel.setTextColor(
                Color.rgb(
                        123,
                        132,
                        146
                )
        );


        // ========================================================
        // B
        // ========================================================

        binding.optionB.setBackgroundResource(
                R.drawable.bg_option_normal
        );

        binding.optionBLabel.setText("B");

        binding.optionBLabel.setTextColor(
                Color.rgb(
                        123,
                        132,
                        146
                )
        );


        // ========================================================
        // C
        // ========================================================

        binding.optionC.setBackgroundResource(
                R.drawable.bg_option_normal
        );

        binding.optionCLabel.setText("C");

        binding.optionCLabel.setTextColor(
                Color.rgb(
                        123,
                        132,
                        146
                )
        );


        // ========================================================
        // D
        // ========================================================

        binding.optionD.setBackgroundResource(
                R.drawable.bg_option_normal
        );

        binding.optionDLabel.setText("D");

        binding.optionDLabel.setTextColor(
                Color.rgb(
                        123,
                        132,
                        146
                )
        );


        // ========================================================
        // TEXT COLOR
        // ========================================================

        binding.optionAText.setTextColor(
                Color.rgb(
                        51,
                        51,
                        51
                )
        );

        binding.optionBText.setTextColor(
                Color.rgb(
                        51,
                        51,
                        51
                )
        );

        binding.optionCText.setTextColor(
                Color.rgb(
                        51,
                        51,
                        51
                )
        );

        binding.optionDText.setTextColor(
                Color.rgb(
                        51,
                        51,
                        51
                )
        );


        // ========================================================
        // TYPEFACE
        // ========================================================

        binding.optionAText.setTypeface(
                null,
                Typeface.NORMAL
        );

        binding.optionBText.setTypeface(
                null,
                Typeface.NORMAL
        );

        binding.optionCText.setTypeface(
                null,
                Typeface.NORMAL
        );

        binding.optionDText.setTypeface(
                null,
                Typeface.NORMAL
        );


        // ========================================================
        // LABEL BACKGROUND
        // ========================================================

        binding.optionALabel.setBackgroundResource(
                R.drawable.bg_option_letter
        );

        binding.optionBLabel.setBackgroundResource(
                R.drawable.bg_option_letter
        );

        binding.optionCLabel.setBackgroundResource(
                R.drawable.bg_option_letter
        );

        binding.optionDLabel.setBackgroundResource(
                R.drawable.bg_option_letter
        );
    }


    // ============================================================
    // BUTTONS
    // ============================================================

    private void setupButtons() {

        // ========================================================
        // BACK
        // ========================================================

        binding.toolbar.setNavigationOnClickListener(
                v -> finish()
        );


        // ========================================================
        // OPTION A
        // ========================================================

        binding.optionA.setOnClickListener(
                v -> selectAnswer("a")
        );


        // ========================================================
        // OPTION B
        // ========================================================

        binding.optionB.setOnClickListener(
                v -> selectAnswer("b")
        );


        // ========================================================
        // OPTION C
        // ========================================================

        binding.optionC.setOnClickListener(
                v -> selectAnswer("c")
        );


        // ========================================================
        // OPTION D
        // ========================================================

        binding.optionD.setOnClickListener(
                v -> selectAnswer("d")
        );


        // ========================================================
        // SKIP
        // ========================================================

        binding.skipButton.setOnClickListener(
                v -> skipQuestion()
        );


        // ========================================================
        // REPORT
        // ========================================================

        binding.reportButton.setOnClickListener(
                v -> showReportBottomSheet()
        );


        // ========================================================
        // NEXT
        // ========================================================

        binding.nextButton.setOnClickListener(
                v -> {

                    if (!answered) {

                        Toast.makeText(
                                this,
                                "Please select an answer",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    nextQuestion();
                }
        );


        // ========================================================
        // INITIAL STATE
        // ========================================================

        setNextButtonEnabled(
                false
        );
    }


    // ============================================================
    // SELECT ANSWER
    // ============================================================

    private void selectAnswer(
            String selected
    ) {

        if (answered) {

            return;
        }


        Question question =
                questionList.get(
                        currentPosition
                );


        String correctOption =
                question.getCorrectOption();


        if (correctOption == null ||
                correctOption.trim().isEmpty()) {

            return;
        }


        correctOption =
                correctOption
                        .trim()
                        .toLowerCase();


        selected =
                selected
                        .trim()
                        .toLowerCase();


        answered = true;

        selectedOption = selected;


        int subjectId =
                question.getSubjectId();


        // ========================================================
        // CORRECT
        // ========================================================

        if (selected.equals(
                correctOption
        )) {

            correctCount++;


            // ====================================================
            // SUBJECT CORRECT
            // ====================================================

            int oldCorrect =
                    subjectCorrectMap.containsKey(
                            subjectId
                    )
                            ? subjectCorrectMap.get(
                            subjectId
                    )
                            : 0;


            subjectCorrectMap.put(
                    subjectId,
                    oldCorrect + 1
            );


            showCorrectOption(
                    selected
            );


        }

        // ========================================================
        // WRONG
        // ========================================================

        else {

            wrongCount++;


            showWrongOption(
                    selected
            );


            showCorrectOption(
                    correctOption
            );
        }


        // ========================================================
        // EXPLANATION
        // ========================================================

        if (showExplanation &&
                question.getExplanation() != null &&
                !question.getExplanation()
                        .trim()
                        .isEmpty()) {

            binding.explanationText.setText(
                    question.getExplanation()
            );


            binding.explanationLayout.setVisibility(
                    View.VISIBLE
            );
        }


        // ========================================================
        // ENABLE NEXT
        // ========================================================

        setNextButtonEnabled(
                true
        );
    }


    // ============================================================
    // ENABLE / DISABLE NEXT
    // ============================================================

    private void setNextButtonEnabled(
            boolean enabled
    ) {

        binding.nextButton.setEnabled(
                enabled
        );


        if (enabled) {

            binding.nextButton.setAlpha(
                    1f
            );

        } else {

            binding.nextButton.setAlpha(
                    0.45f
            );
        }
    }


    // ============================================================
    // CORRECT OPTION
    // ============================================================

    private void showCorrectOption(
            String option
    ) {

        switch (option) {

            case "a":

                binding.optionA.setBackgroundResource(
                        R.drawable.bg_option_correct
                );

                binding.optionALabel.setText("✓");

                binding.optionALabel.setTextColor(
                        Color.WHITE
                );

                binding.optionALabel.setBackgroundResource(
                        R.drawable.bg_option_letter_selected
                );

                binding.optionAText.setTextColor(
                        Color.rgb(
                                81,
                                68,
                                208
                        )
                );

                binding.optionAText.setTypeface(
                        null,
                        Typeface.BOLD
                );

                break;


            case "b":

                binding.optionB.setBackgroundResource(
                        R.drawable.bg_option_correct
                );

                binding.optionBLabel.setText("✓");

                binding.optionBLabel.setTextColor(
                        Color.WHITE
                );

                binding.optionBLabel.setBackgroundResource(
                        R.drawable.bg_option_letter_selected
                );

                binding.optionBText.setTextColor(
                        Color.rgb(
                                81,
                                68,
                                208
                        )
                );

                binding.optionBText.setTypeface(
                        null,
                        Typeface.BOLD
                );

                break;


            case "c":

                binding.optionC.setBackgroundResource(
                        R.drawable.bg_option_correct
                );

                binding.optionCLabel.setText("✓");

                binding.optionCLabel.setTextColor(
                        Color.WHITE
                );

                binding.optionCLabel.setBackgroundResource(
                        R.drawable.bg_option_letter_selected
                );

                binding.optionCText.setTextColor(
                        Color.rgb(
                                81,
                                68,
                                208
                        )
                );

                binding.optionCText.setTypeface(
                        null,
                        Typeface.BOLD
                );

                break;


            case "d":

                binding.optionD.setBackgroundResource(
                        R.drawable.bg_option_correct
                );

                binding.optionDLabel.setText("✓");

                binding.optionDLabel.setTextColor(
                        Color.WHITE
                );

                binding.optionDLabel.setBackgroundResource(
                        R.drawable.bg_option_letter_selected
                );

                binding.optionDText.setTextColor(
                        Color.rgb(
                                81,
                                68,
                                208
                        )
                );

                binding.optionDText.setTypeface(
                        null,
                        Typeface.BOLD
                );

                break;
        }
    }


    // ============================================================
    // WRONG OPTION
    // ============================================================

    private void showWrongOption(
            String option
    ) {

        switch (option) {

            case "a":

                binding.optionA.setBackgroundResource(
                        R.drawable.bg_option_wrong
                );

                binding.optionALabel.setText("✕");

                binding.optionALabel.setTextColor(
                        Color.WHITE
                );

                binding.optionALabel.setBackgroundResource(
                        R.drawable.bg_option_letter_wrong
                );

                break;


            case "b":

                binding.optionB.setBackgroundResource(
                        R.drawable.bg_option_wrong
                );

                binding.optionBLabel.setText("✕");

                binding.optionBLabel.setTextColor(
                        Color.WHITE
                );

                binding.optionBLabel.setBackgroundResource(
                        R.drawable.bg_option_letter_wrong
                );

                break;


            case "c":

                binding.optionC.setBackgroundResource(
                        R.drawable.bg_option_wrong
                );

                binding.optionCLabel.setText("✕");

                binding.optionCLabel.setTextColor(
                        Color.WHITE
                );

                binding.optionCLabel.setBackgroundResource(
                        R.drawable.bg_option_letter_wrong
                );

                break;


            case "d":

                binding.optionD.setBackgroundResource(
                        R.drawable.bg_option_wrong
                );

                binding.optionDLabel.setText("✕");

                binding.optionDLabel.setTextColor(
                        Color.WHITE
                );

                binding.optionDLabel.setBackgroundResource(
                        R.drawable.bg_option_letter_wrong
                );

                break;
        }
    }


    // ============================================================
    // SKIP QUESTION
    // ============================================================

    private void skipQuestion() {

        if (skippedQuestionCount >=
                MAX_SKIP_COUNT) {

            Toast.makeText(
                    this,
                    "You can skip maximum 3 questions",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        skippedQuestionCount++;

        skippedCount++;


        Toast.makeText(
                this,
                "Skipped "
                        + skippedQuestionCount
                        + " / "
                        + MAX_SKIP_COUNT,
                Toast.LENGTH_SHORT
        ).show();


        nextQuestion();
    }


    // ============================================================
    // NEXT QUESTION
    // ============================================================

    private void nextQuestion() {

        if (currentPosition <
                questionList.size() - 1) {

            currentPosition++;

            showQuestion();

        } else {

            finishPractice();
        }
    }


    // ============================================================
    // FINISH PRACTICE
    // ============================================================

    private void finishPractice() {

        stopPracticeTimer();
        savePracticeStudyTime();


        int totalQuestions =
                questionList.size();


        Intent intent =
                new Intent(
                        PracticeQuestionActivity.this,
                        PracticeResultActivity.class
                );


        // ========================================================
        // BASIC RESULT
        // ========================================================

        intent.putExtra(
                "total_questions",
                totalQuestions
        );


        intent.putExtra(
                "correct_count",
                correctCount
        );


        intent.putExtra(
                "wrong_count",
                wrongCount
        );


        intent.putExtra(
                "skipped_count",
                skippedCount
        );


        // ========================================================
        // SUBJECT PERFORMANCE
        // ========================================================

        ArrayList<String> resultSubjectNames =
                new ArrayList<>();


        ArrayList<Integer> resultSubjectCorrect =
                new ArrayList<>();


        ArrayList<Integer> resultSubjectTotal =
                new ArrayList<>();


        for (Map.Entry<Integer, Integer> entry :
                subjectTotalMap.entrySet()) {

            int subjectId =
                    entry.getKey();


            int total =
                    entry.getValue();


            int correct =
                    subjectCorrectMap.containsKey(
                            subjectId
                    )
                            ? subjectCorrectMap.get(
                            subjectId
                    )
                            : 0;


            // ====================================================
            // SUBJECT NAME
            // ====================================================

            String subjectName =
                    getSubjectName(
                            subjectId
                    );


            resultSubjectNames.add(
                    subjectName
            );


            resultSubjectCorrect.add(
                    correct
            );


            resultSubjectTotal.add(
                    total
            );
        }


        // ========================================================
        // SEND SUBJECT DATA
        // ========================================================

        intent.putStringArrayListExtra(
                "subject_names",
                resultSubjectNames
        );


        intent.putIntegerArrayListExtra(
                "subject_correct",
                resultSubjectCorrect
        );


        intent.putIntegerArrayListExtra(
                "subject_total",
                resultSubjectTotal
        );


        // ========================================================
        // SUBJECT IDS
        // ========================================================

        int[] subjectIds =
                getIntent().getIntArrayExtra(
                        "subject_ids"
                );


        if (subjectIds != null) {

            intent.putExtra(
                    "subject_ids",
                    subjectIds
            );
        }


        // ========================================================
        // OPEN RESULT
        // ========================================================

        startActivity(
                intent
        );


        finish();
    }


    // ============================================================
    // GET SUBJECT NAME
    // ============================================================

// ============================================================
// GET SUBJECT NAME
// ============================================================

    private String getSubjectName(
            int subjectId
    ) {

        // ========================================================
        // GET SUBJECT IDS
        // ========================================================

        int[] subjectIds =
                getIntent().getIntArrayExtra(
                        "subject_ids"
                );


        // ========================================================
        // CHECK DATA
        // ========================================================

        if (subjectIds == null ||
                subjectIds.length == 0) {

            return "Unknown Subject";
        }


        // ========================================================
        // MATCH ID WITH NAME
        // ========================================================

        for (int i = 0;
             i < subjectIds.length;
             i++) {

            if (subjectIds[i] == subjectId) {

                // ------------------------------------------------
                // NAME AVAILABLE
                // ------------------------------------------------

                if (incomingSubjectNames != null &&
                        i < incomingSubjectNames.size()) {

                    String subjectName =
                            incomingSubjectNames.get(i);


                    if (subjectName != null &&
                            !subjectName.trim().isEmpty()) {

                        return subjectName.trim();
                    }
                }

                break;
            }
        }


        // ========================================================
        // FALLBACK
        // ========================================================

        return "Subject ID: " + subjectId;
    }

    // ============================================================
    // START TOTAL PRACTICE TIMER
    // ============================================================

    private void startPracticeTimer() {

        stopPracticeTimer();


        practiceTimer =
                new CountDownTimer(
                        totalPracticeTime,
                        1000
                ) {

                    @Override
                    public void onTick(
                            long millisUntilFinished
                    ) {

                        long totalSeconds =
                                millisUntilFinished
                                        / 1000;


                        long minutes =
                                totalSeconds / 60;


                        long seconds =
                                totalSeconds % 60;


                        binding.timerText.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "%02d:%02d",
                                        minutes,
                                        seconds
                                )
                        );
                    }


                    @Override
                    public void onFinish() {

                        binding.timerText.setText(
                                "00:00"
                        );


                        Toast.makeText(
                                PracticeQuestionActivity.this,
                                "Practice time is over",
                                Toast.LENGTH_LONG
                        ).show();


                        finishPractice();
                    }
                };


        practiceTimer.start();


        binding.timerIcon.setVisibility(
                View.VISIBLE
        );


        binding.timerText.setVisibility(
                View.VISIBLE
        );
    }


    // ============================================================
    // STOP PRACTICE TIMER
    // ============================================================

    private void stopPracticeTimer() {

        if (practiceTimer != null) {

            practiceTimer.cancel();

            practiceTimer = null;
        }
    }


    // ============================================================
    // REPORT BOTTOM SHEET
    // ============================================================

    private void showReportBottomSheet() {

        if (questionList == null ||
                questionList.isEmpty()) {

            return;
        }


        Question question =
                questionList.get(
                        currentPosition
                );


        BottomSheetDialog dialog =
                new BottomSheetDialog(
                        this
                );


        BottomSheetReportBinding reportBinding =
                BottomSheetReportBinding.inflate(
                        getLayoutInflater()
                );


        dialog.setContentView(
                reportBinding.getRoot()
        );


        // ========================================================
        // DEFAULT REASON
        // ========================================================

        reportBinding.reportReasonGroup
                .check(
                        reportBinding.reasonWrongQuestion
                                .getId()
                );


        // ========================================================
        // CANCEL
        // ========================================================

        reportBinding.cancelReportButton
                .setOnClickListener(
                        v -> dialog.dismiss()
                );


        // ========================================================
        // SUBMIT
        // ========================================================

        reportBinding.submitReportButton
                .setOnClickListener(
                        v -> {

                            int selectedId =
                                    reportBinding
                                            .reportReasonGroup
                                            .getCheckedRadioButtonId();


                            if (selectedId == -1) {

                                Toast.makeText(
                                        this,
                                        "Please select a reason",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            String reason =
                                    "";


                            if (selectedId ==
                                    reportBinding
                                            .reasonWrongAnswer
                                            .getId()) {

                                reason =
                                        "Wrong Answer";

                            } else if (
                                    selectedId ==
                                            reportBinding
                                                    .reasonWrongExplanation
                                                    .getId()) {

                                reason =
                                        "Wrong Explanation";

                            } else if (
                                    selectedId ==
                                            reportBinding
                                                    .reasonWrongSpelling
                                                    .getId()) {

                                reason =
                                        "Spelling Mistake";

                            } else if (
                                    selectedId ==
                                            reportBinding
                                                    .reasonWrongQuestion
                                                    .getId()) {

                                reason =
                                        "Wrong Question";

                            } else if (
                                    selectedId ==
                                            reportBinding
                                                    .reasonOther
                                                    .getId()) {

                                reason =
                                        "Other";
                            }


                            String comment =
                                    reportBinding
                                            .reportComment
                                            .getText()
                                            .toString()
                                            .trim();


                            sendReportEmail(
                                    question,
                                    reason,
                                    comment
                            );


                            dialog.dismiss();
                        }
                );


        dialog.show();
    }


    // ============================================================
    // SEND REPORT EMAIL
    // ============================================================

    private void sendReportEmail(
            Question question,
            String reason,
            String comment
    ) {

        StringBuilder body =
                new StringBuilder();


        body.append(
                "Question Bank Practice - Question Report\n\n"
        );


        // ========================================================
        // QUESTION INFO
        // ========================================================

        body.append(
                "Question Number: "
        );

        body.append(
                currentPosition + 1
        );

        body.append("\n");


        body.append(
                "Question ID: "
        );

        body.append(
                question.getId()
        );

        body.append("\n");


        body.append(
                "Subject ID: "
        );

        body.append(
                question.getSubjectId()
        );

        body.append("\n");


        body.append(
                "University ID: "
        );

        body.append(
                question.getUniversityId()
        );

        body.append("\n");


        body.append(
                "Year: "
        );

        body.append(
                question.getYearTag()
        );

        body.append("\n\n");


        // ========================================================
        // QUESTION
        // ========================================================

        body.append(
                "QUESTION:\n"
        );

        body.append(
                question.getQuestionText()
        );

        body.append("\n\n");


        // ========================================================
        // OPTIONS
        // ========================================================

        body.append(
                "OPTIONS:\n"
        );


        try {

            JSONObject options =
                    new JSONObject(
                            question.getOptions()
                    );


            body.append(
                    "A. "
            );

            body.append(
                    options.optString("a")
            );

            body.append("\n");


            body.append(
                    "B. "
            );

            body.append(
                    options.optString("b")
            );

            body.append("\n");


            body.append(
                    "C. "
            );

            body.append(
                    options.optString("c")
            );

            body.append("\n");


            body.append(
                    "D. "
            );

            body.append(
                    options.optString("d")
            );

            body.append("\n");


        } catch (Exception ignored) {
        }


        // ========================================================
        // ANSWER
        // ========================================================

        body.append("\n");

        body.append(
                "Correct Answer: "
        );

        body.append(
                question.getCorrectOption()
        );

        body.append("\n\n");


        // ========================================================
        // REPORT
        // ========================================================

        body.append(
                "REPORT REASON:\n"
        );

        body.append(
                reason
        );

        body.append("\n\n");


        // ========================================================
        // USER COMMENT
        // ========================================================

        body.append(
                "USER COMMENT:\n"
        );


        if (comment.isEmpty()) {

            body.append(
                    "No additional comment"
            );

        } else {

            body.append(
                    comment
            );
        }


        body.append("\n\n");


        // ========================================================
        // EXPLANATION
        // ========================================================

        body.append(
                "CURRENT EXPLANATION:\n"
        );


        if (question.getExplanation() != null) {

            body.append(
                    question.getExplanation()
            );

        } else {

            body.append(
                    "No explanation"
            );
        }


        // ========================================================
        // EMAIL
        // ========================================================

        Intent emailIntent =
                new Intent(
                        Intent.ACTION_SENDTO
                );


        emailIntent.setData(
                Uri.parse(
                        "mailto:"
                                + REPORT_EMAIL
                )
        );


        emailIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Question Report - ID "
                        + question.getId()
        );


        emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                body.toString()
        );


        try {

            startActivity(
                    Intent.createChooser(
                            emailIntent,
                            "Send Report"
                    )
            );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "No email app found",
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        stopPracticeTimer();
        savePracticeStudyTime();


        super.onDestroy();


        if (questionRepository != null) {

            questionRepository.close();

            questionRepository = null;
        }


        binding = null;
    }
}