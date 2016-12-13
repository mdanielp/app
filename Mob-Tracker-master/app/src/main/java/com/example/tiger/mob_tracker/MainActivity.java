package com.example.tiger.mob_tracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*
* the bulk of our code here, we extend and implement as well as call all other classes
*/ 

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public AlarmManager alarmManager;
    public PendingIntent pendingIntent;
    public String time;
    public int realtime;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            int level = i.getIntExtra("level", 0);
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressbar);
            pb.setProgress(level);
            TextView tv = (TextView) findViewById(R.id.textfield);
            // battery measurer function
            // Integer.toString(level) is the numerical value of the battery
            tv.setText("Battery Level: " + Integer.toString(level) + "%");
        }

    };

//initial function 
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //battery stuff
        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
  
        //Starts MobTrakerService
        startService(new Intent(getBaseContext(), MobTrackerService.class));
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        time = settings.getString("timer_preference", "");

            /////////////ALARM
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            //we make pendingintnet which can be updated in the future
             pendingIntent = PendingIntent.getBroadcast(this, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);

          //attempt for dynamic allocation several others were made but do not compile 
            realtime = 1;
            if(time == null || time == "0")
            {
                time = "1";
            }
            if(time == "1")
                realtime = 1;
            else if(time == "2")
                realtime = 2;
            else if(time == "3")
                realtime = 3;
            else if(time == "4")
                realtime = 4;
            else if(time == "5")
                realtime = 5;
            else if(time == "6")
                realtime = 6;
            else if(time == "7")
                realtime = 7;
            else if(time == "8")
                realtime = 8;
            else if(time == "9")
                realtime = 9;

            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            //RTC_WAKEUP make alive even phone is in sleep. when it will be trigered
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (realtime * 60 * 1000), pendingIntent);





        //we need to call sharePreferences settings in order it to get the data from xml
        // loaded
        loadPreferences();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

//commented out since it called on more than just the back and power button
// in the future we plan on also placing this in the service class so that it works in the background
/*
    public void onUserLeaveHint(){
        super.onUserLeaveHint();
        //canceling timer
//       alarmManager.cancel(pendingIntent);
//        pendingIntent.cancel();

        Toast.makeText(this, "sreen_refresh", Toast.LENGTH_LONG).show();
        //resetting timer

        //pendingIntent = PendingIntent.getBroadcast(this, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (60 * 1000), pendingIntent);

    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int selectedId = item.getItemId();

        // for xml
        switch(selectedId)
        {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, Settings.class));
                Toast.makeText(this, "settings", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, about_app.class));
                Toast.makeText(this, "about", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_search:
                Toast.makeText(this, "search", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_share:
                Toast.makeText(this, "search", Toast.LENGTH_LONG).show();
                Intent i=new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject test");
                i.putExtra(android.content.Intent.EXTRA_TEXT, "MobTracker www.mobTracker.org");
                startActivity(Intent.createChooser(i,"Share via"));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

//another attempt at changing time other changes to settings were attempted but were removed 
/*    public void changetime() {
        alarmManager.cancel(pendingIntent);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //RTC_WAKEUP make alive even phone is in sleep. when it will be trigered
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (realtime * 60 * 1000), pendingIntent);

    }
    */

    //makes instance of preferences called settings sets it to showSettings (the key value in the listpref)
    public void loadPreferences()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        settings.getBoolean("is_first", true);
        TextView ShowSettings = (TextView) findViewById(R.id.showSettings);
        boolean silentmode = settings.getBoolean("override_preference", Boolean.parseBoolean(""));
        ShowSettings.setText((settings.getString("email_address", "")) +  " -- " + (settings.getString("phone_number", "")) + " -- " +
                (settings.getString("timer_preference", "1")) + "  --  "  + silentmode  + "++++" + (settings.getBoolean("is_first", Boolean.parseBoolean("true"))));
    }

//when a change is made to the settings we call this function
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadPreferences();
        Toast.makeText(this, realtime, Toast.LENGTH_LONG).show();
        alarmManager.cancel(pendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (realtime * 60 * 1000), pendingIntent);

    }

}
