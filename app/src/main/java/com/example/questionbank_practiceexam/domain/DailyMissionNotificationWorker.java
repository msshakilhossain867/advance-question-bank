package com.example.questionbank_practiceexam.domain;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.NotificationPrefs;
import com.example.questionbank_practiceexam.data.local.repository.DailyMissionRepository;

public class DailyMissionNotificationWorker extends Worker {

    // ============================================================
    // CONSTANTS
    // ============================================================

    private static final String CHANNEL_ID =
            "daily_mission_channel";

    private static final int NOTIFICATION_ID =
            1001;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public DailyMissionNotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {

        super(
                context,
                workerParams
        );
    }


    // ============================================================
    // WORK
    // ============================================================

    @NonNull
    @Override
    public Result doWork() {

        Context context =
                getApplicationContext();


        // ========================================================
        // TIME CHECK
        // ========================================================

        Calendar now =
                Calendar.getInstance();

        int hour =
                now.get(Calendar.HOUR_OF_DAY);


        /*
         * 5 PM - 11:59 PM
         */

        if (
                hour < 17
                        ||
                        hour >= 24
        ) {

            return Result.success();
        }


        // ========================================================
        // REPOSITORY
        // ========================================================

        DailyMissionRepository repository =
                new DailyMissionRepository(
                        context
                );


        try {

            // ====================================================
            // CHECK ALL MISSIONS
            // ====================================================

            boolean completed =
                    repository.areAllMissionsCompleted();


            // ====================================================
            // ALL COMPLETE
            // ====================================================

            if (completed) {

                return Result.success();
            }


            // ====================================================
            // COMPLETED COUNT
            // ====================================================

            int completedCount =
                    repository.getCompletedMissionCount();


            int totalCount =
                    repository.getTotalMissionCount();


            // ====================================================
            // SEND / SAVE NOTIFICATION
            // ====================================================

            showNotification(
                    completedCount,
                    totalCount
            );


            return Result.success();


        } catch (Exception e) {

            e.printStackTrace();

            return Result.retry();


        } finally {

            repository.close();
        }
    }


    // ============================================================
    // SHOW NOTIFICATION
    // ============================================================

   /* private void showNotification(
            int completed,
            int total
    ) {

        Context context =
                getApplicationContext();


        // ========================================================
        // DOUBLE CHECK PERMISSION
        // ========================================================

        if (
                Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.TIRAMISU
                        &&
                        context.checkSelfPermission(
                                Manifest.permission.POST_NOTIFICATIONS
                        )
                                != PackageManager.PERMISSION_GRANTED
        ) {

            return;
        }




        // ========================================================
        // CHANNEL
        // ========================================================

        createNotificationChannel();


        // ========================================================
        // MESSAGE
        // ========================================================

        String message =
                "You completed "
                        + completed
                        + " of "
                        + total
                        + " today's missions. "
                        + "Complete the rest!";


        // ========================================================
        // BUILDER
        // ========================================================

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                R.drawable.notification
                        )
                        .setContentTitle(
                                "Daily Mission Reminder"
                        )
                        .setContentText(
                                message
                        )
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(message)
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_DEFAULT
                        )
                        .setAutoCancel(true);


        // ========================================================
        // SEND
        // ========================================================

        NotificationManagerCompat
                .from(context)
                .notify(
                        NOTIFICATION_ID,
                        builder.build()
                );
    }*/

    private void showNotification(
            int completed,
            int total
    ) {

        Context context =
                getApplicationContext();


        // ============================================================
        // MESSAGE
        // ============================================================

        String message =
                "You completed "
                        + completed
                        + " of "
                        + total
                        + " today's missions. "
                        + "Complete the rest!";


        // ============================================================
        // SAVE INSIDE APP
        // ============================================================

        NotificationPrefs notificationPrefs =
                new NotificationPrefs(context);


        notificationPrefs.addNotification(
                "Daily Mission Reminder",
                message
        );


        // ============================================================
        // ANDROID 13+ PERMISSION CHECK
        // ============================================================

        if (
                Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.TIRAMISU
                        &&
                        context.checkSelfPermission(
                                Manifest.permission.POST_NOTIFICATIONS
                        )
                                != PackageManager.PERMISSION_GRANTED
        ) {

            // System notification যাবে না
            // কিন্তু app-এর ভিতরে notification save হয়ে গেছে

            return;
        }


        // ============================================================
        // CHANNEL
        // ============================================================

        createNotificationChannel();


        // ============================================================
        // BUILDER
        // ============================================================

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                R.drawable.notification
                        )
                        .setContentTitle(
                                "Daily Mission Reminder"
                        )
                        .setContentText(
                                message
                        )
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(message)
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_DEFAULT
                        )
                        .setAutoCancel(true);


        // ============================================================
        // SEND SYSTEM NOTIFICATION
        // ============================================================

        NotificationManagerCompat
                .from(context)
                .notify(
                        NOTIFICATION_ID,
                        builder.build()
                );
    }


    // ============================================================
    // CREATE CHANNEL
    // ============================================================

    private void createNotificationChannel() {

        if (
                Build.VERSION.SDK_INT <
                        Build.VERSION_CODES.O
        ) {

            return;
        }


        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "Daily Mission",
                        NotificationManager.IMPORTANCE_DEFAULT
                );


        channel.setDescription(
                "Daily mission reminder notifications"
        );


        NotificationManager manager =
                getApplicationContext()
                        .getSystemService(
                                NotificationManager.class
                        );


        if (manager != null) {

            manager.createNotificationChannel(
                    channel
            );
        }
    }
}