package com.tesca.dabbaapp;


import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tesca.dabbaapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertFragment extends DialogFragment {


    public AlertFragment() {
        // Required empty public constructor
    }

   public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_alert, container);
       getDialog().setTitle("Alerta!!!");

        // Inflate the layout for this fragment
        return view;
    }
}
