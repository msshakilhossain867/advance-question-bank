package com.example.questionbank_practiceexam.presentation.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.data.local.entity.DailyMission;

import java.util.List;

public class DailyMissionAdapter
        extends RecyclerView.Adapter<
        DailyMissionAdapter.MissionViewHolder> {


    private final List<DailyMission> missionList;


    public DailyMissionAdapter(
            List<DailyMission> missionList
    ) {

        this.missionList = missionList;
    }


    @NonNull
    @Override
    public MissionViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_daily_mission,
                        parent,
                        false
                );


        return new MissionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull MissionViewHolder holder,
            int position
    ) {

        DailyMission mission =
                missionList.get(position);


        holder.missionIcon.setText(
                mission.getIcon()
        );


        holder.missionTitle.setText(
                mission.getTitle()
        );


        holder.missionDescription.setText(
                mission.getDescription()
        );


        holder.missionProgressBar.setProgress(
                mission.getProgress()
        );


        // ========================================================
        // COMPLETED
        // ========================================================

        if (mission.isCompleted()) {

            holder.completedText.setVisibility(
                    View.VISIBLE
            );


            holder.missionProgressBar.setProgress(
                    100
            );


            holder.missionProgressText.setText(
                    "Completed"
            );


            holder.missionProgressText.setTextColor(
                    Color.rgb(
                            46,
                            125,
                            50
                    )
            );

        } else {

            holder.completedText.setVisibility(
                    View.GONE
            );


            holder.missionProgressText.setText(
                    mission.getCurrent()
                            + " / "
                            + mission.getTarget()
            );


            holder.missionProgressText.setTextColor(
                    Color.GRAY
            );
        }
    }


    @Override
    public int getItemCount() {

        return missionList.size();
    }


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    static class MissionViewHolder
            extends RecyclerView.ViewHolder {


        TextView missionIcon;

        TextView missionTitle;

        TextView missionDescription;

        TextView missionProgressText;

        TextView completedText;

        ProgressBar missionProgressBar;


        MissionViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            missionIcon =
                    itemView.findViewById(
                            R.id.missionIcon
                    );


            missionTitle =
                    itemView.findViewById(
                            R.id.missionTitle
                    );


            missionDescription =
                    itemView.findViewById(
                            R.id.missionDescription
                    );


            missionProgressText =
                    itemView.findViewById(
                            R.id.missionProgressText
                    );


            completedText =
                    itemView.findViewById(
                            R.id.completedText
                    );


            missionProgressBar =
                    itemView.findViewById(
                            R.id.missionProgressBar
                    );
        }
    }
}