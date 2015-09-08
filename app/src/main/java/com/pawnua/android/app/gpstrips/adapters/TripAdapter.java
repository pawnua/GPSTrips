package com.pawnua.android.app.gpstrips.adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.activities.TripDetailActivity;
import com.pawnua.android.app.gpstrips.activities.TripEditActivity;
import com.pawnua.android.app.gpstrips.model.LocationUtils;
import com.pawnua.android.app.gpstrips.model.Trip;
import com.pawnua.android.app.gpstrips.model.TripCharacteristics;

import java.util.List;

/**
 * Created by MiK on 14.06.2015.
 */
public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private List<Trip> tripList;

    private Trip trip;

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public TripAdapter(List<Trip> tripList) {
        this.tripList = tripList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Trip trip = tripList.get(position);

        TripCharacteristics tripCharacteristics = TripCharacteristics.getTripCharacteristics(trip);

        holder.tripName.setText(trip.getName());
        holder.tripDescription.setText(String.valueOf(trip.getDescription()));

        if (tripCharacteristics!=null) {
            holder.tripDistance.setText(LocationUtils.getDistance(tripCharacteristics.getDistance()));
            holder.tripAvgSpeed.setText(LocationUtils.getSpeed(tripCharacteristics.getAvgSpeed()));
            holder.tripDuration.setText(LocationUtils.getDuration(tripCharacteristics.getDuration()));
        }


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, TripDetailActivity.class);
                intent.putExtra(BaseColumns._ID, trip.getId());

                context.startActivity(intent);
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setTrip(trip);
                return false;
//                Context context = v.getContext();
//                Intent intent = new Intent(context, TripEditActivity.class);
//                intent.putExtra(BaseColumns._ID, trip.getId());
//
//                context.startActivity(intent);
//                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        private View view;
        private TextView tripName;
        private TextView tripDescription;

        private TextView tripDistance;
        private TextView tripAvgSpeed;
        private TextView tripDuration;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            itemView.setOnCreateContextMenuListener(this);

            tripName = (TextView) itemView.findViewById(R.id.tripName);
            tripDescription = (TextView) itemView.findViewById(R.id.tripDescription);

            tripDistance = (TextView) itemView.findViewById(R.id.tripDistance);
            tripAvgSpeed = (TextView) itemView.findViewById(R.id.tripAvgSpeed);
            tripDuration = (TextView) itemView.findViewById(R.id.tripDuration);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.action_open,Menu.NONE,R.string.action_open);
            menu.add(Menu.NONE, R.id.action_edit,Menu.NONE,R.string.action_edit);
            menu.add(Menu.NONE, R.id.action_delete, Menu.NONE, R.string.action_delete);
        }
    }
}
