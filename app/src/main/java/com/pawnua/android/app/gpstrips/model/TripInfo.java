package com.pawnua.android.app.gpstrips.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.adapters.TripInfoAdapter;

import java.util.ArrayList;
import java.util.List;

public class TripInfo{

    public static final String MAX_SPEED = "MaxSpeed";
    public static final String DURATION = "Duration";
    public static final String START_TIME = "StartTime";
    public static final String FINISH_TIME = "FinishTime";
    public static final String MOVING_TIME = "MovingTime";
    public static final String DISTANCE = "Distance";
    public static final String MOVING_DISTANCE = "MovingDistance";
    public static final String AVG_SPEED = "AvgSpeed";

    public String id;
    public Drawable infoIco;
    public String infoLabel;
    public String infoValue;
    public String infoAd;

    public TripInfo(String id, Drawable infoIco, String infoLabel, String infoValue, String infoAd) {
        this.id = id;
        this.infoIco = infoIco;
        this.infoLabel = infoLabel;
        this.infoValue = infoValue;
        this.infoAd = infoAd;

    }

    public static void setValue(TripInfoAdapter tripInfoAdapter, String id, String infoValue){
        TripInfo tripInfo = tripInfoAdapter.getItem(id);
        tripInfo.infoValue = infoValue;

        tripInfoAdapter.notifyDataSetChanged();
    }

    public static List<TripInfo> initTripInfo(Context context, TripCharacteristics tripCharacteristics) {
        List<TripInfo> tripInfoList = new ArrayList<>();

        if (tripCharacteristics == null) return tripInfoList;

        tripInfoList.add(new TripInfo(MAX_SPEED,
                context.getResources().getDrawable(R.drawable.speedometer),
                context.getString(R.string.string_max_speed),
                LocationUtils.getSpeed(tripCharacteristics.getMaxSpeed()),
                context.getString(R.string.string_kmh)));
        tripInfoList.add(new TripInfo(DISTANCE,
                context.getResources().getDrawable(R.drawable.circlecompass),
                context.getString(R.string.string_distance),
                LocationUtils.getDistance(tripCharacteristics.getDistance()),
                context.getString(R.string.string_km)));
        tripInfoList.add(new TripInfo(START_TIME,
                context.getResources().getDrawable(R.drawable.clock),
                context.getString(R.string.string_start_time),
                LocationUtils.getTime(tripCharacteristics.getStartTime()),
                ""));
        tripInfoList.add(new TripInfo(FINISH_TIME,
                context.getResources().getDrawable(R.drawable.clock),
                context.getString(R.string.string_finish_time),
                LocationUtils.getTime(tripCharacteristics.getFinishTime()),
                ""));
        tripInfoList.add(new TripInfo(DURATION,
                context.getResources().getDrawable(R.drawable.clock1),
                context.getString(R.string.string_duration_trip),
                LocationUtils.getDuration(tripCharacteristics.getStartTime(), tripCharacteristics.getFinishTime()),
                ""));
        tripInfoList.add(new TripInfo(MOVING_TIME,
                context.getResources().getDrawable(R.drawable.clock1),
                context.getString(R.string.string_moving_time),
                LocationUtils.getDuration(tripCharacteristics.getMovingTime()),
                ""));
        tripInfoList.add(new TripInfo(AVG_SPEED,
                context.getResources().getDrawable(R.drawable.speedometer1),
                context.getString(R.string.string_avg_speed),
                LocationUtils.getSpeed(tripCharacteristics.getAvgSpeed()),
                context.getString(R.string.string_kmh)));

        tripInfoList.add(new TripInfo(MOVING_DISTANCE,
                context.getResources().getDrawable(R.drawable.circlecompass),
                context.getString(R.string.string_moving_distance),
                LocationUtils.getDistance(tripCharacteristics.getMovingDistance()),
                context.getString(R.string.string_km)));

        return tripInfoList;
    }

}
