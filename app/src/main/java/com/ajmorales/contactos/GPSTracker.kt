package com.ajmorales.contactos

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*

/**
 * Created by antonio on 12/4/17.
 */
open class GPSTracker : Service {

    private var mContext: Context? = null
    private var isGPSEnabled = false        // Flag for GPS status
    private var isNetworkEnabled = false    // Flag for network status
    private var canGetLocation = false      // if Location co-ordinates are available using GPS or Network

    private var location: Location? = null  // Location
    private var latitude = 0.0              // Latitude
    private var longitude = 0.0             // Longitude

    private var locationManager: LocationManager? = null
    private var activity: Activity? = null

    val address = false //Geodecoder obtain an Address.

    constructor() {}
    constructor(context: Context?, activity: Activity?) {
        mContext = context
        this.activity = activity
        getLocation()
    }

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1000 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = 1000 * 60.toLong()  // 1 minute
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): Location? {
        try {
            locationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // Getting GPS status
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            // Getting network status
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            //Compruebo que tengo permisos
            if (isGPSEnabled && isNetworkEnabled) {
                canGetLocation = true
                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), mLocationListener)
                    Log.d("Network", "Network")

                    if (locationManager != null) {
                        location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
            }

            // If GPS enabled, get latitude/longitude using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 50)
                    } else {
                        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), mLocationListener)
                        Log.d("GPS Enabled", "GPS Enabled")

                        if (locationManager != null) {
                            location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) { e.printStackTrace() }

        return location
    }//getLocation

    private val mLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }

        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }

        return longitude
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     * @return boolean
     */
    fun canGetLocation(): Boolean { return canGetLocation }

    override fun onBind(arg0: Intent): IBinder? { return null }

    /**
     * Gives you complete address of the location
     *
     * @return complete address in String
     */
    fun getLocationAddress(mlatitude: Double, mlongitude: Double): String {
        return if (canGetLocation) {
            val geocoder = Geocoder(mContext, Locale.getDefault())
            // Get the current location from the input parameter list
            // Create a list to contain the result address
            var addresses: List<Address>? = null

            addresses = try {
                    geocoder.getFromLocation(mlatitude, mlongitude, 1) // Devuelve una dirección

            } catch (e1: IOException) {
                e1.printStackTrace()
                return "IO Exception trying to get address:$e1"

            } catch (e2: IllegalArgumentException) {
                // Error message to post in the log
                val errorString = ("Illegal arguments $mlatitude , $mlongitude passed to address service")
                e2.printStackTrace()
                return errorString
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.isNotEmpty()) {

                //this.getAddress=true;

                // Get the first address
                val address = addresses[0]
                /*
                 * Format the first line of address (if available), city, and
                 * country name.
                 */
                 /*

                      address.getCountryName());*/
                // Return String format
                String.format("%s",  // If there's a street address, add it
                    address.getAddressLine(0), "",  // Locality is usually a city
                    address.locality
                )
            } else {
                "DIRECCIÓN NO ENCONTRADA: \n ! GPS sin señal ! \n Intentelo más tarde \n Pruebe en un lugar abierto, evite los interiores."
            }
        } else {
            "Localización no disponible"
        }
    }
}