package com.pawnua.android.app.gpstrips.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.model.TripInfo;

import java.util.List;

/**
 * Created by MiK on 14.06.2015.
 */
public class TripInfoAdapter extends RecyclerView.Adapter<TripInfoAdapter.ViewHolder> {

    private List<TripInfo> tripInfoList;

    private TripInfo tripInfo;

    public TripInfoAdapter(List<TripInfo> tripInfoList) {
        this.tripInfoList = tripInfoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_info_trip, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        tripInfo = tripInfoList.get(position);

        holder.tripInfoIco.setImageDrawable(tripInfo.infoIco);

        holder.tripInfoLabel.setText(tripInfo.infoLabel);
        holder.tripInfoValue.setText(tripInfo.infoValue);
        holder.tripInfoAd.setText(tripInfo.infoAd);

    }

    public TripInfo getItem(String id) {
        TripInfo item = null;

        for(int i = 0; i < getItemCount(); i++) {

            item = tripInfoList.get(i);
            if (item.id == id) {
                return item;
            }
        }
        return item;
    }

    @Override
    public int getItemCount() {
        return tripInfoList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        private View view;
        private ImageView tripInfoIco;
        private TextView tripInfoLabel;
        private TextView tripInfoValue;
        private TextView tripInfoAd;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            tripInfoIco = (ImageView) itemView.findViewById(R.id.tripInfoIco);
            tripInfoLabel = (TextView) itemView.findViewById(R.id.tripInfoLabel);
            tripInfoValue = (TextView) itemView.findViewById(R.id.tripInfoValue);
            tripInfoAd = (TextView) itemView.findViewById(R.id.tripInfoAd);
        }

    }


}

