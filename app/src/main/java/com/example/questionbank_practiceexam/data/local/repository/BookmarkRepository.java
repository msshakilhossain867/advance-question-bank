package com.example.questionbank_practiceexam.data.local.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class BookmarkRepository {

    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    // ============================================================
    // OFFLINE APP
    // ============================================================

    private static final String USER_ID = "";


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public BookmarkRepository(Context context) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );

        database =
                databaseHelper.openDatabase();
    }


    // ============================================================
    // CHECK BOOKMARK
    // ============================================================

    public boolean isBookmarked(
            int questionId
    ) {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT id " +
                                    "FROM bookmarks " +
                                    "WHERE user_id = ? " +
                                    "AND question_id = ? " +
                                    "LIMIT 1",

                            new String[]{
                                    USER_ID,
                                    String.valueOf(questionId)
                            }
                    );

            return cursor != null &&
                    cursor.moveToFirst();

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
    // ADD BOOKMARK
    // ============================================================

    public boolean addBookmark(
            int questionId
    ) {

        try {

            // Already bookmarked
            if (isBookmarked(questionId)) {

                return true;
            }


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
                    "created_at",
                    System.currentTimeMillis()
            );


            long result =
                    database.insert(
                            "bookmarks",
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
    // REMOVE BOOKMARK
    // ============================================================

    public boolean removeBookmark(
            int questionId
    ) {

        try {

            int deleted =
                    database.delete(
                            "bookmarks",

                            "user_id = ? " +
                                    "AND question_id = ?",

                            new String[]{
                                    USER_ID,
                                    String.valueOf(questionId)
                            }
                    );


            return deleted > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    // ============================================================
    // TOGGLE BOOKMARK
    // ============================================================

    public boolean toggleBookmark(
            int questionId
    ) {

        if (isBookmarked(questionId)) {

            removeBookmark(questionId);

            return false;
        }


        return addBookmark(questionId);
    }


    // ============================================================
    // GET BOOKMARK QUESTION IDS
    // ============================================================

    public List<Integer>
    getBookmarkedQuestionIds() {

        List<Integer> ids =
                new ArrayList<>();


        Cursor cursor = null;


        try {

            cursor =
                    database.rawQuery(

                            "SELECT question_id " +
                                    "FROM bookmarks " +
                                    "WHERE user_id = ? " +
                                    "ORDER BY created_at DESC",

                            new String[]{
                                    USER_ID
                            }
                    );


            while (
                    cursor != null &&
                            cursor.moveToNext()
            ) {

                ids.add(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "question_id"
                                )
                        )
                );
            }


        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null) {

                cursor.close();
            }
        }


        return ids;
    }


    // ============================================================
    // BOOKMARK COUNT
    // ============================================================

    public int getBookmarkCount() {

        Cursor cursor = null;


        try {

            cursor =
                    database.rawQuery(
                            "SELECT COUNT(*) " +
                                    "FROM bookmarks " +
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
// CLEAR ALL BOOKMARKS
// ============================================================

    public boolean clearAllBookmarks() {

        try {

            int deleted = database.delete(
                    "bookmarks",
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

