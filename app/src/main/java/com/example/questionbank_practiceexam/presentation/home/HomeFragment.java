package com.example.questionbank_practiceexam.presentation.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.questionbank_practiceexam.presentation.unlist.UniversityListActivity;
import com.example.questionbank_practiceexam.data.local.entity.University;
import com.example.questionbank_practiceexam.data.local.repository.UniversityRepository;
import com.example.questionbank_practiceexam.databinding.FragmentHomeBinding;
import com.example.questionbank_practiceexam.presentation.years.YearsActivity;
import com.example.questionbank_practiceexam.utils.Utils;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    private UniversityRepository universityRepository;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return  binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        universityRepository =
                new UniversityRepository(requireContext());

        loadUniversity();

        setupClickListeners();


    }







// ============================================================
    // LOAD UNIVERSITY
    // ============================================================

    private void loadUniversity() {

        University university =
                universityRepository.getTargetUniversity();

        if (university == null) {
            return;
        }

        /*
         * এখন আমরা একটাই University নিয়ে কাজ করছি।
         *
         * Database:
         *
         * id = 1
         * name = DU
         * full_name = Dhaka University
         */

        binding.unName.setText(university.getFullName());
        binding.unShortName.setText(university.getName()+"All Unit");
        binding.btnMoveYearActivity.setOnClickListener(v -> {

                    int universityId =
                            university.getId();

                    String universityName =
                            university.getName();

                    Intent intent =
                            new Intent(
                                    requireContext(),
                                    YearsActivity.class
                            );

                    intent.putExtra(
                            "university_id",
                            universityId
                    );

                    intent.putExtra(
                            "university_name",
                            universityName
                    );

                    startActivity(intent);
        }

        );


    }


// ============================================================
// CLICK LISTENERS
// ============================================================

private void setupClickListeners() {

    binding.btnMenu.setOnClickListener(v -> {

        // পরে Navigation Drawer open করব

    });

    binding.btnNotification.setOnClickListener(v -> {

        // পরে notification screen/dialog করব

    });

    binding.nextVersityList.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Utils.moveToNextActivity(requireContext(),UniversityListActivity.class);
        }
    });

    binding.btnViewAllUni.setOnClickListener(v -> {

        // এখন কিছু করব না
        // কারণ আপাতত একটি University নিয়েই কাজ করছি
        Utils.moveToNextActivity(requireContext(),UniversityListActivity.class);

    });

    binding.btnViewAllRecent.setOnClickListener(v -> {

        // পরে Recent Activity screen

    });
}

// ============================================================
// ON DESTROY VIEW
// ============================================================

@Override
public void onDestroyView() {

    super.onDestroyView();

    if (universityRepository != null) {
        universityRepository.close();
        universityRepository = null;
    }

    binding = null;
}
}