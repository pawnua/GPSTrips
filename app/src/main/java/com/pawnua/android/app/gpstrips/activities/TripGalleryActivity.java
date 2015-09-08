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

import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.pawnua.android.app.gpstrips.GalleryDataManager;
import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.adapters.ImageAdapter;
import com.pawnua.android.app.gpstrips.model.Trip;

import java.io.File;

public class TripGalleryActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private File mGalleryFolder;

    private Trip trip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_gallery);

        trip = Trip.getTripByID(getIntent().getLongExtra(BaseColumns._ID, -1));

        mFragmentManager = getSupportFragmentManager();

        mGalleryFolder = GalleryDataManager.createImageGallery(trip);

        initRecyclerView();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getTripTitle(trip));

    }

    private void initRecyclerView() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        int totalCols = width / 200 - 1;

        if (totalCols < 2)
            totalCols = 2;

        LinearLayoutManager layoutManager = new GridLayoutManager(this, totalCols);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        ImageAdapter adapter = new ImageAdapter(this, GalleryDataManager.getFileImages(mGalleryFolder), trip);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.trip_galleryRecyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_trip_detail, menu);
        return true;
    }

}
