package com.example.questionbank_practiceexam.data.local.entity;

public class Question {

    private int id;
    private int subjectId;
    private int universityId;

    private String yearTag;
    private String questionText;
    private String questionImage;       // নতুন কলাম: প্রশ্নের ইমেজ
    private String options;
    private String optionImages;      // নতুন কলাম: অপশনগুলোর ইমেজ (JSON)
    private String correctOption;
    private String explanation;
    private String explanationImage;    // নতুন কলাম: ব্যাখ্যার ইমেজ

    // ============================================================
    // CONSTRUCTOR (New with Image fields)
    // ============================================================

    public Question(
            int id,
            int subjectId,
            int universityId,
            String yearTag,
            String questionText,
            String questionImage,
            String options,
            String optionImages,
            String correctOption,
            String explanation,
            String explanationImage
    ) {
        this.id = id;
        this.subjectId = subjectId;
        this.universityId = universityId;
        this.yearTag = yearTag;
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.options = options;
        this.optionImages = optionImages;
        this.correctOption = correctOption;
        this.explanation = explanation;
        this.explanationImage = explanationImage;
    }

    // পুরানো কোডের কম্প্যাটিবিলিটির জন্য ওভারলোডেড কনস্ট্রাক্টর (যদি প্রয়োজন হয়)
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
        this(id, subjectId, universityId, yearTag, questionText, null, options, null, correctOption, explanation, null);
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

    public String getQuestionImage() {
        return questionImage;
    }

    public String getOptions() {
        return options;
    }

    public String getOptionImages() {
        return optionImages;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getExplanationImage() {
        return explanationImage;
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

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public void setOptionImages(String optionImages) {
        this.optionImages = optionImages;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setExplanationImage(String explanationImage) {
        this.explanationImage = explanationImage;
    }
}