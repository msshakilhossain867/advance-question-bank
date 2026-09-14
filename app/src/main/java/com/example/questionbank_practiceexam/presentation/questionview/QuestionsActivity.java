package com.example.questionbank_practiceexam.presentation.questionview;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.Question;
import com.example.questionbank_practiceexam.data.local.repository.BookmarkRepository;
import com.example.questionbank_practiceexam.data.local.repository.QuestionRepository;
import com.example.questionbank_practiceexam.data.local.repository.ReadingProgressRepository;
import com.example.questionbank_practiceexam.data.local.repository.RecentLearningRepository;
import com.example.questionbank_practiceexam.data.local.repository.StudyTimeRepository;
import com.example.questionbank_practiceexam.databinding.ActivityQuestionsBinding;
import com.example.questionbank_practiceexam.helpers.LatexRenderer;

import org.json.JSONObject;

import java.util.List;


public class QuestionsActivity extends AppCompatActivity {


    // ============================================================
    // VIEW BINDING
    // ============================================================

    private ActivityQuestionsBinding binding;


    // ============================================================
    // REPOSITORIES
    // ============================================================

    private QuestionRepository questionRepository;

    private BookmarkRepository bookmarkRepository;

    private ReadingProgressRepository readingProgressRepository;

    private RecentLearningRepository recentLearningRepository;


    // ============================================================
    // QUESTIONS
    // ============================================================

    private List<Question> questionList;

    private int currentPosition = 0;

    private int subjectId;

    private String subjectName;


    // ============================================================
    // READING TIME
    // ============================================================

    private long readingStartTime = 0L;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        EdgeToEdge.enable(this);


        // ========================================================
        // VIEW BINDING
        // ========================================================

        binding =
                ActivityQuestionsBinding.inflate(
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
        // GET INTENT DATA
        // ========================================================

        subjectId =
                getIntent().getIntExtra(
                        "subject_id",
                        -1
                );


        subjectName =
                getIntent().getStringExtra(
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
        // QUESTION REPOSITORY
        // ========================================================

        questionRepository =
                new QuestionRepository(
                        getApplicationContext()
                );


        // ========================================================
        // BOOKMARK REPOSITORY
        // ========================================================

        bookmarkRepository =
                new BookmarkRepository(
                        getApplicationContext()
                );


        // ========================================================
        // READING PROGRESS
        // ========================================================

        readingProgressRepository =
                new ReadingProgressRepository(
                        getApplicationContext()
                );


        // ========================================================
        // RECENT LEARNING
        // ========================================================

        recentLearningRepository =
                new RecentLearningRepository(
                        this
                );


        readingStartTime =
                System.currentTimeMillis();


        // ========================================================
        // LOAD QUESTIONS
        // ========================================================

        loadQuestions();


        // ========================================================
        // BUTTONS
        // ========================================================

        setupButtons();


        // ========================================================
        // BACK BUTTON
        // ========================================================

        getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(true) {

                            @Override
                            public void handleOnBackPressed() {

                                saveRecentLearning();

                                finish();
                            }
                        }
                );
    }


    // ============================================================
    // LOAD QUESTIONS
    // ============================================================

