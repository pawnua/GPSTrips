/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pawnua.android.app.gpstrips.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.pawnua.android.app.gpstrips.GalleryDataManager;
import com.pawnua.android.app.gpstrips.GeoTagImage;
import com.pawnua.android.app.gpstrips.GpxTrackWriter;
import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.model.TripInfo;
import com.pawnua.android.app.gpstrips.adapters.TripInfoAdapter;
import com.pawnua.android.app.gpstrips.fragments.AboutFragment;
import com.pawnua.android.app.gpstrips.model.LocationUtils;
import com.pawnua.android.app.gpstrips.model.Trip;
import com.pawnua.android.app.gpstrips.model.TripCharacteristics;
import com.pawnua.android.app.gpstrips.model.TripLocation;
import com.pawnua.android.app.gpstrips.services.LocationService;
import com.pawnua.android.app.gpstrips.services.LocationServiceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TripDetailActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "TDActivity_PAWNUA";
    private File photoFile;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private File mGalleryFolder;

    private FragmentManager mFragmentManager;
    private Context context;

    private Trip trip;

    private Boolean isServiceStarted = false;

    private MenuItem menu_startStopService;

    private TripDetailReceiver receiver;

    private TripInfoAdapter adapter;
    private TripCharacteristics tripCharacteristics;

    private TextView tvSpeed;
    private TextView tvMaxSpeed;
    private TextView tvDistance;
//    private TextView tvTime;
//    private TextView tvAltitude;

    private TextView tvStartTime;
    private TextView tvFinishTime;
    private TextView tvDurationTrip;

    private RecyclerView recyclerView;

    private String photoFileString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        context = this;

        // restore photoFile after Camera shooting in portrait mode
        if (savedInstanceState!=null){
            photoFileString = savedInstanceState.getString("photoFile", "");
            if (photoFileString!=""){
                photoFile = new File(photoFileString);
            }
        }


        Typeface custom_font = Typeface.createFromAsset(getAssets(), "font/WLM.ttf");

        tvSpeed = (TextView) findViewById(R.id.currentSpeed);


        tvSpeed.setTypeface(custom_font);
//        tvMaxSpeed.setTypeface(custom_font);

        trip = Trip.getTripByID(getIntent().getLongExtra(BaseColumns._ID, -1));

        tripCharacteristics = TripCharacteristics.getTripCharacteristics(trip);
        if (tripCharacteristics == null) initTripCharacteristics(trip);

        // initRecyclerView
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        adapter = new TripInfoAdapter(TripInfo.initTripInfo(this, tripCharacteristics));

        recyclerView = (RecyclerView) findViewById(R.id.trip_infoRecyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        // ... initRecyclerView


        mFragmentManager = getSupportFragmentManager();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(this);

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getTripTitle(trip));

//        CollapsingToolbarLayout collapsingToolbar =
//                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle(tripName);

        mGalleryFolder = GalleryDataManager.createImageGallery(trip);

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter("Data");
        receiver = new TripDetailReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        isServiceStarted = LocationService.isInstanceCreated();

