package com.example.questionbank_practiceexam.data.local.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;
import com.example.questionbank_practiceexam.data.local.entity.University;

import java.util.ArrayList;
import java.util.List;

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







    // ============================================================
    // GET TARGET UNIVERSITY
    // ============================================================

    public University getTargetUniversity() {

        open();

        University university = null;

        Cursor cursor = db.query(
                "universities",
                new String[]{
                        "id",
                        "name",
                        "full_name",
                        "logo_url",
                        "is_target"
                },
                "is_target = ?",
                new String[]{"1"},
                null,
                null,
                "id ASC",
                "1"
        );

        try {

            if (cursor != null && cursor.moveToFirst()) {

                university = new University(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("logo_url")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("is_target"))
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return university;
    }

    // ============================================================
    // GET UNIVERSITY BY ID
    // ============================================================

    public University getUniversityById(int universityId) {

        open();

        University university = null;

        Cursor cursor = db.query(
                "universities",
                new String[]{
                        "id",
                        "name",
                        "full_name",
                        "logo_url",
                        "is_target"
                },
                "id = ?",
                new String[]{String.valueOf(universityId)},
                null,
                null,
                null,
                "1"
        );

        try {

            if (cursor != null && cursor.moveToFirst()) {

                university = new University(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("logo_url")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("is_target"))
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return university;
    }

    // ============================================================
    // GET ALL UNIVERSITIES
    // ভবিষ্যতে প্রয়োজন হলে ব্যবহার করব
    // ============================================================

    public List<University> getAllUniversities() {

        open();

        List<University> universities = new ArrayList<>();

        Cursor cursor = db.query(
                "universities",
                new String[]{
                        "id",
                        "name",
                        "full_name",
                        "logo_url",
                        "is_target"
                },
                null,
                null,
                null,
                null,
                "id ASC"
        );

        try {

            while (cursor != null && cursor.moveToNext()) {

                universities.add(
                        new University(
                                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("logo_url")),
                                cursor.getInt(cursor.getColumnIndexOrThrow("is_target"))
                        )
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return universities;
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
