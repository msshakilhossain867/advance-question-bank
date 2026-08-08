package com.example.questionbank_practiceexam.data.local.entity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;

public class UniversityRepository {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public UniversityRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // ============================================================
    // OPEN DATABASE
    // ============================================================

    public void open() {
        if (db == null || !db.isOpen()) {
            db = dbHelper.openDatabase();
        }
    }


    // ============================================================
    // CLOSE DATABASE
    // ============================================================

    public void close() {
        if (db != null && db.isOpen()) {
            dbHelper.close();
            db = null;
        }
    }
}
