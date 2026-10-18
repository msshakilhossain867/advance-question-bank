package com.example.questionbank_practiceexam.presentation.fragments;

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
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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

        setupSuccessRateChart(0.0);
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
    // ACTIVITY HORIZONTAL BAR CHART
    // Reading / Practice / Mock
    // ============================================================

    private void setupActivityChart(
            List<SevenDayProgress> data
    ) {

        ArrayList<BarEntry> entries =
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

            entries.add(
                    new BarEntry(
                            i,
                            new float[]{
                                    (float) item.getReading(),
                                    (float) item.getPracticeTotal(),
                                    (float) item.getMockTotal()
                            }
                    )
            );


            labels.add(
                    getDayLabel(
                            item.getDate()
                    )
            );
        }


        BarDataSet dataSet =
                new BarDataSet(
                        entries,
                        "Activity"
                );

        dataSet.setColors(
                COLOR_READING,
                COLOR_PRACTICE,
                COLOR_MOCK
        );

        dataSet.setStackLabels(
                new String[]{
                        "Reading",
                        "Practice",
                        "Mock Test"
                }
        );

        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(9f);
        dataSet.setValueTextColor(
                isDarkMode() ? Color.parseColor("#E2E8F0") : Color.DKGRAY
        );


        BarData barData =
                new BarData(
                        dataSet
                );

        barData.setBarWidth(0.5f);


        binding.activityChart.setData(
                barData
        );

        binding.activityChart.setFitBars(true);


        configureHorizontalChart(
                binding.activityChart,
                labels
        );


        binding.activityChart.invalidate();
    }


    // ============================================================
    // PERFORMANCE PIE / DONUT CHART
    // Correct / Wrong / Skipped
    // ============================================================

    private void setupPerformanceChart(
            List<SevenDayProgress> data
    ) {

        int totalCorrect = 0;
        int totalWrong = 0;
        int totalSkipped = 0;

        for (SevenDayProgress item : data) {
            totalCorrect += (item.getPracticeCorrect() + item.getMockCorrect());
            totalWrong += (item.getPracticeWrong() + item.getMockWrong());
            totalSkipped += (item.getPracticeSkipped() + item.getMockSkipped());
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (totalCorrect > 0) entries.add(new PieEntry(totalCorrect, "Correct"));
        if (totalWrong > 0) entries.add(new PieEntry(totalWrong, "Wrong"));
        if (totalSkipped > 0) entries.add(new PieEntry(totalSkipped, "Skipped"));

        PieDataSet dataSet = new PieDataSet(entries, "Performance Summary");
        dataSet.setColors(COLOR_CORRECT, COLOR_WRONG, COLOR_SKIPPED);
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(dataSet);

        PieChart chart = binding.performanceChart;
        chart.setData(pieData);
        chart.getDescription().setEnabled(false);

        // ডোনাট চার্ট স্টাইল কনফিগারেশন
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(50f);
        chart.setTransparentCircleRadius(55f);
        chart.setHoleColor(Color.TRANSPARENT);

        chart.setCenterText("Performance");
        chart.setCenterTextSize(13f);
        chart.setCenterTextColor(isDarkMode() ? Color.parseColor("#E2E8F0") : Color.DKGRAY);

        // লেজেন্ড কনফিগারেশন
        if (chart.getLegend() != null) {
            chart.getLegend().setEnabled(true);
            chart.getLegend().setTextSize(12f);
            chart.getLegend().setTextColor(
                    isDarkMode() ? Color.parseColor("#94A3B8") : Color.parseColor("#64748B")
            );
        }

        chart.animateY(800);
        chart.invalidate();
    }


    // ============================================================
    // HORIZONTAL CHART CONFIG
    // ============================================================

    private void configureHorizontalChart(
            HorizontalBarChart chart,
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
        // RIGHT AXIS (Horizontal-এর ক্ষেত্রে এটি নিষ্ক্রিয় রাখা হয়)
        // --------------------------------------------------------

        chart.getAxisRight()
                .setEnabled(false);


        // --------------------------------------------------------
        // LEFT AXIS (Horizontal বার চার্টে এটি নিচে বা মূল অক্ষ হিসেবে কাজ করে)
        // --------------------------------------------------------

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(10f);


        // --------------------------------------------------------
        // X AXIS (Horizontal বার চার্টে এটি বাম পাশের দিনগুলোর লেবেল দেখায়)
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


        xAxis.setAvoidFirstLastClipping(
                true
        );


        // --------------------------------------------------------
        // ANIMATION
        // --------------------------------------------------------

        chart.animateY(
                700
        );

        applyHorizontalChartTheme(chart);


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

            correct +=
                    item.getPracticeCorrect();


            total +=
                    item.getPracticeTotal();


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

        setupSuccessRateChart(success);
    }

    private void setupSuccessRateChart(double successRate) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        float successFloat = (float) successRate;
        float remainingFloat = 100f - successFloat;
        if (remainingFloat < 0) remainingFloat = 0f;

        entries.add(new PieEntry(successFloat, "")); // Success এর জন্য ফাঁকা লেবেল
        entries.add(new PieEntry(remainingFloat, "")); // Remaining এর জন্য ফাঁকা লেবেল (কোনো টেক্সট দেখাবে না)

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                Color.rgb(46, 125, 50),
                isDarkMode() ? Color.parseColor("#334155") : Color.parseColor("#E2E8F0")
        );
        dataSet.setDrawValues(false);

        PieData pieData = new PieData(dataSet);
        PieChart chart = binding.successRateChart;

        chart.setData(pieData);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(65f);
        chart.setTransparentCircleRadius(70f);
        chart.setHoleColor(Color.TRANSPARENT);

        chart.setCenterText(String.format(Locale.getDefault(), "%.1f%%", successRate));
        chart.setCenterTextSize(16f);
        chart.setCenterTextColor(isDarkMode() ? Color.parseColor("#E2E8F0") : Color.parseColor("#1E293B"));

        chart.setTouchEnabled(false);
        chart.animateY(1000);
        chart.invalidate();
    }

    // ============================================================
    // DYNAMIC CHART THEME (Light / Dark Mode Support)
    // ============================================================

    private boolean isDarkMode() {
        int nightModeFlags = requireContext().getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    private void applyHorizontalChartTheme(HorizontalBarChart chart) {
        boolean isDark = isDarkMode();
        int textColor = isDark ? Color.parseColor("#94A3B8") : Color.parseColor("#64748B");
        int gridColor = isDark ? Color.parseColor("#334155") : Color.parseColor("#E2E8F0");

        if (chart.getLegend() != null) {
            chart.getLegend().setTextColor(textColor);
        }

        if (chart.getXAxis() != null) {
            chart.getXAxis().setTextColor(textColor);
            chart.getXAxis().setGridColor(gridColor);
        }

        if (chart.getAxisLeft() != null) {
            chart.getAxisLeft().setTextColor(textColor);
            chart.getAxisLeft().setGridColor(gridColor);
        }
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

            if (binding.successRateChart != null) {

                binding.successRateChart.clear();
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