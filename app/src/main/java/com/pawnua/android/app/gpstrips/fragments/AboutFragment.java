package com.pawnua.android.app.gpstrips.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pawnua.android.app.gpstrips.R;

/**
 */
public class AboutFragment extends DialogFragment {

    private Button aboutBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_layout, container, false);
        getDialog().setTitle(getString(R.string.about));

        aboutBtn = (Button) view.findViewById(R.id.buttonClose);

        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
