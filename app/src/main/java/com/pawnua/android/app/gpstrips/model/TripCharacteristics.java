package com.pawnua.android.app.gpstrips.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.pawnua.android.app.gpstrips.services.LocationService;

import java.util.List;

/**
 * Created by MiK on 21.08.2015.
 */
@Table(name = "TripCharacteristics", id = "_id")
public class TripCharacteristics extends Model {

    // 10 ratio of measurement
    final static long MIN_TIME_MEASURE = 10 * LocationService.MIN_TIME;

    @Column(name = "Trip")
    private Trip trip;

    @Column(name = "TripLocation")
    private TripLocation tripLocation;

    @Column(name = "MaxSpeed")
    private float maxSpeed;

    @Column(name = "AvgSpeed")
    private float avgSpeed;

    @Column(name = "Duration")
    private long duration;

    @Column(name = "StartTime")
    private long startTime;

    @Column(name = "FinishTime")
    private long finishTime;

    @Column(name = "MovingTime")
    private long movingTime;

    @Column(name = "Distance")
    private double distance;

    @Column(name = "MovingDistance")
    private double movingDistance;

    public TripLocation getTripLocation() {
        return tripLocation;
    }

    public void setTripLocation(TripLocation tripLocation) {
        this.tripLocation = tripLocation;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public long getMovingTime() {
        return movingTime;
    }

    public void setMovingTime(long movingTime) {
        this.movingTime = movingTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public double getMovingDistance() {
        return movingDistance;
    }

    public void setMovingDistance(double movingDistance) {
        this.movingDistance = movingDistance;
    }

    public TripCharacteristics() {
        super();
    }

    public TripCharacteristics(Trip trip) {
        super();
        this.trip = trip;
    }

    public static void deleteTripCharacteristics(Trip trip) {

        ActiveAndroid.beginTransaction();
        try{
            new Delete().from(TripCharacteristics.class).where("Trip = ?", trip.getId()).execute();
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }

    }

    public static TripCharacteristics getTripCharacteristics(Trip trip) {
        TripCharacteristics tripCharacteristics = new Select()
                .from(TripCharacteristics.class)
                .where("Trip = ?", trip.getId())
                .executeSingle();

//        if (tripCharacteristics==null) {
//            tripCharacteristics = new TripCharacteristics(trip);
//        }

        return tripCharacteristics;
    }

    public static TripCharacteristics updateTripCharacteristics(Trip trip, TripLocation newTripLocation) {

        double deltaDistance;

        long deltaTime = 0;

        TripCharacteristics tripCharacteristics = getTripCharacteristics(trip);

        if (tripCharacteristics==null)
            tripCharacteristics = initTripCharacteristics(trip);

        double distance = tripCharacteristics.getDistance();
        double movingDistance = tripCharacteristics.getMovingDistance();
        float maxSpeed = tripCharacteristics.getMaxSpeed();
        long movingTime = tripCharacteristics.getMovingTime();

        TripLocation prevTripLocation = tripCharacteristics.getTripLocation();

        // calc maxSpeed
        if (newTripLocation.getSpeed() > maxSpeed){
            maxSpeed = newTripLocation.getSpeed();
        }

        if (prevTripLocation != null){

            // distance
            LatLng prevLatLng = new LatLng(prevTripLocation.getLatitude(), prevTripLocation.getLongitude());
            LatLng latLng = new LatLng(newTripLocation.getLatitude(), newTripLocation.getLongitude());
            deltaDistance = SphericalUtil.computeDistanceBetween(prevLatLng, latLng);
            distance += deltaDistance;

            // calc movingTime
            deltaTime = newTripLocation.getTime() - prevTripLocation.getTime();

            if (deltaTime <= MIN_TIME_MEASURE && newTripLocation.getSpeed() > 0) {

                movingTime += deltaTime;

                // moving distance
                movingDistance += deltaDistance;

            }
        }

        if (tripCharacteristics.getStartTime()==0)
            tripCharacteristics.setStartTime(newTripLocation.getTime());

        tripCharacteristics.setDistance(distance);
        tripCharacteristics.setMaxSpeed(maxSpeed);
        tripCharacteristics.setMovingTime(movingTime);
        tripCharacteristics.setMovingDistance(movingDistance);
        tripCharacteristics.setFinishTime(newTripLocation.getTime());
        tripCharacteristics.setAvgSpeed(LocationUtils.calcSpeed(movingDistance, movingTime));
        tripCharacteristics.setDuration(newTripLocation.getTime() - tripCharacteristics.getStartTime());
        tripCharacteristics.setTripLocation(newTripLocation);

        tripCharacteristics.save();

        return tripCharacteristics;
    }

    public static TripCharacteristics initTripCharacteristics(Trip trip) {

        double deltaDistance;

        double distance = 0;
        double movingDistance = 0;
        float maxSpeed = 0;
        long startTime = 0;
        long finishTime = 0;
        long movingTime = 0;

        long deltaTime = 0;

        TripCharacteristics tripCharacteristics = new TripCharacteristics(trip);

        List<TripLocation> tripLocations = TripLocation.getAllLocations(trip);

        if (tripLocations.size() == 0) {
            tripCharacteristics.save();
            return tripCharacteristics;
        }

        TripLocation firstTripLocation = tripLocations.get(0);
        TripLocation lastTripLocation = tripLocations.get(tripLocations.size() - 1);

        long prevTime = firstTripLocation.getTime();

        LatLng prevLatLng = new LatLng(firstTripLocation.getLatitude(), firstTripLocation.getLongitude());

        maxSpeed = firstTripLocation.getSpeed();

        for (int i = 1; i < tripLocations.size(); i++){

            TripLocation tripLocation = tripLocations.get(i);

            // calc maxSpeed
            if (tripLocation.getSpeed() > maxSpeed){
                maxSpeed = tripLocation.getSpeed();
            }

            // distance
            LatLng latLng = new LatLng(tripLocation.getLatitude(), tripLocation.getLongitude());
            deltaDistance = SphericalUtil.computeDistanceBetween(prevLatLng, latLng);
            distance += deltaDistance;
            prevLatLng = latLng;

            // calc movingTime
            deltaTime = tripLocation.getTime() - prevTime;
            prevTime = tripLocation.getTime();

            if (deltaTime <= MIN_TIME_MEASURE && tripLocation.getSpeed() > 0) {
                movingTime += deltaTime;

                // moving distance
                movingDistance += deltaDistance;

            }

        }

        startTime = firstTripLocation.getTime();
        finishTime = lastTripLocation.getTime();

        tripCharacteristics.setDistance(distance);
        tripCharacteristics.setMaxSpeed(maxSpeed);
        tripCharacteristics.setMovingTime(movingTime);
        tripCharacteristics.setMovingDistance(movingDistance);
        tripCharacteristics.setStartTime(startTime);
        tripCharacteristics.setFinishTime(finishTime);
        tripCharacteristics.setAvgSpeed(LocationUtils.calcSpeed(movingDistance, movingTime));
        tripCharacteristics.setDuration(finishTime - startTime);
        tripCharacteristics.setTripLocation(lastTripLocation);

        tripCharacteristics.save();

        return tripCharacteristics;
    }

}
