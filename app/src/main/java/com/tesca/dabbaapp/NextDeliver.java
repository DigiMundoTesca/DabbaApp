package com.tesca.dabbaapp;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NextDeliver extends DialogFragment {

    public NextDeliver() {
        // Required empty public constructor
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_next_deliver, container);
        getDialog().setTitle("Siguiente entrega");

        // Inflate the layout for this fragment
        return view;
    }
}
