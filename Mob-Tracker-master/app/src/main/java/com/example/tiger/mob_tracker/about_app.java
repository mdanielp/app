package com.example.tiger.mob_tracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

/**
 * Created by tiger on 01/11/16.
 */

public class about_app extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_popup);
        //define a size of window
        //display metircs object
        DisplayMetrics window = new DisplayMetrics();
        //store the created value
        getWindowManager().getDefaultDisplay().getMetrics(window);

        //set up window height
        int width = window.widthPixels;
        int height = window.heightPixels;

        //apply to a window and set up size * 70%
        getWindow().setLayout((int)(width*0.7), (int)(height*0.4));

    }



}
