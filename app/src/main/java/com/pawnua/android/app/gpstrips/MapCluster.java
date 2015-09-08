package com.pawnua.android.app.gpstrips;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.pawnua.android.app.gpstrips.model.PictureLocation;
import com.pawnua.android.app.gpstrips.model.Trip;

import java.util.List;

/**
 * Created by MiK on 13.08.2015.
 */
public class MapCluster implements ClusterManager.OnClusterClickListener<PictureLocation>, ClusterManager.OnClusterItemClickListener<PictureLocation> {

    private GoogleMap mMap;
    private Context mContext;
    private List<PictureLocation> values;
    private Trip trip;

    private ClusterManager<PictureLocation> mClusterManager;

    public MapCluster(Context mContext, GoogleMap mMap, Trip trip, List<PictureLocation> values) {
        this.mContext = mContext;
        this.mMap = mMap;
        this.trip = trip;
        this.values = values;
    }

    public void setUpMapCluster() {

        //Initialize the manager with the mContext and the map.
        mClusterManager = new ClusterManager<>(mContext, mMap);
        mClusterManager.setRenderer(new PictureLocationRenderer(mContext, mMap, mClusterManager));

        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

        mClusterManager.addItems(values);

        mClusterManager.cluster();

        // Custom InfoWindow
        // http://stackoverflow.com/questions/21885225/showing-custom-infowindow-for-android-maps-utility-library-for-android

    }


    @Override
    public boolean onClusterClick(Cluster<PictureLocation> cluster) {
        // open Gallery with first item in cluster
        GalleryDataManager.openGallery(mContext, trip, cluster.getItems().iterator().next().getImagePath());
        return false;
    }

    @Override
    public boolean onClusterItemClick(PictureLocation pictureLocation) {

        // open Gallery
        GalleryDataManager.openGallery(mContext, trip, pictureLocation.getImagePath());

        return false;
    }

}
