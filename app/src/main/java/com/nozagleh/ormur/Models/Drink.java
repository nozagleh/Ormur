package com.nozagleh.ormur.Models;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * A custom classs object that represents a drink object.
 *
 * Created by arnarfreyr on 13/02/2018.
 */

@IgnoreExtraProperties
public class Drink {
    private String id;
    private String title;
    private Double rating;
    private String description;
    private String location;
    private Bitmap image;

    /**
     * Empty constructor
     */
    public Drink() {}

    /**
     * Optional constuctor.
     *
     * @param id The drink id
     * @param title The drink title
     * @param rating The drink rating
     * @param description The drink description
     * @param location The drink location (where the user was when the drink was added)
     */
    public Drink(String id, String title, Double rating, String description, String location) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.description = description;
        this.location = location;
    }

    /**
     * Get the drink id.
     *
     * @return int id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the drink id.
     *
     * @param id Drink id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the drink title.
     *
     * @return string title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the drink title.
     *
     * @param title The drink title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the drink rating.
     *
     * @return double rating
     */
    public Double getRating() {
        return rating;
    }

    /**
     * Set the drink rating.
     *
     * @param rating Drink rating
     */
    public void setRating(Double rating) {
        this.rating = rating;
    }

    /**
     * Get the drink description.
     *
     * @return string Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the drink description.
     *
     * @param description Drink description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the drink location.
     * Where the user was located when the drink was added.
     *
     * @return String location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the drink location, automatically is the last
     * recorded user location.
     *
     * @param location Current user location, or last recorded
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get the drink image.
     *
     * @return bitmap image
     */
    @Exclude
    public Bitmap getImage() {
        return image;
    }

    /**
     * Set the drink image.
     *
     * @param image Drink image
     */
    @Exclude
    public void setImage(Bitmap image) {
        this.image = image;
    }
}
