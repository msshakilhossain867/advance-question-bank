package com.example.questionbank_practiceexam.presentation.subjects;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.questionbank_practiceexam.presentation.questionview.QuestionsActivity;
import com.example.questionbank_practiceexam.data.local.entity.Subject;
import com.example.questionbank_practiceexam.data.local.repository.SubjectRepository;
import com.example.questionbank_practiceexam.databinding.ActivitySubjectsBinding;
import com.example.questionbank_practiceexam.presentation.adapters.SubjectAdapter;


import java.util.List;

public class SubjectsActivity extends AppCompatActivity {

    private ActivitySubjectsBinding binding;
    private SubjectRepository subjectRepository;
    private SubjectAdapter subjectAdapter;

    private int unitId;
    private String unitName;

    private int yearId;
    private String yearTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // ============================================================
        // VIEW BINDING
        // ============================================================

        binding = ActivitySubjectsBinding.inflate(
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
        // GET SELECTED UNIT
        // ============================================================

        unitId = getIntent().getIntExtra(
                "unit_id",
                -1
        );

        unitName = getIntent().getStringExtra(
                "unit_name"
        );

        // ============================================================
        // GET YEAR INFORMATION
        // ============================================================

        yearId = getIntent().getIntExtra(
                "year_id",
                -1
        );

        yearTitle = getIntent().getStringExtra(
                "year_title"
        );

        if (unitId == -1) {

            Toast.makeText(
                    this,
                    "Invalid unit",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // ============================================================
        // REPOSITORY
        // ============================================================

        subjectRepository =
                new SubjectRepository(
                        getApplicationContext()
                );

        // ============================================================
        // LOAD SUBJECTS
        // ============================================================

        loadSubjects();
    }

    // ================================================================
    // LOAD SUBJECTS FROM DATABASE
    // ================================================================

    private void loadSubjects() {

        List<Subject> subjects =
                subjectRepository.getSubjectsByUnit(
                        unitId
                );

        if (subjects == null || subjects.isEmpty()) {

            Toast.makeText(
                    this,
                    "No subject found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ============================================================
        // ADAPTER
        // ============================================================

        subjectAdapter =
                new SubjectAdapter(
                        subjects,
                        subject -> {

                            int subjectId =
                                    subject.getId();

                            String subjectName =
                                    subject.getName();

                            // =================================================
                            // NEXT:
                            // Subject → Questions
                            // =================================================

                            Intent intent =
                                    new Intent(
                                            SubjectsActivity.this,
                                          QuestionsActivity.class
                                    );

                            intent.putExtra(
                                    "subject_id",
                                    subjectId
                            );

                            intent.putExtra(
                                    "subject_name",
                                    subjectName
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
                );

        // ============================================================
        // RECYCLER VIEW
        // ============================================================

        binding.subjectRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this)
        );

        binding.subjectRecyclerView.setAdapter(
                subjectAdapter
        );
    }

    // ================================================================
    // CLOSE
    // ================================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (subjectRepository != null) {
            subjectRepository.close();
            subjectRepository = null;
        }

        binding = null;
    }
}