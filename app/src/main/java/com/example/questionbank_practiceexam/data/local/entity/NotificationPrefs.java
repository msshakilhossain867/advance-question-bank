package com.example.questionbank_practiceexam.data.local.entity;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationPrefs {

    private static final String PREF_NAME =
            "app_notifications";

    private static final String KEY_NOTIFICATIONS =
            "notifications";

    private final SharedPreferences preferences;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public NotificationPrefs(Context context) {

        preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREF_NAME,
                                Context.MODE_PRIVATE
                        );
    }


    // ============================================================
    // ADD NOTIFICATION
    // ============================================================

    public void addNotification(
            String title,
            String message
    ) {

        try {

            JSONArray oldArray =
                    getNotificationsJson();


            JSONObject object =
                    new JSONObject();


            long id =
                    System.currentTimeMillis();


            object.put(
                    "id",
                    id
            );


            object.put(
                    "title",
                    title
            );


            object.put(
                    "message",
                    message
            );


            object.put(
                    "time",
                    System.currentTimeMillis()
            );


            object.put(
                    "read",
                    false
            );


            // ====================================================
            // NEW NOTIFICATION FIRST
            // ====================================================

            JSONArray newArray =
                    new JSONArray();


            newArray.put(object);


            for (
                    int i = 0;
                    i < oldArray.length();
                    i++
            ) {

                newArray.put(
                        oldArray.getJSONObject(i)
                );
            }


            preferences.edit()
                    .putString(
                            KEY_NOTIFICATIONS,
                            newArray.toString()
                    )
                    .apply();


        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // ============================================================
    // GET NOTIFICATIONS
    // ============================================================

    public List<NotificationItem> getNotifications() {

        List<NotificationItem> list =
                new ArrayList<>();


        try {

            JSONArray array =
                    getNotificationsJson();


            for (
                    int i = 0;
                    i < array.length();
                    i++
            ) {

                JSONObject object =
                        array.getJSONObject(i);


                list.add(
                        new NotificationItem(

                                object.optLong(
                                        "id"
                                ),

                                object.optString(
                                        "title"
                                ),

                                object.optString(
                                        "message"
                                ),

                                object.optLong(
                                        "time"
                                ),

                                object.optBoolean(
                                        "read"
                                )
                        )
                );
            }


        } catch (Exception e) {

            e.printStackTrace();
        }


        return list;
    }


    // ============================================================
    // INTERNAL JSON
    // ============================================================

    private JSONArray getNotificationsJson() {

        try {

            String json =
                    preferences.getString(
                            KEY_NOTIFICATIONS,
                            "[]"
                    );


            return new JSONArray(json);


        } catch (Exception e) {

            return new JSONArray();
        }
    }


    // ============================================================
    // UNREAD COUNT
    // ============================================================

    public int getUnreadCount() {

        int count = 0;


        try {

            JSONArray array =
                    getNotificationsJson();


            for (
                    int i = 0;
                    i < array.length();
                    i++
            ) {

                JSONObject object =
                        array.getJSONObject(i);


                if (
                        !object.optBoolean(
                                "read",
                                false
                        )
                ) {

                    count++;
                }
            }


        } catch (Exception e) {

            e.printStackTrace();
        }


        return count;
    }


    // ============================================================
    // HAS UNREAD
    // ============================================================

    public boolean hasUnreadNotifications() {

        return getUnreadCount() > 0;
    }


    // ============================================================
    // MARK ONE AS READ
    // ============================================================

    public void markAsRead(
            long notificationId
    ) {

        try {

            JSONArray array =
                    getNotificationsJson();


            for (
                    int i = 0;
                    i < array.length();
                    i++
            ) {

                JSONObject object =
                        array.getJSONObject(i);


                if (
                        object.optLong("id")
                                == notificationId
                ) {

                    object.put(
                            "read",
                            true
                    );

                    break;
                }
            }


            preferences.edit()
                    .putString(
                            KEY_NOTIFICATIONS,
                            array.toString()
                    )
                    .apply();


        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // ============================================================
    // MARK ALL AS READ
    // ============================================================

    public void markAllAsRead() {

        try {

            JSONArray array =
                    getNotificationsJson();


            for (
                    int i = 0;
                    i < array.length();
                    i++
            ) {

                array.getJSONObject(i)
                        .put(
                                "read",
                                true
                        );
            }


            preferences.edit()
                    .putString(
                            KEY_NOTIFICATIONS,
                            array.toString()
                    )
                    .apply();


        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // ============================================================
    // CLEAR ALL
    // ============================================================

    public void clearAll() {

        preferences.edit()
                .remove(KEY_NOTIFICATIONS)
                .apply();
    }


    // ============================================================
    // NOTIFICATION ITEM
    // ============================================================

    public static class NotificationItem {

        private final long id;

        private final String title;

        private final String message;

        private final long time;

        private final boolean read;


        public NotificationItem(
                long id,
                String title,
                String message,
                long time,
                boolean read
        ) {

            this.id = id;

            this.title = title;

            this.message = message;

            this.time = time;

            this.read = read;
        }


        public long getId() {
            return id;
        }


        public String getTitle() {
            return title;
        }


        public String getMessage() {
            return message;
        }


        public long getTime() {
            return time;
        }


        public boolean isRead() {
            return read;
        }


        public String getFormattedTime() {

            return new SimpleDateFormat(
                    "dd MMM, hh:mm a",
                    Locale.getDefault()
            ).format(
                    new Date(time)
            );
        }
    }
}