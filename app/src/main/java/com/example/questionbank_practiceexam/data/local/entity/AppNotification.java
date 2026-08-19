package com.example.questionbank_practiceexam.data.local.entity;

public class AppNotification {

    private final int id;
    private final String title;
    private final String message;
    private final long time;

    public AppNotification(
            int id,
            String title,
            String message,
            long time
    ) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }
}