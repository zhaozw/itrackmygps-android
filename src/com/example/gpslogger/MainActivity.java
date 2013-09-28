package com.example.gpslogger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private RunManager runManager;
    private Button btnStart;
    private Button btnStop;

    private int ctrUpdate = 0;

    private TextView tvGPSCounter;
    private TextView tvGPSLatitude;
    private TextView tvGPSLongitude;
    private TextView tvGPSAltitude;
    private TextView tvGPSBearing;
    private TextView tvGPSSpeed;
    private TextView tvGPSDateTime;
    private TextView tvGPSAccuracy;
    private TextView tvGPSProvider;
    private TextView tvGPSTotalSatellites;

    private ArrayList gpsSatelliteList; // loop through satellites to get status
    private ListView lvSatellites;
    private SatteliteListAdapter satelliteAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runManager = RunManager.get(getApplicationContext());

        //find the buttons
        btnStart = (Button)findViewById(R.id.btn_start);
        btnStop = (Button)findViewById(R.id.btn_stop);

        //find the textviews
        tvGPSCounter = (TextView)findViewById(R.id.tvGPSCounter);
        tvGPSLatitude = (TextView)findViewById(R.id.tvGPSLatitude);
        tvGPSLongitude = (TextView)findViewById(R.id.tvGPSLongitude);
        tvGPSAltitude = (TextView)findViewById(R.id.tvGPSAltitude);
        tvGPSBearing = (TextView)findViewById(R.id.tvGPSBearing);
        tvGPSSpeed = (TextView)findViewById(R.id.tvGPSSpeed);
        tvGPSDateTime = (TextView)findViewById(R.id.tvGPSTimestamp);
        tvGPSAccuracy = (TextView)findViewById(R.id.tvGPSAccuracy);
        tvGPSProvider = (TextView)findViewById(R.id.tvGPSProvider);
        tvGPSTotalSatellites = (TextView)findViewById(R.id.tvGPSFixTotalSatellites);

        lvSatellites = (ListView)findViewById(R.id.lv_satellites);
        gpsSatelliteList = new ArrayList<GpsSatellite>();
        satelliteAdapter = new SatteliteListAdapter(this, gpsSatelliteList);
        lvSatellites.setAdapter(satelliteAdapter);
    }

    public void buttonStartPressed(View view){
        Log.i(TAG, "buttonStartPressed");
        runManager.startLocationUpdates();
        updateButtons();
    }

    public void buttonStopPressed(View view){
        Log.i(TAG, "buttonStopPressed");
        runManager.stopLocationUpdates();
        updateButtons();
    }

    private void updateButtons() {
        boolean started = runManager.isTrackingRun();

        btnStart.setEnabled(!started);
        btnStop.setEnabled(started);
    }

    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

        @Override
        protected void onLocationReceived(Context context, Location loc) {
            displayGPSDetails(loc);
        }

        protected void onSatelliteReceived(Context context, ArrayList newSatellites){
            gpsSatelliteList = new ArrayList<GpsSatellite>(newSatellites);
            satelliteAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            if(enabled){
                Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "GPS disabled", Toast.LENGTH_LONG).show();
            }
        }

    };

    @Override
    public void onStart() {
        super.onStart();
        this.registerReceiver(mLocationReceiver,new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        this.unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    private void displayGPSDetails(Location location) {
        ctrUpdate++;

        tvGPSCounter.setText(Integer.toString(ctrUpdate));
        tvGPSLatitude.setText(Double.toString(location.getLatitude()));
        tvGPSLongitude.setText(Double.toString(location.getLongitude()));
        tvGPSAltitude.setText(Double.toString(location.getAltitude()));
        tvGPSBearing.setText(Float.toString(location.getBearing()));
        tvGPSSpeed.setText(Float.toString(location.getSpeed()));
        tvGPSAccuracy.setText(Float.toString(location.getAccuracy()));
        tvGPSProvider.setText(location.getProvider());

        String gpsDateTime = CustomDateUtils.formatDateTimestamp(location.getTime());
        tvGPSDateTime.setText(gpsDateTime);

        int satellitesWithFix = location.getExtras().getInt("satellites");
        int satellitesTotal = gpsSatelliteList.size();
        String s = Integer.toString(satellitesWithFix) + "/" + satellitesTotal;
        tvGPSTotalSatellites.setText(s);
    }

    public void onPause() {
        super.onPause(); // Always call the superclass method first
        Log.i(TAG, "paused");

        //locationManager.removeUpdates(gpslocationListener);
    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first

        Log.i(TAG, "resume");
        //locationManager.requestLocationUpdates(gpsProvider, 0, 0, gpslocationListener);
    }

}