package com.example.questionbank_practiceexam.data.local.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;
import com.example.questionbank_practiceexam.data.local.entity.Subject;

import java.util.ArrayList;
import java.util.List;

public class SubjectRepository {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public SubjectRepository(Context context) {
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
    // GET SUBJECTS BY UNIT
    // ============================================================

    public List<Subject> getSubjectsByUnit(int unitId) {

        open();

        List<Subject> subjects = new ArrayList<>();

        Cursor cursor = db.query(
                "subjects",
                new String[]{
                        "id",
                        "unit_id",
                        "name",
                        "icon_url"
                },
                "unit_id = ?",
                new String[]{
                        String.valueOf(unitId)
                },
                null,
                null,
                "id ASC"
        );

        try {

            if (cursor != null && cursor.moveToFirst()) {

                do {

                    subjects.add(
                            new Subject(
                                    cursor.getInt(
                                            cursor.getColumnIndexOrThrow("id")
                                    ),
                                    cursor.getInt(
                                            cursor.getColumnIndexOrThrow("unit_id")
                                    ),
                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow("name")
                                    ),
                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow("icon_url")
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

        return subjects;
    }

    // ============================================================
    // GET SUBJECT BY ID
    // ============================================================

    public Subject getSubjectById(int subjectId) {

        open();

        Subject subject = null;

        Cursor cursor = db.query(
                "subjects",
                new String[]{
                        "id",
                        "unit_id",
                        "name",
                        "icon_url"
                },
                "id = ?",
                new String[]{
                        String.valueOf(subjectId)
                },
                null,
                null,
                null,
                "1"
        );

        try {

            if (cursor != null && cursor.moveToFirst()) {

                subject = new Subject(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        ),
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("unit_id")
                        ),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("name")
                        ),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("icon_url")
                        )
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return subject;
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