package com.example.questionbank_practiceexam.presentation.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.Subject;
import com.example.questionbank_practiceexam.databinding.ItemSubjectBinding;

import java.util.List;

public class SubjectAdapter
        extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private final List<Subject> subjectList;
    private final OnSubjectClickListener listener;

    // ============================================================
    // CLICK LISTENER
    // ============================================================

    public interface OnSubjectClickListener {
        void onSubjectClick(Subject subject);
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public SubjectAdapter(
            List<Subject> subjectList,
            OnSubjectClickListener listener
    ) {
        this.subjectList = subjectList;
        this.listener = listener;
    }

    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        ItemSubjectBinding binding =
                ItemSubjectBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new SubjectViewHolder(binding);
    }

    // ============================================================
    // BIND DATA
    // ============================================================

    @Override
    public void onBindViewHolder(
            @NonNull SubjectViewHolder holder,
            int position
    ) {

        Subject subject = subjectList.get(position);
        Context context = holder.itemView.getContext();

        // ========================================================
        // SUBJECT NAME
        // ========================================================

        holder.binding.subjectName.setText(
                subject.getName()
        );

        // ========================================================
        // SUBTITLE
        // ========================================================

        holder.binding.subjectSubtitle.setText(
                "Start Learning"
        );

        // ========================================================
        // SUBJECT ICON (DYNAMIC)
        // ========================================================

        setSubjectIcon(context, holder, subject.getName());

        // ========================================================
        // DIFFERENT COLORS
        // ========================================================

        int[] colors = {
                Color.rgb(106, 53, 184),   // Purple
                Color.rgb(20, 148, 174),   // Cyan
                Color.rgb(36, 178, 88),    // Green
                Color.rgb(20, 148, 174),   // Blue
                Color.rgb(100, 88, 80),    // Brown
                Color.rgb(60, 91, 126),    // Dark Blue
                Color.rgb(60, 91, 126),    // Dark Blue
                Color.rgb(24, 91, 151)     // Navy
        };

        int color =
                colors[position % colors.length];

        holder.binding.subjectCard.setCardBackgroundColor(
                color
        );

        // ========================================================
        // CLICK
        // ========================================================

        holder.binding.subjectCard.setOnClickListener(v -> {

            if (listener != null) {
                listener.onSubjectClick(subject);
            }

        });
    }

    // ============================================================
    // SET SUBJECT ICON METHOD
    // ============================================================

    private void setSubjectIcon(
            Context context,
            SubjectViewHolder holder,
            String subjectName
    ) {
        if (subjectName == null) return;

        String drawableName =
                subjectName
                        .trim()
                        .toLowerCase()
                        .replace(" ", "_")
                        .replace("-", "_")
                        + "_sub_icon";

        int resourceId =
                context.getResources().getIdentifier(
                        drawableName,
                        "drawable",
                        context.getPackageName()
                );

        if (resourceId != 0) {
            holder.binding.subjectIcon.setImageResource(
                    resourceId
            );
        } else {
            // Default icon if specific icon not found
            holder.binding.subjectIcon.setImageResource(
                    R.drawable.biology_sub_icon
            );
        }
    }

    // ============================================================
    // ITEM COUNT
    // ============================================================

    @Override
    public int getItemCount() {

        return subjectList == null
                ? 0
                : subjectList.size();
    }

    // ============================================================
    // VIEW HOLDER
    // ============================================================

    static class SubjectViewHolder
            extends RecyclerView.ViewHolder {

        private final ItemSubjectBinding binding;

        public SubjectViewHolder(
                ItemSubjectBinding binding
        ) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }
}