package com.tesca.dabbaapp;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class AlarmService extends IntentService {

    private static final String ACTION_ALARM = "com.tesca.dabbaapp.action.FOO";


    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        final Long hora = intent.getExtras().getLong("hora");

        new CountDownTimer(hora, 1000) {
            public void onTick(long millisUntilFinished) {
                String a = String.format("%02d:%02d:%02d",
                        MILLISECONDS.toHours(millisUntilFinished),
                        MILLISECONDS.toMinutes(millisUntilFinished) -
                                TimeUnit.HOURS.toMinutes(MILLISECONDS.toHours(millisUntilFinished)), // The change is in this line
                        MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes(millisUntilFinished)));
                Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT).show();
                if (a.equals("00:15:00")) {
                    handleActionAlarm(a);
                }
                if (a.equals("00:05:00")){
                    handleActionAlarm(a);
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }


    private void handleActionAlarm(String a) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.moto2)
                        .setContentTitle("Alerta")
                        .setContentText("Te faltan" + a + "minutos")
                        .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                //to post your notification to the notification bar with a id. If a notification with same id already exists, it will get replaced with updated information.
        notificationManager.notify(0, builder.build());


    }


}
