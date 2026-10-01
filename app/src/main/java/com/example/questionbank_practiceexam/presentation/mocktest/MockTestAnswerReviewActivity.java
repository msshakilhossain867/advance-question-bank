package com.example.questionbank_practiceexam.presentation.mocktest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.databinding.ActivityMockTestAnswerReviewBinding;
import com.example.questionbank_practiceexam.databinding.ItemAnswerReviewBinding;
import com.example.questionbank_practiceexam.helpers.LatexRenderer;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MockTestAnswerReviewActivity
        extends AppCompatActivity {

    // ============================================================
    // BINDING
    // ============================================================

    private ActivityMockTestAnswerReviewBinding binding;


    // ============================================================
    // ALL QUESTIONS
    // ============================================================

    private final List<ReviewQuestion> allQuestions =
            new ArrayList<>();


    // ============================================================
    // FILTERED QUESTIONS
    // ============================================================

    private final List<ReviewQuestion> filteredQuestions =
            new ArrayList<>();


    // ============================================================
    // ADAPTER
    // ============================================================

    private AnswerReviewAdapter answerReviewAdapter;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);


        binding =
                ActivityMockTestAnswerReviewBinding.inflate(
                        getLayoutInflater()
                );


        setContentView(
                binding.getRoot()
        );


        // ========================================================
        // READ DATA
        // ========================================================

        readReviewData();


        // ========================================================
        // SETUP TABS
        // ========================================================

        setupTabs();


        // ========================================================
        // RECYCLER
        // ========================================================

        setupRecycler();


        // ========================================================
        // DEFAULT FILTER
        // ========================================================

        filterQuestions(0);


        // ========================================================
        // BACK
        // ========================================================

        binding.toolbar.setNavigationOnClickListener(
                v -> finish()
        );
    }


    // ============================================================
    // READ REVIEW DATA
    // ============================================================

    private void readReviewData() {

        String json =
                getIntent().getStringExtra(
                        "review_data"
                );


        if (json == null ||
                json.trim().isEmpty()) {

            return;
        }


        try {

            JSONArray array =
                    new JSONArray(json);


            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.getJSONObject(i);


                ReviewQuestion question =
                        new ReviewQuestion();


                // =================================================
                // ORIGINAL QUESTION POSITION
                // =================================================

                question.originalPosition =
                        i;


                // =================================================
                // QUESTION ID
                // =================================================

                question.questionId =
                        object.optInt(
                                "question_id",
                                -1
                        );


                // =================================================
                // QUESTION
                // =================================================

                question.question =
                        object.optString(
                                "question",
                                ""
                        );


                // =================================================
                // OPTIONS
                // =================================================

                question.options =
                        object.optString(
                                "options",
                                "{}"
                        );


                // =================================================
                // SELECTED ANSWER
                // =================================================

                question.selectedAnswer =
                        object.optString(
                                "selected_answer",
                                ""
                        );


                // =================================================
                // CORRECT ANSWER
                // =================================================

                question.correctAnswer =
                        object.optString(
                                "correct_answer",
                                ""
                        );


                // =================================================
                // SKIPPED
                // =================================================

                question.skipped =
                        object.optBoolean(
                                "skipped",
                                false
                        );


                // =================================================
                // EXPLANATION
                // =================================================

                question.explanation =
                        object.optString(
                                "explanation",
                                ""
                        );


                // readReviewData() এর ভেতরের লूप-এ এগুলো যুক্ত করে নিবেন:
                question.questionImage = object.optString("question_image", "");
                question.optionImages = object.optString("option_images", "");
                question.explanationImage = object.optString("explanation_image", "");


                allQuestions.add(
                        question
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // ============================================================
    // SETUP TABS
    // ============================================================

    private void setupTabs() {

        binding.filterTabLayout.removeAllTabs();


        /*
         * সব
         */
        TabLayout.Tab allTab =
                binding.filterTabLayout.newTab();

        allTab.setText(
                "সব (" +
                        allQuestions.size() +
                        ")"
        );

        binding.filterTabLayout.addTab(
                allTab
        );


        /*
         * সঠিক
         */
        TabLayout.Tab correctTab =
                binding.filterTabLayout.newTab();

        correctTab.setText(
                "সঠিক (" +
                        getCorrectCount() +
                        ")"
        );

        binding.filterTabLayout.addTab(
                correctTab
        );


        /*
         * ভুল
         */
        TabLayout.Tab wrongTab =
                binding.filterTabLayout.newTab();

        wrongTab.setText(
                "ভুল (" +
                        getWrongCount() +
                        ")"
        );

        binding.filterTabLayout.addTab(
                wrongTab
        );


        /*
         * উত্তর দেওয়া হয়নি
         */
        TabLayout.Tab unansweredTab =
                binding.filterTabLayout.newTab();

        unansweredTab.setText(
                "উত্তর দেওয়া হয়নি (" +
                        getUnansweredCount() +
                        ")"
        );

        binding.filterTabLayout.addTab(
                unansweredTab
        );


        // ========================================================
        // TAB CLICK
        // ========================================================

        binding.filterTabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {

                    @Override
                    public void onTabSelected(
                            TabLayout.Tab tab
                    ) {

                        int position =
                                tab.getPosition();


                        filterQuestions(
                                position
                        );
                    }


                    @Override
                    public void onTabUnselected(
                            TabLayout.Tab tab
                    ) {

                    }


                    @Override
                    public void onTabReselected(
                            TabLayout.Tab tab
                    ) {

                        filterQuestions(
                                tab.getPosition()
                        );
                    }
                }
        );
    }


    // ============================================================
    // SETUP RECYCLER
    // ============================================================

    private void setupRecycler() {

        binding.answerReviewRecycler
                .setLayoutManager(
                        new LinearLayoutManager(
                                this
                        )
                );


        answerReviewAdapter =
                new AnswerReviewAdapter();


        binding.answerReviewRecycler
                .setAdapter(
                        answerReviewAdapter
                );
    }


    // ============================================================
    // FILTER QUESTIONS
    // ============================================================

    private void filterQuestions(
            int filterType
    ) {

        filteredQuestions.clear();


        for (ReviewQuestion question :
                allQuestions) {


            boolean unanswered =
                    isUnanswered(question);


            boolean correct =
                    isCorrect(question);


            boolean wrong =
                    isWrong(question);


            switch (filterType) {

                // =================================================
                // ALL
                // =================================================

                case 0:

                    filteredQuestions.add(
                            question
                    );

                    break;


                // =================================================
                // CORRECT
                // =================================================

                case 1:

                    if (correct) {

                        filteredQuestions.add(
                                question
                        );
                    }

                    break;


                // =================================================
                // WRONG
                // =================================================

                case 2:

                    if (wrong) {

                        filteredQuestions.add(
                                question
                        );
                    }

                    break;


                // =================================================
                // UNANSWERED
                // =================================================

                case 3:

                    if (unanswered) {

                        filteredQuestions.add(
                                question
                        );
                    }

                    break;
            }
        }


        if (answerReviewAdapter != null) {

            answerReviewAdapter.notifyDataSetChanged();
        }


        /*
         * প্রতিবার নতুন filter করলে
         * list একদম উপরে যাবে।
         */
        binding.answerReviewRecycler
                .scrollToPosition(0);
    }


    // ============================================================
    // IS UNANSWERED
    // ============================================================

    private boolean isUnanswered(
            ReviewQuestion question
    ) {

        if (question == null) {
            return true;
        }


        if (question.skipped) {
            return true;
        }


        return question.selectedAnswer == null ||
                question.selectedAnswer
                        .trim()
                        .isEmpty();
    }


    // ============================================================
    // IS CORRECT
    // ============================================================

    private boolean isCorrect(
            ReviewQuestion question
    ) {

        if (question == null) {
            return false;
        }


        if (isUnanswered(question)) {
            return false;
        }


        String selected =
                question.selectedAnswer
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );


        String correct =
                question.correctAnswer
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );


        return !correct.isEmpty() &&
                selected.equals(correct);
    }


    // ============================================================
    // IS WRONG
    // ============================================================

    private boolean isWrong(
            ReviewQuestion question
    ) {

        if (question == null) {
            return false;
        }


        if (isUnanswered(question)) {
            return false;
        }


        return !isCorrect(question);
    }


    // ============================================================
    // CORRECT COUNT
    // ============================================================

    private int getCorrectCount() {

        int count = 0;


        for (ReviewQuestion question :
                allQuestions) {

            if (isCorrect(question)) {

                count++;
            }
        }


        return count;
    }


    // ============================================================
    // WRONG COUNT
    // ============================================================

    private int getWrongCount() {

        int count = 0;


        for (ReviewQuestion question :
                allQuestions) {

            if (isWrong(question)) {

                count++;
            }
        }


        return count;
    }


    // ============================================================
    // UNANSWERED COUNT
    // ============================================================

    private int getUnansweredCount() {

        int count = 0;


        for (ReviewQuestion question :
                allQuestions) {

            if (isUnanswered(question)) {

                count++;
            }
        }


        return count;
    }


    // ============================================================
    // REVIEW MODEL
    // ============================================================

    // ============================================================
