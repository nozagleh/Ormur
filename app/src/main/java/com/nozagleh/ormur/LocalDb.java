package com.nozagleh.ormur;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.nozagleh.ormur.Models.OfflineDrink;
import com.nozagleh.ormur.Models.OfflineDrinkDao;

@Database(entities = {OfflineDrink.class}, version = 1)
public abstract class LocalDb extends RoomDatabase {
    public abstract OfflineDrinkDao offlineDrinkDao();
}
