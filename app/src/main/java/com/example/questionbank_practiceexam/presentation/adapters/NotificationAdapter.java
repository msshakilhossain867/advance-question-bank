package com.example.questionbank_practiceexam.presentation.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.NotificationPrefs;

import java.util.List;

public class NotificationAdapter
        extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<NotificationPrefs.NotificationItem> notificationList;

    private final OnNotificationReadListener listener;


    public interface OnNotificationReadListener {

        void onNotificationRead(
                long notificationId
        );
    }


    public NotificationAdapter(
            List<NotificationPrefs.NotificationItem> notificationList,
            OnNotificationReadListener listener
    ) {

        this.notificationList = notificationList;

        this.listener = listener;
    }


    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_notification,
                        parent,
                        false
                );


        return new NotificationViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull NotificationViewHolder holder,
            int position
    ) {

        NotificationPrefs.NotificationItem item =
                notificationList.get(position);


        holder.title.setText(
                item.getTitle()
        );


        holder.message.setText(
                item.getMessage()
        );


        holder.time.setText(
                item.getFormattedTime()
        );


        // ========================================================
        // UNREAD
        // ========================================================

        if (!item.isRead()) {

            holder.unreadDot.setVisibility(
                    View.VISIBLE
            );

            holder.readIcon.setVisibility(
                    View.GONE
            );

        }

        // ========================================================
        // READ
        // ========================================================

        else {

            holder.unreadDot.setVisibility(
                    View.INVISIBLE
            );

            holder.readIcon.setVisibility(
                    View.VISIBLE
            );
        }


        // ========================================================
        // CLICK
        // ========================================================

        holder.itemView.setOnClickListener(v -> {

            if (!item.isRead()) {

                listener.onNotificationRead(
                        item.getId()
                );
            }
        });
    }


    @Override
    public int getItemCount() {

        return notificationList.size();
    }


    static class NotificationViewHolder
            extends RecyclerView.ViewHolder {

        TextView title;

        TextView message;

        TextView time;

        TextView readIcon;

        View unreadDot;


        public NotificationViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            unreadDot =
                    itemView.findViewById(
                            R.id.unreadDot
                    );


            title =
                    itemView.findViewById(
                            R.id.notificationTitle
                    );


            message =
                    itemView.findViewById(
                            R.id.notificationMessage
                    );


            time =
                    itemView.findViewById(
                            R.id.notificationTime
                    );


            readIcon =
                    itemView.findViewById(
                            R.id.readIcon
                    );
        }
    }
}