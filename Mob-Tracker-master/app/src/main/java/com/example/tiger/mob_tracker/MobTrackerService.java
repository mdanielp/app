package com.example.tiger.mob_tracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.provider.Contacts.SettingsColumns.KEY;
import static java.util.logging.Logger.global;

/*
* enables background functionality for timer, alarm, and sms 
*/

//<service android:name=".MobTrackerService" />
public class MobTrackerService extends Service {

    public SharedPreferences settings;

    private Handler HandleIt = new Handler();
    private Timer timer = new Timer();
    boolean timeout = false;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            HandleIt.post(new Runnable(){
               public void run(){

                   Toast.makeText(getApplicationContext(), TextonScreen(), Toast.LENGTH_SHORT).show();

                   //get screen light up
                   PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
                   boolean isScreenOn = pm.isScreenOn();
                   if(isScreenOn==false) {
                       pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK |  PowerManager.ON_AFTER_RELEASE, "My Tag");
                   }
               }
            });
        }


    }

    private String TextonScreen()
    {
        timeout = true;
        return "it is running";


    }
    boolean isTimeout()
    {
        return timeout;
    }
    @Override
    public void onCreate() {

        // TODO Auto-generated method stub
        super.onCreate();
       // Toast.makeText(this, "Service is created", Toast.LENGTH_SHORT).show();



    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        settings = getSharedPreferences("timer_preference", MODE_PRIVATE);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    onSharedPreferenceChanged(settings, "timer_preference");
        settings = getSharedPreferences("timer_preference", MODE_PRIVATE);

        String preferenceTime = settings.getString("timer_preference", "1");
        int INTERVAL=  (60000 * Integer.parseInt(preferenceTime));

        // TODO Auto-generated method stub
        // Display the Toast Message
        //Toast.makeText(this, "Start Service" + preferenceTime, Toast.LENGTH_SHORT).show();
        // Execute an action after period time
        //comes from the TimeDisplayTimerTask class
        timer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, INTERVAL);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // Display the Toast Message
        //Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }
}