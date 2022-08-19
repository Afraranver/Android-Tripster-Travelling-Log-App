package com.example.tripster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Map;

public class AlarmSetter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // get preferences
        SharedPreferences preferences = context.getSharedPreferences("name_of_your_pref", 0);
        Map<String, ?> scheduleData = preferences.getAll();

        // set the schedule time
        if(scheduleData.containsKey("fromHour") && scheduleData.containsKey("toHour")) {
            int fromHour = (Integer) scheduleData.get("fromHour");
            int fromMinute = (Integer) scheduleData.get("fromMinute");

            int toHour = (Integer) scheduleData.get("toHour");
            int toMinute = (Integer) scheduleData.get("toMinute");

            //Do some action
        }
    }

}
