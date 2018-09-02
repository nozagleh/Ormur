package com.nozagleh.ormur;

import android.arch.persistence.room.Room;
import android.content.Context;

/**
 * Statics class.
 *
 * Provides basic static functionality that is needed throughout the application.
 * Can for example keep the state of the application context
 * and can thus be easily fetched in a non-app context class.
 *
 * Functions
 * - Store the app context
 * - Set the local cache DB connection
 */
public class Statics {
    // Static application context variable
    public static Context appContext;
    // Static local database variable
    public static LocalDb localDb;

    /**
     * Set the application context, for ease of access
     * in non-app context classes.
     *
     * @param context Application context
     */
    public static void setAppContext(Context context) {
        if (appContext == null) {
            appContext = context;
        }
    }

    /**
     * Set the local database connection.
     */
    public static void setLocalDbConnection() {
        localDb = Room.databaseBuilder(appContext, LocalDb.class, "ormur-local-db").allowMainThreadQueries().build();
    }
}
