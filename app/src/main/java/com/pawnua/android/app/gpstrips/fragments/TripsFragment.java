package com.pawnua.android.app.gpstrips.fragments;

import android.content.Context;
import android.content.Intent;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.activities.TripDetailActivity;
import com.pawnua.android.app.gpstrips.activities.TripEditActivity;
import com.pawnua.android.app.gpstrips.adapters.TripAdapter;
import com.pawnua.android.app.gpstrips.model.Trip;

/**
 * Created by MiK on 29.07.2015.
 */
public class TripsFragment extends Fragment implements DeleteWarningDialogListener {

    private final String TAG = "TripsFragment_PAWNUA";
    private TripAdapter adapter;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_trips,container,false);

        initFAB(v);
        initRecyclerView(v);

        return v;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        updateTripAdapter();

    }

    private void updateTripAdapter() {
        adapter = new TripAdapter(Trip.getAllTrips());
        recyclerView.swapAdapter(adapter, false);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
//        stopLocationService();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    private void initFAB(View v) {
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, TripEditActivity.class);
//                intent.putExtra("Trip", trip);

                context.startActivity(intent);

            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Context context = getActivity();

        switch (item.getItemId()) {
            case R.id.action_open:
                Log.d(TAG, "action_open");
                Intent intent = new Intent(context, TripDetailActivity.class);
                intent.putExtra(BaseColumns._ID, adapter.getTrip().getId());

                context.startActivity(intent);
                break;
            case R.id.action_edit:
                Log.d(TAG, "action_edit");
                Intent intentEdit = new Intent(context, TripEditActivity.class);
                intentEdit.putExtra(BaseColumns._ID, adapter.getTrip().getId());

                context.startActivity(intentEdit);
                break;
            case R.id.action_delete:
                Log.d(TAG, "action_delete");
                // ask a confirmative question to delete
                ConfirmQuestionFragment confirmQuestionFragment = ConfirmQuestionFragment.newInstance(adapter.getTrip().getName());
                confirmQuestionFragment.setDeleteWarningDialogListener(this);
                confirmQuestionFragment.show(getFragmentManager(), "question");
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void initRecyclerView(View v) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView = (RecyclerView) v.findViewById(R.id.tripsRecyclerView);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

//        adapter = new TripAdapter(Trip.getAllTrips());
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        Log.d(TAG, "onDialogPositiveClick");
        Trip.deleteTrip(adapter.getTrip());
        updateTripAdapter();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        Log.d(TAG, "onDialogNegativeClick");
    }
}