    private void loadQuestions() {

        questionList =
                questionRepository
                        .getQuestionsBySubject(
                                subjectId
                        );


        if (
                questionList == null ||
                        questionList.isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "No questions found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // ========================================================
        // RESUME POSITION
        // ========================================================

        int savedPosition =
                getIntent().getIntExtra(
                        "content_position",
                        0
                );


        if (
                savedPosition >= 0 &&
                        savedPosition < questionList.size()
        ) {

            currentPosition =
                    savedPosition;

        } else {

            currentPosition = 0;
        }


        showQuestion();
    }


    // ============================================================
    // SAVE READING STUDY TIME
    // ============================================================

    private void saveReadingStudyTime() {

        if (readingStartTime <= 0) {
            return;
        }


        long completedAt =
                System.currentTimeMillis();


        long durationSeconds =
                (
                        completedAt -
                                readingStartTime
                ) / 1000;


        if (durationSeconds <= 0) {

            readingStartTime = 0L;

            return;
        }


        StudyTimeRepository repository =
                new StudyTimeRepository(
                        this
                );


        repository.saveStudySession(
                "READING",
                0,
                durationSeconds,
                readingStartTime,
                completedAt
        );


        repository.close();


        readingStartTime = 0L;
    }


    // ============================================================
    // SAVE RECENT LEARNING
    // ============================================================

    private void saveRecentLearning() {

        if (
                recentLearningRepository == null ||
                        questionList == null ||
                        questionList.isEmpty()
        ) {

            return;
        }


        Question question =
                questionList.get(
                        currentPosition
                );


        // ========================================================
        // CHAPTER / TOPIC
        // ========================================================

        String chapterName =
                question.getYearTag();


        if (
                chapterName == null ||
                        chapterName.trim().isEmpty()
        ) {

            chapterName =
                    "Practice Questions";
        }


        // ========================================================
        // PROGRESS
        // ========================================================

        int progressPercent = 0;


        if (questionList.size() > 0) {

            progressPercent =
                    (int) (
                            (
                                    (currentPosition + 1)
                                            * 100.0
                            )
                                    / questionList.size()
                    );
        }


        // ========================================================
        // SAVE
        // ========================================================

        recentLearningRepository.saveRecentLearning(

                subjectId,

                subjectName == null
                        ? "Questions"
                        : subjectName,

                0,

                chapterName,

                currentPosition,

                progressPercent
        );
    }


    // ============================================================
    // SHOW CURRENT QUESTION
    // ============================================================

    private void showQuestion() {

        if (
                questionList == null ||
                        questionList.isEmpty()
        ) {

            return;
        }


        Question question =
                questionList.get(
                        currentPosition
                );


        saveRecentLearning();


        // ========================================================
        // MARK QUESTION AS READ
        // ========================================================

        if (
                readingProgressRepository != null
        ) {

            readingProgressRepository
                    .markQuestionAsRead(
                            question.getId()
                    );
        }


        // ========================================================
        // BOOKMARK
        // ========================================================

        updateBookmarkButton(
                question.getId()
        );


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

        if (
                subjectName != null &&
                        !subjectName.trim().isEmpty()
        ) {

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

        if (
                question.getYearTag() != null &&
                        !question.getYearTag()
                                .trim()
                                .isEmpty()
        ) {

            binding.topicText.setText(
                    question.getYearTag()
            );

        } else {

            binding.topicText.setText(
                    "Practice Question"
            );
        }


        // ========================================================
        // QUESTION
        // ========================================================

        LatexRenderer.renderQuestion(
                binding.questionText,
                question.getQuestionText()
        );


        // ========================================================
        // OPTIONS
        // ========================================================

        setOptions(
                question
        );


        // ========================================================
        // RESET OPTION STYLE
        // ========================================================

        resetOptions();


        // ========================================================
        // CORRECT ANSWER
        // ========================================================

        showCorrectAnswer(
                question.getCorrectOption(),
                question
        );


        // ========================================================
        // EXPLANATION
        // ========================================================

        if (
                question.getExplanation() != null &&
                        !question.getExplanation()
                                .trim()
                                .isEmpty()
        ) {

            LatexRenderer.renderExplanation(
                    binding.explanationText,
                    question.getExplanation()
            );


            binding.explanationLayout
                    .setVisibility(
                            View.VISIBLE
                    );

        } else {

            binding.explanationLayout
                    .setVisibility(
                            View.GONE
                    );
        }


        // ========================================================
        // NAVIGATION
        // ========================================================

        updateNavigationButtons();
    }


    // ============================================================
    // SET OPTIONS
    // ============================================================

    private void setOptions(Question question) {

        try {

            JSONObject options =
                    new JSONObject(question.getOptions());

            LatexRenderer.renderOption(
                    binding.option1,
                    "A.&nbsp;&nbsp;" +
                            options.optString("a", "")
            );

            LatexRenderer.renderOption(
                    binding.option2,
                    "B.&nbsp;&nbsp;" +
                            options.optString("b", "")
            );

            LatexRenderer.renderOption(
                    binding.option3,
                    "C.&nbsp;&nbsp;" +
                            options.optString("c", "")
            );

            LatexRenderer.renderOption(
                    binding.option4,
                    "D.&nbsp;&nbsp;" +
                            options.optString("d", "")
            );

        } catch (Exception e) {

            LatexRenderer.renderOption(
                    binding.option1,
                    "A."
            );

            LatexRenderer.renderOption(
                    binding.option2,
                    "B."
            );

            LatexRenderer.renderOption(
                    binding.option3,
                    "C."
            );

            LatexRenderer.renderOption(
                    binding.option4,
                    "D."
            );
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
    }


    // ============================================================
    // SHOW CORRECT ANSWER
    // ============================================================

    private void showCorrectAnswer(
            String correctOption,
            Question question
    ) {

        if (correctOption == null) {
            return;
        }

        String correct =
                correctOption
                        .trim()
                        .toLowerCase();

        try {

            JSONObject options =
                    new JSONObject(
                            question.getOptions()
                    );

            switch (correct) {

                case "a":

                    binding.option1.setBackgroundResource(
                            R.drawable.bg_option_correct
                    );

                    LatexRenderer.renderCorrectOption(
                            binding.option1,
                            "A.&nbsp;&nbsp;" +
                                    options.optString("a", "")
                    );

                    break;


                case "b":

                    binding.option2.setBackgroundResource(
                            R.drawable.bg_option_correct
                    );

                    LatexRenderer.renderCorrectOption(
                            binding.option2,
                            "B.&nbsp;&nbsp;" +
                                    options.optString("b", "")
                    );

                    break;


                case "c":

                    binding.option3.setBackgroundResource(
                            R.drawable.bg_option_correct
                    );

                    LatexRenderer.renderCorrectOption(
                            binding.option3,
                            "C.&nbsp;&nbsp;" +
                                    options.optString("c", "")
                    );

                    break;


                case "d":

                    binding.option4.setBackgroundResource(
                            R.drawable.bg_option_correct
                    );

                    LatexRenderer.renderCorrectOption(
                            binding.option4,
                            "D.&nbsp;&nbsp;" +
                                    options.optString("d", "")
                    );

                    break;
            }

        } catch (Exception e) {
            // JSON parse error
        }
    }


    // ============================================================
    // BUTTONS
    // ============================================================

    private void setupButtons() {


        // ========================================================
        // BACK
        // ========================================================

        binding.backButton.setOnClickListener(
                v -> {

                    saveRecentLearning();

                    finish();
                }
        );


        // ========================================================
        // PREVIOUS
        // ========================================================

        binding.previousButton.setOnClickListener(
                v -> {

                    if (
                            currentPosition > 0
                    ) {

                        currentPosition--;

                        showQuestion();
                    }
                }
        );


        // ========================================================
        // NEXT
        // ========================================================

        binding.nextButton.setOnClickListener(
                v -> {

                    if (
                            currentPosition <
                                    questionList.size() - 1
                    ) {

                        currentPosition++;

                        showQuestion();

                    } else {

                        saveReadingStudyTime();


                        Toast.makeText(
                                this,
                                "All questions completed",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );


        // ========================================================
        // BOOKMARK
        // ========================================================

        binding.bookmarkButton.setOnClickListener(
                v -> {

                    if (
                            questionList == null ||
                                    questionList.isEmpty()
                    ) {

                        return;
                    }


                    Question question =
                            questionList.get(
                                    currentPosition
                            );


                    int questionId =
                            question.getId();


                    boolean bookmarked =
                            bookmarkRepository
                                    .toggleBookmark(
                                            questionId
                                    );


                    updateBookmarkButton(
                            questionId
                    );


                    if (bookmarked) {

                        Toast.makeText(
                                this,
                                "প্রশ্নটি Bookmark করা হয়েছে",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                "Bookmark থেকে সরানো হয়েছে",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }


    // ============================================================
    // UPDATE BOOKMARK BUTTON
    // ============================================================

    private void updateBookmarkButton(
            int questionId
    ) {

        if (
                bookmarkRepository == null
        ) {

            return;
        }


        boolean bookmarked =
                bookmarkRepository
                        .isBookmarked(
                                questionId
                        );


        if (bookmarked) {

            binding.bookmarkButton
                    .setImageResource(
                            R.drawable.ic_bookmark_filled
                    );


            binding.bookmarkButton
                    .setContentDescription(
                            "Bookmark করা আছে"
                    );

        } else {

            binding.bookmarkButton
                    .setImageResource(
                            R.drawable.ic_bookmark_border
                    );


            binding.bookmarkButton
                    .setContentDescription(
                            "Bookmark করুন"
                    );
        }
    }


    // ============================================================
    // NAVIGATION BUTTON STATE
    // ============================================================

    private void updateNavigationButtons() {


        // ========================================================
        // PREVIOUS
        // ========================================================

        if (
                currentPosition == 0
        ) {

            binding.previousButton.setEnabled(
                    false
            );


            binding.previousButton.setAlpha(
                    0.5f
            );

        } else {

            binding.previousButton.setEnabled(
                    true
            );


            binding.previousButton.setAlpha(
                    1f
            );
        }


        // ========================================================
        // NEXT
        // ========================================================

        if (
                currentPosition ==
                        questionList.size() - 1
        ) {

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

        // ========================================================
        // SAVE DATA
        // ========================================================

        saveReadingStudyTime();

        saveRecentLearning();


        // ========================================================
        // DESTROY WEBVIEWS
        // ========================================================

        if (binding != null) {

            LatexRenderer.destroy(
                    binding.questionText
            );

            LatexRenderer.destroy(
                    binding.option1
            );

            LatexRenderer.destroy(
                    binding.option2
            );

            LatexRenderer.destroy(
                    binding.option3
            );

            LatexRenderer.destroy(
                    binding.option4
            );

            LatexRenderer.destroy(
                    binding.explanationText
            );
        }


        // ========================================================
        // QUESTION REPOSITORY
        // ========================================================

        if (
                questionRepository != null
        ) {

            questionRepository.close();

            questionRepository = null;
        }


        // ========================================================
        // BOOKMARK REPOSITORY
        // ========================================================

        if (
                bookmarkRepository != null
        ) {

            bookmarkRepository.close();

            bookmarkRepository = null;
        }


        // ========================================================
        // READING PROGRESS
        // ========================================================

        if (
                readingProgressRepository != null
        ) {

            readingProgressRepository.close();

            readingProgressRepository = null;
        }


        // ========================================================
        // SUPER
        // ========================================================

        super.onDestroy();


        binding = null;
    }
}