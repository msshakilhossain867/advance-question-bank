package com.example.questionbank_practiceexam.presentation.years;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.questionbank_practiceexam.data.local.entity.Year;
import com.example.questionbank_practiceexam.data.local.repository.YearRepository;
import com.example.questionbank_practiceexam.databinding.ActivityYearsBinding;
import com.example.questionbank_practiceexam.presentation.adapters.AcademicYearAdapter;
import com.example.questionbank_practiceexam.presentation.units.UnitsActivity;

import java.util.List;

public class YearsActivity extends AppCompatActivity {

    private ActivityYearsBinding binding;

    private AcademicYearAdapter academicYearAdapter;

    private YearRepository yearRepository;

    // Selected University
    private int universityId;
    private String universityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // ============================================================
        // VIEW BINDING
        // ============================================================

        binding = ActivityYearsBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        // ============================================================
        // WINDOW INSETS
        // ============================================================

        ViewCompat.setOnApplyWindowInsetsListener(
                binding.main,
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // ============================================================
        // GET UNIVERSITY DATA
        // ============================================================

        universityId = getIntent().getIntExtra(
                "university_id",
                -1
        );

        universityName = getIntent().getStringExtra(
                "university_name"
        );

        // ============================================================
        // VALIDATE UNIVERSITY
        // ============================================================

        if (universityId == -1) {

            Toast.makeText(
                    this,
                    "Invalid university",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // ============================================================
        // REPOSITORY
        // ============================================================

        yearRepository =
                new YearRepository(
                        getApplicationContext()
                );

        // ============================================================
        // BACK BUTTON
        // ============================================================

//        binding.backButton.setOnClickListener(v ->
//                finish()
//        );

        // ============================================================
        // LOAD YEARS
        // ============================================================

        loadYears();
    }

    // ================================================================
    // LOAD YEARS FROM DATABASE
    // ================================================================

    private void loadYears() {

        List<Year> years =
                yearRepository.getYearsByUniversity(
                        universityId
                );

        if (years == null || years.isEmpty()) {

            Toast.makeText(
                    this,
                    "No academic year found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ============================================================
        // ADAPTER
        // ============================================================

        academicYearAdapter =
                new AcademicYearAdapter(
                        years,
                        year -> {

                            // =================================================
                            // SELECTED YEAR
                            // =================================================

                            int yearId =
                                    year.getId();

                            String yearTitle =
                                    year.getYearTitle();

                            // =================================================
                            // OPEN UNITS
                            // =================================================

                            Intent intent =
                                    new Intent(
                                            YearsActivity.this,
                                            UnitsActivity.class
                                    );

                            intent.putExtra(
                                    "year_id",
                                    yearId
                            );

                            intent.putExtra(
                                    "year_title",
                                    yearTitle
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

        // ============================================================
        // RECYCLER VIEW
        // ============================================================

        binding.yearRecyclerView.setLayoutManager(
                new GridLayoutManager(
                        this,
                        2
                )
        );

        binding.yearRecyclerView.setAdapter(
                academicYearAdapter
        );
    }

    // ================================================================
    // CLOSE DATABASE
    // ================================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (yearRepository != null) {

            yearRepository.close();

            yearRepository = null;
        }

        binding = null;
    }
}