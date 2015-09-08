package com.pawnua.android.app.gpstrips;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.BaseColumns;
import android.webkit.MimeTypeMap;

import com.pawnua.android.app.gpstrips.activities.TripGalleryViewPagerActivity;
import com.pawnua.android.app.gpstrips.model.Trip;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MiK on 14.08.2015.
 */
public class GalleryDataManager {

    public static String GALLERY_LOCATION = "GpsTripsGallery";
    public static String GALLERY_CURRENT_ITEM_PATH = "CurrentItemPath";

    public static File createImageGallery() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File mGalleryFolder = new File(storageDirectory, GALLERY_LOCATION);
        if(!mGalleryFolder.exists()) {
            mGalleryFolder.mkdirs();
        }
        return mGalleryFolder;
    }

    public static File createImageGallery(Trip trip) {

        if (trip== null){
            return createImageGallery();
        }

        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File mGalleryFolder = new File(storageDirectory, GALLERY_LOCATION + "/" +trip.getId());
        if(!mGalleryFolder.exists()) {
            mGalleryFolder.mkdirs();
        }
        return mGalleryFolder;
    }

    public static File createImageFile(File fileFolder) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";

        File image = File.createTempFile(imageFileName,".jpg", fileFolder);

        return image;

    }


    public static void openGallery(Context context, Trip trip, String filepath){

        Intent intent = new Intent(context, TripGalleryViewPagerActivity.class);
        if (trip!=null) {
            intent.putExtra(BaseColumns._ID, trip.getId());
        }
        intent.putExtra(GALLERY_CURRENT_ITEM_PATH, filepath);

        context.startActivity(intent);

    }

    public static File[] getFileImages(File mGalleryFolder) {
        return mGalleryFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String mimeType = getMimeType(pathname.getName());
                return pathname.isFile() && pathname.length() > 0 && mimeType.contains("image");
            }
        });
    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
