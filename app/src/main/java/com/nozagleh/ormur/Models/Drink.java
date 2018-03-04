package com.nozagleh.ormur.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by arnarfreyr on 13/02/2018.
 */

@IgnoreExtraProperties
public class Drink {
    private String id;
    private String title;
    private Double rating;
    private String description;
    private String location;

    public Drink() {
        //this.id = -1;
    }

    public Drink(String id, String title, Double rating, String description, String location) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.description = description;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
