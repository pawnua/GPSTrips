package com.pawnua.android.app.gpstrips.model;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

/**
 * Created by MiK on 02.08.2015.
 */
public class LocationUtils {
    public final static String SPEED = "Speed";
    public final static String MAX_SPEED = "MaxSpeed";
    public final static String TIME = "Time";
    public final static String ALTITUDE = "Altitude";

    private static final String TIMESTAMP_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private static final String TIMESTAMP_FORMAT_DATE = "yyyy-MM-dd";
    private static final String TIMESTAMP_FORMAT_TIME = "HH:mm:ss";

    private static final SimpleDateFormat timestampFormatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
    private static final SimpleDateFormat timestamp_date_Formatter = new SimpleDateFormat(TIMESTAMP_FORMAT_DATE);
    private static final SimpleDateFormat timestamp_time_Formatter = new SimpleDateFormat(TIMESTAMP_FORMAT_TIME);

    public static float calcSpeed(double distance, long time){

        // speed - m/s
        // distance - m
        // time - ms

        float speed = time == 0 ? 0 : (float) distance / (time/1000);
        return speed;
    }

    public static String getDistance(double distance){
        // Convert m to km
        return String.format("%.2f", distance / 1000);
    }

    public static String getSpeed(float speed){
        // Convert m/s to km/h
        float speedKmh = speed * 3.6f;
        return String.format("%.2f", speedKmh);
    }

    public static String getFullTime(long time){
        return timestampFormatter.format(time);
    }

    public static String getDate(long time){
        return timestamp_date_Formatter.format(time);
    }

    public static String getDate(Date time){
        return timestamp_date_Formatter.format(time);
    }

    public static String getTime(long time){
        if (time==0) return "";
        return timestamp_time_Formatter.format(time);
    }

    public static String getDuration(long time){

        long durationSec = (time)/1000;

        int durationHour = (int) durationSec/(60*60);

        durationSec = durationSec - durationHour * 60*60;

        int durationMin = (int) durationSec/60;

        durationSec = durationSec - durationMin * 60;

        return String.format("%1$02d:%2$02d:%3$02d", durationHour, durationMin, durationSec);
    }

    public static String getDuration(long time1, long time2){

        return getDuration(time2 - time1);

    }

}
