package com.nozagleh.ormur;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.nozagleh.ormur.Models.Drink;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Various small utilities used by the application.
 *
 * Created by arnarfreyr on 9.2.2018.
 */

public class Utils {
    private static final String TAG = "Utils";

    // Shared preferences add drink key
    public static final String SP_ADD_DRINK = "com.nozagleh.ormur.SP_ADD_DRINK";

    // Static strings
    public static final Integer IMAGE_CAPTURE = 1;

    /**
     * Change the size of a bitmap object depending on its aspect ration.
     *
     * @param image Original image
     * @param size What size to use
     * @return Bitmap Scaled bitmap image
     */
    public static Bitmap getImageSize(Bitmap image, ImageSizes size) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int max;
        if (size == ImageSizes.LARGE) {
            max = 1920;
        } else if (size == ImageSizes.MEDIUM) {
            max = 1400;
        } else {
            max = 1024;
        }

        int width, height;
        if (imageWidth > imageHeight) {

            float ratio = (float) imageWidth / max;
            width = max;
            height = (int) (imageHeight / ratio);
        } else {
            float ratio = (float) imageHeight / max;
            height = max;
            width = (int) (imageWidth / ratio);
        }

        return Bitmap.createScaledBitmap(image,width,height, false);
    }

    /**
     * Bitmap image sizes
     */
    public enum ImageSizes {
        SMALL,
        MEDIUM,
        LARGE
    }

    /**
     * Create a temporary image file on the file system.
     *
     * @param prefix Image prefix
     * @param suffix Image suffix
     * @param context Current application context
     *
     * @return File The newly created file
     * @throws Exception Something went wrong
     */
    public static File createTempImageFile(String prefix, String suffix, Context context) throws Exception {
        File tempDirectory = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES),".temp"
        );

        if (!tempDirectory.mkdir() || tempDirectory.exists()) {
            Log.d(TAG, "File not created");
        }

        return File.createTempFile(prefix, suffix, tempDirectory);
    }

    /**
     * Show a snackbar, needs the current view to show it in. As
     * well as the message that is to be shown.
     *
     * @param view The view of which the snackbar should be shown in
     * @param message The message to the user
     */
    public static void showSnackBar(View view, String message) {
        Snackbar snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snack.show();
    }

    /**
     * Check if the device is in landscape mode.
     *
     * @param context Current context
     * @return boolean If the orientation matches landscape
     */
    public static boolean isLandscape(Context context) {
        try {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int rotation = display.getRotation();

            return (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270);
        } catch (NullPointerException e) {
            Log.e(TAG, "Error", e);
        }

        return false;
    }

    /**
     * Cache a single image for easier fetching again.
     *
     * @param context The application context.
     * @param name The name of the file.
     * @param image The image to cache.
     * @return mixed File or null of Exception
     */
    public static File cacheImage(Context context, String name, Bitmap image) {
        if (image == null) {
            return null;
        }

        File file;
        try {
            file = new File(context.getFilesDir(), name);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            byte[] bitMapData = byteArrayOutputStream.toByteArray();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bitMapData);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch(IOException e) {
            Log.e(TAG, "Could not create file", e);
            return null;
        }

        return file;
    }

    /**
     * Get a single cached image.
     *
     * @param context The application context
     * @param fileName The name of the file.
     * @return mixed Bitmap or null of no image found
     */
    public static Bitmap getCachedImage(Context context, String fileName) {
        Bitmap image = null;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            image = BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Delete a single cached image.
     *
     * @param context The application context
     * @param fileName The name of the file
     * @return boolean If the operation was a success
     */
    public static boolean deleteCachedImage(Context context, String fileName) {
        return context.deleteFile(fileName);
    }

    /**
     * Assemble and return a temporary cached drink image name.
     * Based on drink title.
     *
     * @param drink Drink instance
     * @return string Temporary drink image name
     */
    public static String getTempDrinkImgName(Drink drink) {
        return drink.getCreatedDate() + "-" + drink.getTitle().replace(" ", "-").toLowerCase();
    }
}
