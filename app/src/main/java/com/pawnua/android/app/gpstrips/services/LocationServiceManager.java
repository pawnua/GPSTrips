package com.pawnua.android.app.gpstrips.services;

import android.content.Context;
import android.content.Intent;
import android.provider.BaseColumns;

import com.pawnua.android.app.gpstrips.model.Trip;

/**
 * Created by MiK on 01.08.2015.
 */
public class LocationServiceManager {

    public static void startLocationService(Context context, Trip trip) {
        Intent intent = new Intent(context, LocationService.class);
        intent.putExtra(BaseColumns._ID, trip.getId());
        context.startService(intent);
    }

    public static void stopLocationService(Context context, Trip trip) {
        Intent intent = new Intent(context, LocationService.class);
        intent.putExtra(BaseColumns._ID, trip.getId());
        context.stopService(intent);
    }

}
