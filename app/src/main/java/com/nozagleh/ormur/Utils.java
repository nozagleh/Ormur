package com.nozagleh.ormur;

import android.graphics.Bitmap;

/**
 * Created by arnarfreyr on 9.2.2018.
 */

public class Utils {

    /**
     * Change the size of a bitmap object depending on its aspect ration.
     *
     * @param image Original image
     * @param size What size to use
     * @return Bitmap Scaled bitmap image
     */
    public static Bitmap getImageSize(Bitmap image, ImageSizes size) {
        float aspectRation = image.getWidth() / image.getHeight();

        int width;
        if (size == ImageSizes.LARGE) {
            width = 1920;
        } else if (size == ImageSizes.MEDIUM) {
            width = 1400;
        } else {
            width = 1024;
        }
        int height = Math.round(width / aspectRation);

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
}
