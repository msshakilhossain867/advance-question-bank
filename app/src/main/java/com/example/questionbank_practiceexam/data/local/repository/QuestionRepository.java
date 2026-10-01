package com.example.questionbank_practiceexam.data.local.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.questionbank_practiceexam.data.local.db.DatabaseHelper;
import com.example.questionbank_practiceexam.data.local.entity.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QuestionRepository {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public QuestionRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
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
    // GET QUESTIONS BY SUBJECT
    // ============================================================

    public List<Question> getQuestionsBySubject(int subjectId) {
        open();
        List<Question> questionList = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    "questions",
                    new String[]{
                            "id",
                            "subject_id",
                            "university_id",
                            "year_tag",
                            "question_text",
                            "question_image",     // নতুন কলাম যুক্ত করা হয়েছে
                            "options",
                            "option_images",      // নতুন কলাম যুক্ত করা হয়েছে
                            "correct_option",
                            "explanation",
                            "explanation_image"   // নতুন কলাম যুক্ত করা হয়েছে
                    },
                    "subject_id = ?",
                    new String[]{String.valueOf(subjectId)},
                    null,
                    null,
                    "id ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Question question = new Question(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("subject_id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("university_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("year_tag")),
                            cursor.getString(cursor.getColumnIndexOrThrow("question_text")),
                            cursor.getString(cursor.getColumnIndexOrThrow("question_image")),
                            cursor.getString(cursor.getColumnIndexOrThrow("options")),
                            cursor.getString(cursor.getColumnIndexOrThrow("option_images")),
                            cursor.getString(cursor.getColumnIndexOrThrow("correct_option")),
                            cursor.getString(cursor.getColumnIndexOrThrow("explanation")),
                            cursor.getString(cursor.getColumnIndexOrThrow("explanation_image"))
                    );
                    questionList.add(question);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return questionList;
    }

    // ============================================================
    // GET SINGLE QUESTION
    // ============================================================

    public Question getQuestionById(int questionId) {
        open();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    "questions",
                    new String[]{
                            "id",
                            "subject_id",
                            "university_id",
                            "year_tag",
                            "question_text",
                            "question_image",
                            "options",
                            "option_images",
                            "correct_option",
                            "explanation",
                            "explanation_image"
                    },
                    "id = ?",
                    new String[]{String.valueOf(questionId)},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                return new Question(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("subject_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("university_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("year_tag")),
                        cursor.getString(cursor.getColumnIndexOrThrow("question_text")),
                        cursor.getString(cursor.getColumnIndexOrThrow("question_image")),
                        cursor.getString(cursor.getColumnIndexOrThrow("options")),
                        cursor.getString(cursor.getColumnIndexOrThrow("option_images")),
                        cursor.getString(cursor.getColumnIndexOrThrow("correct_option")),
                        cursor.getString(cursor.getColumnIndexOrThrow("explanation")),
                        cursor.getString(cursor.getColumnIndexOrThrow("explanation_image"))
                );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public int getQuestionCountBySubjects(Set<Integer> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return 0;
        }

        open();
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < subjectIds.size(); i++) {
            if (i > 0) {
                placeholders.append(",");
            }
            placeholders.append("?");
        }

        String sql = "SELECT COUNT(id) FROM questions WHERE subject_id IN (" + placeholders + ")";
        String[] selectionArgs = new String[subjectIds.size()];
        int index = 0;
        for (Integer subjectId : subjectIds) {
            selectionArgs[index++] = String.valueOf(subjectId);
        }

        Cursor cursor = db.rawQuery(sql, selectionArgs);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ============================================================
    // GET QUESTIONS BY MULTIPLE SUBJECTS
    // ============================================================

    public List<Question> getQuestionsBySubjects(Set<Integer> subjectIds) {
        open();
        List<Question> questionList = new ArrayList<>();

        if (subjectIds == null || subjectIds.isEmpty()) {
            return questionList;
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < subjectIds.size(); i++) {
            if (i > 0) {
                placeholders.append(",");
            }
            placeholders.append("?");
        }

        String sql = "SELECT id, subject_id, university_id, year_tag, question_text, question_image, " +
                "options, option_images, correct_option, explanation, explanation_image " +
                "FROM questions WHERE subject_id IN (" + placeholders + ") ORDER BY id ASC";

        String[] selectionArgs = new String[subjectIds.size()];
        int index = 0;
        for (Integer subjectId : subjectIds) {
            selectionArgs[index++] = String.valueOf(subjectId);
        }

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    Question question = new Question(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("subject_id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("university_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("year_tag")),
                            cursor.getString(cursor.getColumnIndexOrThrow("question_text")),
                            cursor.getString(cursor.getColumnIndexOrThrow("question_image")),
                            cursor.getString(cursor.getColumnIndexOrThrow("options")),
                            cursor.getString(cursor.getColumnIndexOrThrow("option_images")),
                            cursor.getString(cursor.getColumnIndexOrThrow("correct_option")),
                            cursor.getString(cursor.getColumnIndexOrThrow("explanation")),
                            cursor.getString(cursor.getColumnIndexOrThrow("explanation_image"))
                    );
                    questionList.add(question);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return questionList;
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