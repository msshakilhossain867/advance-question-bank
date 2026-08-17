package com.example.questionbank_practiceexam.data.local.entity;


public class SevenDayProgress {

    private final String date;

    private final int reading;

    private final int practiceTotal;
    private final int practiceCorrect;
    private final int practiceWrong;
    private final int practiceSkipped;
    private final double practiceSuccessRate;

    private final int mockTotal;
    private final int mockCorrect;
    private final int mockWrong;
    private final int mockSkipped;
    private final double mockSuccessRate;

    private final int activityCount;


    public SevenDayProgress(
            String date,

            int reading,

            int practiceTotal,
            int practiceCorrect,
            int practiceWrong,
            int practiceSkipped,
            double practiceSuccessRate,

            int mockTotal,
            int mockCorrect,
            int mockWrong,
            int mockSkipped,
            double mockSuccessRate,

            int activityCount
    ) {

        this.date = date;

        this.reading = reading;

        this.practiceTotal = practiceTotal;
        this.practiceCorrect = practiceCorrect;
        this.practiceWrong = practiceWrong;
        this.practiceSkipped = practiceSkipped;
        this.practiceSuccessRate = practiceSuccessRate;

        this.mockTotal = mockTotal;
        this.mockCorrect = mockCorrect;
        this.mockWrong = mockWrong;
        this.mockSkipped = mockSkipped;
        this.mockSuccessRate = mockSuccessRate;

        this.activityCount = activityCount;
    }


    public String getDate() {
        return date;
    }


    public int getReading() {
        return reading;
    }


    public int getPracticeTotal() {
        return practiceTotal;
    }


    public int getPracticeCorrect() {
        return practiceCorrect;
    }


    public int getPracticeWrong() {
        return practiceWrong;
    }


    public int getPracticeSkipped() {
        return practiceSkipped;
    }


    public double getPracticeSuccessRate() {
        return practiceSuccessRate;
    }


    public int getMockTotal() {
        return mockTotal;
    }


    public int getMockCorrect() {
        return mockCorrect;
    }


    public int getMockWrong() {
        return mockWrong;
    }


    public int getMockSkipped() {
        return mockSkipped;
    }


    public double getMockSuccessRate() {
        return mockSuccessRate;
    }


    public int getActivityCount() {
        return activityCount;
    }
}