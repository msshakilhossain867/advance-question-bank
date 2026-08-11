package com.example.questionbank_practiceexam.presentation.unlist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.questionbank_practiceexam.data.local.entity.University;
import com.example.questionbank_practiceexam.data.local.repository.UniversityRepository;
import com.example.questionbank_practiceexam.databinding.ActivityUniversityListBinding;
import com.example.questionbank_practiceexam.presentation.adapters.UniversityAdapter;
import com.example.questionbank_practiceexam.presentation.years.YearsActivity;

import java.util.ArrayList;
import java.util.List;

public class UniversityListActivity extends AppCompatActivity {

   private ActivityUniversityListBinding binding;

    private UniversityRepository universityRepository;
    private UniversityAdapter universityAdapter;

    private List<University> universityList =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // ============================================================
        // VIEW BINDING
        // ============================================================

        binding = ActivityUniversityListBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        // ============================================================
        // REPOSITORY
        // ============================================================

        universityRepository =
                new UniversityRepository(
                        getApplicationContext()
                );

        // ============================================================
        // RECYCLER VIEW
        // ============================================================

        binding.universityRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // ============================================================
        // LOAD UNIVERSITIES
        // ============================================================

        loadUniversities();

        // ============================================================
        // BACK BUTTON
        // ============================================================

        binding.backButton.setOnClickListener(v ->
                finish()
        );

        // ============================================================
        // SEARCH
        // ============================================================

        setupSearch();
    }

    // ================================================================
    // LOAD UNIVERSITIES
    // ================================================================

    private void loadUniversities() {

        universityList =
                universityRepository.getAllUniversities();

        if (universityList == null ||
                universityList.isEmpty()) {

            Toast.makeText(
                    this,
                    "No university found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        setupAdapter(universityList);
    }

    // ================================================================
    // SETUP ADAPTER
    // ================================================================

    private void setupAdapter(List<University> list) {

        universityAdapter = new UniversityAdapter(
                list,
                university -> {

                    int universityId =
                            university.getId();

                    String universityName =
                            university.getName();

                    Intent intent =
                            new Intent(
                                    UniversityListActivity.this,
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

        binding.universityRecyclerView.setAdapter(
                universityAdapter
        );
    }

    // ================================================================
    // SEARCH
    // ================================================================

    private void setupSearch() {

        binding.searchEditText.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        filterUniversities(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );
    }

    // ================================================================
    // FILTER
    // ================================================================

    private void filterUniversities(
            String query
    ) {

        String search =
                query.trim().toLowerCase();

        List<University> filteredList =
                new ArrayList<>();

        for (University university : universityList) {

            String name =
                    university.getName();

            String fullName =
                    university.getFullName();

            if ((name != null &&
                    name.toLowerCase().contains(search))
                    ||
                    (fullName != null &&
                            fullName.toLowerCase().contains(search))) {

                filteredList.add(university);
            }
        }

        setupAdapter(filteredList);
    }

    // ================================================================
    // CLOSE
    // ================================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (universityRepository != null) {

            universityRepository.close();

            universityRepository = null;
        }

        binding = null;
    }
}