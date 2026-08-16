package com.example.questionbank_practiceexam.presentation.bookmarks;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.questionbank_practiceexam.presentation.adapters.BookmarkAdapter;
import com.example.questionbank_practiceexam.data.local.entity.Question;
import com.example.questionbank_practiceexam.data.local.repository.BookmarkRepository;
import com.example.questionbank_practiceexam.data.local.repository.QuestionRepository;
import com.example.questionbank_practiceexam.databinding.ActivityBookmarkBinding;

import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity
        extends AppCompatActivity {


    // ============================================================
    // BINDING
    // ============================================================

    private ActivityBookmarkBinding binding;


    // ============================================================
    // REPOSITORIES
    // ============================================================

    private BookmarkRepository bookmarkRepository;

    private QuestionRepository questionRepository;


    // ============================================================
    // DATA
    // ============================================================

    private final List<Question> questionList =
            new ArrayList<>();


    private BookmarkAdapter adapter;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);


        // ========================================================
        // BINDING
        // ========================================================

        binding =
                ActivityBookmarkBinding.inflate(
                        getLayoutInflater()
                );


        setContentView(
                binding.getRoot()
        );


        // ========================================================
        // REPOSITORY
        // ========================================================

        bookmarkRepository =
                new BookmarkRepository(
                        getApplicationContext()
                );


        questionRepository =
                new QuestionRepository(
                        getApplicationContext()
                );


        // ========================================================
        // RECYCLER
        // ========================================================

        setupRecyclerView();


        // ========================================================
        // BACK
        // ========================================================

        binding.backButton.setOnClickListener(
                v -> finish()
        );


        binding.clearAllButton.setOnClickListener(v -> {

            if (questionList == null ||
                    questionList.isEmpty()) {

                Toast.makeText(
                        this,
                        "No bookmarks to clear",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Clear All Bookmarks")
                    .setMessage(
                            "Are you sure you want to remove all bookmarked questions?"
                    )
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .setPositiveButton(
                            "Clear All",
                            (dialog, which) -> {

                                boolean success =
                                        bookmarkRepository
                                                .clearAllBookmarks();

                                if (success) {

                                    questionList.clear();

                                    adapter.notifyDataSetChanged();

                                    updateEmptyState();

                                    Toast.makeText(
                                            this,
                                            "All bookmarks cleared",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } else {

                                    Toast.makeText(
                                            this,
                                            "Failed to clear bookmarks",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            })
                    .show();
        });


        // ========================================================
        // LOAD
        // ========================================================

        loadBookmarks();
    }


    // ============================================================
    // RECYCLER VIEW
    // ============================================================

    private void setupRecyclerView() {

        binding.bookmarkRecycler.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );


        binding.bookmarkRecycler.setHasFixedSize(
                false
        );


        adapter =
                new BookmarkAdapter(
                        questionList,

                        (question, position) -> {

                            removeBookmark(
                                    question,
                                    position
                            );
                        }
                );


        binding.bookmarkRecycler.setAdapter(
                adapter
        );
    }


    // ============================================================
    // LOAD BOOKMARKS
    // ============================================================

    private void loadBookmarks() {

        questionList.clear();


        // ========================================================
        // GET BOOKMARK IDS
        // ========================================================

        List<Integer> bookmarkedIds =
                bookmarkRepository
                        .getBookmarkedQuestionIds();


        // ========================================================
        // EMPTY
        // ========================================================

        if (bookmarkedIds == null ||
                bookmarkedIds.isEmpty()) {

            showEmptyState();

            return;
        }


        // ========================================================
        // LOAD QUESTIONS
        // ========================================================

        for (Integer questionId :
                bookmarkedIds) {

            if (questionId == null) {
                continue;
            }


            Question question =
                    questionRepository
                            .getQuestionById(
                                    questionId
                            );


            if (question != null) {

                questionList.add(
                        question
                );
            }
        }


        // ========================================================
        // SHOW
        // ========================================================

        if (questionList.isEmpty()) {

            showEmptyState();

        } else {

            showQuestionList();
        }
    }


    // ============================================================
    // REMOVE BOOKMARK
    // ============================================================

    private void removeBookmark(
            Question question,
            int position
    ) {

        if (question == null) {
            return;
        }


        boolean removed =
                bookmarkRepository
                        .removeBookmark(
                                question.getId()
                        );


        if (removed) {

            adapter.removeItem(
                    position
            );


            updateEmptyState();


            Toast.makeText(
                    this,
                    "Bookmark removed",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Bookmark remove করা যায়নি",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ============================================================
    // SHOW LIST
    // ============================================================

    private void showQuestionList() {

        binding.bookmarkRecycler.setVisibility(
                View.VISIBLE
        );


        binding.emptyBookmarkLayout.setVisibility(
                View.GONE
        );
    }


    // ============================================================
    // EMPTY STATE
    // ============================================================

    private void showEmptyState() {

        binding.bookmarkRecycler.setVisibility(
                View.GONE
        );


        binding.emptyBookmarkLayout.setVisibility(
                View.VISIBLE
        );
    }


    // ============================================================
    // UPDATE EMPTY STATE
    // ============================================================

    private void updateEmptyState() {

        if (questionList == null ||
                questionList.isEmpty()) {

            binding.bookmarkRecycler.setVisibility(
                    View.GONE
            );

            binding.emptyBookmarkLayout.setVisibility(
                    View.VISIBLE
            );

            binding.clearAllButton.setVisibility(
                    View.GONE
            );

        } else {

            binding.bookmarkRecycler.setVisibility(
                    View.VISIBLE
            );

            binding.emptyBookmarkLayout.setVisibility(
                    View.GONE
            );

            binding.clearAllButton.setVisibility(
                    View.VISIBLE
            );
        }
    }

    // ============================================================
    // ON RESUME
    // ============================================================

    @Override
    protected void onResume() {

        super.onResume();


        /*
         * QuestionsActivity থেকে নতুন bookmark করে
         * BookmarkActivity-তে ফিরে এলে list refresh হবে।
         */

        if (bookmarkRepository != null &&
                questionRepository != null) {

            loadBookmarks();
        }
    }


    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        if (bookmarkRepository != null) {

            bookmarkRepository.close();

            bookmarkRepository = null;
        }


        if (questionRepository != null) {

            questionRepository.close();

            questionRepository = null;
        }


        binding = null;


        super.onDestroy();
    }
}