// REVIEW MODEL
// ============================================================

    private static class ReviewQuestion {

        int originalPosition = 0;

        int questionId = -1;

        String question = "";

        String questionImage = ""; // প্রশ্নের ইমেজ থাকলে তার পাথ বা URL

        String options = "";

        String optionImages = ""; // অপশনের ইমেজগুলোর JSON বা পাথ

        String selectedAnswer = "";

        String correctAnswer = "";

        boolean skipped = false;

        String explanation = "";

        String explanationImage = ""; // ব্যাখ্যার ইমেজ থাকলে তার পাথ বা URL
    }


    // ============================================================
    // ADAPTER
    // ============================================================

    // ============================================================
// ADAPTER
// ============================================================

    private class AnswerReviewAdapter
            extends RecyclerView.Adapter<
            AnswerReviewAdapter.ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType
        ) {

            ItemAnswerReviewBinding itemBinding =
                    ItemAnswerReviewBinding.inflate(
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

            ReviewQuestion item =
                    filteredQuestions.get(
                            position
                    );


            // ====================================================
            // ORIGINAL QUESTION NUMBER
            // ====================================================

            holder.binding.questionNumber.setText(
                    String.format(
                            Locale.getDefault(),
                            "প্রশ্ন %d",
                            item.originalPosition + 1
                    )
            );


            // ====================================================
            // QUESTION (TextView vs WebView Check with Image Support)
            // ====================================================

            String rawQText = item.question;
            String cleanQText = LatexRenderer.getCleanText(rawQText);
            String qImg = item.questionImage; // ReviewQuestion মডেলে questionImage ফিল্ড থাকতে হবে
            boolean hasQImage = (qImg != null && !qImg.trim().isEmpty());

            if (!hasQImage && !LatexRenderer.containsLaTeXOrHtml(rawQText)) {
                holder.binding.questionWebView.setVisibility(View.GONE);
                holder.binding.questionText.setVisibility(View.VISIBLE);
                holder.binding.questionText.setText(cleanQText);
            } else {
                holder.binding.questionText.setVisibility(View.GONE);
                holder.binding.questionWebView.setVisibility(View.VISIBLE);
                LatexRenderer.renderQuestion(holder.binding.questionWebView, rawQText, qImg);
            }


            // ====================================================
            // OPTIONS
            // ====================================================

            showOptions(
                    holder.binding,
                    item
            );


            // ====================================================
            // UNANSWERED
            // ====================================================

            if (isUnanswered(item)) {

                holder.binding.resultStatus.setText(
                        "উত্তর দেওয়া হয়নি"
                );


                holder.binding.resultStatus.setTextColor(
                        Color.parseColor(
                                "#E58A00"
                        )
                );


                holder.binding.selectedAnswerText.setText(
                        "আপনার উত্তর: —"
                );
            }


            // ====================================================
            // CORRECT
            // ====================================================

            else if (isCorrect(item)) {

                holder.binding.resultStatus.setText(
                        "সঠিক উত্তর ✓"
                );


                holder.binding.resultStatus.setTextColor(
                        Color.parseColor(
                                "#1B9A52"
                        )
                );


                holder.binding.selectedAnswerText.setText(
                        "আপনার উত্তর: "
                                + item.selectedAnswer
                                .toUpperCase(
                                        Locale.ROOT
                                )
                );
            }


            // ====================================================
           // -- WRONG
            // ====================================================

        else {

                holder.binding.resultStatus.setText(
                        "ভুল উত্তর ✕"
                );


                holder.binding.resultStatus.setTextColor(
                        Color.parseColor(
                                "#E5484D"
                        )
                );


                holder.binding.selectedAnswerText.setText(
                        "আপনার উত্তর: "
                                + item.selectedAnswer
                                .toUpperCase(
                                        Locale.ROOT
                                )
                );
            }


            // ====================================================
            // CORRECT ANSWER
            // ====================================================

            if (item.correctAnswer != null &&
                    !item.correctAnswer
                            .trim()
                            .isEmpty()) {

                holder.binding.correctAnswerText.setText(
                        "সঠিক উত্তর: "
                                + item.correctAnswer
                                .toUpperCase(
                                        Locale.ROOT
                                )
                );

            } else {

                holder.binding.correctAnswerText.setText(
                        "সঠিক উত্তর: —"
                );
            }


            // ====================================================
            // EXPLANATION (TextView vs WebView Check)
            // ====================================================

            if (item.explanation != null &&
                    !item.explanation
                            .trim()
                            .isEmpty()) {

                holder.binding.explanationLayout
                        .setVisibility(
                                View.VISIBLE
                        );

                String rawExp = item.explanation;
                String cleanExp = LatexRenderer.getCleanText(rawExp);
                String expImg = item.explanationImage; // অপশনাল
                boolean hasExpImg = (expImg != null && !expImg.trim().isEmpty());

                if (!hasExpImg && !LatexRenderer.containsLaTeXOrHtml(rawExp)) {
                    holder.binding.explanationWebView.setVisibility(View.GONE);
                    holder.binding.explanationText.setVisibility(View.VISIBLE);
                    holder.binding.explanationText.setText(cleanExp);
                } else {
                    holder.binding.explanationText.setVisibility(View.GONE);
                    holder.binding.explanationWebView.setVisibility(View.VISIBLE);
                    LatexRenderer.renderExplanation(holder.binding.explanationWebView, rawExp, expImg);
                }

            } else {

                holder.binding.explanationLayout
                        .setVisibility(
                                View.GONE
                        );
            }
        }


        // ========================================================
        // SHOW OPTIONS
        // ========================================================

        private void showOptions(
                ItemAnswerReviewBinding itemBinding,
                ReviewQuestion question
        ) {

            try {

                JSONObject options =
                        new JSONObject(
                                question.options
                        );

                String optImgJson = question.optionImages; // ReviewQuestion এ থাকতে হবে
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


                String selected =
                        question.selectedAnswer == null
                                ? ""
                                : question.selectedAnswer
                                .trim()
                                .toLowerCase(
                                        Locale.ROOT
                                );


                String correct =
                        question.correctAnswer == null
                                ? ""
                                : question.correctAnswer
                                .trim()
                                .toLowerCase(
                                        Locale.ROOT
                                );


                String optA = LatexRenderer.getCleanText(options.optString("a", ""));
                String optB = LatexRenderer.getCleanText(options.optString("b", ""));
                String optC = LatexRenderer.getCleanText(options.optString("c", ""));
                String optD = LatexRenderer.getCleanText(options.optString("d", ""));


                // Option A
                bindSingleOption(
                        itemBinding.optionAWebView, itemBinding.optionAText,
                        optA, options.optString("a", ""),
                        optionImages != null ? optionImages.optString("a", null) : singleOptImage,
                        "A. "
                );

                // Option B
                bindSingleOption(
                        itemBinding.optionBWebView, itemBinding.optionBText,
                        optB, options.optString("b", ""),
                        optionImages != null ? optionImages.optString("b", null) : null,
                        "B. "
                );

                // Option C
                bindSingleOption(
                        itemBinding.optionCWebView, itemBinding.optionCText,
                        optC, options.optString("c", ""),
                        optionImages != null ? optionImages.optString("c", null) : null,
                        "C. "
                );

                // Option D
                bindSingleOption(
                        itemBinding.optionDWebView, itemBinding.optionDText,
                        optD, options.optString("d", ""),
                        optionImages != null ? optionImages.optString("d", null) : null,
                        "D. "
                );


                // =================================================
                // RESET STYLES
                // =================================================

                resetOptionStyle(itemBinding.optionAWebView, itemBinding.optionAText);
                resetOptionStyle(itemBinding.optionBWebView, itemBinding.optionBText);
                resetOptionStyle(itemBinding.optionCWebView, itemBinding.optionCText);
                resetOptionStyle(itemBinding.optionDWebView, itemBinding.optionDText);


                // =================================================
                // CORRECT OPTION = GREEN
                // =================================================

                if (!correct.isEmpty()) {
                    switch (correct) {
                        case "a":
                            setCorrectOption(itemBinding.optionAWebView, itemBinding.optionAText);
                            break;
                        case "b":
                            setCorrectOption(itemBinding.optionBWebView, itemBinding.optionBText);
                            break;
                        case "c":
                            setCorrectOption(itemBinding.optionCWebView, itemBinding.optionCText);
                            break;
                        case "d":
                            setCorrectOption(itemBinding.optionDWebView, itemBinding.optionDText);
                            break;
                    }
                }


                // =================================================
                // WRONG USER OPTION = RED
                // =================================================

                if (!selected.isEmpty() && !selected.equals(correct)) {
                    switch (selected) {
                        case "a":
                            setWrongOption(itemBinding.optionAWebView, itemBinding.optionAText);
                            break;
                        case "b":
                            setWrongOption(itemBinding.optionBWebView, itemBinding.optionBText);
                            break;
                        case "c":
                            setWrongOption(itemBinding.optionCWebView, itemBinding.optionCText);
                            break;
                        case "d":
                            setWrongOption(itemBinding.optionDWebView, itemBinding.optionDText);
                            break;
                    }
                }

            } catch (Exception e) {
                itemBinding.optionAText.setText("A.");
                itemBinding.optionBText.setText("B.");
                itemBinding.optionCText.setText("C.");
                itemBinding.optionDText.setText("D.");
            }
        }


        // ========================================================
        // BIND SINGLE OPTION
        // ========================================================

        private void bindSingleOption(android.webkit.WebView webView, android.widget.TextView textView, String cleanText, String rawText, String imagePath, String prefix) {
            boolean hasImg = (imagePath != null && !imagePath.trim().isEmpty());
            boolean hasLatex = LatexRenderer.containsLaTeXOrHtml(rawText);
            String formattedCleanText = prefix + cleanText;
            String formattedRawText = prefix + rawText;

            if (!hasImg && !hasLatex) {
                webView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText(formattedCleanText);
            } else {
                textView.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                LatexRenderer.renderOption(webView, formattedRawText, imagePath);

                webView.setOnClickListener(v -> {
                    View parentLayout = (View) webView.getParent().getParent();
                    if (parentLayout != null) {
                        parentLayout.performClick();
                    }
                });

                final long[] pressTime = {0};
                webView.setOnTouchListener((v, event) -> {
                    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                        pressTime[0] = System.currentTimeMillis();
                    } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - pressTime[0] < 200) {
                            View parentLayout = (View) webView.getParent().getParent();
                            if (parentLayout != null) {
                                parentLayout.performClick();
                            }
                        }
                    }
                    return false;
                });
            }
        }


        // ========================================================
        // CORRECT OPTION
        // ========================================================

        private void setCorrectOption(View webView, View textView) {
            if (webView.getVisibility() == View.VISIBLE) {
                webView.setBackgroundResource(R.drawable.bg_answer_review_correct);
            }
            if (textView.getVisibility() == View.VISIBLE) {
                textView.setBackgroundResource(R.drawable.bg_answer_review_correct);
            }
        }


        // ========================================================
        // WRONG OPTION
        // ========================================================

        private void setWrongOption(View webView, View textView) {
            if (webView.getVisibility() == View.VISIBLE) {
                webView.setBackgroundResource(R.drawable.bg_answer_review_wrong);
            }
            if (textView.getVisibility() == View.VISIBLE) {
                textView.setBackgroundResource(R.drawable.bg_answer_review_wrong);
            }
        }


        // ========================================================
        // RESET OPTION
        // ========================================================

        private void resetOptionStyle(View webView, View textView) {
            if (webView.getVisibility() == View.VISIBLE) {
                webView.setBackgroundResource(R.drawable.bg_answer_review_option);
            }
            if (textView.getVisibility() == View.VISIBLE) {
                textView.setBackgroundResource(R.drawable.bg_answer_review_option);
            }
        }


        @Override
        public int getItemCount() {
            return filteredQuestions.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            ItemAnswerReviewBinding binding;

            ViewHolder(ItemAnswerReviewBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }


    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        binding = null;

        super.onDestroy();
    }
}