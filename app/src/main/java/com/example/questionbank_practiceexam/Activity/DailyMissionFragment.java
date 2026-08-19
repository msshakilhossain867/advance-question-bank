package com.example.questionbank_practiceexam.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.questionbank_practiceexam.data.local.entity.DailyMission;
import com.example.questionbank_practiceexam.data.local.repository.DailyMissionRepository;
import com.example.questionbank_practiceexam.databinding.FragmentDailyMissionBinding;
import com.example.questionbank_practiceexam.presentation.adapters.DailyMissionAdapter;

import java.util.ArrayList;
import java.util.List;

public class DailyMissionFragment
        extends Fragment {


    private FragmentDailyMissionBinding binding;


    private DailyMissionAdapter adapter;


    private DailyMissionRepository missionRepository;


    private final List<DailyMission> missionList =
            new ArrayList<>();


    // ============================================================
    // CREATE VIEW
    // ============================================================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        binding =
                FragmentDailyMissionBinding.inflate(
                        inflater,
                        container,
                        false
                );


        return binding.getRoot();
    }


    // ============================================================
    // VIEW CREATED
    // ============================================================

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(
                view,
                savedInstanceState
        );


        missionRepository =
                new DailyMissionRepository(
                        requireContext()
                );


        setupRecyclerView();


        loadDailyMissions();
    }


    // ============================================================
    // RECYCLER VIEW
    // ============================================================

    private void setupRecyclerView() {

        adapter =
                new DailyMissionAdapter(
                        missionList
                );


        binding.missionRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );


        binding.missionRecyclerView.setAdapter(
                adapter
        );


        binding.missionRecyclerView.setNestedScrollingEnabled(
                false
        );
    }


    // ============================================================
    // LOAD DAILY MISSIONS
    // ============================================================

    private void loadDailyMissions() {

        missionList.clear();


        List<DailyMission> missions =
                missionRepository.getTodayMissions();


        if (missions != null) {

            missionList.addAll(
                    missions
            );
        }


        adapter.notifyDataSetChanged();


        updateOverallProgress();
    }


    // ============================================================
    // OVERALL PROGRESS
    // ============================================================

    private void updateOverallProgress() {

        int completed = 0;


        int total =
                missionList.size();


        for (
                DailyMission mission
                : missionList
        ) {

            if (mission.isCompleted()) {

                completed++;
            }
        }


        int percentage = 0;


        if (total > 0) {

            percentage =
                    (completed * 100) / total;
        }


        // ========================================================
        // PROGRESS BAR
        // ========================================================

        binding.overallProgressBar.setProgress(
                percentage
        );


        // ========================================================
        // PERCENTAGE
        // ========================================================

        binding.progressPercentText.setText(
                percentage + "%"
        );


        // ========================================================
        // COUNT
        // ========================================================

        binding.progressCountText.setText(
                completed
                        + " of "
                        + total
                        + " missions completed"
        );


        // ========================================================
        // ALL COMPLETED CARD
        // ========================================================

        if (
                total > 0
                        && completed == total
        ) {

            binding.allCompletedCard.setVisibility(
                    View.VISIBLE
            );

        } else {

            binding.allCompletedCard.setVisibility(
                    View.GONE
            );
        }
    }


    // ============================================================
    // REFRESH WHEN RETURNING TO FRAGMENT
    // ============================================================

    @Override
    public void onResume() {

        super.onResume();


        if (
                missionRepository != null
                        && binding != null
        ) {

            loadDailyMissions();
        }
    }


    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    public void onDestroyView() {

        if (missionRepository != null) {

            missionRepository.close();

            missionRepository = null;
        }


        binding = null;


        super.onDestroyView();
    }
}