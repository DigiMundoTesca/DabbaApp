package com.tesca.dabbaapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class AlarmService extends IntentService {

    private static final String ACTION_ALARM = "com.tesca.dabbaapp.action.FOO";
    private boolean quit;
    private int count=0;
    private static final String Excecute_Alarm = "com.tesca.dabbaapp.action.RUN_INTENT_SERVICE";


    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final Long hora = intent.getExtras().getLong("hora");
        Toast.makeText(getApplicationContext(),"Servicio Iniciado",Toast.LENGTH_LONG).show();

        if (intent != null) {
            final String alarma = intent.getAction();
            if (Excecute_Alarm.equals(alarma)) {
                countDown(hora);
            }
        }
    }

    private void countDown(Long hora) {

        String a = String.format("%02d%02d%02d",
                MILLISECONDS.toHours(hora),
                MILLISECONDS.toMinutes(hora) -
                        TimeUnit.HOURS.toMinutes(MILLISECONDS.toHours(hora)), // The change is in this line
                MILLISECONDS.toSeconds(hora) -
                        TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes(hora)));

        while (!a.equals("000000"))
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
            }
            if (a.equals("001500")) {
                handleActionAlarm(a);
            }
            if (a.equals("000500")){
                handleActionAlarm(a);
            }
            count++;
            int b = Integer.valueOf(a)-count;
            a = Integer.toString(b);
            Toast.makeText(getApplicationContext(),a,Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.quit = true;
        System.out.println("Service is Destroyed");
    }


}
