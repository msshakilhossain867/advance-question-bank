package com.example.questionbank_practiceexam.data.local.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MockTestResultRepository {

    // ============================================================
    // DATABASE
    // ============================================================

    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    // Offline app
    private static final String USER_ID = "";


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public MockTestResultRepository(Context context) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );

        database =
                databaseHelper.openDatabase();
    }


    // ============================================================
    // SAVE MOCK TEST RESULT
    // ============================================================

    public long saveResult(
            int unitId,
            int yearId,
            String unitName,
            String yearTitle,
            int totalQuestions,
            int correctCount,
            int wrongCount,
            int skippedCount,
            int attemptedCount,
            double totalMark,
            double negativeMark,
            double successRate,
            String subjectIds
    ) {

        try {

            ContentValues values =
                    new ContentValues();

            values.put(
                    "user_id",
                    USER_ID
            );

            values.put(
                    "unit_id",
                    unitId
            );

            values.put(
                    "year_id",
                    yearId
            );

            values.put(
                    "unit_name",
                    unitName == null
                            ? ""
                            : unitName
            );

            values.put(
                    "year_title",
                    yearTitle == null
                            ? ""
                            : yearTitle
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
                    "attempted_count",
                    attemptedCount
            );

            values.put(
                    "total_mark",
                    totalMark
            );

            values.put(
                    "negative_mark",
                    negativeMark
            );

            values.put(
                    "success_rate",
                    successRate
            );

            values.put(
                    "subject_ids",
                    subjectIds
            );

            values.put(
                    "completed_at",
                    System.currentTimeMillis()
            );

            return database.insert(
                    "mock_test_results",
                    null,
                    values
            );

        } catch (Exception e) {

            e.printStackTrace();

            return -1;
        }
    }


    // ============================================================
    // SAVE RESULT
    // AUTO ATTEMPTED + SUCCESS RATE
    // ============================================================

    public long saveResult(
            int unitId,
            int yearId,
            String unitName,
            String yearTitle,
            int totalQuestions,
            int correctCount,
            int wrongCount,
            int skippedCount,
            double totalMark,
            double negativeMark,
            String subjectIds
    ) {

        int attemptedCount =
                correctCount + wrongCount;

        double successRate = 0;

        if (totalQuestions > 0) {

            successRate =
                    (correctCount * 100.0)
                            / totalQuestions;
        }

        return saveResult(
                unitId,
                yearId,
                unitName,
                yearTitle,
                totalQuestions,
                correctCount,
                wrongCount,
                skippedCount,
                attemptedCount,
                totalMark,
                negativeMark,
                successRate,
                subjectIds
        );
    }


    // ============================================================
    // GET ALL MOCK TEST RESULTS
    // ============================================================

    public List<MockTestResult> getAllResults() {

        List<MockTestResult> resultList =
                new ArrayList<>();

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT " +
                                    "id, " +
                                    "user_id, " +
                                    "unit_id, " +
                                    "year_id, " +
                                    "unit_name, " +
                                    "year_title, " +
                                    "total_questions, " +
                                    "correct_count, " +
                                    "wrong_count, " +
                                    "skipped_count, " +
                                    "attempted_count, " +
                                    "total_mark, " +
                                    "negative_mark, " +
                                    "success_rate, " +
                                    "subject_ids, " +
                                    "completed_at " +
                                    "FROM mock_test_results " +
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

    public MockTestResult getLatestResult() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT " +
                                    "id, " +
                                    "user_id, " +
                                    "unit_id, " +
                                    "year_id, " +
                                    "unit_name, " +
                                    "year_title, " +
                                    "total_questions, " +
                                    "correct_count, " +
                                    "wrong_count, " +
                                    "skipped_count, " +
                                    "attempted_count, " +
                                    "total_mark, " +
                                    "negative_mark, " +
                                    "success_rate, " +
                                    "subject_ids, " +
                                    "completed_at " +
                                    "FROM mock_test_results " +
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

    public MockTestResult getResultById(
            int resultId
    ) {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT " +
                                    "id, " +
                                    "user_id, " +
                                    "unit_id, " +
                                    "year_id, " +
                                    "unit_name, " +
                                    "year_title, " +
                                    "total_questions, " +
                                    "correct_count, " +
                                    "wrong_count, " +
                                    "skipped_count, " +
                                    "attempted_count, " +
                                    "total_mark, " +
                                    "negative_mark, " +
                                    "success_rate, " +
                                    "subject_ids, " +
                                    "completed_at " +
                                    "FROM mock_test_results " +
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
    // TOTAL MOCK TEST COUNT
    // ============================================================

    public int getTotalMockTestCount() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT COUNT(id) " +
                                    "FROM mock_test_results " +
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
    // TODAY MOCK TEST COUNT
    // ============================================================

    public int getTodayMockTestCount() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT COUNT(id) " +
                                    "FROM mock_test_results " +
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
    // TOTAL QUESTIONS
    // ============================================================

    public int getTotalQuestions() {

        return getTotalInt(
                "total_questions"
        );
    }


    // ============================================================
    // TOTAL CORRECT
    // ============================================================

    public int getTotalCorrect() {

        return getTotalInt(
                "correct_count"
        );
    }


    // ============================================================
    // TOTAL WRONG
    // ============================================================

    public int getTotalWrong() {

        return getTotalInt(
                "wrong_count"
        );
    }


    // ============================================================
    // TOTAL SKIPPED
    // ============================================================

    public int getTotalSkipped() {

        return getTotalInt(
                "skipped_count"
        );
    }


    // ============================================================
    // TOTAL ATTEMPTED
    // ============================================================

    public int getTotalAttempted() {

        return getTotalInt(
                "attempted_count"
        );
    }


    // ============================================================
    // TOTAL MARK
    // ============================================================

    public double getTotalMark() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT COALESCE(SUM(total_mark),0) " +
                                    "FROM mock_test_results " +
                                    "WHERE user_id = ?",

                            new String[]{
                                    USER_ID
                            }
                    );

            if (cursor.moveToFirst()) {

                return cursor.getDouble(0);
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
    // TOTAL NEGATIVE MARK
    // ============================================================

    public double getTotalNegativeMark() {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(
                            "SELECT COALESCE(SUM(negative_mark),0) " +
                                    "FROM mock_test_results " +
                                    "WHERE user_id = ?",

                            new String[]{
                                    USER_ID
                            }
                    );

            if (cursor.moveToFirst()) {

                return cursor.getDouble(0);
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
                                    "COALESCE(SUM(correct_count),0), " +
                                    "COALESCE(SUM(total_questions),0) " +
                                    "FROM mock_test_results " +
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

            cursor =
                    database.rawQuery(
                            "SELECT COALESCE(SUM(total_questions),0) " +
                                    "FROM mock_test_results " +
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
    // INTERNAL TOTAL INTEGER
    // ============================================================

    private int getTotalInt(
            String column
    ) {

        Cursor cursor = null;

        try {

            String sql =
                    "SELECT COALESCE(SUM("
                            + column +
                            "),0) " +
                            "FROM mock_test_results " +
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
    // DELETE ONE RESULT
    // ============================================================

    public boolean deleteResult(
            int resultId
    ) {

        try {

            int deleted =
                    database.delete(
                            "mock_test_results",

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
    // CLEAR ALL RESULTS
    // ============================================================

    public boolean clearAllResults() {

        try {

            int deleted =
                    database.delete(
                            "mock_test_results",
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

    private MockTestResult createResultFromCursor(
            Cursor cursor
    ) {

        return new MockTestResult(

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
                                "unit_id"
                        )
                ),

                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "year_id"
                        )
                ),

                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "unit_name"
                        )
                ),

                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "year_title"
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

                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "attempted_count"
                        )
                ),

                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                "total_mark"
                        )
                ),

                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                "negative_mark"
                        )
                ),

                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                "success_rate"
                        )
                ),

                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "subject_ids"
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

    public static class MockTestResult {

        private final int id;
        private final String userId;

        private final int unitId;
        private final int yearId;

        private final String unitName;
        private final String yearTitle;

        private final int totalQuestions;
        private final int correctCount;
        private final int wrongCount;
        private final int skippedCount;
        private final int attemptedCount;

        private final double totalMark;
        private final double negativeMark;
        private final double successRate;

        private final String subjectIds;

        private final long completedAt;


        public MockTestResult(
                int id,
                String userId,
                int unitId,
                int yearId,
                String unitName,
                String yearTitle,
                int totalQuestions,
                int correctCount,
                int wrongCount,
                int skippedCount,
                int attemptedCount,
                double totalMark,
                double negativeMark,
                double successRate,
                String subjectIds,
                long completedAt
        ) {

            this.id = id;
            this.userId = userId;

            this.unitId = unitId;
            this.yearId = yearId;

            this.unitName = unitName;
            this.yearTitle = yearTitle;

            this.totalQuestions = totalQuestions;
            this.correctCount = correctCount;
            this.wrongCount = wrongCount;
            this.skippedCount = skippedCount;
            this.attemptedCount = attemptedCount;

            this.totalMark = totalMark;
            this.negativeMark = negativeMark;
            this.successRate = successRate;

            this.subjectIds = subjectIds;

            this.completedAt = completedAt;
        }


        public int getId() {
            return id;
        }


        public String getUserId() {
            return userId;
        }


        public int getUnitId() {
            return unitId;
        }


        public int getYearId() {
            return yearId;
        }


        public String getUnitName() {
            return unitName;
        }


        public String getYearTitle() {
            return yearTitle;
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


        public int getAttemptedCount() {
            return attemptedCount;
        }


        public double getTotalMark() {
            return totalMark;
        }


        public double getNegativeMark() {
            return negativeMark;
        }


        public double getSuccessRate() {
            return successRate;
        }


        public String getSubjectIds() {
            return subjectIds;
        }


        public long getCompletedAt() {
            return completedAt;
        }
    }
}