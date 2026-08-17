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