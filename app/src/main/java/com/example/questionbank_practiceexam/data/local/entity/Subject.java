package com.example.questionbank_practiceexam.data.local.entity;

public class Subject {

    private final int id;
    private final int unitId;
    private final String name;
    private final String iconUrl;
     boolean mandatory;
     int choiceGroup;


    public Subject(
            int id,
            int unitId,
            String name,
            String iconUrl,
            boolean mandatory,
            int choiceGroup
    ) {
        this.id = id;
        this.unitId = unitId;
        this.name = name;
        this.iconUrl = iconUrl;

        this.mandatory = mandatory;
        this.choiceGroup = choiceGroup;
    }



    public Subject(
            int id,
            int unitId,
            String name,
            String iconUrl
    ) {
        this.id = id;
        this.unitId = unitId;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    public int getId() {
        return id;
    }

    public int getUnitId() {
        return unitId;
    }

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public int getChoiceGroup() {
        return choiceGroup;
    }
}