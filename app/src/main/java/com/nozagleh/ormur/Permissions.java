package com.nozagleh.ormur;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by arnarfreyr on 9.2.2018.
 */

public class Permissions {

    /**
     * Permission key for the GPS fine location checker.
     */
    private static final int PERMISSION_GPS = 42;

    /**
     * A general permission checker, checks for user permission for the current activity.
     * @param askingActivity The activity which this is invoked in
     * @param permission The permission in question
     * @return boolean, if permission granted or not
     */
    private static boolean check_permission(Activity askingActivity, String permission) {
        int has_permission = ContextCompat.checkSelfPermission(askingActivity, permission);

        if (has_permission == PackageManager.PERMISSION_DENIED) {
            return false;
        }

        return true;
    }

    /**
     * Check if the app has the right permissions to use the GPS.
     *
     * @param askingActivity The activity which this is invoked in
     * @return boolean if has permission
     */
    public static boolean has_gps(Activity askingActivity) {
        return check_permission(askingActivity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Ask for permission for the GPS.
     *
     * @param askingActivity
     */
    public static void ask_gps(Activity askingActivity) {
        ask_permission(askingActivity, Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_GPS);
    }

    /**
     * General permission asker. Takes in the current activity, which permission and the constant key.
     * Invokes the activities onRequestPermissionResults(), returning the key of the
     * permission of the permission was granted, otherwise PERMISSION_DENIED.
     *
     * @param askingActivity The activity which this is invoked in
     * @param permission The permission in question
     * @param permissionKey The app constant key of the permission
     */
    public static void ask_permission(Activity askingActivity, String permission, int permissionKey) {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(askingActivity,
                permission)) {
            //TODO further explain to the user why this permission is needed
        } else {
            // Lets ask for the permission
            ActivityCompat.requestPermissions(askingActivity,
                    new String[]{permission},
                    permissionKey);
        }
    }

    public static boolean is_granted(String permission, int permissionResults) {
        switch (permission) {
            // Check for fine location permission
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return PackageManager.PERMISSION_GRANTED == permissionResults;
        }

        // Return false if no permission matches
        return false;
    }
}
