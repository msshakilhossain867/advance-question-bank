package com.example.questionbank_practiceexam.data.local.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;
import com.example.questionbank_practiceexam.data.local.entity.SevenDayProgress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProgressDailyRepository {

    private static final String USER_ID = "";

    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ProgressDailyRepository(Context context) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );

        database =
                databaseHelper.openDatabase();
    }


    // ============================================================
    // TODAY DATE
    // ============================================================

    private String getTodayDate() {

        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(
                new Date()
        );
    }


    // ============================================================
    // SYNC TODAY PROGRESS
    // ============================================================

    public void syncTodayProgress() {

        if (database == null ||
                !database.isOpen()) {

            database =
                    databaseHelper.openDatabase();
        }


        String today =
                getTodayDate();


        // ========================================================
        // READING
        // ========================================================

        int readCount =
                getReadCount(today);


        // ========================================================
        // PRACTICE
        // ========================================================

        PracticeData practiceData =
                getPracticeData(today);


        // ========================================================
        // MOCK TEST
        // ========================================================

        MockData mockData =
                getMockData(today);


        // ========================================================
        // INSERT / UPDATE DAILY SUMMARY
        // ========================================================

        database.execSQL(
                "INSERT OR REPLACE INTO progress_daily (" +

                        "id, " +
                        "user_id, " +
                        "progress_date, " +

                        "read_count, " +

                        "practice_count, " +
                        "practice_total, " +
                        "practice_correct, " +
                        "practice_wrong, " +
                        "practice_skipped, " +
                        "practice_success_rate, " +

                        "mock_test_count, " +
                        "mock_total, " +
                        "mock_correct, " +
                        "mock_wrong, " +
                        "mock_skipped, " +
                        "mock_success_rate, " +

                        "updated_at" +

                        ") VALUES (" +

                        "(SELECT id FROM progress_daily " +
                        "WHERE user_id = ? " +
                        "AND progress_date = ? " +
                        "LIMIT 1), " +

                        "?, " +
                        "?, " +

                        "?, " +

                        "?, ?, ?, ?, ?, ?, " +

                        "?, ?, ?, ?, ?, ?, " +

                        "?" +

                        ")",

                new Object[]{

                        USER_ID,
                        today,

                        USER_ID,
                        today,

                        readCount,

                        practiceData.count,
                        practiceData.total,
                        practiceData.correct,
                        practiceData.wrong,
                        practiceData.skipped,
                        practiceData.successRate,

                        mockData.count,
                        mockData.total,
                        mockData.correct,
                        mockData.wrong,
                        mockData.skipped,
                        mockData.successRate,

                        System.currentTimeMillis()
                }
        );
    }


    // ============================================================
    // GET READ COUNT
    // ============================================================

    private int getReadCount(
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

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return 0;
    }


    // ============================================================
    // PRACTICE DATA
    // ============================================================

    private PracticeData getPracticeData(
            String date
    ) {

        PracticeData data =
                new PracticeData();

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(

                            "SELECT " +

                                    "COUNT(*) AS practice_count, " +

                                    "COALESCE(SUM(total_questions), 0) " +
                                    "AS total_questions, " +

                                    "COALESCE(SUM(correct_count), 0) " +
                                    "AS correct_count, " +

                                    "COALESCE(SUM(wrong_count), 0) " +
                                    "AS wrong_count, " +

                                    "COALESCE(SUM(skipped_count), 0) " +
                                    "AS skipped_count " +

                                    "FROM practice_results " +

                                    "WHERE user_id = ? " +

                                    "AND date(" +
                                    "completed_at / 1000, " +
                                    "'unixepoch', " +
                                    "'localtime'" +
                                    ") = ?",

                            new String[]{
                                    USER_ID,
                                    date
                            }
                    );


            if (cursor.moveToFirst()) {

                data.count =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "practice_count"
                                )
                        );

                data.total =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "total_questions"
                                )
                        );

                data.correct =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "correct_count"
                                )
                        );

                data.wrong =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "wrong_count"
                                )
                        );

                data.skipped =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "skipped_count"
                                )
                        );


                data.successRate =
                        calculateSuccessRate(
                                data.correct,
                                data.total
                        );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return data;
    }


    // ============================================================
    // MOCK DATA
    // ============================================================

    private MockData getMockData(
            String date
    ) {

        MockData data =
                new MockData();

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(

                            "SELECT " +

                                    "COUNT(*) AS mock_test_count, " +

                                    "COALESCE(SUM(total_questions), 0) " +
                                    "AS total_questions, " +

                                    "COALESCE(SUM(correct_count), 0) " +
                                    "AS correct_count, " +

                                    "COALESCE(SUM(wrong_count), 0) " +
                                    "AS wrong_count, " +

                                    "COALESCE(SUM(skipped_count), 0) " +
                                    "AS skipped_count " +

                                    "FROM mock_test_results " +

                                    "WHERE user_id = ? " +

                                    "AND date(" +
                                    "completed_at / 1000, " +
                                    "'unixepoch', " +
                                    "'localtime'" +
                                    ") = ?",

                            new String[]{
                                    USER_ID,
                                    date
                            }
                    );


            if (cursor.moveToFirst()) {

                data.count =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "mock_test_count"
                                )
                        );

                data.total =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "total_questions"
                                )
                        );

                data.correct =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "correct_count"
                                )
                        );

                data.wrong =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "wrong_count"
                                )
                        );

                data.skipped =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "skipped_count"
                                )
                        );


                data.successRate =
                        calculateSuccessRate(
                                data.correct,
                                data.total
                        );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return data;
    }


    // ============================================================
    // SUCCESS RATE
    // ============================================================

    private double calculateSuccessRate(
            int correct,
            int total
    ) {

        if (total <= 0) {

            return 0;
        }

        return Math.round(
                (correct * 10000.0) / total
        ) / 100.0;
    }


    // ============================================================
    // GET TODAY PROGRESS
    // ============================================================

    public ProgressDaily getTodayProgress() {

        syncTodayProgress();

        return getProgress(
                getTodayDate()
        );
    }


    // ============================================================
    // GET PROGRESS BY DATE
    // ============================================================

    public ProgressDaily getProgress(
            String date
    ) {

        Cursor cursor = null;

        try {

            cursor =
                    database.rawQuery(

                            "SELECT * " +
                                    "FROM progress_daily " +
                                    "WHERE user_id = ? " +
                                    "AND progress_date = ? " +
                                    "LIMIT 1",

                            new String[]{
                                    USER_ID,
                                    date
                            }
                    );


            if (cursor.moveToFirst()) {

                return new ProgressDaily(

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "read_count"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "practice_count"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "practice_total"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "practice_correct"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "practice_wrong"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "practice_skipped"
                                )
                        ),

                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "practice_success_rate"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "mock_test_count"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "mock_total"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "mock_correct"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "mock_wrong"
                                )
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "mock_skipped"
                                )
                        ),

                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "mock_success_rate"
                                )
                        )
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }


    // ============================================================
