package com.example.questionbank_practiceexam.presentation.mocktest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.Subject;
import com.example.questionbank_practiceexam.data.local.repository.SubjectRepository;
import com.example.questionbank_practiceexam.databinding.ActivityMockTestSubjectBinding;
import com.example.questionbank_practiceexam.presentation.adapters.MockTestSubjectAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MockTestSubjectActivity
        extends AppCompatActivity {


    // ============================================================
    // BINDING
    // ============================================================

    private ActivityMockTestSubjectBinding binding;


    // ============================================================
    // REPOSITORY
    // ============================================================

    private SubjectRepository subjectRepository;


    // ============================================================
    // UNIT / YEAR
    // ============================================================

    private int unitId = -1;

    private String unitName;

    private int yearId = -1;

    private String yearTitle;


    // ============================================================
    // SUBJECT DATA
    // ============================================================

    private List<Subject> allSubjects =
            new ArrayList<>();


    private List<Subject> mandatorySubjects =
            new ArrayList<>();


    /*
     * choice_group -> subjects
     *
     * Example:
     *
     * 1 -> Higher Math, Biology
     * 2 -> Bangla, English
     */
    private final Map<Integer, List<Subject>>
            choiceGroups =
            new HashMap<>();


    // ============================================================
    // SELECTED SUBJECTS
    // ============================================================

    private final Set<Integer>
            selectedSubjectIds =
            new HashSet<>();


    // ============================================================
    // ADAPTERS
    // ============================================================

    private MockTestSubjectAdapter mandatoryAdapter;


    /*
     * groupId -> adapter
     */
    private final Map<Integer, MockTestSubjectAdapter>
            groupAdapters =
            new HashMap<>();


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        EdgeToEdge.enable(this);


        binding =
                ActivityMockTestSubjectBinding.inflate(
                        getLayoutInflater()
                );


        setContentView(
                binding.getRoot()
        );

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // ========================================================
        // GET INTENT DATA
        // ========================================================

        unitId =
                getIntent().getIntExtra(
                        "unit_id",
                        -1
                );


        unitName =
                getIntent().getStringExtra(
                        "unit_name"
                );


        yearId =
                getIntent().getIntExtra(
                        "year_id",
                        -1
                );


        yearTitle =
                getIntent().getStringExtra(
                        "year_title"
                );


        // ========================================================
        // VALIDATION
        // ========================================================

        if (unitId == -1) {

            Toast.makeText(
                    this,
                    "Invalid unit",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // ========================================================
        // TOOLBAR
        // ========================================================

        binding.toolbar.setNavigationOnClickListener(
                v -> finish()
        );


        // ========================================================
        // REPOSITORY
        // ========================================================

        subjectRepository =
                new SubjectRepository(
                        getApplicationContext()
                );


        // ========================================================
        // LOAD SUBJECTS
        // ========================================================

        loadSubjects();


        // ========================================================
        // REVIEW & START
        // ========================================================

        binding.reviewStartButton.setOnClickListener(
                v -> reviewAndStart()
        );
    }


    // ============================================================
    // LOAD SUBJECTS
    // ============================================================

    private void loadSubjects() {

        allSubjects =
                subjectRepository.getSubjectsByUnitForMockTest(
                        unitId
                );


        if (allSubjects == null ||
                allSubjects.isEmpty()) {

            Toast.makeText(
                    this,
                    "No subjects found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // ========================================================
        // CLEAR OLD DATA
        // ========================================================

        mandatorySubjects.clear();

        choiceGroups.clear();

        selectedSubjectIds.clear();

        groupAdapters.clear();


        // ========================================================
        // SEPARATE SUBJECTS
        // ========================================================

        for (Subject subject :
                allSubjects) {


            // ====================================================
            // MANDATORY
            // ====================================================

            if (subject.isMandatory()) {

                mandatorySubjects.add(
                        subject
                );


                /*
                 * Mandatory automatically selected
                 */
                selectedSubjectIds.add(
                        subject.getId()
                );

            }


            // ====================================================
            // OPTIONAL
            // ====================================================

            else {

                int group =
                        subject.getChoiceGroup();


                /*
                 * choice_group > 0
                 *
                 * Example:
                 *
                 * Higher Math -> 1
                 * Biology     -> 1
                 *
                 * Bangla      -> 2
                 * English     -> 2
                 */

                if (group > 0) {

                    if (!choiceGroups.containsKey(
                            group
                    )) {

                        choiceGroups.put(
                                group,
                                new ArrayList<>()
                        );
                    }


                    choiceGroups
                            .get(group)
                            .add(subject);
                }
            }
        }


        // ========================================================
        // MANDATORY UI
        // ========================================================

        setupMandatorySubjects();


        // ========================================================
        // CHOICE GROUP UI
        // ========================================================

        setupChoiceGroups();


        // ========================================================
        // STATUS
        // ========================================================

        updateChoiceStatus();
    }


    // ============================================================
    // MANDATORY SUBJECTS
    // ============================================================

    private void setupMandatorySubjects() {

        mandatoryAdapter =
                new MockTestSubjectAdapter(
                        mandatorySubjects,
                        selectedIds -> {

                            /*
                             * Mandatory subject-এর
                             * selection এখানে change করার দরকার নেই।
                             */
                        }
                );


        // ========================================================
        // SELECT ALL MANDATORY
        // ========================================================

        mandatoryAdapter.setSelectedIds(
                selectedSubjectIds
        );


        // ========================================================
        // RECYCLER
        // ========================================================

        binding.mandatorySubjectRecycler
                .setLayoutManager(
                        new LinearLayoutManager(
                                this
                        )
                );


        binding.mandatorySubjectRecycler
                .setAdapter(
                        mandatoryAdapter
                );
    }


    // ============================================================
    // CHOICE GROUPS
    // ============================================================

    private void setupChoiceGroups() {

        /*
         * পুরনো dynamic views remove
         */

        binding.choiceGroupContainer
                .removeAllViews();


        groupAdapters.clear();


        // ========================================================
        // NO GROUP
        // ========================================================

        if (choiceGroups.isEmpty()) {

            binding.choiceGroupContainer
                    .setVisibility(
                            View.GONE
                    );


            binding.choiceStatus.setText(
                    "No optional subject"
            );


            return;
        }


        binding.choiceGroupContainer
                .setVisibility(
                        View.VISIBLE
                );


        // ========================================================
        // SORT GROUP ID
        // ========================================================

        List<Integer> groupIds =
                new ArrayList<>(
                        choiceGroups.keySet()
                );


        java.util.Collections.sort(
                groupIds
        );


        // ========================================================
        // CREATE EACH GROUP
        // ========================================================

        for (Integer groupId :
                groupIds) {


            List<Subject> groupSubjects =
                    choiceGroups.get(
                            groupId
                    );


            if (groupSubjects == null ||
                    groupSubjects.isEmpty()) {

                continue;
            }


            // ====================================================
            // GROUP TITLE
            // ====================================================

            TextView groupTitle =
                    new TextView(this);


            groupTitle.setText(
                    "Group "
                            + groupId
            );


            groupTitle.setTextSize(
                    13
            );


            groupTitle.setTextColor(
                    Color.parseColor(
                            "#1858A8"
                    )
            );


            groupTitle.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );


            groupTitle.setPadding(
                    0,
                    12,
                    0,
                    4
            );


            binding.choiceGroupContainer
                    .addView(
                            groupTitle
                    );


            // ====================================================
            // GROUP SUBTITLE
            // ====================================================

            TextView groupSubtitle =
                    new TextView(this);


            groupSubtitle.setText(
                    "Select exactly 1 subject"
            );


            groupSubtitle.setTextSize(
                    9
            );


            groupSubtitle.setTextColor(
                    Color.parseColor(
                            "#888888"
                    )
            );


            groupSubtitle.setPadding(
                    0,
                    0,
                    0,
                    6
            );


            binding.choiceGroupContainer
                    .addView(
                            groupSubtitle
                    );


            // ====================================================
            // RECYCLER
            // ====================================================

            RecyclerView recyclerView =
                    new RecyclerView(this);


            recyclerView.setLayoutParams(
                    new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );


            recyclerView.setNestedScrollingEnabled(
                    false
            );


            recyclerView.setOverScrollMode(
                    View.OVER_SCROLL_NEVER
            );


            recyclerView.setLayoutManager(
                    new LinearLayoutManager(
                            this
                    )
            );


            // ====================================================
            // ADAPTER
            // ====================================================

            MockTestSubjectAdapter adapter =
                    new MockTestSubjectAdapter(
                            groupSubjects,
                            selectedIds -> {

                                handleGroupSelection(
                                        groupId,
                                        groupSubjects,
                                        selectedIds
                                );
                            }
                    );


            // ====================================================
            // EXISTING SELECTION
            // ====================================================

            Set<Integer> currentGroupSelection =
                    getSelectedFromGroup(
                            groupSubjects
                    );


            adapter.setSelectedIds(
                    currentGroupSelection
            );


            // ====================================================
            // SAVE ADAPTER
            // ====================================================

            groupAdapters.put(
                    groupId,
                    adapter
            );


            // ====================================================
            // SET ADAPTER
            // ====================================================

            recyclerView.setAdapter(
                    adapter
            );


            binding.choiceGroupContainer
                    .addView(
                            recyclerView
                    );
        }
    }


    // ============================================================
    // GET SELECTED FROM GROUP
    // ============================================================

    private Set<Integer> getSelectedFromGroup(
            List<Subject> subjects
    ) {

        Set<Integer> result =
                new HashSet<>();


        for (Subject subject :
                subjects) {

            if (selectedSubjectIds.contains(
                    subject.getId()
            )) {

                result.add(
                        subject.getId()
                );
            }
        }


        return result;
    }


    // ============================================================
    // HANDLE GROUP SELECTION
    // ============================================================

    private void handleGroupSelection(
            int groupId,
            List<Subject> groupSubjects,
            Set<Integer> adapterSelected
    ) {


        // ========================================================
        // REMOVE OLD SELECTION FROM THIS GROUP
        // ========================================================

        for (Subject subject :
                groupSubjects) {

            selectedSubjectIds.remove(
                    subject.getId()
            );
        }


        // ========================================================
        // ADD NEW SELECTION
        // ========================================================

        if (!adapterSelected.isEmpty()) {

            /*
             * Exactly 1 রাখব।
             */

            Integer selectedId =
                    adapterSelected
                            .iterator()
                            .next();


            selectedSubjectIds.add(
                    selectedId
            );


            // ====================================================
            // FORCE ONLY ONE IN ADAPTER
            // ====================================================

            MockTestSubjectAdapter adapter =
                    groupAdapters.get(
                            groupId
                    );


            if (adapter != null) {

                Set<Integer> onlyOne =
                        new HashSet<>();


                onlyOne.add(
                        selectedId
                );


                adapter.setSelectedIds(
                        onlyOne
                );
            }
        }


        // ========================================================
        // STATUS
        // ========================================================

        updateChoiceStatus();
    }


    // ============================================================
    // UPDATE CHOICE STATUS
    // ============================================================

    private void updateChoiceStatus() {

        if (choiceGroups.isEmpty()) {

            binding.choiceStatus.setText(
                    "No optional subject"
            );

            return;
        }


        int selectedGroups = 0;


        for (Map.Entry<Integer,
                List<Subject>> entry :
                choiceGroups.entrySet()) {


            boolean selected =
                    false;


            for (Subject subject :
                    entry.getValue()) {

                if (selectedSubjectIds.contains(
                        subject.getId()
                )) {

                    selected = true;

                    break;
                }
            }


            if (selected) {

                selectedGroups++;
            }
        }


        binding.choiceStatus.setText(
                "Select "
                        + selectedGroups
                        + " / "
                        + choiceGroups.size()
        );
    }


    // ============================================================
    // REVIEW & START
    // ============================================================

    private void reviewAndStart() {


        // ========================================================
        // CHECK EVERY GROUP
        // ========================================================

        for (Map.Entry<Integer,
                List<Subject>> entry :
                choiceGroups.entrySet()) {


            int groupId =
                    entry.getKey();


            List<Subject> subjects =
                    entry.getValue();


            boolean selected =
                    false;


            for (Subject subject :
                    subjects) {

                if (selectedSubjectIds.contains(
                        subject.getId()
                )) {

                    selected = true;

                    break;
                }
            }


            // ====================================================
            // NOT SELECTED
            // ====================================================

            if (!selected) {

                Toast.makeText(
                        this,
                        "Please select 1 subject from Group "
                                + groupId,
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }
        }


        // ========================================================
        // EVERYTHING OK
        // ========================================================

        startReview();
    }


    // ============================================================
    // START REVIEW
    // ============================================================

    private void startReview() {


        int[] subjectIds =
                new int[
                        selectedSubjectIds.size()
                        ];


        int index = 0;


        for (Integer id :
                selectedSubjectIds) {

            subjectIds[index++] =
                    id;
        }


        // ========================================================
        // REVIEW ACTIVITY
        // ========================================================

        Intent intent =
                new Intent(
                        MockTestSubjectActivity.this,
                        MockTestReviewActivity.class
                );


        intent.putExtra(
                "subject_ids",
                subjectIds
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


        startActivity(
                intent
        );
    }


    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        if (subjectRepository != null) {

            subjectRepository.close();

            subjectRepository = null;
        }


        binding = null;


        super.onDestroy();
    }
}