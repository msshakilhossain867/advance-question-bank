package com.example.questionbank_practiceexam.presentation.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.Subject;
import com.example.questionbank_practiceexam.databinding.ItemPracticeSubjectBinding;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PracticeSubjectAdapter
        extends RecyclerView.Adapter<PracticeSubjectAdapter.SubjectViewHolder> {

    private final List<Subject> subjectList;
    private final Set<Integer> selectedSubjectIds = new HashSet<>();

    private final OnSubjectSelectionListener listener;

    public interface OnSubjectSelectionListener {
        void onSelectionChanged(Set<Integer> selectedIds);
    }

    public PracticeSubjectAdapter(
            List<Subject> subjectList,
            OnSubjectSelectionListener listener
    ) {
        this.subjectList = subjectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        ItemPracticeSubjectBinding binding =
                ItemPracticeSubjectBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new SubjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull SubjectViewHolder holder,
            int position
    ) {

        Subject subject = subjectList.get(position);

        holder.binding.subjectName.setText(
                subject.getName()
        );

        // ============================================================
        // SUBJECT ICON
        // ============================================================

        setSubjectIcon(
                holder.binding.subjectIcon.getContext(),
                holder,
                subject.getName()
        );

        // ============================================================
        // SELECTED STATE
        // ============================================================

        boolean selected =
                selectedSubjectIds.contains(subject.getId());

        updateSelectionUI(
                holder.binding,
                selected
        );

        // ============================================================
        // CLICK
        // ============================================================

        holder.binding.subjectCard.setOnClickListener(v -> {

            int subjectId = subject.getId();

            if (selectedSubjectIds.contains(subjectId)) {

                // Unselect
                selectedSubjectIds.remove(subjectId);

            } else {

                // Select
                selectedSubjectIds.add(subjectId);
            }

            updateSelectionUI(
                    holder.binding,
                    selectedSubjectIds.contains(subjectId)
            );

            if (listener != null) {
                listener.onSelectionChanged(
                        new HashSet<>(selectedSubjectIds)
                );
            }
        });
    }

    // ================================================================
    // SELECTED UI
    // ================================================================

    private void updateSelectionUI(
            ItemPracticeSubjectBinding binding,
            boolean selected
    ) {

        if (selected) {

            binding.subjectCard.setCardBackgroundColor(
                    Color.parseColor("#F4F2FF")
            );

            binding.subjectCard.setStrokeColor(
                    Color.parseColor("#6254E8")
            );

            binding.checkIcon.setVisibility(
                    View.VISIBLE
            );

        } else {

            binding.subjectCard.setCardBackgroundColor(
                    Color.WHITE
            );

            binding.subjectCard.setStrokeColor(
                    Color.parseColor("#D9DCE3")
            );

            binding.checkIcon.setVisibility(
                    View.GONE
            );
        }
    }

    // ================================================================
    // SUBJECT ICON
    // ================================================================

    private void setSubjectIcon(
            Context context,
            SubjectViewHolder holder,
            String subjectName
    ) {

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

            // Default icon
            holder.binding.subjectIcon.setImageResource(
                    R.drawable.biology_sub_icon
            );
        }
    }

    // ================================================================
    // SELECTED SUBJECTS
    // ================================================================

    public Set<Integer> getSelectedSubjectIds() {
        return new HashSet<>(selectedSubjectIds);
    }

    public void clearSelection() {

        selectedSubjectIds.clear();

        notifyDataSetChanged();

        if (listener != null) {
            listener.onSelectionChanged(
                    new HashSet<>(selectedSubjectIds)
            );
        }
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    // ================================================================
    // VIEW HOLDER
    // ================================================================

    static class SubjectViewHolder
            extends RecyclerView.ViewHolder {

        private final ItemPracticeSubjectBinding binding;

        public SubjectViewHolder(
                ItemPracticeSubjectBinding binding
        ) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }
}