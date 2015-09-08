package com.pawnua.android.app.gpstrips.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pawnua.android.app.gpstrips.R;

/**
 */
public class ConfirmQuestionFragment extends DialogFragment  implements DialogInterface.OnClickListener {

    DeleteWarningDialogListener listener;

    public void setDeleteWarningDialogListener(DeleteWarningDialogListener listener) {
        this.listener = listener;
    }

    public static ConfirmQuestionFragment newInstance(String title) {
        ConfirmQuestionFragment frag = new ConfirmQuestionFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getArguments().getString("title");

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setIcon(R.drawable.ic_info_outline_black_48dp)
                .setPositiveButton(R.string.q_yes, this)
                .setNegativeButton(R.string.q_no, this)
                .setMessage(R.string.q_message_text);
        return adb.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                if (listener!=null) {
                    listener.onDialogPositiveClick(this);
                }
                break;
            case Dialog.BUTTON_NEGATIVE:
                if (listener!=null) {
                    listener.onDialogNegativeClick(this);
                }
                break;
        }
    }
}
