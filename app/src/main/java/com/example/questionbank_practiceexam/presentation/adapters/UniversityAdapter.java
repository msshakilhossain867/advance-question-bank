package com.example.questionbank_practiceexam.presentation.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.University;
import com.example.questionbank_practiceexam.databinding.ItemUniversityBinding;


import java.util.List;

public class UniversityAdapter
        extends RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder> {

    private final List<University> universityList;
    private final OnUniversityClickListener listener;

    // ============================================================
    // CLICK LISTENER
    // ============================================================

    public interface OnUniversityClickListener {
        void onUniversityClick(University university);
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public UniversityAdapter(
            List<University> universityList,
            OnUniversityClickListener listener
    ) {
        this.universityList = universityList;
        this.listener = listener;
    }

    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    @NonNull
    @Override
    public UniversityViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        ItemUniversityBinding binding =
                ItemUniversityBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new UniversityViewHolder(binding);
    }

    // ============================================================
    // BIND DATA
    // ============================================================

    @Override
    public void onBindViewHolder(
            @NonNull UniversityViewHolder holder,
            int position
    ) {

        University university =
                universityList.get(position);

        // ========================================================
        // UNIVERSITY NAME
        // ========================================================

        String name = university.getName();

        if (name != null && !name.trim().isEmpty()) {

            holder.binding.universityName.setText(name);

        } else {

            holder.binding.universityName.setText(
                    "University"
            );
        }

        // ========================================================
        // FULL NAME / INFO
        // ========================================================

        String fullName =
                university.getFullName();

        if (fullName != null &&
                !fullName.trim().isEmpty()) {

            holder.binding.universityUnits.setText(
                    fullName
            );

        } else {

            holder.binding.universityUnits.setText(
                    "University"
            );
        }

        // ========================================================
        // LOGO
        // ========================================================

        /*
         * আপাতত database-এর logo_url ব্যবহার করছি না।
         *
         * কারণ তোমার database-এ logo_url TEXT হলেও
         * এখন local drawable ব্যবহার করছো।
         *
         * পরে চাইলে logo_url অনুযায়ী image load করা যাবে।
         */

        setUniversityLogo(
                holder,
                university.getId()
        );

        // ========================================================
        // CARD CLICK
        // ========================================================

        holder.binding.universityCard.setOnClickListener(v -> {

            if (listener != null) {

                listener.onUniversityClick(
                        university
                );
            }
        });

        // পুরো card-এর click
        holder.binding.getRoot().setOnClickListener(v -> {

            if (listener != null) {

                listener.onUniversityClick(
                        university
                );
            }
        });
    }

    // ============================================================
    // UNIVERSITY LOGO
    // ============================================================

    private void setUniversityLogo(
            UniversityViewHolder holder,
            int universityId
    ) {

        switch (universityId) {

            case 1:

                holder.binding.universityLogo.setImageResource(
                        R.drawable.du_icon
                );

                break;

            default:

                holder.binding.universityLogo.setImageResource(
                        R.drawable.du_icon
                );

                break;
        }
    }

    // ============================================================
    // ITEM COUNT
    // ============================================================

    @Override
    public int getItemCount() {

        return universityList == null
                ? 0
                : universityList.size();
    }

    // ============================================================
    // VIEW HOLDER
    // ============================================================

    static class UniversityViewHolder
            extends RecyclerView.ViewHolder {

        private final ItemUniversityBinding binding;

        public UniversityViewHolder(
                ItemUniversityBinding binding
        ) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
