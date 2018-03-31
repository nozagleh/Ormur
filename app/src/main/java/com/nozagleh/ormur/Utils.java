package com.nozagleh.ormur;

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

import java.io.File;
import java.net.ConnectException;

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
     * @throws Exception
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
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();

        return (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270);
    }
}
