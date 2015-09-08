package com.pawnua.android.app.gpstrips.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.fragments.AboutFragment;
import com.pawnua.android.app.gpstrips.fragments.TripsFragment;
import com.pawnua.android.app.gpstrips.services.LocationService;

/**
 * Created by MiK on 29.07.2015.
 */
public class TripsActivity extends AppCompatActivity {

    private final String TAG = "TripsActivity_PAWNUA";

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private FragmentManager mFragmentManager;
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        // If LocationService started open TripDetailActivity
        if (LocationService.isInstanceCreated()){
            Intent notificationIntent = new Intent(this, TripDetailActivity.class);
            notificationIntent.putExtra(BaseColumns._ID, LocationService.getRunningTripId());
            startActivity(notificationIntent);
            finish();
            return;
        }

        mFragmentManager = getSupportFragmentManager();

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);

        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

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

        // Open TripsFragment
        TripsFragment fragment = new TripsFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }

    public void startLocationService() {
        Log.d(TAG, "startLocationService");
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    public void stopLocationService() {
        Log.d(TAG, "stopLocationService");
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_startService: {
                startLocationService();
                return true;
            }
            case R.id.action_stopService: {
                stopLocationService();
                return true;
            }

            case R.id.action_about: {
                new AboutFragment().show(mFragmentManager, "about_layout");
                return true;
            }

        }


        return super.onOptionsItemSelected(item);
    }
}
