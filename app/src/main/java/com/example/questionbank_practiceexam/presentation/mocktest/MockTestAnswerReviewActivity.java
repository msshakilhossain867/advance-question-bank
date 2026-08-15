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

    private static class ReviewQuestion {

        int originalPosition = 0;

        int questionId = -1;

        String question = "";

        String options = "";

        String selectedAnswer = "";

        String correctAnswer = "";

        boolean skipped = false;

        String explanation = "";
    }


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
            // QUESTION
            // ====================================================

            holder.binding.questionText.setText(
                    item.question
            );


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
                        "উত্তর দেওয়া হয়নি"
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
            // WRONG
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
            // EXPLANATION
            // ====================================================

            if (item.explanation != null &&
                    !item.explanation
                            .trim()
                            .isEmpty()) {

                holder.binding.explanationLayout
                        .setVisibility(
                                View.VISIBLE
                        );


                holder.binding.explanationText.setText(
                        item.explanation
                );

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


                // =================================================
                // OPTION TEXT
                // =================================================

                itemBinding.optionAText.setText(
                        "A. " +
                                options.optString(
                                        "a",
                                        ""
                                )
                );


                itemBinding.optionBText.setText(
                        "B. " +
                                options.optString(
                                        "b",
                                        ""
                                )
                );


                itemBinding.optionCText.setText(
                        "C. " +
                                options.optString(
                                        "c",
                                        ""
                                )
                );


                itemBinding.optionDText.setText(
                        "D. " +
                                options.optString(
                                        "d",
                                        ""
                                )
                );


                // =================================================
                // RESET
                // =================================================

                resetOptionStyle(
                        itemBinding.optionAText
                );

                resetOptionStyle(
                        itemBinding.optionBText
                );

                resetOptionStyle(
                        itemBinding.optionCText
                );

                resetOptionStyle(
                        itemBinding.optionDText
                );


                // =================================================
                // CORRECT OPTION = GREEN
                // =================================================

                if (!correct.isEmpty()) {

                    switch (correct) {

                        case "a":

                            setCorrectOption(
                                    itemBinding.optionAText
                            );

                            break;


                        case "b":

                            setCorrectOption(
                                    itemBinding.optionBText
                            );

                            break;


                        case "c":

                            setCorrectOption(
                                    itemBinding.optionCText
                            );

                            break;


                        case "d":

                            setCorrectOption(
                                    itemBinding.optionDText
                            );

                            break;
                    }
                }


                // =================================================
                // WRONG USER OPTION = RED
                // =================================================

                if (!selected.isEmpty() &&
                        !selected.equals(correct)) {

                    switch (selected) {

                        case "a":

                            setWrongOption(
                                    itemBinding.optionAText
                            );

                            break;


                        case "b":

                            setWrongOption(
                                    itemBinding.optionBText
                            );

                            break;


                        case "c":

                            setWrongOption(
                                    itemBinding.optionCText
                            );

                            break;


                        case "d":

                            setWrongOption(
                                    itemBinding.optionDText
                            );

                            break;
                    }
                }

            } catch (Exception e) {

                itemBinding.optionAText.setText(
                        "A."
                );


                itemBinding.optionBText.setText(
                        "B."
                );


                itemBinding.optionCText.setText(
                        "C."
                );


                itemBinding.optionDText.setText(
                        "D."
                );
            }
        }


        // ========================================================
        // CORRECT OPTION
        // ========================================================

        private void setCorrectOption(
                View view
        ) {

            view.setBackgroundResource(
                    R.drawable.bg_answer_review_correct
            );


            view.setAlpha(
                    1f
            );
        }


        // ========================================================
        // WRONG OPTION
        // ========================================================

        private void setWrongOption(
                View view
        ) {

            view.setBackgroundResource(
                    R.drawable.bg_answer_review_wrong
            );


            view.setAlpha(
                    1f
            );
        }


        // ========================================================
        // RESET OPTION
        // ========================================================

        private void resetOptionStyle(
                View view
        ) {

            view.setBackgroundResource(
                    R.drawable.bg_answer_review_option
            );


            view.setAlpha(
                    1f
            );
        }


        // ========================================================
        // ITEM COUNT
        // ========================================================

        @Override
        public int getItemCount() {

            return filteredQuestions.size();
        }


        // ========================================================
        // VIEW HOLDER
        // ========================================================

        class ViewHolder
                extends RecyclerView.ViewHolder {

            ItemAnswerReviewBinding binding;


            ViewHolder(
                    ItemAnswerReviewBinding binding
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
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        binding = null;

        super.onDestroy();
    }
}