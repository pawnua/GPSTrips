package com.pawnua.android.app.gpstrips.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.activities.TripDetailActivity;
import com.pawnua.android.app.gpstrips.model.LocationUtils;
import com.pawnua.android.app.gpstrips.model.Trip;
import com.pawnua.android.app.gpstrips.model.TripCharacteristics;
import com.pawnua.android.app.gpstrips.model.TripLocation;

/**
 * Created by MiK on 30.07.2015.
 */
public class LocationService extends Service {

    public final static long MIN_TIME = 1000 * 1; // 1 sec
    public final static long MIN_DISTANCE = 1; // 1 meter

    boolean test = false; // true - include NETWORK_PROVIDER

    private static LocationService instance = null;

    private LocationManager locationManager;
    private NotificationManager notificationManager;

    private Notification notification;
    private static final int NOTIFY_ID = 1;

    private final String TAG = "LocationService_PAWNUA";

    private Trip trip;

    private static long runningTripId;

    public static long getRunningTripId() {
        return runningTripId;
    }

    private float maxSpeed;

    private Context mContext;

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

        instance = this;
        runningTripId = -1;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mContext = this;

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startNotification() {
        Intent notificationIntent = new Intent(this, TripDetailActivity.class);
        notificationIntent.putExtra(BaseColumns._ID, trip.getId());;
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(trip.getName())
                .setSmallIcon(R.drawable.ic_directions_bike_white_48dp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        notification  = builder.build();

        startForeground(NOTIFY_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        instance = null;
        runningTripId = -1;

        locationManager.removeUpdates(locationListener);

        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        runningTripId = intent.getLongExtra(BaseColumns._ID, -1);

//        trip = Trip.getTripByID(intent.getLongExtra(BaseColumns._ID, -1));
        trip = Trip.getTripByID(runningTripId);

        maxSpeed = TripLocation.getMaxSpeed(trip);

        startNotification();

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME, MIN_DISTANCE,
                locationListener);

        // just for test, remove
        if (test) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000 * 10, 10,
                    locationListener);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged " + location.toString());

            float currentSpeed = location.getSpeed();

            TripLocation tripLocation = new TripLocation.Builder(trip)
                    .setLatitude(location.getLatitude())
                    .setLongitude(location.getLongitude())
                    .setSpeed(location.getSpeed())
                    .setAltitude(location.getAltitude())
                    .setTime(location.getTime())
                    .setBearing(location.getBearing())
                    .setAccuracy(location.getAccuracy())
                    .build();

            tripLocation.save();

            // update TripCharacteristics
            TripCharacteristics tripCharacteristics = TripCharacteristics.updateTripCharacteristics(trip, tripLocation);

            if (currentSpeed > maxSpeed){
                maxSpeed = currentSpeed;
            }

            Intent intent = new Intent("Data");
//            intent.putExtra(BaseColumns._ID, tripCharacteristics.getId());

            intent.putExtra(LocationUtils.SPEED, currentSpeed);
            intent.putExtra(LocationUtils.TIME, location.getTime());
            intent.putExtra(LocationUtils.ALTITUDE, location.getAltitude());

//            intent.putExtra(LocationUtils.MAX_SPEED, maxSpeed);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled " + provider.toString());
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled " + provider.toString());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged provider: " + provider.toString() + " status: " + status);
        }
    };
}
