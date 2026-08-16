package com.example.questionbank_practiceexam.presentation.adapters;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.Question;
import com.example.questionbank_practiceexam.databinding.ItemBookmarkQuestionBinding;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class BookmarkAdapter
        extends RecyclerView.Adapter<
        BookmarkAdapter.ViewHolder> {


    // ============================================================
    // DATA
    // ============================================================

    private final List<Question> questionList;


    // ============================================================
    // CALLBACK
    // ============================================================

    public interface OnBookmarkRemoveListener {

        void onBookmarkRemove(
                Question question,
                int position
        );
    }


    private final OnBookmarkRemoveListener listener;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public BookmarkAdapter(
            List<Question> questionList,
            OnBookmarkRemoveListener listener
    ) {

        this.questionList =
                questionList;

        this.listener =
                listener;
    }


    // ============================================================
    // CREATE
    // ============================================================

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        ItemBookmarkQuestionBinding binding =
                ItemBookmarkQuestionBinding.inflate(
                        LayoutInflater.from(
                                parent.getContext()
                        ),
                        parent,
                        false
                );

        return new ViewHolder(binding);
    }


    // ============================================================
    // BIND
    // ============================================================

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Question question =
                questionList.get(position);


        // ========================================================
        // QUESTION NUMBER
        // ========================================================

        holder.binding.questionNumber.setText(
                "Question " +
                        (position + 1)
        );


        // ========================================================
        // YEAR / TOPIC
        // ========================================================

        String yearTag =
                question.getYearTag();


        if (yearTag != null &&
                !yearTag.trim().isEmpty()) {

            holder.binding.topicText.setText(
                    yearTag
            );

            holder.binding.topicText.setVisibility(
                    View.VISIBLE
            );

        } else {

            holder.binding.topicText.setVisibility(
                    View.GONE
            );
        }


        // ========================================================
        // QUESTION
        // ========================================================

        holder.binding.questionText.setText(
                question.getQuestionText()
        );


        // ========================================================
        // OPTIONS
        // ========================================================

        setOptions(
                holder,
                question
        );


        // ========================================================
        // CORRECT ANSWER
        // ========================================================

        setCorrectAnswer(
                holder,
                question
        );


        // ========================================================
        // EXPLANATION
        // ========================================================

        String explanation =
                question.getExplanation();


        if (explanation != null &&
                !explanation.trim().isEmpty()) {

            holder.binding.explanationText.setText(
                    explanation
            );

            holder.binding.explanationLayout.setVisibility(
                    View.VISIBLE
            );

        } else {

            holder.binding.explanationLayout.setVisibility(
                    View.GONE
            );
        }


        // ========================================================
        // REMOVE BOOKMARK
        // ========================================================

        holder.binding.removeBookmarkButton
                .setOnClickListener(
                        v -> {

                            if (listener != null) {

                                listener.onBookmarkRemove(
                                        question,
                                        holder.getBindingAdapterPosition()
                                );
                            }
                        }
                );
    }


    // ============================================================
    // SET OPTIONS
    // ============================================================

    private void setOptions(
            ViewHolder holder,
            Question question
    ) {

        try {

            JSONObject options =
                    new JSONObject(
                            question.getOptions()
                    );


            String a =
                    options.optString(
                            "a",
                            ""
                    );


            String b =
                    options.optString(
                            "b",
                            ""
                    );


            String c =
                    options.optString(
                            "c",
                            ""
                    );


            String d =
                    options.optString(
                            "d",
                            ""
                    );


            holder.binding.optionA.setText(
                    "A. " + a
            );


            holder.binding.optionB.setText(
                    "B. " + b
            );


            holder.binding.optionC.setText(
                    "C. " + c
            );


            holder.binding.optionD.setText(
                    "D. " + d
            );

        } catch (Exception e) {

            holder.binding.optionA.setText("A.");
            holder.binding.optionB.setText("B.");
            holder.binding.optionC.setText("C.");
            holder.binding.optionD.setText("D.");
        }


        resetOptionColors(holder);


        highlightCorrectAnswer(
                holder,
                question.getCorrectOption()
        );
    }


    // ============================================================
    // RESET OPTION COLORS
    // ============================================================

    private void resetOptionColors(
            ViewHolder holder
    ) {

        holder.binding.optionA.setBackgroundResource(
                R.drawable.bg_bookmark_option
        );

        holder.binding.optionB.setBackgroundResource(
                R.drawable.bg_bookmark_option
        );

        holder.binding.optionC.setBackgroundResource(
                R.drawable.bg_bookmark_option
        );

        holder.binding.optionD.setBackgroundResource(
                R.drawable.bg_bookmark_option
        );


        holder.binding.optionA.setTextColor(
                Color.rgb(51, 51, 51)
        );

        holder.binding.optionB.setTextColor(
                Color.rgb(51, 51, 51)
        );

        holder.binding.optionC.setTextColor(
                Color.rgb(51, 51, 51)
        );

        holder.binding.optionD.setTextColor(
                Color.rgb(51, 51, 51)
        );
    }


    // ============================================================
    // HIGHLIGHT CORRECT OPTION
    // ============================================================

    private void highlightCorrectAnswer(
            ViewHolder holder,
            String correctOption
    ) {

        if (correctOption == null) {
            return;
        }


        switch (
                correctOption
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        )
        ) {

            case "a":

                setCorrectStyle(
                        holder.binding.optionA
                );

                break;


            case "b":

                setCorrectStyle(
                        holder.binding.optionB
                );

                break;


            case "c":

                setCorrectStyle(
                        holder.binding.optionC
                );

                break;


            case "d":

                setCorrectStyle(
                        holder.binding.optionD
                );

                break;
        }
    }


    // ============================================================
    // CORRECT STYLE
    // ============================================================

    private void setCorrectStyle(TextView view) {

        view.setBackgroundResource(
                R.drawable.bg_bookmark_correct
        );

        view.setTextColor(
                android.graphics.Color.rgb(
                        35,
                        131,
                        71
                )
        );
    }


    // ============================================================
    // CORRECT ANSWER TEXT
    // ============================================================

    private void setCorrectAnswer(
            ViewHolder holder,
            Question question
    ) {

        String correct =
                question.getCorrectOption();


        if (correct == null ||
                correct.trim().isEmpty()) {

            holder.binding.correctAnswerLayout
                    .setVisibility(
                            View.GONE
                    );

            return;
        }


        String answerText =
                getCorrectAnswerText(
                        question
                );


        holder.binding.correctAnswerText.setText(
                answerText
        );


        holder.binding.correctAnswerLayout
                .setVisibility(
                        View.VISIBLE
                );
    }


    // ============================================================
    // GET CORRECT ANSWER WITH OPTION TEXT
    // ============================================================

    private String getCorrectAnswerText(
            Question question
    ) {

        String correct =
                question.getCorrectOption()
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );


        try {

            JSONObject options =
                    new JSONObject(
                            question.getOptions()
                    );


            String optionText =
                    options.optString(
                            correct,
                            ""
                    );


            String letter =
                    correct.toUpperCase(
                            Locale.ROOT
                    );


            if (optionText.isEmpty()) {

                return letter;
            }


            return letter +
                    ". " +
                    optionText;

        } catch (Exception e) {

            return correct.toUpperCase(
                    Locale.ROOT
            );
        }
    }


    // ============================================================
    // COUNT
    // ============================================================

    @Override
    public int getItemCount() {

        return questionList.size();
    }


    // ============================================================
    // REMOVE ITEM
    // ============================================================

    public void removeItem(
            int position
    ) {

        if (position < 0 ||
                position >= questionList.size()) {

            return;
        }


        questionList.remove(
                position
        );


        notifyItemRemoved(
                position
        );


        notifyItemRangeChanged(
                position,
                questionList.size() - position
        );
    }


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        final ItemBookmarkQuestionBinding binding;


        ViewHolder(
                ItemBookmarkQuestionBinding binding
        ) {

            super(
                    binding.getRoot()
            );

            this.binding =
                    binding;
        }
    }
}


