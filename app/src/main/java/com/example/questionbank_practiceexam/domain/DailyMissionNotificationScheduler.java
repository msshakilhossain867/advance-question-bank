package com.example.questionbank_practiceexam.domain;


import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DailyMissionNotificationScheduler {

    private static final String WORK_NAME =
            "daily_mission_notification";


    public static void schedule(Context context) {

        Context appContext =
                context.getApplicationContext();


        Calendar now =
                Calendar.getInstance();


        Calendar start =
                Calendar.getInstance();

        start.set(
                Calendar.HOUR_OF_DAY,
                17
        );

        start.set(
                Calendar.MINUTE,
                0
        );

        start.set(
                Calendar.SECOND,
                0
        );

        start.set(
                Calendar.MILLISECOND,
                0
        );


        long initialDelay;


        // ========================================================
        // BEFORE 5 PM
        // ========================================================

        if (now.before(start)) {

            initialDelay =
                    start.getTimeInMillis()
                            - now.getTimeInMillis();

        }

        // ========================================================
        // AFTER 5 PM
        // ========================================================

        else {

            /*
             * এখন 5 PM পার হয়ে গেছে।
             *
             * প্রথম check এখন থেকে 1 hour পরে হবে।
             */

            initialDelay =
                    TimeUnit.HOURS.toMillis(1);
        }


        // ========================================================
        // EVERY 1 HOUR
        // ========================================================

        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        DailyMissionNotificationWorker.class,
                        1,
                        TimeUnit.HOURS
                )
                        .setInitialDelay(
                                initialDelay,
                                TimeUnit.MILLISECONDS
                        )
                        .build();


        WorkManager
                .getInstance(appContext)
                .enqueueUniquePeriodicWork(
                        WORK_NAME,
                        ExistingPeriodicWorkPolicy.UPDATE,
                        request
                );
    }
}