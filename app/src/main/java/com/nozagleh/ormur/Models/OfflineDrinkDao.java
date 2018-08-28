package com.nozagleh.ormur.Models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Offline drink interface.
 *
 * The interface provides the local database queries needed
 * for selecting, inserting, updating and deleting the local
 * database cached drinks.
 */
@Dao
public interface OfflineDrinkDao {
    /**
     * Get all the drinks from the database.
     */
    @Query("SELECT * FROM drinks")
    List<Drink> getAll();

    /**
     * Only get unsynced entries from the database.
     */
    @Query("SELECT * FROM drinks WHERE is_synced = 0")
    List<Drink> unsyncedDrinks();

    /**
     * Insert a list of drinks into the database.
     * @param drinks List of drinks
     */
    @Insert
    void insertAll(Drink...drinks);

    /**
     * Insert a single drink into the database.
     *
     * @param drink Single drink instance
     */
    @Insert
    void insertSingle(Drink drink);

    /**
     * Update a single drink in the database.
     *
     * @param drink Single drink instance
     */
    @Update
    void updateSingle(Drink drink);

    /**
     * Delete a single drink from the database.
     *
     * @param drink Single drink instance
     */
    @Delete
    void delete(Drink drink);
}
