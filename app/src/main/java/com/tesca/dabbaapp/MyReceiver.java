package com.tesca.dabbaapp;

import android.widget.Toast;

/**
 * Created by Itzli on 20/12/2016.
 */

public class MyReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(android.content.Context context, android.content.Intent intent) {
        Toast.makeText(context, "Modificar la lógica de programación", Toast.LENGTH_LONG).show();
    }
}