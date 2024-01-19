package com.example.myapplication.ui.theme

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class LocationActivity : ComponentActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 123

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is already granted, proceed with your location-related tasks
            // ...
            startUpdateLocationService()
        }
    }

    private fun startUpdateLocationService() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode == ConnectionResult.SUCCESS) {
            // Google Play services are available, proceed with location retrieval using Google Play services
            val serviceIntent = Intent(
                this,
                GoogleServiceLocationService::class.java
            )
            startService(serviceIntent)
        } else {
            // Google Play services are not available, use alternative methods to get the location
            // ...
            val serviceIntent = Intent(
                this,
                LocationUpdateService::class.java
            )
            startService(serviceIntent)
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, proceed with your location-related tasks
                // Start the location service
                startUpdateLocationService()
            } else {
                // Location permission denied, handle it accordingly (e.g., show a message)
                // ...
            }
        }
    }

    private val dataReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Handle the received data here
            val newData = intent.getStringExtra("data")
            // Update your view with the new data
            txtLocation.setText(newData)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(dataReceiver, IntentFilter(GoogleServiceLocationService.ACTION_UPDATE_LOCATION));
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(dataReceiver);
    }

    lateinit var txtLocation: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        txtLocation = findViewById(R.id.locationTv)
        findViewById<TextView>(R.id.backTv).setOnClickListener {
            finish()
        }
        checkLocationPermission();
    }
}