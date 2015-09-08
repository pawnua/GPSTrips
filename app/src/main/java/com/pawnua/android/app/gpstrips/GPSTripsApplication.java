package com.pawnua.android.app.gpstrips;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by MiK on 29.07.2015.
 */
public class GPSTripsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }

}
