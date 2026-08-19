package com.example.questionbank_practiceexam.data.local.repository;

import android.content.Context;

import com.example.questionbank_practiceexam.data.local.entity.DailyMission;

import java.util.ArrayList;
import java.util.List;

public class DailyMissionRepository {

    // ============================================================
    // DAILY TARGET
    // ============================================================

    private static final int READ_TARGET = 20;

    private static final int PRACTICE_TARGET = 30;

    private static final int MOCK_TARGET = 1;


    // ============================================================
    // REPOSITORIES
    // ============================================================

    private final ReadingProgressRepository readingRepository;

    private final PracticeResultRepository practiceRepository;

    private final MockTestResultRepository mockRepository;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public DailyMissionRepository(
            Context context
    ) {

        Context appContext =
                context.getApplicationContext();


        readingRepository =
                new ReadingProgressRepository(
                        appContext
                );


        practiceRepository =
                new PracticeResultRepository(
                        appContext
                );


        mockRepository =
                new MockTestResultRepository(
                        appContext
                );
    }


    // ============================================================
    // GET TODAY MISSIONS
    // ============================================================

    public List<DailyMission> getTodayMissions() {

        List<DailyMission> missions =
                new ArrayList<>();


        // ========================================================
        // 1. READING
        // ========================================================

        int read =
                readingRepository.getTodayReadCount();


        missions.add(
                new DailyMission(

                        1,

                        "Reading",

                        "Read 20 questions today",

                        "📖",

                        read,

                        READ_TARGET
                )
        );


        // ========================================================
        // 2. PRACTICE
        // ========================================================

        int practice =
                getTodayPracticeQuestions();


        missions.add(
                new DailyMission(

                        2,

                        "Practice",

                        "Practice 30 questions today",

                        "✍️",

                        practice,

                        PRACTICE_TARGET
                )
        );


        // ========================================================
        // 3. MOCK TEST
        // ========================================================

        int mock =
                mockRepository.getTodayMockTestCount();


        missions.add(
                new DailyMission(

                        3,

                        "Mock Test",

                        "Complete one mock test today",

                        "🎯",

                        mock,

                        MOCK_TARGET
                )
        );


        return missions;
    }


    // ============================================================
    // TODAY PRACTICE QUESTIONS
    // ============================================================

    private int getTodayPracticeQuestions() {

        return practiceRepository
                .getTodayQuestions();
    }


    // ============================================================
    // ALL COMPLETED
    // ============================================================

    public boolean areAllMissionsCompleted() {

        List<DailyMission> missions =
                getTodayMissions();


        for (
                DailyMission mission
                : missions
        ) {

            if (!mission.isCompleted()) {

                return false;
            }
        }


        return true;
    }


    // ============================================================
    // COMPLETED COUNT
    // ============================================================

    public int getCompletedMissionCount() {

        int count = 0;


        List<DailyMission> missions =
                getTodayMissions();


        for (
                DailyMission mission
                : missions
        ) {

            if (mission.isCompleted()) {

                count++;
            }
        }


        return count;
    }


    // ============================================================
    // TOTAL MISSION
    // ============================================================

    public int getTotalMissionCount() {

        return 3;
    }


    // ============================================================
    // CLOSE
    // ============================================================

    public void close() {

        readingRepository.close();

        practiceRepository.close();

        mockRepository.close();
    }
}