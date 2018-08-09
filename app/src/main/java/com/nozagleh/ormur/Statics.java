package com.nozagleh.ormur;

import android.content.Context;

public class Statics {
    public static Context appContext;

    public static void setAppContext(Context context) {
        if (appContext == null) {
            appContext = context;
        }
    }
}
