package com.example.questionbank_practiceexam.data.local.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReadingProgressRepository {

    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    // ============================================================
    // OFFLINE USER
    // ============================================================

    private static final String USER_ID = "";


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ReadingProgressRepository(Context context) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );

        database =
                databaseHelper.openDatabase();
    }


    // ============================================================
    // MARK QUESTION AS READ
    // ============================================================

    public boolean markQuestionAsRead(
            int questionId
    ) {

        try {

            // ----------------------------------------------------
            // CHECK ALREADY READ
            // ----------------------------------------------------

            if (isQuestionRead(questionId)) {

                return true;
            }


            // ----------------------------------------------------
            // CURRENT TIME
            // ----------------------------------------------------

            long currentTime =
                    System.currentTimeMillis();


            String readDate =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).format(
                            new Date(currentTime)
                    );


            // ----------------------------------------------------
            // INSERT
            // ----------------------------------------------------

            ContentValues values =
                    new ContentValues();

            values.put(
                    "user_id",
                    USER_ID
            );

            values.put(
                    "question_id",
                    questionId
            );

            values.put(
                    "read_date",
                    readDate
            );

            values.put(
                    "read_at",
                    currentTime
            );


            long result =
                    database.insert(
                            "reading_progress",
                            null,
                            values
                    );


            return result != -1;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    // ============================================================
    // CHECK QUESTION READ
    // ============================================================

    public boolean isQuestionRead(
            int questionId
    ) {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(

                            "SELECT id " +
                                    "FROM reading_progress " +
                                    "WHERE user_id = ? " +
                                    "AND question_id = ? " +
                                    "LIMIT 1",

                            new String[]{
                                    USER_ID,
                                    String.valueOf(questionId)
                            }
                    );


            return cursor.moveToFirst();

        } catch (Exception e) {

            e.printStackTrace();

            return false;

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }


    // ============================================================
    // TOTAL READ COUNT
    // ============================================================

    public int getTotalReadCount() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(

                            "SELECT COUNT(*) " +
                                    "FROM reading_progress " +
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
    // TODAY READ COUNT
    // ============================================================

    public int getTodayReadCount() {

        Cursor cursor = null;

        try {

            String today =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).format(
                            new Date()
                    );


            cursor =
                    database.rawQuery(

                            "SELECT COUNT(*) " +
                                    "FROM reading_progress " +
                                    "WHERE user_id = ? " +
                                    "AND read_date = ?",

                            new String[]{
                                    USER_ID,
                                    today
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
    // GET READ COUNT BY DATE
    // ============================================================

    public int getReadCountByDate(
            String date
    ) {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(

                            "SELECT COUNT(*) " +
                                    "FROM reading_progress " +
                                    "WHERE user_id = ? " +
                                    "AND read_date = ?",

                            new String[]{
                                    USER_ID,
                                    date
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
    // CLOSE
    // ============================================================

    public void close() {

        if (databaseHelper != null) {

            databaseHelper.close();
        }

        database = null;
    }
}