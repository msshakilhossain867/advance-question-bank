package com.example.questionbank_practiceexam.data.local.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;
import com.example.questionbank_practiceexam.data.local.entity.Year;

import java.util.ArrayList;
import java.util.List;

public class YearRepository {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public YearRepository(Context context) {
        dbHelper = new DatabaseHelper(
                context.getApplicationContext()
        );
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
    // GET YEARS BY UNIVERSITY
    // ============================================================

    public List<Year> getYearsByUniversity(int universityId) {

        open();

        List<Year> years = new ArrayList<>();

        Cursor cursor = db.query(
                "years",
                new String[]{
                        "id",
                        "university_id",
                        "year_title"
                },
                "university_id = ?",
                new String[]{
                        String.valueOf(universityId)
                },
                null,
                null,
                "id ASC"
        );

        try {

            if (cursor != null && cursor.moveToFirst()) {

                do {

                    years.add(
                            new Year(
                                    cursor.getInt(
                                            cursor.getColumnIndexOrThrow("id")
                                    ),
                                    cursor.getInt(
                                            cursor.getColumnIndexOrThrow("university_id")
                                    ),
                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow("year_title")
                                    )
                            )
                    );

                } while (cursor.moveToNext());
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return years;
    }

    // ============================================================
    // GET YEAR BY ID
    // ============================================================

    public Year getYearById(int yearId) {

        open();

        Year year = null;

        Cursor cursor = db.query(
                "years",
                new String[]{
                        "id",
                        "university_id",
                        "year_title"
                },
                "id = ?",
                new String[]{
                        String.valueOf(yearId)
                },
                null,
                null,
                null,
                "1"
        );

        try {

            if (cursor != null && cursor.moveToFirst()) {

                year = new Year(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        ),
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("university_id")
                        ),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("year_title")
                        )
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return year;
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
