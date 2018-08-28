package com.nozagleh.ormur;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * General network checker class.
 *
 * Constains static methods to check the device's network status.
 */
public class NetworkChecker {
    private final static String TAG = "NetworkChecker";

    /**
     * Check the network connection,
     * based on type of network and the app context.
     *
     * @param type Type of network
     * @param appContext The application context
     * @return boolean has network
     */
    public static boolean networkCheck(int type, Context appContext) {
        ConnectivityManager connManager = (ConnectivityManager)
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connManager.getNetworkInfo(type);

        return networkInfo.isConnected();
    }

    /**
     * Check if the device has wifi access.
     *
     * @param context The application context
     * @return boolean has network
     */
    public static boolean hasWifi(Context context) {
        return networkCheck(ConnectivityManager.TYPE_WIFI, context);
    }

    /**
     * Check if the device has mobile network access.
     * @param context The application context
     * @return boolean has mobile network access
     */
    public static boolean hasMobileNetwork(Context context) {
        return networkCheck(ConnectivityManager.TYPE_MOBILE, context);
    }

    /**
     * Check if the device has a general network connection.
     *
     * @param context The application context
     * @return boolean has any type of network
     */
    public static boolean hasNetwork(Context context) {
        return hasWifi(context) || hasMobileNetwork(context);
    }
}
