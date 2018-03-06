package com.nozagleh.ormur;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Manager for the location listener and for getting
 * the newest location possible via GPS.
 *
 * Starts listening for location changes, and get current location. Option for removing
 * the location listener so it wont be listening for the whole lifecycle of the application.
 *
 * Created by arnarfreyr on 9.2.2018.
 */

public class Locator {
    // Class tag
    private static String CLASS_NAME = "Locator";

    // Setup location manager, listener and default location
    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static Location returnLocation = null;

    /**
     * Start listening for current location and location changes.
     *
     * @param activity The current activity
     */
    public static void startListening(Activity activity) {
        // Init the location manager
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // Only set the location listener if it has not been set before
        if (locationListener == null) {
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    // Return the current location
                    returnLocation = location;
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };
        }

        // Check if the right permissions have been granted
        if(Permissions.hasGPS(activity)) {
            try{
                // Start requesting location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
            } catch (SecurityException e) {
                // Log the error
                Log.d(CLASS_NAME, e.getMessage());
            }
        }
    }

    /**
     * Stop listening for location changes.
     * Remove the location listener.
     */
    public static void stopListening() {
        locationManager.removeUpdates(locationListener);
    }

    /**
     * Get the currently set location updated via the listener.
     *
     * @return Location Current location
     */
    public static Location getLocation() {
        return returnLocation;
    }
}
