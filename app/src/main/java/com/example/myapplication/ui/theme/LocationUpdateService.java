package com.example.myapplication.ui.theme;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationUpdateService extends Service {

    private LocationManager locationManager;
    private LocationListener locationListener;

    public static final String ACTION_UPDATE_LOCATION = "com.ightk.playground.ACTION_UPDATE_LOCATION";

    @Override
    public void onCreate() {
        super.onCreate();

        // Create the LocationManager instance
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create the LocationListener instance
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // You can send the location to a server or update UI here
                String newLocation = "Location: " + location.getLatitude() + ", " + location.getLongitude();
                // Create an Intent with the broadcast action
                Intent intent = new Intent(ACTION_UPDATE_LOCATION);
                // Add the data to the intent
                intent.putExtra("data", newLocation);
                // Send the broadcast
                sendBroadcast(intent);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Handle location provider status changes here
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Handle location provider enabled here
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Handle location provider disabled here
            }
        };
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start listening for location updates
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0,
                locationListener
        );

        // Return START_STICKY to make the service restart if it gets terminated by the system
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop listening for location updates
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // This service does not support binding, so return null
        return null;
    }
}
