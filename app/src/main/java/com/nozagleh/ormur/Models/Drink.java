package com.nozagleh.ormur.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * A custom classs object that represents a drink object.
 *
 * Created by arnarfreyr on 13/02/2018.
 */

@Entity
@IgnoreExtraProperties
public class Drink {
    private String id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "rating")
    private Double rating;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "location")
    private String location;
    private Bitmap image;
    @ColumnInfo(name = "created_date")
    private String createdDate;
    @ColumnInfo(name = "updated_date")
    private String updatedDate;

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
    public Drink(String id, String title, Double rating, String description, String location, String createdDate, String updatedDate) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.description = description;
        this.location = location;

        // Set drink dates
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
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

    /**
     * Get the created date of the drink.
     *
     * @return String Created date
     */
    public String getCreatedDate() {
        return this.createdDate;
    }

    /**
     * Set the created date of the drink.
     *
     * @param date The date it was created
     */
    public void setCreatedDate(String date) {
        this.createdDate = date;
    }

    /**
     * Get the date when the drink was last updated.
     *
     * @return String Last updated date
     */
    public String getUpdatedDate() {
        return this.updatedDate;
    }

    /**
     * Set the last updated date.
     *
     * @param date Last updated date
     */
    public void setUpdatedDate(String date) {
        this.updatedDate = date;
    }
}
