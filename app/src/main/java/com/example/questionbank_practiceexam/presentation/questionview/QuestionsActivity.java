package com.example.questionbank_practiceexam.presentation.questionview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.Question;
import com.example.questionbank_practiceexam.data.local.repository.QuestionRepository;
import com.example.questionbank_practiceexam.databinding.ActivityQuestionsBinding;


import org.json.JSONObject;

import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    private ActivityQuestionsBinding binding;
    private QuestionRepository questionRepository;

    private List<Question> questionList;

    private int currentPosition = 0;

    private int subjectId;
    private String subjectName;

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // ========================================================
        // VIEW BINDING
        // ========================================================

        binding = ActivityQuestionsBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        // ========================================================
        // GET INTENT DATA
        // ========================================================

        subjectId = getIntent().getIntExtra(
                "subject_id",
                -1
        );

        subjectName = getIntent().getStringExtra(
                "subject_name"
        );

        if (subjectId == -1) {

            Toast.makeText(
                    this,
                    "Invalid subject",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // ========================================================
        // REPOSITORY
        // ========================================================

        questionRepository = new QuestionRepository(
                getApplicationContext()
        );

        // ========================================================
        // LOAD QUESTIONS
        // ========================================================

        loadQuestions();

        // ========================================================
        // BUTTONS
        // ========================================================

        setupButtons();
    }

    // ============================================================
    // LOAD QUESTIONS
    // ============================================================

    private void loadQuestions() {

        questionList =
                questionRepository.getQuestionsBySubject(
                        subjectId
                );

        if (questionList == null ||
                questionList.isEmpty()) {

            Toast.makeText(
                    this,
                    "No questions found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        currentPosition = 0;

        showQuestion();
    }

    // ============================================================
    // SHOW CURRENT QUESTION
    // ============================================================

    private void showQuestion() {

        if (questionList == null ||
                questionList.isEmpty()) {
            return;
        }

        Question question =
                questionList.get(currentPosition);

        // ========================================================
        // QUESTION NUMBER
        // ========================================================

        binding.questionNumber.setText(
                (currentPosition + 1)
                        + " / "
                        + questionList.size()
        );

        // ========================================================
        // SUBJECT
        // ========================================================

        if (subjectName != null &&
                !subjectName.trim().isEmpty()) {

            binding.subjectText.setText(
                    subjectName
            );

        } else {

            binding.subjectText.setText(
                    "Question"
            );
        }

        // ========================================================
        // YEAR / TOPIC
        // ========================================================

        if (question.getYearTag() != null &&
                !question.getYearTag().trim().isEmpty()) {

            binding.topicText.setText(
                    question.getYearTag()
            );

        } else {

            binding.topicText.setText(
                    "Practice Question"
            );
        }

        // ========================================================
        // QUESTION TEXT
        // ========================================================

        binding.questionText.setText(
                question.getQuestionText()
        );

        // ========================================================
        // OPTIONS
        // ========================================================

        setOptions(question);

        // ========================================================
        // RESET OPTIONS
        // ========================================================

        resetOptions();

        // ========================================================
        // AUTO SELECT CORRECT ANSWER
        // ========================================================

        showCorrectAnswer(
                question.getCorrectOption()
        );

        // ========================================================
        // EXPLANATION
        // ========================================================

        if (question.getExplanation() != null &&
                !question.getExplanation().trim().isEmpty()) {

            binding.explanationText.setText(
                    question.getExplanation()
            );

            binding.explanationLayout.setVisibility(
                    View.VISIBLE
            );

        } else {

            binding.explanationLayout.setVisibility(
                    View.GONE
            );
        }

        // ========================================================
        // NAVIGATION BUTTONS
        // ========================================================

        updateNavigationButtons();
    }

    // ============================================================
    // SET OPTIONS FROM JSON
    // ============================================================

    private void setOptions(Question question) {

        try {

            JSONObject options =
                    new JSONObject(
                            question.getOptions()
                    );

            binding.option1.setText(
                    "A. " + options.optString("a")
            );

            binding.option2.setText(
                    "B. " + options.optString("b")
            );

            binding.option3.setText(
                    "C. " + options.optString("c")
            );

            binding.option4.setText(
                    "D. " + options.optString("d")
            );

        } catch (Exception e) {

            binding.option1.setText("A.");
            binding.option2.setText("B.");
            binding.option3.setText("C.");
            binding.option4.setText("D.");
        }
    }

    // ============================================================
    // RESET OPTIONS
    // ============================================================



    private void resetOptions() {

        binding.option1.setBackgroundResource(
                R.drawable.bg_option_normal
        );

        binding.option2.setBackgroundResource(
                R.drawable.bg_option_normal
        );

        binding.option3.setBackgroundResource(
                R.drawable.bg_option_normal
        );

        binding.option4.setBackgroundResource(
                R.drawable.bg_option_normal
        );

        binding.option1.setTextColor(
                Color.rgb(34, 34, 34)
        );

        binding.option2.setTextColor(
                Color.rgb(34, 34, 34)
        );

        binding.option3.setTextColor(
                Color.rgb(34, 34, 34)
        );

        binding.option4.setTextColor(
                Color.rgb(34, 34, 34)
        );
    }

    // ============================================================
    // AUTO SELECT CORRECT ANSWER
    // ============================================================

    private void showCorrectAnswer(
            String correctOption
    ) {

        if (correctOption == null) {
            return;
        }

        switch (correctOption.trim().toLowerCase()) {

            case "a":

                binding.option1.setBackgroundResource(
                        R.drawable.bg_option_correct
                );

                binding.option1.setTextColor(
                        Color.rgb(35, 131, 71)
                );

                binding.option1.setTypeface(
                        binding.option1.getTypeface(),
                        android.graphics.Typeface.BOLD
                );

                break;

            case "b":

                binding.option2.setBackgroundResource(
                        R.drawable.bg_option_correct
                );

                binding.option2.setTextColor(
                        Color.rgb(35, 131, 71)
                );

                binding.option2.setTypeface(
                        binding.option2.getTypeface(),
                        android.graphics.Typeface.BOLD
                );

                break;

            case "c":

                binding.option3.setBackgroundResource(
                        R.drawable.bg_option_correct
                );

                binding.option3.setTextColor(
                        Color.rgb(35, 131, 71)
                );

                binding.option3.setTypeface(
                        binding.option3.getTypeface(),
                        android.graphics.Typeface.BOLD
                );

                break;

            case "d":

                binding.option4.setBackgroundResource(
                        R.drawable.bg_option_correct
                );

                binding.option4.setTextColor(
                        Color.rgb(35, 131, 71)
                );

                binding.option4.setTypeface(
                        binding.option4.getTypeface(),
                        android.graphics.Typeface.BOLD
                );

                break;
        }
    }

    // ============================================================
    // BUTTONS
    // ============================================================

    private void setupButtons() {

        // ========================================================
        // BACK
        // ========================================================

        binding.backButton.setOnClickListener(v ->
                finish()
        );

        // ========================================================
        // PREVIOUS
        // ========================================================

        binding.previousButton.setOnClickListener(v -> {

            if (currentPosition > 0) {

                currentPosition--;

                showQuestion();
            }
        });

        // ========================================================
        // NEXT
        // ========================================================

        binding.nextButton.setOnClickListener(v -> {

            if (currentPosition <
                    questionList.size() - 1) {

                currentPosition++;

                showQuestion();

            } else {

                Toast.makeText(
                        this,
                        "All questions completed",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // ========================================================
        // BOOKMARK
        // ========================================================

        binding.bookmarkButton.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Bookmark feature coming next",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    // ============================================================
    // NAVIGATION BUTTON STATE
    // ============================================================

    private void updateNavigationButtons() {

        // ========================================================
        // PREVIOUS
        // ========================================================

        if (currentPosition == 0) {

            binding.previousButton.setEnabled(false);
            binding.previousButton.setAlpha(0.5f);

        } else {

            binding.previousButton.setEnabled(true);
            binding.previousButton.setAlpha(1f);
        }

        // ========================================================
        // NEXT
        // ========================================================

        if (currentPosition ==
                questionList.size() - 1) {

            binding.nextButton.setText(
                    "Finish"
            );

        } else {

            binding.nextButton.setText(
                    "Next"
            );
        }
    }

    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (questionRepository != null) {

            questionRepository.close();

            questionRepository = null;
        }

        binding = null;
    }
}