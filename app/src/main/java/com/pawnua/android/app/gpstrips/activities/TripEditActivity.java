package com.pawnua.android.app.gpstrips.activities;

import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.fragments.AboutFragment;
import com.pawnua.android.app.gpstrips.model.Trip;
import com.pawnua.android.app.gpstrips.services.LocationServiceManager;

import java.util.Date;

/**
 * Created by MiK on 01.08.2015.
 */
public class TripEditActivity extends AppCompatActivity {

    private EditText etTripName;
    private TextInputLayout tilTripName;
    private EditText etTripDescription;
    private TextInputLayout tilTripDescription;

    private Trip trip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_edit);

        trip = Trip.getTripByID(getIntent().getLongExtra(BaseColumns._ID, -1));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getTripTitle(trip));

        // Trip Name
        tilTripName = (TextInputLayout) findViewById(R.id.tilTripName);
        etTripName = (EditText) tilTripName.findViewById(R.id.etTripName);
        if (trip!= null){
            etTripName.setText(trip.getName());
        }
        else {
            etTripName.setText(Trip.newTripName(this));
        }

        tilTripName.setHint(getString(R.string.trip_name));

        // Trip Description
        tilTripDescription = (TextInputLayout) findViewById(R.id.tilTripDescription);
        etTripDescription = (EditText) tilTripDescription.findViewById(R.id.etTripDescription);
        if (trip!= null){
            etTripDescription.setText(trip.getDescription());
        }

        tilTripDescription.setHint(getString(R.string.trip_description));


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

            case R.id.action_save:
                if (trip == null) {
                    trip = new Trip();
                    trip.setDate(new Date().getTime());
                }

                trip.setName(etTripName.getText().toString());
                trip.setDescription(etTripDescription.getText().toString());
                trip.save();

                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_edit, menu);
        return true;
    }

}
