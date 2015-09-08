package com.pawnua.android.app.gpstrips.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pawnua.android.app.gpstrips.GalleryDataManager;
import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.model.Trip;

import java.io.File;

/**
 * Created by PawnUA on 07/08/2015.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final Context mContext;
    private File[] fileList;
    private Trip trip;

    public ImageAdapter(Context mContext, File[] fileList, Trip trip) {
        this.mContext = mContext;
        this.fileList = fileList;
        this.trip = trip;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final String filePath = fileList[position].getAbsolutePath();

        Glide.with(mContext)
                .load(filePath)
                .centerCrop()
//                .placeholder(R.drawable.loading_spinner)
                .crossFade()
                .into(holder.getImageView());

        holder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open Gallery
                GalleryDataManager.openGallery(v.getContext(), trip, filePath);

            }
        });

    }

    @Override
    public int getItemCount() {
        return fileList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.imageGalleryView);

        }

        public ImageView getImageView() {
            return imageView;
        }
    }

}
