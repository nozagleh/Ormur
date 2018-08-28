package com.nozagleh.ormur.jobManager;

import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.nozagleh.ormur.FirebaseData;
import com.nozagleh.ormur.Models.Drink;
import com.nozagleh.ormur.NetworkChecker;
import com.nozagleh.ormur.NotificationCentral;
import com.nozagleh.ormur.R;
import com.nozagleh.ormur.Statics;

import java.util.ArrayList;
import java.util.List;

/**
 * The background sync job service itself.
 *
 * Runs the checking and syncing of drinks if they are listed unsynced
 * in the local caching database.
 */
public class SyncDrinksJobService extends JobService {
    private static final String JOB_TAG = "SyncDrinksJobService";

    @Override
    public boolean onStartJob(JobParameters job) {
        Statics.setAppContext(this);

        // Check if the device has network connection
        if (!NetworkChecker.hasNetwork(this)) {
            return true;
        }

        // Establish a connection to the local DB
        Statics.setLocalDbConnection();
        // Get the drinks from the database
        List<Drink> drinks = Statics.localDb.offlineDrinkDao().unsyncedDrinks();

        // Init an empty list of updated drinks
        List<Drink> updatedDrinks = new ArrayList<>();
        // A counter for informative matters (DEPRECATED)
        Integer counter = 0;

        // Loop through the locally stored drinks
        for (Drink drink : drinks) {
            // Check if the current drink is synced or not
            if (!drink.getSynced()) {
                // Upload the drink
                String key = FirebaseData.setDrink(drink, drink.getId());

                if(key != null) {
                    // Set the drink to synced, but keep it in the database
                    drink.setSynced(true);
                    Statics.localDb.offlineDrinkDao().updateSingle(drink);

                    // Add the drink to the updated drinks
                    updatedDrinks.add(drink);
                    counter++;
                }
            }
        }

        // If there are any updated drinks, call the notification central
        if (counter > 0) {
            NotificationCentral.sendSynced(updatedDrinks);
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
