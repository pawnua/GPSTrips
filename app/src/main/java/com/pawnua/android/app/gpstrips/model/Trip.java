package com.pawnua.android.app.gpstrips.model;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.pawnua.android.app.gpstrips.R;

import java.util.Date;
import java.util.List;

/**
 * Created by MiK on 01.08.2015.
 */
@Table(name = "Trips", id = "_id")
public class Trip extends Model {

    @Column(name = "Date")
    private long date;

    @Column(name = "Name")
    private String name;

    @Column(name = "Description")
    private String description;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Trip() {
        super();
    }

    public Trip(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    public static Trip getTripByID(long id) {
        return Trip.load(Trip.class, id);
    }
    public static void addTrip(Trip trip){
        trip.save();
    }

    public static String newTripName(Context c) {
        return String.format("%1s %2s", c.getString(R.string.trip_name), LocationUtils.getDate(new Date()));
    }

    public static void deleteAll(){
        new Delete().from(Trip.class).execute();
    }

    public static List<Trip> getAllTrips() {
        return new Select()
                .from(Trip.class)
                .orderBy("Date DESC, Name ASC")
                .execute();
    }

    public static void deleteTrip(Trip trip) {

        ActiveAndroid.beginTransaction();
        try{
            new Delete().from(TripCharacteristics.class).where("Trip = ?", trip.getId()).execute();
            new Delete().from(TripLocation.class).where("Trip = ?", trip.getId()).execute();
            trip.delete();
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }

    }
}