//        showDistance(trip);


    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

    }

    private void setMenuVisible() {
        if (menu_startStopService != null) {
            if (isServiceStarted == true) {
                // stop
                menu_startStopService.setIcon(R.drawable.ic_pause_white_48dp);
            }
            else{
                // start
                menu_startStopService.setIcon(R.drawable.ic_play_arrow_white_48dp);
            }

        }
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

            case R.id.action_startStopService: {
                if (isServiceStarted){
                    LocationServiceManager.stopLocationService(this, trip);
                    isServiceStarted = !isServiceStarted;
                    setMenuVisible();
                }
                else{
                    LocationServiceManager.startLocationService(this, trip);
                    isServiceStarted = !isServiceStarted;
                    setMenuVisible();
                }

                return true;
            }

            case R.id.action_startService: {
                LocationServiceManager.startLocationService(this, trip);
                setMenuVisible();
                return true;
            }
            case R.id.action_stopService: {
                LocationServiceManager.stopLocationService(this, trip);
                setMenuVisible();
                return true;
            }

            case R.id.action_showLocationList: {
                Intent intent = new Intent(this, TripLocationListActivity.class);
                intent.putExtra(BaseColumns._ID, trip.getId());

                startActivity(intent);
                return true;
            }

            case R.id.action_openMap: {
                openMap();
                return true;
            }

            case R.id.action_about: {
                new AboutFragment().show(mFragmentManager, "about_layout");
                return true;
            }

            case R.id.action_export: {

                saveTripTask(trip);
                return true;
            }

            case R.id.action_exportDisk: {

                Intent intent = new Intent(this, DiskCreateFileActivity.class);
                intent.putExtra(BaseColumns._ID, trip.getId());

                startActivity(intent);

                return true;
            }

            case R.id.action_show_camera: {

                takePhoto();
                return true;
            }

            case R.id.action_show_gallery: {

                openThumbGallery();
                return true;
            }

            case R.id.action_show_gallery_vp: {

                // open Gallery
                GalleryDataManager.openGallery(this, trip, "");

                return true;
            }

            case R.id.action_share: {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, trip.getName());
                sendIntent.setType("message/rfc822");
//                sendIntent.setType("text/plain");

                Intent openInChooser = Intent.createChooser(sendIntent, "");

                startActivity(openInChooser);

                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        //Checking if the item is in checked state or not, if not make it in checked state
        if (menuItem.isChecked()) menuItem.setChecked(false);
        else menuItem.setChecked(true);

        //Closing drawer on item click
        drawerLayout.closeDrawers();

        //Check to see which item was being clicked and perform appropriate action
        switch (menuItem.getItemId()) {

            case R.id.action_openMap:
                openMap();
                return true;

            case R.id.action_show_camera: {

                takePhoto();
                return true;
            }

            case R.id.action_show_gallery: {

                openThumbGallery();
                return true;
            }

            case R.id.action_export: {

                saveTripTask(trip);
                return true;
            }

            case R.id.action_about: {
                new AboutFragment().show(mFragmentManager, "about_layout");
                return true;
            }

            default:
                Toast.makeText(getApplicationContext(), R.string.SmthWrong, Toast.LENGTH_SHORT).show();
                return true;

        }
    }

    public void openThumbGallery() {
        Intent intent = new Intent(this, TripGalleryActivity.class);
        intent.putExtra(BaseColumns._ID, trip.getId());

        startActivity(intent);
    }

    public void openMap() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(BaseColumns._ID, trip.getId());

        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // photoFile == null (reCreate activity) after Camera shoot
        if (photoFile!=null)
            outState.putString("photoFile",photoFile.getAbsolutePath());
    }

    private void takePhoto() {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            photoFile = GalleryDataManager.createImageFile(mGalleryFolder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }

    private void saveTripTask(final Trip trip) {
        new AsyncTask<Trip, Void, Void>() {

            @Override
            protected Void doInBackground(Trip... params) {
                GpxTrackWriter.saveTrip(params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.d(TAG, "TripDetailReceiver saveTripTask executed");
                Toast.makeText(context, getString(R.string.file_exported), Toast.LENGTH_SHORT);
            }

        }.execute(trip);
    }



    private void showDistance(Trip trip) {
        new AsyncTask<Trip, Void, Void>() {

            private List<LatLng> coordinates;
            private double distance;

            @Override
            protected Void doInBackground(Trip... params) {
                List<TripLocation> tripLocations = TripLocation.getAllLocations(params[0]);
                if (tripLocations != null && !tripLocations.isEmpty()) {
                    coordinates = new ArrayList<LatLng>();
                    for (TripLocation tripLocation : tripLocations) {
                        LatLng latLng = new LatLng(tripLocation.getLatitude(), tripLocation.getLongitude());
                        coordinates.add(latLng);
                    }
                }

                if (coordinates == null || coordinates.isEmpty()) {
                } else {
                    distance = SphericalUtil.computeLength(coordinates);
                }

                // update tripCharacteristics
//                tripCharacteristics = TripLocation.getTripCharacteristics(params[0]);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (coordinates == null || coordinates.isEmpty()) {
                } else {
                    TripInfo.setValue(adapter, TripInfo.DISTANCE, String.format("%.2f", distance / 1000));
//                    TripInfo.setValue(adapter, TripInfo.AVG_SPEED, LocationUtils.getSpeed(distance, tripCharacteristics.getMovingTime()));

/*
                    tvDistance.setText(String.format("%.2f", distance/1000));
*/
                }
            }

        }.execute(trip);
    }

    private void initTripCharacteristics(Trip trip) {
        new AsyncTask<Trip, Void, Void>() {

            @Override
            protected Void doInBackground(Trip... params) {

                tripCharacteristics = TripCharacteristics.initTripCharacteristics(params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                adapter = new TripInfoAdapter(TripInfo.initTripInfo(context, tripCharacteristics));
                recyclerView.swapAdapter(adapter, true);

            }

        }.execute(trip);
    }

    private void MarkGeoTagImage(final Trip trip) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (photoFile!=null && photoFile.exists()){
                    Location location = GeoTagImage.readGeoTagImage(photoFile.getAbsolutePath());
                    if (location==null){
                        location = TripLocation.getLastLocation(trip);

                        GeoTagImage.MarkGeoTagImage(photoFile.getAbsolutePath(), location);
                    }
                }

            }
        }).start();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            MarkGeoTagImage(trip);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_detail, menu);
        this.menu_startStopService = menu.findItem(R.id.action_startStopService);
        setMenuVisible();
        return true;
    }


    private class TripDetailReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "TripDetailReceiver onReceive " + intent.toString());
            float speed = intent.getFloatExtra(LocationUtils.SPEED, 0);
            long time = intent.getLongExtra(LocationUtils.TIME, 0);
            double altitude = intent.getDoubleExtra(LocationUtils.ALTITUDE, 0);

//            float maxSpeed = intent.getFloatExtra(LocationUtils.MAX_SPEED, 0);

            tvSpeed.setText(LocationUtils.getSpeed(speed));

            tripCharacteristics = TripCharacteristics.getTripCharacteristics(trip);

            adapter = new TripInfoAdapter(TripInfo.initTripInfo(context, tripCharacteristics));
            adapter.notifyDataSetChanged();
            recyclerView.swapAdapter(adapter, false);

//            TripInfo.setValue(adapter, TripInfo.MAX_SPEED, LocationUtils.getSpeed(maxSpeed));
/*
            tvMaxSpeed.setText(LocationUtils.getSpeed(maxSpeed));
*/

//            tvTime.setText(LocationUtils.getTime(time));
//            tvAltitude.setText(String.format("%s", altitude));
        }
    }

}
