package com.example.questionbank_practiceexam.data.local.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;

public class StudyTimeRepository {

    // ============================================================
    // DATABASE
    // ============================================================

    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private static final String USER_ID = "";


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public StudyTimeRepository(Context context) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );

        database =
                databaseHelper.openDatabase();
    }


    // ============================================================
    // SAVE STUDY SESSION
    // ============================================================

    public long saveStudySession(
            String sessionType,
            int referenceId,
            long durationSeconds,
            long startedAt,
            long completedAt
    ) {

        try {

            if (durationSeconds <= 0) {
                return -1;
            }

            ContentValues values =
                    new ContentValues();

            values.put(
                    "user_id",
                    USER_ID
            );

            values.put(
                    "session_type",
                    sessionType == null
                            ? "UNKNOWN"
                            : sessionType
            );

            values.put(
                    "reference_id",
                    referenceId
            );

            values.put(
                    "duration_seconds",
                    durationSeconds
            );

            values.put(
                    "started_at",
                    startedAt
            );

            values.put(
                    "completed_at",
                    completedAt
            );

            return database.insert(
                    "study_sessions",
                    null,
                    values
            );

        } catch (Exception e) {

            e.printStackTrace();

            return -1;
        }
    }


    // ============================================================
    // TOTAL STUDY SECONDS
    // ============================================================

    public long getTotalStudySeconds() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT COALESCE(" +
                                    "SUM(duration_seconds), 0" +
                                    ") " +
                                    "FROM study_sessions " +
                                    "WHERE user_id = ?",

                            new String[]{
                                    USER_ID
                            }
                    );

            if (cursor.moveToFirst()) {

                return cursor.getLong(0);
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return 0;
    }


    // ============================================================
    // TODAY STUDY SECONDS
    // ============================================================

    public long getTodayStudySeconds() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT COALESCE(" +
                                    "SUM(duration_seconds), 0" +
                                    ") " +
                                    "FROM study_sessions " +
                                    "WHERE user_id = ? " +
                                    "AND date(" +
                                    "completed_at / 1000," +
                                    "'unixepoch'," +
                                    "'localtime'" +
                                    ") = date('now','localtime')",

                            new String[]{
                                    USER_ID
                            }
                    );

            if (cursor.moveToFirst()) {

                return cursor.getLong(0);
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return 0;
    }


    // ============================================================
    // TOTAL STUDY SESSIONS
    // ============================================================

    public int getTotalStudySessions() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT COUNT(id) " +
                                    "FROM study_sessions " +
                                    "WHERE user_id = ?",

                            new String[]{
                                    USER_ID
                            }
                    );

            if (cursor.moveToFirst()) {

                return cursor.getInt(0);
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return 0;
    }


    // ============================================================
    // CLEAR STUDY HISTORY
    // ============================================================

    public boolean clearAllStudyTime() {

        try {

            int deleted =
                    database.delete(
                            "study_sessions",
                            "user_id = ?",
                            new String[]{
                                    USER_ID
                            }
                    );

            return deleted > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    // ============================================================
    // CLOSE
    // ============================================================

    public void close() {

        if (databaseHelper != null) {

            databaseHelper.close();
        }

        database = null;
    }
}