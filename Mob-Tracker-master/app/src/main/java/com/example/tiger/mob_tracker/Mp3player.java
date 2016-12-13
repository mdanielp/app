package com.example.tiger.mob_tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/*
* This si the Alarm class but is named Mp3
*/
import static android.os.PowerManager.FULL_WAKE_LOCK;

public class Mp3player extends Activity {

    //variabels
    private Button buttonPlayStop;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private final Handler handler = new Handler();
    private Timer timer = new Timer();
    private PowerManager.WakeLock mWakeLock;


    // at the start of mp3Player
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //set xml
        setContentView(R.layout.mp3_player);
        RelativeLayout color = (RelativeLayout) findViewById(R.id.mp3_player);
        initViews();
        //call flash too
        flashtime();


        Intent BATTERYintent = this.registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        int level = BATTERYintent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
       //Toast.makeText(getApplicationContext(), level, Toast.LENGTH_LONG).show();

        //Create a task which the timer will execute.  This should be an implementation of the TimerTask interface.
        //I have created an inner class below which fits the bill.

        MyTimer mt = new MyTimer();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(FULL_WAKE_LOCK, "My Wake Lock");
        mWakeLock.acquire();



        //layout items for this class
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        MediaPlayer mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //handle if battery is below 20%... don't flash or sound
        if(level > 20) {
            timer.schedule(mt, 100, 100);
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }


    }

    private void initViews() {
        buttonPlayStop = (Button) findViewById(R.id.ButtonPlayStop);
        buttonPlayStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick();
            }
        });

        // we use the raw files sound this can be anything we desire
        mediaPlayer = MediaPlayer.create(this, R.raw.sound);
        seekBar = (SeekBar) findViewById(R.id.SeekBar01);
        seekBar.setMax(mediaPlayer.getDuration());
        //check for touch to clancle alarm
        seekBar.setOnTouchListener(new OnTouchListener() {
            @Override
               public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);
                return false;
            }
        });
    }

    //updater to contiue alarm and handle delay
    public void startPlayProgressUpdater() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        } else {
            mediaPlayer.pause();
            buttonPlayStop.setText(getString(R.string.stop_str));
            seekBar.setProgress(0);
        }
    }

    private void seekChange(View v) {
        if (mediaPlayer.isPlaying()) {
            SeekBar sb = (SeekBar) v;
            mediaPlayer.seekTo(sb.getProgress());
        }
    }

    // cancel on stop button click
    private void buttonClick() {

        buttonPlayStop.setText(getString(R.string.stop_str));
        mediaPlayer.stop();
        finish();

    }


    //An inner class which is an implementation of the TImerTask interface to be used by the Timer.
    class MyTimer extends TimerTask {

        public void run() {

            //<activity android:name=".Flash"/>

            //This runs in a background thread.
            //We cannot call the UI from this thread, so we must call the main UI thread and pass a runnable
            runOnUiThread(new Runnable() {

                public void run() {
                    Random random = new Random();
                    int rendomNumber = random.nextInt(3);
                    RelativeLayout color = (RelativeLayout) findViewById(R.id.mp3_player);
                    if(rendomNumber == 1)
                        color.setBackgroundColor(Color.RED);
                    if(rendomNumber == 2)
                        color.setBackgroundColor(Color.BLUE);
                    if(rendomNumber ==3)
                        color.setBackgroundColor(Color.BLUE);
                    //The random generator creates values between [0,256) for use as RGB values used below to create a random color
                    //We call the RelativeLayout object and we change the color.  The first parameter in argb() is the alpha.
                    //color.setBackgroundColor(Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
                }

            });

            /////////////////////////////////////
        }

    }

    // flash
    public void flashtime()
    {
        new CountDownTimer(12000, 1000)
        {
            public void onTick(long millisUntilFinished)
            {

            }

            public void onFinish()
            {
                timer.cancel();
                mediaPlayer.stop();
                finish();
            }
        }.start();

    }

}
