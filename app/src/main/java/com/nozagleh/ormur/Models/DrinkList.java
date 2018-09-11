package com.nozagleh.ormur.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom list class for listing drinks.
 * The class can be customized and functions can be added
 * such as sorting, fetching and other operations on lists.
 *
 * Created by arnarfreyr on 09/04/2018.
 */
public class DrinkList {
    private List<Drink> drinkList;

    /**
     * Empty constructor.
     */
    public DrinkList() {
        drinkList = new ArrayList<>();
    }

    /**
     * Constructor that takes in an already made list.
     *
     * @param listOfDrinks Drink list.
     */
    public DrinkList(List<Drink> listOfDrinks) {
        drinkList = listOfDrinks;
    }

    /**
     * Add a drink to the list.
     *
     * @param drink Drink object
     */
    public void addDrink(Drink drink) {
        drinkList.add(drink);
    }

    /**
     * Remove a certain drink from the list.
     *
     * @param drink Drink to remove
     */
    public void removeDrink(Drink drink) {
        drinkList.remove(drink);
    }

    /**
     * Check if any of the drinks in the list contain the same drink being included.
     *
     * @param id The id of the current drink being checked.
     * @return Boolean true if has, false if not
     */
    public boolean hasDrink(String id) {
        for (int i = 0; i < drinkList.size(); i++) {
            if (drinkList.get(i).getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get a single drink from the list.
     *
     * @param pos The position of the drink
     * @return A single drink
     */
    public Drink getDrink(int pos) {
        if (drinkList.size() >= pos) {
            return drinkList.get(pos);
        }

        return null;
    }

    /**
     * Get a single drink from the list by the id(key) of the drink.
     *
     * @param id Drink id/key
     * @return mixed Drink or null if empty
     */
    public Drink getDrinkByKey(String id) {
        for (int i = 0; i < drinkList.size(); i++) {
            if (drinkList.get(i).getId().equals(id)) {
                return drinkList.get(i);
            }
        }

        return null;
    }

    /**
     * Get the list size.
     *
     * @return The size of the list
     */
    public int listSize() {
        return drinkList.size();
    }
}
