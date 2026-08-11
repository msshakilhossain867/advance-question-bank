package com.example.questionbank_practiceexam.data.local.entity;


public class Unit {

    private final int id;
    private final int yearId;
    private final String unitName;

    public Unit(int id, int yearId, String unitName) {
        this.id = id;
        this.yearId = yearId;
        this.unitName = unitName;
    }

    public int getId() {
        return id;
    }

    public int getYearId() {
        return yearId;
    }

    public String getUnitName() {
        return unitName;
    }
}