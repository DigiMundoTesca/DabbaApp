package com.tesca.dabbaapp;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;


public class AlarmService extends IntentService {

    private static final String ACTION_ALARM = "com.tesca.dabbaapp.action.FOO";

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ALARM.equals(action)) {
                handleActionAlarm();
            }
        }
    }


    private void handleActionAlarm() {

        // Intent para iniciar la aplicación
        startActivity(new Intent(AlarmService.this, Tabbed_Requests.class));

        // Mostrar Fragment con alarma
        dialog();

    }

    private void dialog() //Alert dialog
    {

        final AlertFragment dialog = new AlertFragment();
        dialog.show(SupportFragmentManager(), "dialog");
        final MediaPlayer mp = MediaPlayer.create(AlarmService.this, R.raw.alert);
        mp.start();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dialog.dismiss(); // Close alert dialog
                t.cancel(); // Stop timer to avoid crash report
            }
        }, 5000); // Starts activity after 5 seconds
    }

    private android.support.v4.app.FragmentManager SupportFragmentManager() {
        return null;
    }

}
