package com.example.tiger.mob_tracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/*
* SMS class which sends the message to the number the user defines and also gets and sends the location
*/

public class SMS extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    //variable
    private String myGPSLocation;
    private LocationManager myLocationManager;
    boolean issent = false;
    SmsManager smsManager = SmsManager.getDefault();
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //we used google to get the location
        buildGoogleApiClient();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();
    }

   //error state and motification
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(Bundle arg0) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        //setting the string to be sent to the number
        if (mLastLocation != null) {
            String loc = ("I am lost Latitude: "+ String.valueOf(mLastLocation.getLatitude())+" - Longitude: "+
                    String.valueOf(mLastLocation.getLongitude()));
            message(loc);
        }

    }

   //simple toast
    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();

    }

    //use of google for maps
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //sms method
    public String message(String text)
    {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String num = settings.getString("phone_number", "");

        //we don't send without a number
        if(num != "") {
            smsManager.sendTextMessage(num, null, text, null, null);
            issent = true;
            this.finish();
        } else { Toast.makeText(this, "Phone is not SET ", Toast.LENGTH_SHORT).show();   }
        this.finish();
        return text;
        }
}
