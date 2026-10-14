package com.example.questionbank_practiceexam.presentation.questionview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    // ============================================================
    // SHOW CURRENT QUESTION
    // ============================================================

    private void showQuestion() {

        if (questionList == null || questionList.isEmpty()) {
            return;
        }

        // নতুন প্রশ্ন আসার সাথে সাথে স্ক্রিন একদম উপরে (0,0) পজিশনে নিয়ে যাওয়া
        if (binding.nestedScrollView != null) {
            binding.nestedScrollView.scrollTo(0, 0);
        }

        Question question = questionList.get(currentPosition);

        saveRecentLearning();

        if (readingProgressRepository != null) {
            readingProgressRepository.markQuestionAsRead(question.getId());
        }

        updateBookmarkButton(question.getId());

        binding.questionNumber.setText(
                (currentPosition + 1) + " / " + questionList.size()
        );

        if (subjectName != null && !subjectName.trim().isEmpty()) {
            binding.subjectText.setText(subjectName);
        } else {
            binding.subjectText.setText("Question");
        }

        if (question.getYearTag() != null && !question.getYearTag().trim().isEmpty()) {
            binding.topicText.setText(question.getYearTag());
        } else {
            binding.topicText.setText("Practice Question");
        }

        // ========================================================
        // QUESTION (TextView vs WebView Check with Clean Text)
        // ========================================================
        String rawQText = question.getQuestionText();
        String cleanQText = LatexRenderer.getCleanText(rawQText); // জেসন রিমুভ করার জন্য
        String qImg = question.getQuestionImage();
        boolean hasQImage = (qImg != null && !qImg.trim().isEmpty());

        if (!hasQImage && !LatexRenderer.containsLaTeXOrHtml(rawQText)) {
            // সাধারণ টেক্সট (সুপার ফাস্ট TextView, কোনো JSON দেখাবে না)
            binding.questionText.setVisibility(View.GONE);
            binding.questionTv.setVisibility(View.VISIBLE);
            binding.questionTv.setText(cleanQText);
        } else {
            // লেটেক্স বা ইমেজ থাকলে WebView (এখানে rawQText যাবে যাতে লেটেক্স রেন্ডার হয়)
            binding.questionTv.setVisibility(View.GONE);
            binding.questionText.setVisibility(View.VISIBLE);
            LatexRenderer.renderQuestion(binding.questionText, rawQText, qImg);
        }

        // ========================================================
        // OPTIONS
        // ========================================================
        setOptions(question);

        // ========================================================
        // CORRECT ANSWER & EXPLANATION
        // ========================================================
        String correctOptStr = question.getCorrectOption() != null ? question.getCorrectOption().trim().toUpperCase() : "";
        if (!correctOptStr.isEmpty()) {
            binding.correctAnswer.setText("সঠিক উত্তর: অপশন " + correctOptStr);
            binding.correctAnswer.setVisibility(View.VISIBLE);
        } else {
            binding.correctAnswer.setVisibility(View.GONE);
        }

        String rawExplanation = question.getExplanation();
        String cleanExplanation = LatexRenderer.getCleanText(rawExplanation);
        boolean hasExplanationText = cleanExplanation != null && !cleanExplanation.trim().isEmpty();
        boolean hasExplanationImage = question.getExplanationImage() != null && !question.getExplanationImage().trim().isEmpty();

        if (hasExplanationText || hasExplanationImage) {
            LatexRenderer.renderExplanation(
                    binding.explanationText,
                    rawExplanation,
                    question.getExplanationImage()
            );
            binding.explanationLayout.setVisibility(View.VISIBLE);
        } else {
            binding.explanationLayout.setVisibility(View.GONE);
        }

        updateNavigationButtons();
    }


    // ============================================================
    // SET OPTIONS (Text + Option Images support via JSON)
    // ============================================================

    // ============================================================
