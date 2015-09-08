package com.pawnua.android.app.gpstrips.db;

import com.activeandroid.query.Select;
import com.pawnua.android.app.gpstrips.model.Trip;

import java.util.List;

/**
 * Created by MiK on 01.08.2015.
 */
public class TripDataManager {

    public Trip addTrip(String name, String description){

        Trip trip = new Trip();
//        trip.name = name;
//        trip.description = description;
        trip.save();

        return trip;

    }

    public static List<Trip> getAllTrips() {
        return new Select()
                .from(Trip.class)
                .orderBy("Name ASC")
                .execute();
    }
}
