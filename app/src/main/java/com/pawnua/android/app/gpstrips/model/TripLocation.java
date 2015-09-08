package com.pawnua.android.app.gpstrips.model;

import android.location.Location;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.pawnua.android.app.gpstrips.services.LocationService;

import java.util.List;

/**
 * Created by MiK on 01.08.2015.
 */
@Table(name = "TripLocation", id = "_id")
public class TripLocation extends Model {

    @Column(name = "Trip")
    private Trip trip;

    @Column(name = "Latitude")
    private double latitude;

    @Column(name = "Longitude")
    private double longitude;

    @Column(name = "Time")
    private long time;

    @Column(name = "Speed")
    private float speed;

    @Column(name = "Altitude")
    private double altitude;

    @Column(name = "Bearing")
    private float bearing;

    @Column(name = "Accuracy")
    private float accuracy;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTime() {
        return time;
    }

    public float getSpeed() {
        return speed;
    }

    public double getAltitude() {
        return altitude;
    }

    public static List<TripLocation> getAllLocations(Trip trip) {
        return new Select()
                .from(TripLocation.class)
                .where("Trip = ?", trip.getId())
                .orderBy("Time ASC")
                .execute();
    }

    public static float getMaxSpeed(Trip trip) {
        TripLocation tripLocation = new Select()
                .from(TripLocation.class)
                .where("Trip = ?", trip.getId())
                .orderBy("Speed DESC")
                .executeSingle();

        if (tripLocation == null){
            return 0;
        }

        return tripLocation.getSpeed();
    }

//    public static TripCharacteristics getTripCharacteristics(Trip trip) {
//
//        // 10 ratio of measurement
//        final long MIN_TIME_MEASURE = 10 * LocationService.MIN_TIME;
//
//        List<TripLocation> tripLocations = new Select()
//                .from(TripLocation.class)
//                .where("Trip = ?", trip.getId())
//                .orderBy("Time ASC")
//                .execute();
//
//        float maxSpeed = 0;
//        long startTime = 0;
//        long finishTime = 0;
//        long movingTime = 0;
//
//        long deltaTime = 0;
//        long prevTime = tripLocations.size() != 0 ? tripLocations.get(0).getTime(): 0;
//
//        for (TripLocation tripLocation:tripLocations){
//            // calc maxSpeed
//            if (tripLocation.getSpeed() > maxSpeed){
//                maxSpeed = tripLocation.getSpeed();
//            }
//
//            // calc movingTime
//            deltaTime = tripLocation.getTime() - prevTime;
//
//            if (deltaTime < MIN_TIME_MEASURE) {
//                movingTime = movingTime + deltaTime;
//            }
//
//            prevTime = tripLocation.getTime();
//        }
//
//        if (tripLocations.size() > 0){
//            startTime = tripLocations.get(0).getTime();
//            finishTime = tripLocations.get(tripLocations.size() - 1).getTime();
//        }
//
//        TripCharacteristics tripCharacteristics = new TripCharacteristics(trip);
//        tripCharacteristics.setMaxSpeed(maxSpeed);
//        tripCharacteristics.setStartTime(startTime);
//        tripCharacteristics.setFinishTime(finishTime);
//        tripCharacteristics.setMovingTime(movingTime);
//
//        return tripCharacteristics;
//    }

    public static Location getLastLocation(Trip trip) {

        Location location = null;

        TripLocation tripLocation = new Select()
                .from(TripLocation.class)
                .where("Trip = ?", trip.getId())
                .orderBy("Time DESC")
                .executeSingle();

        if (tripLocation == null){
            return location;
        }

        location = new Location("");
        location.setLatitude(tripLocation.getLatitude());
        location.setLongitude(tripLocation.getLongitude());
        location.setTime(tripLocation.getTime());

        return location;
    }

    public TripLocation() {
        super();
    }

    public TripLocation(Trip trip, double latitude, double longitude, long time, float speed, double altitude, float bearing, float accuracy) {
        super();
        this.trip = trip;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.speed = speed;
        this.altitude = altitude;
        this.bearing = bearing;
        this.accuracy = accuracy;
    }

    public static class Builder {

        private Trip trip;

        private double latitude;
        private double longitude;
        private long time;
        private float speed;
        private double altitude;
        private float bearing;
        private float accuracy;

        public Builder(Trip trip) {
            this.trip = trip;
        }

        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setTime(long time) {
            this.time = time;
            return this;
        }

        public Builder setSpeed(float speed) {
            this.speed = speed;
            return this;
        }

        public Builder setAltitude(double altitude) {
            this.altitude = altitude;
            return this;
        }

        public Builder setBearing(float bearing) {
            this.bearing = bearing;
            return this;
        }

        public Builder setAccuracy(float accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public TripLocation build() {
            return new TripLocation(trip, latitude, longitude, time, speed, altitude, bearing, accuracy);
        }

    }
}
