package com.nozagleh.ormur.Models;

/**
 * Created by arnarfreyr on 13/02/2018.
 */

public class Drink {
    private String title;
    private Float rating;
    private String description;
    private String location;

    public Drink() {
    }

    public Drink(String title, Float rating, String description, String location) {
        this.title = title;
        this.rating = rating;
        this.description = description;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
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
