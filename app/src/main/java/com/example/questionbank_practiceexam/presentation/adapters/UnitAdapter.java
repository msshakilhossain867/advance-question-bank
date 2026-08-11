package com.example.questionbank_practiceexam.presentation.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.data.local.entity.Unit;
import com.example.questionbank_practiceexam.databinding.ItemUnitBinding;

import java.util.List;

public class UnitAdapter
        extends RecyclerView.Adapter<UnitAdapter.UnitViewHolder> {

    private final List<Unit> unitList;
    private final OnUnitClickListener listener;

    // ============================================================
    // CLICK LISTENER
    // ============================================================

    public interface OnUnitClickListener {
        void onUnitClick(Unit unit);
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public UnitAdapter(
            List<Unit> unitList,
            OnUnitClickListener listener
    ) {
        this.unitList = unitList;
        this.listener = listener;
    }

    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    @NonNull
    @Override
    public UnitViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        ItemUnitBinding binding =
                ItemUnitBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new UnitViewHolder(binding);
    }

    // ============================================================
    // BIND DATA
    // ============================================================

    @Override
    public void onBindViewHolder(
            @NonNull UnitViewHolder holder,
            int position
    ) {

        Unit unit = unitList.get(position);

        // ========================================================
        // UNIT NAME
        // ========================================================

        holder.binding.unitName.setText(
                unit.getUnitName()
        );

        // ========================================================
        // SUBTITLE
        // ========================================================

        holder.binding.unitDescription.setText(
                "Start Learning"
        );

        // ========================================================
        // DIFFERENT COLORS
        // ========================================================

        int[] colors = {
                Color.rgb(25, 118, 210),   // Blue
                Color.rgb(22, 163, 74),    // Green
                Color.rgb(249, 115, 22),   // Orange
                Color.rgb(239, 68, 68)     // Red
        };

        int color =
                colors[position % colors.length];

        holder.binding.unitCard.setCardBackgroundColor(
                color
        );

        // ========================================================
        // CLICK
        // ========================================================

        holder.binding.unitCard.setOnClickListener(v -> {

            if (listener != null) {
                listener.onUnitClick(unit);
            }

        });
    }

    // ============================================================
    // ITEM COUNT
    // ============================================================

    @Override
    public int getItemCount() {

        return unitList == null
                ? 0
                : unitList.size();
    }

    // ============================================================
    // VIEW HOLDER
    // ============================================================

    static class UnitViewHolder
            extends RecyclerView.ViewHolder {

        private final ItemUnitBinding binding;

        public UnitViewHolder(
                ItemUnitBinding binding
        ) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }
}