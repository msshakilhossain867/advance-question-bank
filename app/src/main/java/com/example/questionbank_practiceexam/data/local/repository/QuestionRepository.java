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

        dbHelper =
                new DatabaseHelper(context);
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

    public List<Question> getQuestionsBySubject(
            int subjectId
    ) {

        open();

        List<Question> questionList =
                new ArrayList<>();

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
                            "options",
                            "correct_option",
                            "explanation"
                    },

                    "subject_id = ?",

                    new String[]{
                            String.valueOf(subjectId)
                    },

                    null,
                    null,

                    "id ASC"
            );

            if (cursor != null &&
                    cursor.moveToFirst()) {

                do {

                    Question question =
                            new Question(

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

                                    cursor.getInt(
                                            cursor.getColumnIndexOrThrow(
                                                    "university_id"
                                            )
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    "year_tag"
                                            )
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    "question_text"
                                            )
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    "options"
                                            )
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    "correct_option"
                                            )
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    "explanation"
                                            )
                                    )
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

    public Question getQuestionById(
            int questionId
    ) {

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
                            "options",
                            "correct_option",
                            "explanation"
                    },

                    "id = ?",

                    new String[]{
                            String.valueOf(questionId)
                    },

                    null,
                    null,
                    null
            );

            if (cursor != null &&
                    cursor.moveToFirst()) {

                return new Question(

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("subject_id")
                        ),

                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("university_id")
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow("year_tag")
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow("question_text")
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow("options")
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow("correct_option")
                        ),

                        cursor.getString(
                                cursor.getColumnIndexOrThrow("explanation")
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

        String sql = "SELECT COUNT(id) " +
                        "FROM questions " +
                        "WHERE subject_id IN (" +
                        placeholders +
                        ")";

        String[] selectionArgs =
                new String[subjectIds.size()];

        int index = 0;

        for (Integer subjectId : subjectIds) {

            selectionArgs[index++] =
                    String.valueOf(subjectId);
        }

        Cursor cursor =
                db.rawQuery(
                        sql,
                        selectionArgs
                );

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

    public List<Question> getQuestionsBySubjects(
            Set<Integer> subjectIds
    ) {

        open();

        List<Question> questionList =
                new ArrayList<>();

        if (subjectIds == null ||
                subjectIds.isEmpty()) {

            return questionList;
        }

        StringBuilder placeholders =
                new StringBuilder();

        for (int i = 0;
             i < subjectIds.size();
             i++) {

            if (i > 0) {
                placeholders.append(",");
            }

            placeholders.append("?");
        }

        String sql =
                "SELECT id, subject_id, university_id, " +
                        "year_tag, question_text, options, " +
                        "correct_option, explanation " +
                        "FROM questions " +
                        "WHERE subject_id IN (" +
                        placeholders +
                        ") " +
                        "ORDER BY id ASC";

        String[] selectionArgs =
                new String[subjectIds.size()];

        int index = 0;

        for (Integer subjectId : subjectIds) {

            selectionArgs[index++] =
                    String.valueOf(subjectId);
        }

        Cursor cursor = null;

        try {

            cursor = db.rawQuery(
                    sql,
                    selectionArgs
            );

            if (cursor.moveToFirst()) {

                do {

                    Question question =
                            new Question(

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

                                    cursor.getInt(
                                            cursor.getColumnIndexOrThrow(
                                                    "university_id"
                                            )
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    "year_tag"
                                            )
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    "question_text"
                                            )
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    "options"
                                            )
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    "correct_option"
                                            )
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow(
                                                    "explanation"
                                            )
                                    )
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
// GET QUESTIONS BY MULTIPLE SUBJECTS
// ============================================================


    // ============================================================
    // CLOSE DATABASE
    // ============================================================

    public void close() {

        if (db != null &&
                db.isOpen()) {

            dbHelper.close();

            db = null;
        }
    }
}