// SET OPTIONS (Text + Option Images support)
// ============================================================
    private void setOptions(Question question) {
        try {
            JSONObject options = new JSONObject(question.getOptions());
            String optImgJson = question.getOptionImages();
            JSONObject optionImages = null;
            String singleOptImage = null;

            if (optImgJson != null && !optImgJson.trim().isEmpty()) {
                optImgJson = optImgJson.trim();
                if (optImgJson.startsWith("{")) {
                    try {
                        optionImages = new JSONObject(optImgJson);
                    } catch (Exception ignored) {}
                } else {
                    singleOptImage = optImgJson;
                }
            }

            String correctOption = question.getCorrectOption() != null ? question.getCorrectOption().trim().toLowerCase() : "";

            // অপশনগুলোর র ম্যাথোড থেকে JSON ক্লিন করে টেক্সট বের করা
            String optA = LatexRenderer.getCleanText(options.optString("a", ""));
            String optB = LatexRenderer.getCleanText(options.optString("b", ""));
            String optC = LatexRenderer.getCleanText(options.optString("c", ""));
            String optD = LatexRenderer.getCleanText(options.optString("d", ""));

            // অপশন এ (A)
            bindSingleOption(
                    binding.option1, binding.option1Tv,
                    optA,
                    options.optString("a", ""), // র টেক্সট WebView এর জন্য
                    optionImages != null ? optionImages.optString("a", null) : singleOptImage,
                    "a".equals(correctOption), "A. "
            );

            // অপশন বি (B)
            bindSingleOption(
                    binding.option2, binding.option2Tv,
                    optB,
                    options.optString("b", ""),
                    optionImages != null ? optionImages.optString("b", null) : null,
                    "b".equals(correctOption), "B. "
            );

            // অপশন সি (C)
            bindSingleOption(
                    binding.option3, binding.option3Tv,
                    optC,
                    options.optString("c", ""),
                    optionImages != null ? optionImages.optString("c", null) : null,
                    "c".equals(correctOption), "C. "
            );

            // অপশন ডি (D)
            bindSingleOption(
                    binding.option4, binding.option4Tv,
                    optD,
                    options.optString("d", ""),
                    optionImages != null ? optionImages.optString("d", null) : null,
                    "d".equals(correctOption), "D. "
            );

        } catch (Exception e) {
            // ফால்ব্যাক
            binding.option1Tv.setVisibility(View.VISIBLE);
            binding.option1Tv.setText("A. ");
            binding.option1.setVisibility(View.GONE);
        }
    }

    private void bindSingleOption(WebView webView, android.widget.TextView textView, String cleanText, String rawText, String imagePath, boolean isCorrect, String prefix) {
        boolean hasImg = (imagePath != null && !imagePath.trim().isEmpty());
        boolean hasLatex = LatexRenderer.containsLaTeXOrHtml(rawText);
        String formattedCleanText = prefix + cleanText;
        String formattedRawText = prefix + rawText;

        // ব্যাকগ্রাউন্ড সেট করা
        int bgRes = isCorrect ? R.drawable.bg_option_correct : R.drawable.bg_option_normal;
        webView.setBackgroundResource(bgRes);
        textView.setBackgroundResource(bgRes);

        if (!hasImg && !hasLatex) {
            // সাধারণ টেক্সট (সুপার ফাস্ট TextView)
            webView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(formattedCleanText);
        } else {
            // লেটেক্স বা ইমেজ থাকলে WebView
            textView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            if (isCorrect) {
                LatexRenderer.renderCorrectOption(webView, formattedRawText, imagePath);
            } else {
                LatexRenderer.renderOption(webView, formattedRawText, imagePath);
            }
        }
    }
    // অপশন লেটার যুক্ত করার ছোট হেল্পার (যদি আপনার আগের মতো A., B. দেখাতে চান)
    private String getOptionPrefix(WebView webView) {
        if (webView == binding.option1) return "A.&nbsp;&nbsp;";
        if (webView == binding.option2) return "B.&nbsp;&nbsp;";
        if (webView == binding.option3) return "C.&nbsp;&nbsp;";
        if (webView == binding.option4) return "D.&nbsp;&nbsp;";
        return "";
    }


    // ============================================================
    // RESET OPTIONS
    // ============================================================




    // ============================================================
    // SHOW CORRECT ANSWER
    // ============================================================

    // ============================================================
// SHOW CORRECT ANSWER (Updated with Image Support)
// ============================================================

    // ============================================================
// SHOW CORRECT ANSWER (Updated with Image Support)
// ============================================================




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
                        backHomeRead();


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


    private void backHomeRead(){
        // ============================================================
// ALL QUESTIONS COMPLETED DIALOG
// ============================================================

        saveReadingStudyTime();

// কাস্টম স্মার্ট অ্যালার্ট ডায়ালগ তৈরি
        android.app.Dialog completionDialog = new android.app.Dialog(this);
        completionDialog.setContentView(R.layout.dialog_completion); // নিচে লেআউট দেওয়া আছে, অথবা কোডের মাধ্যমেও করতে পারেন

// যদি কাস্টম লেআউট ফাইল ব্যবহার না করে সরাসরি কোড দিয়ে ডায়ালগ বানাতে চান:
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_completion, null);
        builder.setView(dialogView);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

// ডায়ালগের ভেতরে ভিউগুলো ইনিশিয়ালাইজ করা (ধরে নিচ্ছি লেআউটে buttonHome এবং buttonReadAgain আছে)
        android.widget.Button btnHome = dialogView.findViewById(R.id.btnHome);
        android.widget.Button btnReadAgain = dialogView.findViewById(R.id.btnReadAgain);

// ১. Back Home অপশন
        btnHome.setOnClickListener(v -> {
            dialog.dismiss();

            // মেইন অ্যাক্টিভিটিতে ব্যাক করে হোম ফ্রেগমেন্টে পাঠানো বা অ্যাক্টিভিটি বন্ধ করা
            Intent intent = new Intent(this, com.example.questionbank_practiceexam.presentation.main.MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

// ২. Read Again অপশন (পুনরায় রিড অ্যাক্টিভিটিতে পাঠানো বা রিফ্রেশ করা)
        btnReadAgain.setOnClickListener(v -> {
            dialog.dismiss();

            // বর্তমান অ্যাক্টিভিটি রিস্টার্ট বা রিফ্রেশ করার জন্য
            finish();
            startActivity(getIntent());
        });

        dialog.setCancelable(false);
        dialog.show();
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