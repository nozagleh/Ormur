package com.nozagleh.ormur.jobManager;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;


/**
 * The class handles the job scheduling of background
 * jobs that the application will run.
 */
public class JobHandler {
    private static FirebaseJobDispatcher dispatcher;

    // Offline job sync
    private static String SYNC_TAG = "com.nozagleh.ormur.offlineSync";

    public static void initJobDispatcher(Context context) {
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
    }

    /**
     * Starts and configures a sync background job.
     */
    public static void startSyncJob() {
        Job syncJob = dispatcher.newJobBuilder()
                .setService(SyncDrinksJobService.class)
                .setTag(SYNC_TAG)
                .setRecurring(true)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(0, 120))
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setReplaceCurrent(true)
                .setConstraints(
                        Constraint.ON_ANY_NETWORK
                )
                .build();

        dispatcher.mustSchedule(syncJob);
    }
}
