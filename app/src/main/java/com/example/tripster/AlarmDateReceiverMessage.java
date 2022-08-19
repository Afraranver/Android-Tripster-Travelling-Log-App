package com.example.tripster;


import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.auth.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmDateReceiverMessage extends BroadcastReceiver implements AlarmDateReceiverMessageS {

    private static final int NOTIFICATION_ID = 5;
    private static final int START_STICKY = -1;
    private String userID = "";

    @Override
    public void onReceive(Context k1, Intent k2) {
        // TODO Auto-generated method stub
        Toast.makeText(k1, "Alarm received!", Toast.LENGTH_LONG).show();

        Intent intent2 = new Intent(k1, SplashActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(k1,  0, intent2, 0);
        PendingIntent dimiss = PendingIntent.getActivity(k1,  0, intent2, 0);


        int temp = onStartCommand(k2, 5, 1);

        Notification.Builder builder = new Notification.Builder(k1);
        builder.setContentTitle("Reminder");
        builder.setContentText(userID);
        builder.setSmallIcon(R.drawable.ic_baseline_note_24);
        builder.setLights(Color.RED, 3000, 3000);
        builder.addAction (R.drawable.ic_baseline_cancel_24,"Dismiss", dimiss);
//        builder.addAction (R.drawable.ic_baseline_alarm_on_24,"tst", dimiss);
        builder.setAutoCancel(true);
        Intent notifyIntent = new Intent(k1, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(k1, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(k1);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);

    }

    private String getTime(){
        DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        return date;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userID = intent.getStringExtra("UserID");
        return START_STICKY;
    }



}