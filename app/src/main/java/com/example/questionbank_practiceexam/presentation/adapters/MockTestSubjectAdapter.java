package com.example.questionbank_practiceexam.presentation.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.data.local.entity.Subject;
import com.example.questionbank_practiceexam.databinding.ItemPracticeSubjectBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockTestSubjectAdapter
        extends RecyclerView.Adapter<MockTestSubjectAdapter.ViewHolder> {

    private final List<Subject> subjectList;

    private final Set<Integer> selectedIds =
            new HashSet<>();

    private final OnSubjectClickListener listener;


    // ============================================================
    // LISTENER
    // ============================================================

    public interface OnSubjectClickListener {

        void onSelectionChanged(
                Set<Integer> selectedIds
        );
    }


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public MockTestSubjectAdapter(
            List<Subject> subjectList,
            OnSubjectClickListener listener
    ) {

        this.subjectList = subjectList;
        this.listener = listener;

        // ========================================================
        // MANDATORY SUBJECT AUTO SELECT
        // ========================================================

        for (Subject subject : subjectList) {

            if (subject.isMandatory()) {

                selectedIds.add(
                        subject.getId()
                );
            }
        }
    }


    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        ItemPracticeSubjectBinding binding =
                ItemPracticeSubjectBinding.inflate(
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

        Subject subject =
                subjectList.get(position);


        // ========================================================
        // NAME
        // ========================================================

        holder.binding.subjectName.setText(
                subject.getName()
        );


        // ========================================================
        // SELECTED
        // ========================================================

        boolean selected =
                selectedIds.contains(
                        subject.getId()
                );


        updateUI(
                holder.binding,
                selected
        );


        // ========================================================
        // CLICK
        // ========================================================

        holder.binding.subjectCard.setOnClickListener(
                v -> {

                    int subjectId =
                            subject.getId();


                    // =================================================
                    // MANDATORY
                    // =================================================

                    if (subject.isMandatory()) {

                        // Mandatory কখনো unselect হবে না
                        return;
                    }


                    // =================================================
                    // ALREADY SELECTED
                    // =================================================

                    if (selectedIds.contains(
                            subjectId
                    )) {

                        selectedIds.remove(
                                subjectId
                        );

                    }


                    // =================================================
                    // NOT SELECTED
                    // =================================================

                    else {

                        int choiceGroup =
                                subject.getChoiceGroup();


                        // =============================================
                        // OPTIONAL GROUP
                        // =============================================

                        if (choiceGroup > 0) {

                            /*
                             * একই choice_group-এর
                             * আগের selected subject remove হবে।
                             *
                             * Group 1 শুধু Group 1-কে affect করবে।
                             * Group 2 শুধু Group 2-কে affect করবে।
                             */

                            removeSelectedFromGroup(
                                    choiceGroup
                            );
                        }


                        selectedIds.add(
                                subjectId
                        );
                    }


                    // =================================================
                    // UPDATE UI
                    // =================================================

                    notifyDataSetChanged();


                    // =================================================
                    // CALLBACK
                    // =================================================

                    if (listener != null) {

                        listener.onSelectionChanged(
                                new HashSet<>(
                                        selectedIds
                                )
                        );
                    }
                }
        );
    }


    // ============================================================
    // REMOVE SELECTED SUBJECT FROM SAME GROUP
    // ============================================================

    private void removeSelectedFromGroup(
            int choiceGroup
    ) {

        if (choiceGroup <= 0) {
            return;
        }


        Integer removeId = null;


        for (Subject subject : subjectList) {

            if (subject.isMandatory()) {
                continue;
            }


            if (subject.getChoiceGroup()
                    == choiceGroup) {

                if (selectedIds.contains(
                        subject.getId()
                )) {

                    removeId =
                            subject.getId();

                    break;
                }
            }
        }


        if (removeId != null) {

            selectedIds.remove(
                    removeId
            );
        }
    }


    // ============================================================
    // UPDATE UI
    // ============================================================

    private void updateUI(
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


    // ============================================================
    // SET SELECTED IDS
    // ============================================================

    public void setSelectedIds(
            Set<Integer> ids
    ) {

        selectedIds.clear();


        // ========================================================
        // MANDATORY ALWAYS SELECTED
        // ========================================================

        for (Subject subject : subjectList) {

            if (subject.isMandatory()) {

                selectedIds.add(
                        subject.getId()
                );
            }
        }


        // ========================================================
        // ADD OPTIONAL SELECTION
        // ========================================================

        if (ids != null) {

            for (Integer id : ids) {

                if (id == null) {
                    continue;
                }


                Subject subject =
                        findSubjectById(id);


                if (subject == null) {
                    continue;
                }


                if (subject.isMandatory()) {
                    continue;
                }


                selectedIds.add(id);
            }
        }


        notifyDataSetChanged();
    }


    // ============================================================
    // FIND SUBJECT
    // ============================================================

    private Subject findSubjectById(
            int subjectId
    ) {

        for (Subject subject :
                subjectList) {

            if (subject.getId()
                    == subjectId) {

                return subject;
            }
        }

        return null;
    }


    // ============================================================
    // GET SELECTED IDS
    // ============================================================

    public Set<Integer> getSelectedIds() {

        return new HashSet<>(
                selectedIds
        );
    }


    // ============================================================
    // CLEAR OPTIONAL SELECTION
    // ============================================================

    public void clearOptionalSelection() {

        selectedIds.clear();


        // Mandatory আবার select
        for (Subject subject :
                subjectList) {

            if (subject.isMandatory()) {

                selectedIds.add(
                        subject.getId()
                );
            }
        }


        notifyDataSetChanged();


        if (listener != null) {

            listener.onSelectionChanged(
                    new HashSet<>(
                            selectedIds
                    )
            );
        }
    }


    // ============================================================
    // ITEM COUNT
    // ============================================================

    @Override
    public int getItemCount() {

        return subjectList.size();
    }


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        ItemPracticeSubjectBinding binding;


        ViewHolder(
                ItemPracticeSubjectBinding binding
        ) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }
}