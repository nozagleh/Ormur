package com.nozagleh.ormur;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChecker {
    private final static String TAG = "NetworkChecker";

    public static boolean networkCheck(int type, Context appContext) {
        ConnectivityManager connManager = (ConnectivityManager)
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connManager.getNetworkInfo(type);

        return networkInfo.isConnected();
    }

    public static boolean hasWifi(Context context) {
        return networkCheck(ConnectivityManager.TYPE_WIFI, context);
    }

    public static boolean hasMobileNetwork(Context context) {
        return networkCheck(ConnectivityManager.TYPE_MOBILE, context);
    }

    public static boolean hasNetwork(Context context) {
        return hasWifi(context) || hasMobileNetwork(context);
    }
}
