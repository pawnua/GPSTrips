package com.pawnua.android.app.gpstrips.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pawnua.android.app.gpstrips.GalleryDataManager;
import com.pawnua.android.app.gpstrips.MapCluster;
import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.fragments.AboutFragment;
import com.pawnua.android.app.gpstrips.model.PictureLocation;
import com.pawnua.android.app.gpstrips.model.LocationUtils;
import com.pawnua.android.app.gpstrips.model.Trip;
import com.pawnua.android.app.gpstrips.model.TripLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MiK on 01.08.2015.
 */
public class MapActivity extends AppCompatActivity implements GoogleMap.OnMarkerDragListener {

    private Trip trip;
    private GoogleMap mMap;
    private File mGalleryFolder;
    private Context mContext;

    private List<TripLocation> tripLocations;

    private MapCluster mapCluster;

    private static final int BOUNDING_BOX_PADDING_PX = 50;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_map);

        mContext = this;

        trip = Trip.getTripByID(getIntent().getLongExtra(BaseColumns._ID, -1));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getTripTitle(trip));

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap!=null) {
//          mMap.setPadding(0,64,0,0);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerDragListener(this);

            UiSettings mapUI = mMap.getUiSettings();
            mapUI.setCompassEnabled(true);
            mapUI.setMyLocationButtonEnabled(true);
            mapUI.setAllGesturesEnabled(true);
            mapUI.setZoomControlsEnabled(true);
        }

        mGalleryFolder = GalleryDataManager.createImageGallery(trip);

        showTrack(trip);

    }

    private String getTripTitle(Trip trip){

        String tripTitle;

        if (trip == null ) {
            tripTitle = getResources().getString(R.string.new_trip);
        }
        else{
            tripTitle = trip.getName();
        }

        return tripTitle;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_about: {
                new AboutFragment().show(getSupportFragmentManager(), "about_layout");
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_map, menu);
        return true;
    }

    private void showTrack(final Trip trip) {
        new AsyncTask<Trip, Void, Void>() {

            private List<LatLng> coordinates;
            private LatLngBounds bounds;
            private List<PictureLocation> pictureLocations;
            private TripLocation tripMaxSpeed;
            private float maxSpeed;

            @Override
            protected Void doInBackground(Trip... params) {
                tripLocations = TripLocation.getAllLocations(params[0]);
                maxSpeed = 0;
                float currentSpeed = 0;
                if (tripLocations != null && !tripLocations.isEmpty()) {
                    coordinates = new ArrayList<LatLng>();
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (TripLocation tripLocation : tripLocations) {
                        LatLng latLng = new LatLng(tripLocation.getLatitude(), tripLocation.getLongitude());
                        builder.include(latLng);
                        coordinates.add(latLng);

                        currentSpeed = tripLocation.getSpeed();

                        if (currentSpeed > maxSpeed){
                            maxSpeed = currentSpeed;
                            tripMaxSpeed = tripLocation;
                        }


                    }
                    bounds = builder.build();
                }

                pictureLocations = PictureLocation.getImagesWithLocation(mGalleryFolder.getAbsolutePath(), mContext);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if (mMap == null) return;

                mMap.clear();

                // add track
                if (coordinates == null || coordinates.isEmpty()) {
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                            BOUNDING_BOX_PADDING_PX));
                    mMap.addPolyline(new PolylineOptions().geodesic(true).color(Color.RED).addAll(coordinates));
                }

                if (coordinates.size() > 0){
                    // add start marker
                    mMap.addMarker(new MarkerOptions()
                            .position(coordinates.get(0))
                            .title(getString(R.string.start))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    );

                    // add end marker
                    mMap.addMarker(new MarkerOptions()
                            .position(coordinates.get(coordinates.size() - 1))
                            .title(getString(R.string.end))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    );

                    // add max speed marker
                    if (tripMaxSpeed!=null) {

                        LatLng latLng = new LatLng(tripMaxSpeed.getLatitude(), tripMaxSpeed.getLongitude());

                        mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(LocationUtils.getSpeed(maxSpeed) + " " + getString(R.string.string_kmh))
                                        .draggable(true)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        );
                    }

                }

                // add marker clustering
                // https://developers.google.com/maps/documentation/android/utility/marker-clustering

                mapCluster = new MapCluster(mContext, mMap, trip, pictureLocations);
                mapCluster.setUpMapCluster();

            }

        }.execute(trip);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        marker.showInfoWindow();
    }

    @Override
    public void onMarkerDrag(Marker marker) {

        // find nearest track point and show speed
        TripLocation tripMinDelta = findNearestTripLocation(marker);

        if (tripMinDelta!=null) {
            marker.setTitle(LocationUtils.getSpeed(tripMinDelta.getSpeed()) + " " + getString(R.string.string_kmh));
            marker.showInfoWindow();
        }

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        // find nearest track point and move marker there
        TripLocation tripMinDelta = findNearestTripLocation(marker);

        if (tripMinDelta!=null) {
            LatLng latLng = new LatLng(tripMinDelta.getLatitude(), tripMinDelta.getLongitude());
            marker.setTitle(LocationUtils.getSpeed(tripMinDelta.getSpeed()) + " " + getString(R.string.string_kmh));
            marker.setPosition(latLng);
            marker.showInfoWindow();
        }

    }

    public TripLocation findNearestTripLocation(Marker marker) {

        double minDelta = 100;
        double cDelta = 100;

        double markerLat = marker.getPosition().latitude;
        double markerLon = marker.getPosition().longitude;

        TripLocation tripMinDelta = null;

        for (TripLocation tripLocation : tripLocations) {
            cDelta = Math.pow(tripLocation.getLatitude() - markerLat, 2) + Math.pow(tripLocation.getLongitude() - markerLon,2);

            if (cDelta < minDelta) {
                tripMinDelta = tripLocation;
                minDelta = cDelta;
            }
        }
        return tripMinDelta;
    }
}
