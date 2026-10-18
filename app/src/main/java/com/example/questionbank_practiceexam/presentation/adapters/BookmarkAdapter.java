package com.example.questionbank_practiceexam.presentation.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.Question;
import com.example.questionbank_practiceexam.databinding.ItemBookmarkQuestionBinding;
import com.example.questionbank_practiceexam.helpers.LatexRenderer;
// আপনার প্রোজেক্ট অনুযায়ী সঠিক প্যাথ থেকে LatexRenderer ইম্পোর্ট করুন
// import com.example.questionbank_practiceexam.utils.LatexRenderer;

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
        // QUESTION (TextView vs WebView Check with Image Support)
        // ========================================================

        String rawQText = question.getQuestionText();
        String cleanQText = LatexRenderer.getCleanText(rawQText);
        String qImg = question.getQuestionImage(); // Question এনটিতে এই মেথড থাকতে হবে
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
        // EXPLANATION (TextView vs WebView Check)
        // ========================================================

        String rawExp = question.getExplanation();

        if (rawExp != null &&
                !rawExp.trim().isEmpty()) {

            holder.binding.explanationLayout.setVisibility(
                    View.VISIBLE
            );

            String cleanExp = LatexRenderer.getCleanText(rawExp);
            String expImg = question.getExplanationImage(); // অপশনাল
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

            String optImgJson = question.getOptionImages(); // অপশন ইমেজ JSON
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


            String optA = LatexRenderer.getCleanText(options.optString("a", ""));
            String optB = LatexRenderer.getCleanText(options.optString("b", ""));
            String optC = LatexRenderer.getCleanText(options.optString("c", ""));
            String optD = LatexRenderer.getCleanText(options.optString("d", ""));


            // Option A
            bindSingleOption(
                    holder.binding.optionAWebView, holder.binding.optionAText,
                    optA, options.optString("a", ""),
                    optionImages != null ? optionImages.optString("a", null) : singleOptImage,
                    "A. "
            );

            // Option B
            bindSingleOption(
                    holder.binding.optionBWebView, holder.binding.optionBText,
                    optB, options.optString("b", ""),
                    optionImages != null ? optionImages.optString("b", null) : null,
                    "B. "
            );

            // Option C
            bindSingleOption(
                    holder.binding.optionCWebView, holder.binding.optionCText,
                    optC, options.optString("c", ""),
                    optionImages != null ? optionImages.optString("c", null) : null,
                    "C. "
            );

            // Option D
            bindSingleOption(
                    holder.binding.optionDWebView, holder.binding.optionDText,
                    optD, options.optString("d", ""),
                    optionImages != null ? optionImages.optString("d", null) : null,
                    "D. "
            );

        } catch (Exception e) {
            holder.binding.optionAText.setText("A.");
            holder.binding.optionBText.setText("B.");
            holder.binding.optionCText.setText("C.");
            holder.binding.optionDText.setText("D.");
        }


        resetOptionColors(holder);


        highlightCorrectAnswer(
                holder,
                question.getCorrectOption()
        );
    }


    // ============================================================
    // BIND SINGLE OPTION
    // ============================================================

    private void bindSingleOption(WebView webView, TextView textView, String cleanText, String rawText, String imagePath, String prefix) {
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
        }
    }


    // ============================================================
    // RESET OPTION COLORS
    // ============================================================

    private void resetOptionColors(
            ViewHolder holder
    ) {

        holder.binding.optionAText.setBackgroundResource(R.drawable.bg_bookmark_option);
        holder.binding.optionAWebView.setBackgroundResource(R.drawable.bg_bookmark_option);

        holder.binding.optionBText.setBackgroundResource(R.drawable.bg_bookmark_option);
        holder.binding.optionBWebView.setBackgroundResource(R.drawable.bg_bookmark_option);

        holder.binding.optionCText.setBackgroundResource(R.drawable.bg_bookmark_option);
        holder.binding.optionCWebView.setBackgroundResource(R.drawable.bg_bookmark_option);

        holder.binding.optionDText.setBackgroundResource(R.drawable.bg_bookmark_option);
        holder.binding.optionDWebView.setBackgroundResource(R.drawable.bg_bookmark_option);


        int optionTextColor = androidx.core.content.ContextCompat.getColor(
                holder.itemView.getContext(),
                R.color.option_text_color
        );

        holder.binding.optionAText.setTextColor(optionTextColor);
        holder.binding.optionBText.setTextColor(optionTextColor);
        holder.binding.optionCText.setTextColor(optionTextColor);
        holder.binding.optionDText.setTextColor(optionTextColor);
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
                setCorrectStyle(holder.binding.optionAText, holder.binding.optionAWebView);
                break;

            case "b":
                setCorrectStyle(holder.binding.optionBText, holder.binding.optionBWebView);
                break;

            case "c":
                setCorrectStyle(holder.binding.optionCText, holder.binding.optionCWebView);
                break;

            case "d":
                setCorrectStyle(holder.binding.optionDText, holder.binding.optionDWebView);
                break;
        }
    }


    // ============================================================
    // CORRECT STYLE
    // ============================================================

    private void setCorrectStyle(TextView textView, WebView webView) {
        if (textView.getVisibility() == View.VISIBLE) {
            textView.setBackgroundResource(R.drawable.bg_bookmark_correct);
            textView.setTextColor(Color.rgb(35, 131, 71));
        }
        if (webView.getVisibility() == View.VISIBLE) {
            webView.setBackgroundResource(R.drawable.bg_bookmark_correct);
        }
    }


    // ============================================================
    // CORRECT ANSWER TEXT
    // ============================================================

    // ============================================================
    // CORRECT ANSWER (TextView vs WebView Check with Image & LaTeX Support)
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


        try {

            JSONObject options =
                    new JSONObject(
                            question.getOptions()
                    );


            String optionText =
                    options.optString(
                            correct.trim().toLowerCase(Locale.ROOT),
                            ""
                    );


            String letter =
                    correct.trim().toUpperCase(
                            Locale.ROOT
                    );


            String rawAnswerText = letter + ". " + optionText;
            String cleanAnswerText = LatexRenderer.getCleanText(rawAnswerText);

            // অপশনের ইমেজ থাকলে তা সঠিক উত্তরের সাথে দেখানোর জন্য (যদি প্রয়োজন হয়)
            String optImgJson = question.getOptionImages();
            String answerImage = null;
            if (optImgJson != null && !optImgJson.trim().isEmpty()) {
                optImgJson = optImgJson.trim();
                if (optImgJson.startsWith("{")) {
                    try {
                        JSONObject optionImages = new JSONObject(optImgJson);
                        answerImage = optionImages.optString(correct.trim().toLowerCase(Locale.ROOT), null);
                    } catch (Exception ignored) {}
                } else {
                    answerImage = optImgJson;
                }
            }

            boolean hasImg = (answerImage != null && !answerImage.trim().isEmpty());
            boolean hasLatex = LatexRenderer.containsLaTeXOrHtml(rawAnswerText);

            if (!hasImg && !hasLatex) {
                holder.binding.correctAnswerWebView.setVisibility(View.GONE);
                holder.binding.correctAnswerText.setVisibility(View.VISIBLE);
                holder.binding.correctAnswerText.setText(cleanAnswerText);
            } else {
                holder.binding.correctAnswerText.setVisibility(View.GONE);
                holder.binding.correctAnswerWebView.setVisibility(View.VISIBLE);
                LatexRenderer.renderOption(holder.binding.correctAnswerWebView, rawAnswerText, answerImage);
            }

            holder.binding.correctAnswerLayout
                    .setVisibility(
                            View.VISIBLE
                    );

        } catch (Exception e) {
            holder.binding.correctAnswerWebView.setVisibility(View.GONE);
            holder.binding.correctAnswerText.setVisibility(View.VISIBLE);
            holder.binding.correctAnswerText.setText(correct.toUpperCase(Locale.ROOT));

            holder.binding.correctAnswerLayout
                    .setVisibility(
                            View.VISIBLE
                    );
        }
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