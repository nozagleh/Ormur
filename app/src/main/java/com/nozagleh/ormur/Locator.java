package com.nozagleh.ormur;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by arnarfreyr on 9.2.2018.
 */

public class Locator {
    private static String CLASS_NAME = "Locator";

    public static void startListening(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        if(Permissions.has_gps(activity)) {
            try{
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            } catch (SecurityException e) {
                Log.d(CLASS_NAME, e.getMessage());
            }
        }
    }

    public static Location getLocation(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;

        return (Permissions.has_gps(activity)) ? locationManager.getLastKnownLocation(locationProvider) : new Location(locationProvider);
    }
}
