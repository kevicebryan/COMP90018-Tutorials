package com.example.sensor_gps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.sensor_gps.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Google's API for location services
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Configuration of all settings of FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    private val Request_Code_Location = 22

    private lateinit var latttv: TextView
    private lateinit var longtv: TextView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        @Suppress("DEPRECATION")
        locationRequest = LocationRequest.create().apply { interval = 5000 }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d("LocationTest", "Location updates")
                locationResult.lastLocation?.let { updateUI(it) }
            }
        }

        latttv = binding.latitudeInput
        longtv = binding.longitudeInput
        button = binding.button

        button.setOnClickListener {
            updateLocation()
        }
    }

    private fun updateLocation() {
        // if user grants permission
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

            // get the last location
            fusedLocationClient.lastLocation
                .addOnSuccessListener(this) { location ->
                    if (location == null) {
                        Log.d("LocationTest", "null")
                    } else {
                        Log.d("LocationTest", "Success")
                        updateUI(location)
                    }
                }
        } else {
            // if the user hasn't granted permission, ask for it explicitly
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Request_Code_Location)
        }
    }

    private fun updateUI(location: Location) {
        latttv.text = location.latitude.toString()
        longtv.text = location.longitude.toString()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Request_Code_Location) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocation()
            }
        }
    }
}
