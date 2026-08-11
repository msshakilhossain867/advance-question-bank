package com.example.questionbank_practiceexam.data.local.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;
import com.example.questionbank_practiceexam.data.local.entity.Unit;

import java.util.ArrayList;
import java.util.List;

public class UnitRepository {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public UnitRepository(Context context) {
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
    // GET UNITS BY YEAR
    // ============================================================

    public List<Unit> getUnitsByYear(int yearId) {

        open();

        List<Unit> units = new ArrayList<>();

        Cursor cursor = db.query(
                "units",
                new String[]{
                        "id",
                        "year_id",
                        "unit_name"
                },
                "year_id = ?",
                new String[]{
                        String.valueOf(yearId)
                },
                null,
                null,
                "id ASC"
        );

        try {

            if (cursor != null && cursor.moveToFirst()) {

                do {

                    units.add(
                            new Unit(
                                    cursor.getInt(
                                            cursor.getColumnIndexOrThrow("id")
                                    ),
                                    cursor.getInt(
                                            cursor.getColumnIndexOrThrow("year_id")
                                    ),
                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow("unit_name")
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

        return units;
    }

    // ============================================================
    // GET UNIT BY ID
    // ============================================================

    public Unit getUnitById(int unitId) {

        open();

        Unit unit = null;

        Cursor cursor = db.query(
                "units",
                new String[]{
                        "id",
                        "year_id",
                        "unit_name"
                },
                "id = ?",
                new String[]{
                        String.valueOf(unitId)
                },
                null,
                null,
                null,
                "1"
        );

        try {

            if (cursor != null && cursor.moveToFirst()) {

                unit = new Unit(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        ),
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("year_id")
                        ),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("unit_name")
                        )
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return unit;
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