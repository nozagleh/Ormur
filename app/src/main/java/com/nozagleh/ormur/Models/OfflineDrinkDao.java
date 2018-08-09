package com.nozagleh.ormur.Models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface OfflineDrinkDao {
    @Query("SELECT * FROM offlineDrink")
    List<OfflineDrink> getAll();

    @Query("SELECT * FROM offlineDrink WHERE is_synced = false")
    List<OfflineDrink> unsyncedDrinks();

    @Insert
    void insertAll(OfflineDrink...drinks);

    @Insert
    void insertSingle(OfflineDrink drink);

    @Delete
    void delete(OfflineDrink drink);
}
