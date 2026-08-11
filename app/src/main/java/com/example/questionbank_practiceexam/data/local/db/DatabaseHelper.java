package com.example.questionbank_practiceexam.data.local.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.questionbank_practiceexam.data.security.CryptoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "du_question_bank.db";
    private static final int DATABASE_VERSION = 2;

    private static String DATABASE_PATH = "";
    private final Context mContext;
    private SQLiteDatabase mDatabase;


    // ============================================================
    // 1. UNIVERSITIES TABLE
    // ============================================================

    public static final String TABLE_UNIVERSITIES = "universities";

    public static final String COLUMN_UNIVERSITY_ID = "id";
    public static final String COLUMN_UNIVERSITY_NAME = "name";
    public static final String COLUMN_UNIVERSITY_FULL_NAME = "full_name";
    public static final String COLUMN_UNIVERSITY_LOGO_URL = "logo_url";
    public static final String COLUMN_UNIVERSITY_IS_TARGET = "is_target";


    // ============================================================
    // 2. YEARS TABLE
    // ============================================================

    public static final String TABLE_YEARS = "years";

    public static final String COLUMN_YEAR_ID = "id";
    public static final String COLUMN_YEAR_UNIVERSITY_ID = "university_id";
    public static final String COLUMN_YEAR_TITLE = "year_title";


    // ============================================================
    // 3. UNITS TABLE
    // ============================================================

    public static final String TABLE_UNITS = "units";

    public static final String COLUMN_UNIT_ID = "id";
    public static final String COLUMN_UNIT_YEAR_ID = "year_id";
    public static final String COLUMN_UNIT_NAME = "unit_name";


    // ============================================================
    // 4. SUBJECTS TABLE
    // ============================================================

    public static final String TABLE_SUBJECTS = "subjects";

    public static final String COLUMN_SUBJECT_ID = "id";
    public static final String COLUMN_SUBJECT_UNIT_ID = "unit_id";
    public static final String COLUMN_SUBJECT_NAME = "name";
    public static final String COLUMN_SUBJECT_ICON_URL = "icon_url";


    // ============================================================
    // 5. QUESTIONS TABLE
    // ============================================================

    public static final String TABLE_QUESTIONS = "questions";

    public static final String COLUMN_QUESTION_ID = "id";
    public static final String COLUMN_QUESTION_SUBJECT_ID = "subject_id";
    public static final String COLUMN_QUESTION_UNIVERSITY_ID = "university_id";
    public static final String COLUMN_QUESTION_YEAR_TAG = "year_tag";
    public static final String COLUMN_QUESTION_TEXT = "question_text";
    public static final String COLUMN_QUESTION_OPTIONS = "options";
    public static final String COLUMN_QUESTION_CORRECT_OPTION = "correct_option";
    public static final String COLUMN_QUESTION_EXPLANATION = "explanation";


    // ============================================================
    // 6. USER PROFILE TABLE
    // ============================================================

    public static final String TABLE_USER_PROFILE = "user_profile";

    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_AVATAR_URL = "avatar_url";
    public static final String COLUMN_USER_SELECTED_UNIVERSITY_ID =
            "selected_university_id";


    // ============================================================
    // 7. DAILY OVERVIEW TABLE
    // ============================================================

    public static final String TABLE_DAILY_OVERVIEW = "daily_overview";

    public static final String COLUMN_DAILY_ID = "id";
    public static final String COLUMN_DAILY_USER_ID = "user_id";
    public static final String COLUMN_DAILY_STUDY_TIME = "study_time_minutes";
    public static final String COLUMN_DAILY_QUESTIONS_SOLVED = "questions_solved";
    public static final String COLUMN_DAILY_ACCURACY = "accuracy_percentage";
    public static final String COLUMN_DAILY_MOCK_TESTS = "mock_tests_given";
    public static final String COLUMN_DAILY_DATE = "date";


    // ============================================================
    // 8. USER SUBJECT PROGRESS TABLE
    // ============================================================

    public static final String TABLE_USER_SUBJECT_PROGRESS =
            "user_subject_progress";

    public static final String COLUMN_PROGRESS_ID = "id";
    public static final String COLUMN_PROGRESS_USER_ID = "user_id";
    public static final String COLUMN_PROGRESS_SUBJECT_ID = "subject_id";
    public static final String COLUMN_PROGRESS_PERCENTAGE =
            "completed_percentage";
    public static final String COLUMN_PROGRESS_LAST_UPDATED =
            "last_updated";


    // ============================================================
    // 9. RECENT ACTIVITIES TABLE
    // ============================================================

    public static final String TABLE_RECENT_ACTIVITIES =
            "recent_activities";

    public static final String COLUMN_ACTIVITY_ID = "id";
    public static final String COLUMN_ACTIVITY_USER_ID = "user_id";
    public static final String COLUMN_ACTIVITY_TITLE = "title";
    public static final String COLUMN_ACTIVITY_SUBTITLE = "subtitle";
    public static final String COLUMN_ACTIVITY_TYPE = "activity_type";
    public static final String COLUMN_ACTIVITY_TIME_AGO = "time_ago";
    public static final String COLUMN_ACTIVITY_CREATED_AT = "created_at";


    // ============================================================
    // 10. MOCK TESTS TABLE
    // ============================================================

    public static final String TABLE_MOCK_TESTS = "mock_tests";

    public static final String COLUMN_MOCK_TEST_ID = "id";
    public static final String COLUMN_MOCK_TEST_UNIT_ID = "unit_id";
    public static final String COLUMN_MOCK_TEST_TITLE = "title";
    public static final String COLUMN_MOCK_TEST_TOTAL_QUESTIONS =
            "total_questions";
    public static final String COLUMN_MOCK_TEST_DURATION =
            "duration_minutes";


    // ============================================================
    // 11. BOOKMARKS TABLE
    // ============================================================

    public static final String TABLE_BOOKMARKS = "bookmarks";

    public static final String COLUMN_BOOKMARK_ID = "id";
    public static final String COLUMN_BOOKMARK_USER_ID = "user_id";
    public static final String COLUMN_BOOKMARK_QUESTION_ID =
            "question_id";
    public static final String COLUMN_BOOKMARK_CREATED_AT =
            "created_at";


    // ============================================================
    // 12. TEST RESULTS TABLE
    // ============================================================

    public static final String TABLE_TEST_RESULTS = "test_results";

    public static final String COLUMN_TEST_RESULT_ID = "id";
    public static final String COLUMN_TEST_RESULT_USER_ID = "user_id";
    public static final String COLUMN_TEST_RESULT_TITLE = "test_title";
    public static final String COLUMN_TEST_RESULT_TOTAL_SCORE =
            "total_score";
    public static final String COLUMN_TEST_RESULT_OBTAINED_SCORE =
            "obtained_score";
    public static final String COLUMN_TEST_RESULT_CORRECT_COUNT =
            "correct_count";
    public static final String COLUMN_TEST_RESULT_WRONG_COUNT =
            "wrong_count";
    public static final String COLUMN_TEST_RESULT_SKIPPED_COUNT =
            "skipped_count";
    public static final String COLUMN_TEST_RESULT_ACCURACY =
            "accuracy_percentage";
    public static final String COLUMN_TEST_RESULT_TIME_TAKEN =
            "time_taken_seconds";
    public static final String COLUMN_TEST_RESULT_CREATED_AT =
            "created_at";


    // ============================================================
    // 13. WRONG ANSWERS TABLE
    // ============================================================

    public static final String TABLE_WRONG_ANSWERS = "wrong_answers";

    public static final String COLUMN_WRONG_ID = "id";
    public static final String COLUMN_WRONG_USER_ID = "user_id";
    public static final String COLUMN_WRONG_QUESTION_ID = "question_id";
    public static final String COLUMN_WRONG_SELECTED_OPTION =
            "user_selected_option";
    public static final String COLUMN_WRONG_TEST_TYPE = "test_type";
    public static final String COLUMN_WRONG_TEST_TITLE = "test_title";
    public static final String COLUMN_WRONG_IS_REVIEWED = "is_reviewed";
    public static final String COLUMN_WRONG_CREATED_AT = "created_at";


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.mContext = context.getApplicationContext();

        DATABASE_PATH =
                this.mContext.getApplicationInfo().dataDir + "/databases/";
    }


    // ============================================================
    // CREATE DATABASE
    // ============================================================

    public void createDatabase() throws IOException {

        boolean dbExist = checkDatabase();

        if (!dbExist) {

            this.getReadableDatabase();
            this.close();

            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Database copy failed!", e);
            }
        }
    }


    // ============================================================
    // CHECK DATABASE
    // ============================================================

    private boolean checkDatabase() {

        File dbFile = new File(DATABASE_PATH + DATABASE_NAME);

        return dbFile.exists();
    }


    // ============================================================
    // COPY + DECRYPT DATABASE
    // ============================================================

    private void copyDatabase() throws IOException {

        InputStream mInput = null;
        OutputStream mOutput = null;

        try {

            mInput = mContext.getAssets().open("du_question_bank.enc");

            File databaseDirectory = new File(DATABASE_PATH);

            if (!databaseDirectory.exists()) {
                databaseDirectory.mkdirs();
            }

            String outFileName = DATABASE_PATH + DATABASE_NAME;

            mOutput = new FileOutputStream(outFileName);

            CryptoUtils.decryptDatabase(mInput, mOutput);

            mOutput.flush();

        } catch (Exception e) {

            e.printStackTrace();

            throw new IOException(
                    "Database decryption/copy failed!",
                    e
            );

        } finally {

            if (mOutput != null) {
                try {
                    mOutput.close();
                } catch (IOException ignored) {
                }
            }

            if (mInput != null) {
                try {
                    mInput.close();
                } catch (IOException ignored) {
                }
            }
        }
    }


    // ============================================================
    // OPEN DATABASE
    // ============================================================

    public SQLiteDatabase openDatabase() {

        String myPath = DATABASE_PATH + DATABASE_NAME;

        mDatabase = SQLiteDatabase.openDatabase(
                myPath,
                null,
                SQLiteDatabase.OPEN_READWRITE
        );

        // Foreign Key ON
        mDatabase.execSQL("PRAGMA foreign_keys=ON");

        return mDatabase;
    }


    // ============================================================
    // GET DATABASE
    // ============================================================

    @Override
    public SQLiteDatabase getReadableDatabase() {

        SQLiteDatabase db = super.getReadableDatabase();

        db.execSQL("PRAGMA foreign_keys=ON");

        return db;
    }


    @Override
    public SQLiteDatabase getWritableDatabase() {

        SQLiteDatabase db = super.getWritableDatabase();

        db.execSQL("PRAGMA foreign_keys=ON");

        return db;
    }


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
         * Databaseটি assets/bcs_offline.enc থেকে copy করা হচ্ছে।
         *
         * তাই এখানে table create করার প্রয়োজন নেই।
         *
         * bcs_offline.enc-এর ভিতরের database-এ
         * universities
         * years
         * units
         * subjects
         * questions
         * user_profile
         * daily_overview
         * user_subject_progress
         * recent_activities
         * mock_tests
         * bookmarks
         * test_results
         * wrong_answers
         *
         * এবং প্রয়োজনীয় indexes থাকতে হবে।
         */

        db.execSQL("PRAGMA foreign_keys=ON");
    }


    // ============================================================
    // ON UPGRADE
    // ============================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        if (oldVersion < 2) {

            /*
             * নতুন database structure ব্যবহার করা হচ্ছে।
             *
             * যেহেতু database assets থেকে encrypted অবস্থায়
             * copy করা হয়, তাই existing database migration
             * প্রয়োজন হলে এখানে ALTER TABLE করতে হবে।
             *
             * নতুন bcs_offline.enc ব্যবহার করলে সাধারণত
             * পুরোনো database delete করে নতুন database copy
             * করাই সহজ।
             */
        }
    }


    // ============================================================
    // CLOSE DATABASE
    // ============================================================

    @Override
    public synchronized void close() {

        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
            mDatabase = null;
        }

        super.close();
    }
}