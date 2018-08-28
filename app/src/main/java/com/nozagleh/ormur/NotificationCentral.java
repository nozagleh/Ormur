package com.nozagleh.ormur;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.nozagleh.ormur.Models.Drink;

import java.io.File;
import java.util.List;

/**
 * Notification central class.
 *
 * Provides the functionality of delivering notifications
 * to the user for the application.
 */
public class NotificationCentral {
    // Global channel id
    protected static String APP_CHANNEL_ID = Statics.appContext.getPackageName() + "-notif";

    // Unsynced entries notification id
    protected static int UNSYNCED_ENTRIES_ID = 42;

    /**
     * Send a notification to the user.
     * Based on dynamic content provided by different aspects of the application.
     *
     * @param title string Notification title
     * @param text string Notification message
     * @param intent string The intent to be run
     */
    private static void sendNotification(String title, String text, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(Statics.appContext, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Statics.appContext, APP_CHANNEL_ID)
                .setSmallIcon(R.drawable.googleg_standard_color_18)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (pendingIntent != null) {
            mBuilder.setAutoCancel(true)
                    .setContentIntent(pendingIntent);
        }

        NotificationManagerCompat manager = NotificationManagerCompat.from(Statics.appContext);
        manager.notify(UNSYNCED_ENTRIES_ID, mBuilder.build());
    }

    /**
     * Send a sync notification to the user.
     *
     * @param drinks List of drinks synced
     */
    public static void sendSynced(List<Drink> drinks) {
        String title;

        if (drinks.size() > 1) {
            title = Statics.appContext.getString(R.string.notification_synced_plural, drinks.size());
        } else {
            title = Statics.appContext.getString(R.string.notification_synced_singular, drinks.size());
        }

        Drink drink = drinks.get(drinks.size());

        Intent intent = new Intent(Statics.appContext, DrinkDetail.class);
        // Put all the drink details into the intent extras
        intent.putExtra("id", drink.getId());
        intent.putExtra("title", drink.getTitle());
        intent.putExtra("description", drink.getDescription());
        intent.putExtra("location", drink.getLocation());
        intent.putExtra("rating", drink.getRating());
        intent.putExtra("createdDate", drink.getCreatedDate());
        intent.putExtra("updatedDate", drink.getUpdatedDate());

        if (drink.getImage() != null) {
            File file = Utils.cacheImage(Statics.appContext, drink.getId() + ".jpeg", drink.getImage());
            intent.putExtra("cachedImage", file.getName());
        }

        sendNotification(
                title,
                Statics.appContext.getString(R.string.notification_synced_text),
                intent);
    }

    /**
     * Create the notification channel if above Android O.
     */
    public static void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Set the name and description of the channel
            CharSequence name = Statics.appContext.getString(R.string.channel_name);
            String description = Statics.appContext.getString(R.string.channel_description);
            // Set the importance of the channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            // Build the channel
            NotificationChannel channel = new NotificationChannel(APP_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = Statics.appContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
