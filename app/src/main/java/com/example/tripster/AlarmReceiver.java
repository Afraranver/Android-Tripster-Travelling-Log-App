package com.example.tripster;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 3;
    @Override
    public void onReceive(Context k1, Intent k2) {
        // TODO Auto-generated method stub
        Toast.makeText(k1, "Alarm received!", Toast.LENGTH_LONG).show();

        Intent intent2 = new Intent(k1, SplashActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(k1,  0, new Intent(), 0);
        PendingIntent dimiss = PendingIntent.getActivity(k1,  0, intent2, 0);


        Notification.Builder builder = new Notification.Builder(k1);
        builder.setContentTitle("It's " + getTime());
        builder.setContentText("You have Event Now!!");
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        builder.setLights(Color.RED, 3000, 3000);
        builder.setSmallIcon(R.drawable.ic_baseline_alarm_on_24);
        builder.addAction (R.drawable.ic_baseline_close_24,"Dismiss", dimiss);
        builder.addAction (R.drawable.ic_baseline_snooze_24,"Snooze", pIntent);
        Intent notifyIntent = new Intent(k1, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(k1, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(k1);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }

    private String getTime(){
//        DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
//        String date=dfDate.format(Calendar.getInstance().getTime());
        DateFormat dfTime = new SimpleDateFormat("HH:mm:a");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return time;
    }

}