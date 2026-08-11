package com.example.questionbank_practiceexam.data.local.entity;

public class Year {

    private final int id;
    private final int universityId;
    private final String yearTitle;

    public Year(int id, int universityId, String yearTitle) {
        this.id = id;
        this.universityId = universityId;
        this.yearTitle = yearTitle;
    }

    public int getId() {
        return id;
    }

    public int getUniversityId() {
        return universityId;
    }

    public String getYearTitle() {
        return yearTitle;
    }
}
