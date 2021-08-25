package com.djustix.nearbites.common.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit

/**
 * The LocationProvider allows Activities and Fragments to request Location Updates.
 * It handles permission, fetching and validating the relevance of Locations.
 */
class LocationProvider(private val context: Context) {
    sealed class LocationData {
        data class Available(val location: Location) : LocationData()
        data class Error(val exception: Exception) : LocationData()
    }

    private val locationData = MutableLiveData<LocationData>()

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest: LocationRequest by lazy {
        val request = LocationRequest.create()
        request.interval = 5000
        request.fastestInterval = 5000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        request
    }

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val location = locationResult?.locations?.firstOrNull()
                if (location != null && location.isActual) {
                    stopLocationUpdates()
                    locationData.postValue(LocationData.Available(location))
                }
            }
        }
    }

    fun startLocationUpdates(permissionProvider: LocationPermissionProvider? = null): LiveData<LocationData> {
        if (isLocationPermissionGranted) {
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (exception: SecurityException) {
                locationData.postValue(LocationData.Error(exception))
            }
        } else if (permissionProvider != null){
            requestPermission(permissionProvider)
        }

        return locationData
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun requestPermission(permissionProvider: LocationPermissionProvider) {
        permissionProvider
            .locationPermissionLauncher
            .launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private val isLocationPermissionGranted: Boolean
        get() {
            val permissionType = ContextCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

            return permissionType == PackageManager.PERMISSION_GRANTED

        }

    private val Location.isActual: Boolean
        get() {
            return System.currentTimeMillis() - time < TimeUnit.MINUTES.toMillis(15)
        }
}