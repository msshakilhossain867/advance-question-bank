package com.example.questionbank_practiceexam.data.local.entity;

public class DailyMission {

    private int id;

    private String title;

    private String description;

    private String icon;

    private int current;

    private int target;

    private boolean completed;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public DailyMission(
            int id,
            String title,
            String description,
            String icon,
            int current,
            int target
    ) {

        this.id = id;

        this.title = title;

        this.description = description;

        this.icon = icon;

        this.current = current;

        this.target = target;

        this.completed =
                current >= target;
    }


    // ============================================================
    // GETTERS
    // ============================================================

    public int getId() {

        return id;
    }


    public String getTitle() {

        return title;
    }


    public String getDescription() {

        return description;
    }


    public String getIcon() {

        return icon;
    }


    public int getCurrent() {

        return current;
    }


    public int getTarget() {

        return target;
    }


    public boolean isCompleted() {

        return completed;
    }


    // ============================================================
    // PROGRESS
    // ============================================================

    public int getProgress() {

        if (target <= 0) {

            return 0;
        }

        return Math.min(
                100,
                (current * 100) / target
        );
    }
}