// GET LAST 7 DAYS OVERALL PROGRESS
// ============================================================

    // ============================================================
// GET LAST 7 DAYS PROGRESS
// ============================================================

    public List<SevenDayProgress> getLast7DaysProgress() {

        List<SevenDayProgress> list =
                new ArrayList<>();

        String today =
                getTodayDate();

        try {

            for (int i = 6; i >= 0; i--) {

                Calendar calendar =
                        Calendar.getInstance();

                calendar.add(
                        Calendar.DAY_OF_YEAR,
                        -i
                );

                String date =
                        new SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                        ).format(
                                calendar.getTime()
                        );

                ProgressDaily progress =
                        getProgress(date);

                if (progress == null) {

                    list.add(
                            new SevenDayProgress(
                                    date,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0
                            )
                    );

                } else {

                    list.add(
                            new SevenDayProgress(
                                    date,

                                    progress.getReadCount(),

                                    progress.getPracticeTotal(),
                                    progress.getPracticeCorrect(),
                                    progress.getPracticeWrong(),
                                    progress.getPracticeSkipped(),

                                    progress.getPracticeSuccessRate(),

                                    progress.getMockTotal(),
                                    progress.getMockCorrect(),
                                    progress.getMockWrong(),
                                    progress.getMockSkipped(),

                                    progress.getMockSuccessRate(),

                                    progress.getPracticeCount()
                                            + progress.getMockTestCount()
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return list;
    }


    // ============================================================
// READING COUNT BY DATE
// ============================================================

    private int getReadingCountForDate(
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


    // ============================================================
    // PRACTICE MODEL
    // ============================================================

    private static class PracticeData {

        int count = 0;
        int total = 0;
        int correct = 0;
        int wrong = 0;
        int skipped = 0;
        double successRate = 0;
    }


    // ============================================================
    // MOCK MODEL
    // ============================================================

    private static class MockData {

        int count = 0;
        int total = 0;
        int correct = 0;
        int wrong = 0;
        int skipped = 0;
        double successRate = 0;
    }


    // ============================================================
    // PROGRESS MODEL
    // ============================================================

    public static class ProgressDaily {

        private final int readCount;

        private final int practiceCount;
        private final int practiceTotal;
        private final int practiceCorrect;
        private final int practiceWrong;
        private final int practiceSkipped;
        private final double practiceSuccessRate;

        private final int mockTestCount;
        private final int mockTotal;
        private final int mockCorrect;
        private final int mockWrong;
        private final int mockSkipped;
        private final double mockSuccessRate;


        public ProgressDaily(
                int readCount,

                int practiceCount,
                int practiceTotal,
                int practiceCorrect,
                int practiceWrong,
                int practiceSkipped,
                double practiceSuccessRate,

                int mockTestCount,
                int mockTotal,
                int mockCorrect,
                int mockWrong,
                int mockSkipped,
                double mockSuccessRate
        ) {

            this.readCount = readCount;

            this.practiceCount = practiceCount;
            this.practiceTotal = practiceTotal;
            this.practiceCorrect = practiceCorrect;
            this.practiceWrong = practiceWrong;
            this.practiceSkipped = practiceSkipped;
            this.practiceSuccessRate =
                    practiceSuccessRate;

            this.mockTestCount = mockTestCount;
            this.mockTotal = mockTotal;
            this.mockCorrect = mockCorrect;
            this.mockWrong = mockWrong;
            this.mockSkipped = mockSkipped;
            this.mockSuccessRate =
                    mockSuccessRate;
        }


        public int getReadCount() {
            return readCount;
        }

        public int getPracticeCount() {
            return practiceCount;
        }

        public int getPracticeTotal() {
            return practiceTotal;
        }

        public int getPracticeCorrect() {
            return practiceCorrect;
        }

        public int getPracticeWrong() {
            return practiceWrong;
        }

        public int getPracticeSkipped() {
            return practiceSkipped;
        }

        public double getPracticeSuccessRate() {
            return practiceSuccessRate;
        }

        public int getMockTestCount() {
            return mockTestCount;
        }

        public int getMockTotal() {
            return mockTotal;
        }

        public int getMockCorrect() {
            return mockCorrect;
        }

        public int getMockWrong() {
            return mockWrong;
        }

        public int getMockSkipped() {
            return mockSkipped;
        }

        public double getMockSuccessRate() {
            return mockSuccessRate;
        }
    }
}