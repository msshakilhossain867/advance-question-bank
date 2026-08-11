package com.example.questionbank_practiceexam.presentation.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.data.local.entity.Year;
import com.example.questionbank_practiceexam.databinding.ItemAcademicYearBinding;

import java.util.List;

public class AcademicYearAdapter
        extends RecyclerView.Adapter<AcademicYearAdapter.YearViewHolder> {

    private final List<Year> yearList;
    private final OnYearClickListener listener;

    // ============================================================
    // CLICK LISTENER
    // ============================================================

    public interface OnYearClickListener {
        void onYearClick(Year year);
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public AcademicYearAdapter(
            List<Year> yearList,
            OnYearClickListener listener
    ) {
        this.yearList = yearList;
        this.listener = listener;
    }

    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    @NonNull
    @Override
    public YearViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        ItemAcademicYearBinding binding =
                ItemAcademicYearBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new YearViewHolder(binding);
    }

    // ============================================================
    // BIND DATA
    // ============================================================

    @Override
    public void onBindViewHolder(
            @NonNull YearViewHolder holder,
            int position
    ) {

        Year year = yearList.get(position);

        // --------------------------------------------------------
        // YEAR TITLE
        // --------------------------------------------------------

        holder.binding.yearText.setText(
                year.getYearTitle()
        );

        // --------------------------------------------------------
        // SUBTITLE
        // --------------------------------------------------------

        holder.binding.yearSubtitle.setText(
                "Start Learning"
        );

        // --------------------------------------------------------
        // DIFFERENT ICON COLORS
        // --------------------------------------------------------

        int[] colors = {
                Color.rgb(25, 118, 210),   // Blue
                Color.rgb(22, 163, 74),    // Green
                Color.rgb(249, 115, 22),   // Orange
                Color.rgb(239, 68, 68)     // Red
        };

        int color =
                colors[position % colors.length];

        holder.binding.iconCard.setCardBackgroundColor(color);

        // --------------------------------------------------------
        // CLICK
        // --------------------------------------------------------

        holder.binding.yearCard.setOnClickListener(v -> {

            if (listener != null) {
                listener.onYearClick(year);
            }

        });
    }

    // ============================================================
    // ITEM COUNT
    // ============================================================

    @Override
    public int getItemCount() {

        return yearList == null
                ? 0
                : yearList.size();
    }

    // ============================================================
    // VIEW HOLDER
    // ============================================================

    static class YearViewHolder
            extends RecyclerView.ViewHolder {

        private final ItemAcademicYearBinding binding;

        public YearViewHolder(
                ItemAcademicYearBinding binding
        ) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }
}