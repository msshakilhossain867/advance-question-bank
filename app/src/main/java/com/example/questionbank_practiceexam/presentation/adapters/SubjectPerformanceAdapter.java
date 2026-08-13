package com.example.questionbank_practiceexam.presentation.adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.questionbank_practiceexam.presentation.exam.PracticeResultActivity;
import com.example.questionbank_practiceexam.databinding.ItemSubjectPerformanceBinding;

import java.util.List;

public class SubjectPerformanceAdapter
        extends RecyclerView.Adapter<
        SubjectPerformanceAdapter.ViewHolder> {

    private final List<
            PracticeResultActivity.SubjectPerformance>
            list;


    public SubjectPerformanceAdapter(
            List<PracticeResultActivity.SubjectPerformance> list
    ) {

        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        ItemSubjectPerformanceBinding binding =
                ItemSubjectPerformanceBinding.inflate(
                        LayoutInflater.from(
                                parent.getContext()
                        ),
                        parent,
                        false
                );

        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        PracticeResultActivity.SubjectPerformance item =
                list.get(position);


        int correct =
                item.getCorrect();


        int total =
                item.getTotal();


        int percentage =
                item.getPercentage();


        // ========================================================
        // SUBJECT NAME
        // ========================================================

        holder.binding.subjectName.setText(
                item.getSubjectName()
        );


        // ========================================================
        // SCORE
        // ========================================================

        holder.binding.subjectScore.setText(
                correct
                        + " / "
                        + total
        );


        // ========================================================
        // PROGRESS
        // ========================================================

        holder.binding.subjectProgress.setMax(
                100
        );


        holder.binding.subjectProgress.setProgress(
                percentage
        );


        // ========================================================
        // PERCENTAGE
        // ========================================================

        holder.binding.subjectPercent.setText(
                percentage + "%"
        );
    }


    @Override
    public int getItemCount() {

        return list.size();
    }


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        final ItemSubjectPerformanceBinding binding;


        ViewHolder(
                ItemSubjectPerformanceBinding binding
        ) {

            super(
                    binding.getRoot()
            );

            this.binding =
                    binding;
        }
    }
}