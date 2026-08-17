package com.example.questionbank_practiceexam.data.local.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;

public class RecentLearningRepository {

    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private static final String USER_ID = "";

    public RecentLearningRepository(Context context) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );

        database =
                databaseHelper.openDatabase();
    }


    // ============================================================
    // SAVE / UPDATE RECENT LEARNING
    // ============================================================

    public long saveRecentLearning(
            int subjectId,
            String subjectName,
            int chapterId,
            String chapterName,
            int contentPosition,
            int progressPercent
    ) {

        try {

            ContentValues values =
                    new ContentValues();

            values.put("user_id", USER_ID);
            values.put("subject_id", subjectId);
            values.put(
                    "subject_name",
                    subjectName == null ? "" : subjectName
            );
            values.put("chapter_id", chapterId);
            values.put(
                    "chapter_name",
                    chapterName == null ? "" : chapterName
            );
            values.put(
                    "content_position",
                    contentPosition
            );
            values.put(
                    "progress_percent",
                    progressPercent
            );
            values.put(
                    "updated_at",
                    System.currentTimeMillis()
            );


            // Existing record আছে কিনা
            Cursor cursor = null;

            try {

                cursor = database.rawQuery(
                        "SELECT id " +
                                "FROM recent_learning " +
                                "WHERE user_id = ? " +
                                "LIMIT 1",

                        new String[]{
                                USER_ID
                        }
                );

                if (cursor.moveToFirst()) {

                    int id =
                            cursor.getInt(0);

                    int updated =
                            database.update(
                                    "recent_learning",
                                    values,
                                    "id = ?",
                                    new String[]{
                                            String.valueOf(id)
                                    }
                            );

                    return updated > 0 ? id : -1;
                }

            } finally {

                if (cursor != null) {
                    cursor.close();
                }
            }


            // প্রথমবার insert
            return database.insert(
                    "recent_learning",
                    null,
                    values
            );

        } catch (Exception e) {

            e.printStackTrace();

            return -1;
        }
    }


    // ============================================================
    // GET RECENT LEARNING
    // ============================================================

    public RecentLearning getRecentLearning() {

        Cursor cursor = null;

        try {

            cursor = database.rawQuery(
                    "SELECT " +
                            "id, " +
                            "user_id, " +
                            "subject_id, " +
                            "subject_name, " +
                            "chapter_id, " +
                            "chapter_name, " +
                            "content_position, " +
                            "progress_percent, " +
                            "updated_at " +
                            "FROM recent_learning " +
                            "WHERE user_id = ? " +
                            "ORDER BY updated_at DESC " +
                            "LIMIT 1",

                    new String[]{
                            USER_ID
                    }
            );

            if (cursor.moveToFirst()) {

                return new RecentLearning(

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "subject_id"
                                )
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "subject_name"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "chapter_id"
                                )
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "chapter_name"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "content_position"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "progress_percent"
                                )
                        ),

                        cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        "updated_at"
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

        return null;
    }


    // ============================================================
    // CLEAR
    // ============================================================

    public boolean clearRecentLearning() {

        try {

            int deleted =
                    database.delete(
                            "recent_learning",
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


    // ============================================================
    // MODEL
    // ============================================================

    public static class RecentLearning {

        private final int id;
        private final int subjectId;
        private final String subjectName;
        private final int chapterId;
        private final String chapterName;
        private final int contentPosition;
        private final int progressPercent;
        private final long updatedAt;


        public RecentLearning(
                int id,
                int subjectId,
                String subjectName,
                int chapterId,
                String chapterName,
                int contentPosition,
                int progressPercent,
                long updatedAt
        ) {

            this.id = id;
            this.subjectId = subjectId;
            this.subjectName = subjectName;
            this.chapterId = chapterId;
            this.chapterName = chapterName;
            this.contentPosition = contentPosition;
            this.progressPercent = progressPercent;
            this.updatedAt = updatedAt;
        }


        public int getId() {
            return id;
        }


        public int getSubjectId() {
            return subjectId;
        }


        public String getSubjectName() {
            return subjectName;
        }


        public int getChapterId() {
            return chapterId;
        }


        public String getChapterName() {
            return chapterName;
        }


        public int getContentPosition() {
            return contentPosition;
        }


        public int getProgressPercent() {
            return progressPercent;
        }


        public long getUpdatedAt() {
            return updatedAt;
        }
    }
}