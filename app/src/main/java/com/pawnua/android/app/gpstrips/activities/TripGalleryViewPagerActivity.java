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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pawnua.android.app.gpstrips.GalleryDataManager;
import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.TouchImageView;
import com.pawnua.android.app.gpstrips.model.Trip;

import java.io.File;
import java.io.FileFilter;

public class TripGalleryViewPagerActivity extends AppCompatActivity {

    private Context mContext;

    private FragmentManager mFragmentManager;
    private File mGalleryFolder;

    private Trip trip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_gallery_viewpager);

        // https://developer.android.com/intl/ru/training/system-ui/immersive.html
        View decorView = getWindow().getDecorView();
// Hide the status bar (Sticky Immersion)
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);



    trip = Trip.getTripByID(getIntent().getLongExtra(BaseColumns._ID, -1));

        int setCurrentItem = getIntent().getIntExtra("CurrentItem", 0);
        String currentItem = getIntent().getStringExtra(GalleryDataManager.GALLERY_CURRENT_ITEM_PATH);

        mFragmentManager = getSupportFragmentManager();
        mContext = this;

        mGalleryFolder = GalleryDataManager.createImageGallery(trip);

        // disable toolbar
//        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle(getTripTitle(trip));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            GalleryViewPagerAdapter galleryViewPagerAdapter = new GalleryViewPagerAdapter(mFragmentManager, GalleryDataManager.getFileImages(mGalleryFolder));
            viewPager.setAdapter(galleryViewPagerAdapter);
            if (setCurrentItem!=0)
                viewPager.setCurrentItem(setCurrentItem);
            if (currentItem!="")
                viewPager.setCurrentItem(galleryViewPagerAdapter.getItemPosition(currentItem));

        }


    }



    @Override
    protected void onResume() {
        super.onResume();
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

    private class GalleryViewPagerAdapter extends PagerAdapter {

        private File[] fileList;

        public GalleryViewPagerAdapter(FragmentManager fm, File[] fileList) {
            super();
            this.fileList = fileList;
        }

        @Override
        public int getItemPosition(Object object) {

            String filename = (String) object;

            for(int i = 0; i < getCount(); i++) {

                File item = fileList[i];
                if(item.getAbsolutePath().equals(filename)) {
                    // item still exists in dataset; return position
                    return i;
                }
            }
            return super.getItemPosition(object);
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final TouchImageView img = new TouchImageView(container.getContext());
//            ImageView img = new ImageView(container.getContext());

            // AsyncTask to load image to View
//            new LoadPhotoTask(img).execute(fileList[position].getAbsolutePath());

            // Use Glide

            // It's doesn't work with custom TouchImageView, only ImageView
//            Glide.with(container.getContext())
//                    .load(fileList[position].getAbsolutePath())
//                    .crossFade()
//                    .into(img);

            // try this one

//            int myWidth = 512;
//            int myHeight = 384;
//            .into(new SimpleTarget<Bitmap>(myWidth, myHeight) {

            Glide.with(container.getContext())
                    .load(fileList[position].getAbsolutePath())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            img.setImageBitmap(bitmap);
                        }
                    });

            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public int getCount() {
            return fileList.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    public class LoadPhotoTask extends AsyncTask<String, Void, Drawable> {
        private TouchImageView imageView;

        public LoadPhotoTask(TouchImageView imageView) {
            this.imageView = imageView;
        }

        protected Drawable doInBackground(String... params) {

            Drawable drawable = null;
            if(isCancelled()) {
                return drawable;
            }

            drawable = Drawable.createFromPath(params[0]);
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if(imageView != null) {
                imageView.setImageDrawable(drawable);
            }
        }
    }
}
