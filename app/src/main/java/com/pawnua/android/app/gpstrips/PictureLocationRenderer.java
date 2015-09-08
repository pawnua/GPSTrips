package com.pawnua.android.app.gpstrips;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.pawnua.android.app.gpstrips.model.PictureLocation;

/**
 * Created by MiK on 13.08.2015.
 */
public class PictureLocationRenderer extends DefaultClusterRenderer<PictureLocation> {

    private final IconGenerator mIconGenerator;
    private final ImageView mImageView;
    private final int mDimension;

    public PictureLocationRenderer(Context context, GoogleMap map, ClusterManager<PictureLocation> clusterManager) {
        super(context, map, clusterManager);
        mIconGenerator = new IconGenerator(context);

        mImageView = new ImageView(context);
        mDimension = (int) context.getResources().getDimension(R.dimen.custom_profile_image);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_profile_padding);
        mImageView.setPadding(padding, padding, padding, padding);
        mIconGenerator.setContentView(mImageView);

    }

    @Override
    protected void onBeforeClusterItemRendered(PictureLocation pictureLocation,
                                               MarkerOptions markerOptions) {

        mImageView.setImageBitmap(pictureLocation.getPoster());
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(pictureLocation.getPoster()));

/*
                // add photos (Markers)
                for (PictureLocation pictureLocation: pictureLocations) {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(pictureLocation.getPosition())
//                            .title("title")
//                            .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//                            .icon(BitmapDescriptorFactory.fromBitmap(pictureLocation.getPoster()))
//                            .snippet("snippet")
                    );

                }
*/

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }


}
