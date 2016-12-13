package com.example.tiger.mob_tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

/*
*  AlarmReceiver class waking phone when alarm initiates 
*/

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences settings = context.getSharedPreferences("is_first", context.MODE_PRIVATE);
        boolean isFirst = settings.getBoolean("is_first", Boolean.parseBoolean(""));

            try {

                //if app is lunched first time
                if (isFirst) {
                    settings.edit().putBoolean("is_first", false).commit();
                } 
                else {

                    Intent newIntent = new Intent(context, Mp3player.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(newIntent);

                    //make a new intent and start it with flag   and send an sms
                    Intent launch = new Intent(context, SMS.class);
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(launch);

                }

            } catch (Exception e) {
                e.printStackTrace();

            }
    }
}
