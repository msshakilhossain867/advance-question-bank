package com.example.questionbank_practiceexam.data.local.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PracticeResultRepository {

    // ============================================================
    // DATABASE
    // ============================================================

    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    // Offline app → user_id empty
    private static final String USER_ID = "";


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public PracticeResultRepository(Context context) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );

        database =
                databaseHelper.openDatabase();
    }


    // ============================================================
    // SAVE PRACTICE RESULT
    // ============================================================

    public long saveResult(
            int totalQuestions,
            int correctCount,
            int wrongCount,
            int skippedCount,
            double successRate,
            String subjectData
    ) {

        try {

            ContentValues values =
                    new ContentValues();

            values.put(
                    "user_id",
                    USER_ID
            );

            values.put(
                    "total_questions",
                    totalQuestions
            );

            values.put(
                    "correct_count",
                    correctCount
            );

            values.put(
                    "wrong_count",
                    wrongCount
            );

            values.put(
                    "skipped_count",
                    skippedCount
            );

            values.put(
                    "success_rate",
                    successRate
            );

            values.put(
                    "subject_data",
                    subjectData
            );

            values.put(
                    "completed_at",
                    System.currentTimeMillis()
            );

            return database.insert(
                    "practice_results",
                    null,
                    values
            );

        } catch (Exception e) {

            e.printStackTrace();

            return -1;
        }
    }


    // ============================================================
    // SAVE RESULT - AUTO SUCCESS RATE
    // ============================================================

    public long saveResult(
            int totalQuestions,
            int correctCount,
            int wrongCount,
            int skippedCount,
            String subjectData
    ) {

        double successRate = 0;

        if (totalQuestions > 0) {

            successRate =
                    (correctCount * 100.0)
                            / totalQuestions;
        }

        return saveResult(
                totalQuestions,
                correctCount,
                wrongCount,
                skippedCount,
                successRate,
                subjectData
        );
    }


    // ============================================================
    // GET ALL RESULTS
    // ============================================================

    public List<PracticeResult> getAllResults() {

        List<PracticeResult> resultList =
                new ArrayList<>();

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT id, user_id, " +
                                    "total_questions, " +
                                    "correct_count, " +
                                    "wrong_count, " +
                                    "skipped_count, " +
                                    "success_rate, " +
                                    "subject_data, " +
                                    "completed_at " +
                                    "FROM practice_results " +
                                    "WHERE user_id = ? " +
                                    "ORDER BY completed_at DESC",

                            new String[]{
                                    USER_ID
                            }
                    );

            while (cursor.moveToNext()) {

                resultList.add(
                        createResultFromCursor(
                                cursor
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

        return resultList;
    }


    // ============================================================
    // GET LATEST RESULT
    // ============================================================

    public PracticeResult getLatestResult() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT id, user_id, " +
                                    "total_questions, " +
                                    "correct_count, " +
                                    "wrong_count, " +
                                    "skipped_count, " +
                                    "success_rate, " +
                                    "subject_data, " +
                                    "completed_at " +
                                    "FROM practice_results " +
                                    "WHERE user_id = ? " +
                                    "ORDER BY completed_at DESC " +
                                    "LIMIT 1",

                            new String[]{
                                    USER_ID
                            }
                    );

            if (cursor.moveToFirst()) {

                return createResultFromCursor(
                        cursor
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
    // GET RESULT BY ID
    // ============================================================

    public PracticeResult getResultById(
            int resultId
    ) {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT id, user_id, " +
                                    "total_questions, " +
                                    "correct_count, " +
                                    "wrong_count, " +
                                    "skipped_count, " +
                                    "success_rate, " +
                                    "subject_data, " +
                                    "completed_at " +
                                    "FROM practice_results " +
                                    "WHERE id = ? " +
                                    "AND user_id = ? " +
                                    "LIMIT 1",

                            new String[]{
                                    String.valueOf(resultId),
                                    USER_ID
                            }
                    );

            if (cursor.moveToFirst()) {

                return createResultFromCursor(
                        cursor
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
    // TODAY PRACTICE COUNT
    // ============================================================

    public int getTodayPracticeCount() {

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
                            "SELECT COUNT(id) " +
                                    "FROM practice_results " +
                                    "WHERE user_id = ? " +
                                    "AND date(completed_at / 1000, 'unixepoch', 'localtime') = ?",

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
    // TOTAL PRACTICE COUNT
    // ============================================================

    public int getTotalPracticeCount() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT COUNT(id) " +
                                    "FROM practice_results " +
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
    // TOTAL QUESTIONS PRACTICED
    // ============================================================

    public int getTotalQuestionsPracticed() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT COALESCE(SUM(total_questions), 0) " +
                                    "FROM practice_results " +
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
    // TOTAL CORRECT
    // ============================================================

    public int getTotalCorrect() {

        return getTotalValue(
                "correct_count"
        );
    }


    // ============================================================
    // TOTAL WRONG
    // ============================================================

    public int getTotalWrong() {

        return getTotalValue(
                "wrong_count"
        );
    }


    // ============================================================
    // TOTAL SKIPPED
    // ============================================================

    public int getTotalSkipped() {

        return getTotalValue(
                "skipped_count"
        );
    }


    // ============================================================
    // INTERNAL TOTAL VALUE
    // ============================================================

    private int getTotalValue(
            String column
    ) {

        Cursor cursor = null;

        try {

            String sql =
                    "SELECT COALESCE(SUM("
                            + column +
                            "), 0) " +
                            "FROM practice_results " +
                            "WHERE user_id = ?";

            cursor =
                    database.rawQuery(
                            sql,
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
    // OVERALL SUCCESS RATE
    // ============================================================

    public double getOverallSuccessRate() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT " +
                                    "COALESCE(SUM(correct_count), 0), " +
                                    "COALESCE(SUM(total_questions), 0) " +
                                    "FROM practice_results " +
                                    "WHERE user_id = ?",

                            new String[]{
                                    USER_ID
                            }
                    );

            if (cursor.moveToFirst()) {

                int correct =
                        cursor.getInt(0);

                int total =
                        cursor.getInt(1);

                if (total > 0) {

                    return
                            (correct * 100.0)
                                    / total;
                }
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
    // TODAY QUESTIONS
    // ============================================================

    public int getTodayQuestions() {

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
                            "SELECT COALESCE(SUM(total_questions), 0) " +
                                    "FROM practice_results " +
                                    "WHERE user_id = ? " +
                                    "AND date(completed_at / 1000, 'unixepoch', 'localtime') = ?",

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
    // DELETE SINGLE RESULT
    // ============================================================

    public boolean deleteResult(
            int resultId
    ) {

        try {

            int deleted =
                    database.delete(
                            "practice_results",

                            "id = ? AND user_id = ?",

                            new String[]{
                                    String.valueOf(resultId),
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
    // CLEAR ALL PRACTICE RESULTS
    // ============================================================

    public boolean clearAllResults() {

        try {

            int deleted =
                    database.delete(
                            "practice_results",
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
    // CURSOR → MODEL
    // ============================================================

    private PracticeResult createResultFromCursor(
            Cursor cursor
    ) {

        return new PracticeResult(

                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "id"
                        )
                ),

                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "user_id"
                        )
                ),

                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "total_questions"
                        )
                ),

                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "correct_count"
                        )
                ),

                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "wrong_count"
                        )
                ),

                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "skipped_count"
                        )
                ),

                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                "success_rate"
                        )
                ),

                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "subject_data"
                        )
                ),

                cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                                "completed_at"
                        )
                )
        );
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

    public static class PracticeResult {

        private final int id;
        private final String userId;
        private final int totalQuestions;
        private final int correctCount;
        private final int wrongCount;
        private final int skippedCount;
        private final double successRate;
        private final String subjectData;
        private final long completedAt;


        public PracticeResult(
                int id,
                String userId,
                int totalQuestions,
                int correctCount,
                int wrongCount,
                int skippedCount,
                double successRate,
                String subjectData,
                long completedAt
        ) {

            this.id = id;
            this.userId = userId;
            this.totalQuestions = totalQuestions;
            this.correctCount = correctCount;
            this.wrongCount = wrongCount;
            this.skippedCount = skippedCount;
            this.successRate = successRate;
            this.subjectData = subjectData;
            this.completedAt = completedAt;
        }


        public int getId() {
            return id;
        }


        public String getUserId() {
            return userId;
        }


        public int getTotalQuestions() {
            return totalQuestions;
        }


        public int getCorrectCount() {
            return correctCount;
        }


        public int getWrongCount() {
            return wrongCount;
        }


        public int getSkippedCount() {
            return skippedCount;
        }


        public double getSuccessRate() {
            return successRate;
        }


        public String getSubjectData() {
            return subjectData;
        }


        public long getCompletedAt() {
            return completedAt;
        }
    }
}