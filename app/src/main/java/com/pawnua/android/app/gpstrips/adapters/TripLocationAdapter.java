package com.pawnua.android.app.gpstrips.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.model.LocationUtils;
import com.pawnua.android.app.gpstrips.model.TripLocation;

import java.util.List;

/**
 * Created by MiK on 14.06.2015.
 */
public class TripLocationAdapter extends RecyclerView.Adapter<TripLocationAdapter.ViewHolder> {

    private List<TripLocation> tripLocationList;

    public TripLocationAdapter(List<TripLocation> tripLocationList) {
        this.tripLocationList = tripLocationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_location, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final TripLocation tripLocation = tripLocationList.get(position);

        holder.latitude.setText("latitude: " + String.valueOf(tripLocation.getLatitude()));
        holder.longitude.setText("longitude: " + String.valueOf(tripLocation.getLongitude()));
//        holder.time.setText("time: " + String.valueOf(tripLocation.getTime()));
        holder.time.setText("time: " + LocationUtils.getFullTime(tripLocation.getTime()));
        holder.speed.setText("speed: " + String.valueOf(tripLocation.getSpeed()));
        holder.altitude.setText("altitude: " + String.valueOf(tripLocation.getAltitude()));

//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = v.getContext();
//                Intent intent = new Intent(context, TripDetailActivity.class);
//                intent.putExtra(TripDetailActivity.EXTRA_NAME, trip.getName());
//
//                context.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return tripLocationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
//        private View view;
        private TextView latitude;
        private TextView longitude;
        private TextView time;
        private TextView speed;
        private TextView altitude;

        public ViewHolder(View itemView) {
            super(itemView);
//            view = itemView;
            latitude = (TextView) itemView.findViewById(R.id.latitude);
            longitude = (TextView) itemView.findViewById(R.id.longitude);
            time = (TextView) itemView.findViewById(R.id.time);
            speed = (TextView) itemView.findViewById(R.id.speed);
            altitude = (TextView) itemView.findViewById(R.id.altitude);
        }
    }
}
