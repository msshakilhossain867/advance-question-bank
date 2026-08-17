package com.example.questionbank_practiceexam.presentation.report;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.questionbank_practiceexam.data.local.entity.SevenDayProgress;
import com.example.questionbank_practiceexam.data.local.repository.ProgressDailyRepository;
import com.example.questionbank_practiceexam.databinding.FragmentProgressBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProgressFragment extends Fragment {

    private FragmentProgressBinding binding;

    private ProgressDailyRepository progressRepository;


    // ============================================================
    // COLORS
    // ============================================================

    private static final int COLOR_READING =
            Color.rgb(33, 150, 243);

    private static final int COLOR_PRACTICE =
            Color.rgb(76, 175, 80);

    private static final int COLOR_MOCK =
            Color.rgb(156, 39, 176);

    private static final int COLOR_CORRECT =
            Color.rgb(46, 125, 50);

    private static final int COLOR_WRONG =
            Color.rgb(211, 47, 47);

    private static final int COLOR_SKIPPED =
            Color.rgb(245, 124, 0);


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
                FragmentProgressBinding.inflate(
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

        progressRepository =
                new ProgressDailyRepository(
                        requireContext()
                );

        loadProgress();
    }


    // ============================================================
    // LOAD PROGRESS
    // ============================================================

    private void loadProgress() {

        if (progressRepository == null) {
            return;
        }


        List<SevenDayProgress> data =
                progressRepository.getLast7DaysProgress();


        if (data == null ||
                data.isEmpty()) {

            showEmptyProgress();

            return;
        }


        updateSummary(data);

        setupActivityChart(data);

        setupPerformanceChart(data);

        calculateOverallSuccess(data);
    }


    // ============================================================
    // EMPTY PROGRESS
    // ============================================================

    private void showEmptyProgress() {

        binding.readingTotalText.setText("0");

        binding.practiceTotalText.setText("0");

        binding.mockTotalText.setText("0");

        binding.overallSuccessText.setText(
                "7 Days Success Rate: 0.0%"
        );
    }


    // ============================================================
    // SUMMARY
    // ============================================================

    private void updateSummary(
            List<SevenDayProgress> data
    ) {

        int reading = 0;

        int practice = 0;

        int mock = 0;


        for (
                SevenDayProgress item
                : data
        ) {

            reading +=
                    item.getReading();


            practice +=
                    item.getPracticeTotal();


            mock +=
                    item.getMockTotal();
        }


        binding.readingTotalText.setText(
                String.valueOf(reading)
        );


        binding.practiceTotalText.setText(
                String.valueOf(practice)
        );


        binding.mockTotalText.setText(
                String.valueOf(mock)
        );
    }


    // ============================================================
    // ACTIVITY GRAPH
    // Reading / Practice / Mock
    // ============================================================

    private void setupActivityChart(
            List<SevenDayProgress> data
    ) {

        ArrayList<Entry> readingEntries =
                new ArrayList<>();


        ArrayList<Entry> practiceEntries =
                new ArrayList<>();


        ArrayList<Entry> mockEntries =
                new ArrayList<>();


        ArrayList<String> labels =
                new ArrayList<>();


        for (
                int i = 0;
                i < data.size();
                i++
        ) {

            SevenDayProgress item =
                    data.get(i);


            readingEntries.add(
                    new Entry(
                            i,
                            item.getReading()
                    )
            );


            practiceEntries.add(
                    new Entry(
                            i,
                            item.getPracticeTotal()
                    )
            );


            mockEntries.add(
                    new Entry(
                            i,
                            item.getMockTotal()
                    )
            );


            labels.add(
                    getDayLabel(
                            item.getDate()
                    )
            );
        }


        LineDataSet readingSet =
                new LineDataSet(
                        readingEntries,
                        "Reading"
                );


        LineDataSet practiceSet =
                new LineDataSet(
                        practiceEntries,
                        "Practice"
                );


        LineDataSet mockSet =
                new LineDataSet(
                        mockEntries,
                        "Mock Test"
                );


        setupLine(
                readingSet,
                COLOR_READING
        );


        setupLine(
                practiceSet,
                COLOR_PRACTICE
        );


        setupLine(
                mockSet,
                COLOR_MOCK
        );


        LineData lineData =
                new LineData(
                        readingSet,
                        practiceSet,
                        mockSet
                );


        binding.activityChart.setData(
                lineData
        );


        configureChart(
                binding.activityChart,
                labels
        );


        binding.activityChart.invalidate();
    }


    // ============================================================
    // PERFORMANCE GRAPH
    // Correct / Wrong / Skipped
    // ============================================================

    private void setupPerformanceChart(
            List<SevenDayProgress> data
    ) {

        ArrayList<Entry> correctEntries =
                new ArrayList<>();


        ArrayList<Entry> wrongEntries =
                new ArrayList<>();


        ArrayList<Entry> skippedEntries =
                new ArrayList<>();


        ArrayList<String> labels =
                new ArrayList<>();


        for (
                int i = 0;
                i < data.size();
                i++
        ) {

            SevenDayProgress item =
                    data.get(i);


            int correct =
                    item.getPracticeCorrect()
                            + item.getMockCorrect();


            int wrong =
                    item.getPracticeWrong()
                            + item.getMockWrong();


            int skipped =
                    item.getPracticeSkipped()
                            + item.getMockSkipped();


            correctEntries.add(
                    new Entry(
                            i,
                            correct
                    )
            );


            wrongEntries.add(
                    new Entry(
                            i,
                            wrong
                    )
            );


            skippedEntries.add(
                    new Entry(
                            i,
                            skipped
                    )
            );


            labels.add(
                    getDayLabel(
                            item.getDate()
                    )
            );
        }


        LineDataSet correctSet =
                new LineDataSet(
                        correctEntries,
                        "Correct"
                );


        LineDataSet wrongSet =
                new LineDataSet(
                        wrongEntries,
                        "Wrong"
                );


        LineDataSet skippedSet =
                new LineDataSet(
                        skippedEntries,
                        "Skipped"
                );


        setupLine(
                correctSet,
                COLOR_CORRECT
        );


        setupLine(
                wrongSet,
                COLOR_WRONG
        );


        setupLine(
                skippedSet,
                COLOR_SKIPPED
        );


        LineData lineData =
                new LineData(
                        correctSet,
                        wrongSet,
                        skippedSet
                );


        binding.performanceChart.setData(
                lineData
        );


        configureChart(
                binding.performanceChart,
                labels
        );


        binding.performanceChart.invalidate();
    }


    // ============================================================
    // LINE STYLE
    // ============================================================

    private void setupLine(
            LineDataSet set,
            int color
    ) {

        set.setColor(color);

        set.setCircleColor(color);

        set.setCircleHoleColor(
                Color.WHITE
        );


        set.setLineWidth(
                2.5f
        );


        set.setCircleRadius(
                4f
        );


        set.setCircleHoleRadius(
                2f
        );


        set.setDrawValues(
                true
        );


        set.setValueTextSize(
                9f
        );


        set.setValueTextColor(
                Color.DKGRAY
        );


        set.setMode(
                LineDataSet.Mode.CUBIC_BEZIER
        );


        set.setDrawFilled(
                false
        );


        set.setHighlightEnabled(
                true
        );


        set.setDrawHighlightIndicators(
                true
        );


        set.setHighlightLineWidth(
                1.5f
        );
    }


    // ============================================================
    // CHART CONFIG
    // ============================================================

    private void configureChart(
            LineChart chart,
            ArrayList<String> labels
    ) {

        chart.getDescription()
                .setEnabled(false);


        chart.setTouchEnabled(true);


        chart.setDragEnabled(true);


        chart.setScaleEnabled(false);


        chart.setPinchZoom(false);


        chart.setDoubleTapToZoomEnabled(false);


        chart.setDrawGridBackground(false);


        // --------------------------------------------------------
        // LEGEND
        // --------------------------------------------------------

        chart.getLegend()
                .setEnabled(true);


        chart.getLegend()
                .setTextSize(12f);


        // --------------------------------------------------------
        // RIGHT AXIS
        // --------------------------------------------------------

        chart.getAxisRight()
                .setEnabled(false);


        // --------------------------------------------------------
        // LEFT AXIS
        // --------------------------------------------------------

        chart.getAxisLeft()
                .setGranularity(1f);


        chart.getAxisLeft()
                .setAxisMinimum(0f);


        chart.getAxisLeft()
                .setTextSize(10f);


        // --------------------------------------------------------
        // X AXIS
        // --------------------------------------------------------

        XAxis xAxis =
                chart.getXAxis();


        xAxis.setPosition(
                XAxis.XAxisPosition.BOTTOM
        );


        xAxis.setGranularity(
                1f
        );


        xAxis.setGranularityEnabled(
                true
        );


        xAxis.setDrawGridLines(
                false
        );


        xAxis.setTextSize(
                10f
        );


        xAxis.setValueFormatter(
                new IndexAxisValueFormatter(
                        labels
                )
        );


        xAxis.setLabelRotationAngle(
                0
        );


        xAxis.setAvoidFirstLastClipping(
                true
        );


        // --------------------------------------------------------
        // ANIMATION
        // --------------------------------------------------------

        chart.animateX(
                700
        );


        chart.invalidate();
    }


    // ============================================================
    // DAY LABEL
    // ============================================================

    private String getDayLabel(
            String date
    ) {

        try {

            Date parsed =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).parse(date);


            if (parsed == null) {
                return "";
            }


            return new SimpleDateFormat(
                    "EEE",
                    Locale.getDefault()
            ).format(
                    parsed
            );

        } catch (Exception e) {

            return "";
        }
    }


    // ============================================================
    // OVERALL SUCCESS
    // ============================================================

    private void calculateOverallSuccess(
            List<SevenDayProgress> data
    ) {

        int correct = 0;

        int total = 0;


        for (
                SevenDayProgress item
                : data
        ) {

            // ----------------------------------------------------
            // PRACTICE
            // ----------------------------------------------------

            correct +=
                    item.getPracticeCorrect();


            total +=
                    item.getPracticeTotal();


            // ----------------------------------------------------
            // MOCK
            // ----------------------------------------------------

            correct +=
                    item.getMockCorrect();


            total +=
                    item.getMockTotal();
        }


        double success = 0;


        if (total > 0) {

            success =
                    (correct * 100.0)
                            / total;
        }


        binding.overallSuccessText.setText(
                String.format(
                        Locale.getDefault(),
                        "7 Days Success Rate: %.1f%%",
                        success
                )
        );
    }


    // ============================================================
    // DESTROY VIEW
    // ============================================================

    @Override
    public void onDestroyView() {

        if (binding != null) {

            if (binding.activityChart != null) {

                binding.activityChart.clear();
            }


            if (binding.performanceChart != null) {

                binding.performanceChart.clear();
            }
        }


        if (progressRepository != null) {

            progressRepository.close();

            progressRepository = null;
        }


        binding = null;

        super.onDestroyView();
    }
}