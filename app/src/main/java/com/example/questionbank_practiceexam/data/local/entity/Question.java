package com.example.questionbank_practiceexam.data.local.entity;

public class Question {

    private int id;
    private int subjectId;
    private int universityId;

    private String yearTag;
    private String questionText;
    private String options;
    private String correctOption;
    private String explanation;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public Question(
            int id,
            int subjectId,
            int universityId,
            String yearTag,
            String questionText,
            String options,
            String correctOption,
            String explanation
    ) {

        this.id = id;
        this.subjectId = subjectId;
        this.universityId = universityId;
        this.yearTag = yearTag;
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
        this.explanation = explanation;
    }

    // ============================================================
    // GETTERS
    // ============================================================

    public int getId() {
        return id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public int getUniversityId() {
        return universityId;
    }

    public String getYearTag() {
        return yearTag;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOptions() {
        return options;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public String getExplanation() {
        return explanation;
    }

    // ============================================================
    // SETTERS
    // ============================================================

    public void setId(int id) {
        this.id = id;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    public void setYearTag(String yearTag) {
        this.yearTag = yearTag;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}