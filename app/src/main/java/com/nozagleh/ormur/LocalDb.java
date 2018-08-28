package com.nozagleh.ormur;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.nozagleh.ormur.Models.Drink;
import com.nozagleh.ormur.Models.OfflineDrinkDao;

/**
 * Abstract class that specifies the connection between the app and the local database.
 */
@Database(entities = {Drink.class}, version = 2, exportSchema = false)
public abstract class LocalDb extends RoomDatabase {
    public abstract OfflineDrinkDao offlineDrinkDao();
}
