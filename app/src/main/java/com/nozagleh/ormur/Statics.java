package com.nozagleh.ormur;

import android.arch.persistence.room.Room;
import android.content.Context;

public class Statics {
    public static Context appContext;
    public static LocalDb localDb;

    public static void setAppContext(Context context) {
        if (appContext == null) {
            appContext = context;
        }
    }

    public static void setLocalDbConnection() {
        localDb = Room.databaseBuilder(appContext, LocalDb.class, "ormur-local-db").build();
    }
}
