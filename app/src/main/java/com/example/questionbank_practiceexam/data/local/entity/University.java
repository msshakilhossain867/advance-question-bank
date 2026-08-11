package com.example.questionbank_practiceexam.data.local.entity;

public class University {

    private int id;
    private String name;
    private String fullName;
    private String logoUrl;
    private int isTarget;

    public University(int id, String name, String fullName,
                      String logoUrl, int isTarget) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.logoUrl = logoUrl;
        this.isTarget = isTarget;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public int getIsTarget() {
        return isTarget;
    }
}