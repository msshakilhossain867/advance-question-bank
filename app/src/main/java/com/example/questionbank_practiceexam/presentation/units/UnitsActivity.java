package com.example.questionbank_practiceexam.presentation.units;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.questionbank_practiceexam.presentation.mocktest.MockTestSubjectActivity;
import com.example.questionbank_practiceexam.presentation.exam.PracticeSetActivity;
import com.example.questionbank_practiceexam.presentation.subjects.SubjectsActivity;
import com.example.questionbank_practiceexam.data.local.entity.Unit;
import com.example.questionbank_practiceexam.data.local.repository.UnitRepository;
import com.example.questionbank_practiceexam.databinding.ActivityUnitsBinding;
import com.example.questionbank_practiceexam.presentation.adapters.UnitAdapter;


import java.util.List;

public class UnitsActivity extends AppCompatActivity {

    private ActivityUnitsBinding binding;
    private UnitRepository unitRepository;
    private UnitAdapter unitAdapter;
    private String mode;

    private int yearId;
    private String yearTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = ActivityUnitsBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

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
        // GET SELECTED YEAR
        // ============================================================

        yearId = getIntent().getIntExtra(
                "year_id",
                -1
        );

        yearTitle = getIntent().getStringExtra(
                "year_title"
        );
        mode = getIntent().getStringExtra("mode");


        if (yearId == -1) {

            Toast.makeText(
                    this,
                    "Invalid academic year",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // ============================================================
        // REPOSITORY
        // ============================================================

        unitRepository =
                new UnitRepository(
                        getApplicationContext()
                );

        // ============================================================
        // LOAD UNITS
        // ============================================================

        loadUnits();

        binding.backButton.setOnClickListener(view -> {
            finish();
        });
    }

    // ============================================================
    // LOAD UNITS FROM DATABASE
    // ============================================================

    private void loadUnits() {

        List<Unit> units =
                unitRepository.getUnitsByYear(
                        yearId
                );

        if (units == null || units.isEmpty()) {

            Toast.makeText(
                    this,
                    "No unit found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        unitAdapter = new UnitAdapter(
                units,
                unit -> {

                    int unitId = unit.getId();

                    String unitName =
                            unit.getUnitName();


                    if ("practice".equals(mode)) {

                        moveToNext(PracticeSetActivity.class, unitId, unitName);

                    } else if ("mock_test".equals(mode)) {

                        moveToNext(MockTestSubjectActivity.class, unitId, unitName);


                    } else {
                        moveToNext(SubjectsActivity.class, unitId, unitName);

                    }


                }
        );

        binding.unitRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.unitRecyclerView.setAdapter(
                unitAdapter
        );
    }


    private void moveToNext(Class<?> activityClass, int unitId, String unitName) {

        Intent intent =
                new Intent(
                        UnitsActivity.this,
                        activityClass
                );

        intent.putExtra(
                "unit_id",
                unitId
        );

        intent.putExtra(
                "unit_name",
                unitName
        );

        intent.putExtra(
                "year_id",
                yearId
        );

        intent.putExtra(
                "year_title",
                yearTitle
        );

        startActivity(intent);


    }

    // ============================================================
    // CLOSE
    // ============================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (unitRepository != null) {
            unitRepository.close();
            unitRepository = null;
        }

        binding = null;
    }
}