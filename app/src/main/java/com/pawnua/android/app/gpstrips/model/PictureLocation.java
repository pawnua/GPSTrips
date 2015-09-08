package com.pawnua.android.app.gpstrips.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.pawnua.android.app.gpstrips.GeoTagImage;
import com.pawnua.android.app.gpstrips.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by MiK on 11.08.2015.
 */
public class PictureLocation implements ClusterItem {
    private String imagePath;
    private LatLng position;
    private Bitmap poster;

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public PictureLocation(String imagePath, LatLng position) {
        this.imagePath = imagePath;
        this.position = position;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }


    public static List<PictureLocation> getImagesWithLocation(String directory, Context mContext){

        List<PictureLocation> pictureLocations = new ArrayList<>();

        File folder = new File(directory);
        File[] files = folder.listFiles();

        String filepath;

        int mDimension = (int) mContext.getResources().getDimension(R.dimen.custom_profile_image);

        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isFile()){
                filepath = files[i].getAbsolutePath();
                Location location = GeoTagImage.readGeoTagImage(filepath);
                if (location!=null){
                    final PictureLocation pictureLocation = new PictureLocation(filepath, new LatLng(location.getLatitude(), location.getLongitude()));

                    // Get resized picture
//                    Bitmap bitmap = getBitmap(filepath);
//                    pictureLocation.setPoster(bitmap);

                    // Use Glide to load resized croped picture

                    try {
                        Bitmap bitmap = Glide.with(mContext)
                                .load(filepath)
                                .asBitmap()
                                .centerCrop()
                                .into(mDimension, mDimension)
                                .get();
                        pictureLocation.setPoster(bitmap);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }


                    pictureLocations.add(pictureLocation);
                }
            }
        }

        return pictureLocations;
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap getBitmap(String filepath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 25, 25);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filepath, options);
    }
}
