package com.nozagleh.ormur;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.nozagleh.ormur.Models.Drink;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by arnarfreyr on 12.2.2018.
 */

public class Data {
    private static String CLASS_TAG = "Data";

    DataInterface dataInterface;
    public RequestQueue requestQueue;
    private static String ROOT_URL = "http://192.168.1.22:8000/";

    public void setupQueue(Activity activity) {
       requestQueue = Volley.newRequestQueue(activity);
    }

    public void makeRequest(String url, final HashMap data, Activity activity, final DataInterface callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                List<Drink> drinks = new ArrayList<>();
                try {
                    JSONArray jsonArray = response.getJSONArray("drinks");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Drink drink = new Drink();
                            drink = gson.fromJson(jsonArray.getString(i), Drink.class);
                            drinks.add(drink);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(CLASS_TAG, e.getMessage());
                }

                callback.OnDataRecieved(drinks);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        this.requestQueue.add(request);
    }

    public void getData(String url, Activity activity, final DataInterface callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                List<Drink> drinks = new ArrayList<>();
                try {
                    JSONArray jsonArray = response.getJSONArray("drinks");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i).getJSONObject("drink");

                            Drink drink = gson.fromJson(obj.toString(), Drink.class);
                            drinks.add(drink);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(CLASS_TAG, e.getMessage());
                }
                callback.OnDataRecieved(drinks);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        this.requestQueue.add(request);
    }

    public void getDrink(Activity activity, DataInterface callback) {
        String url = ROOT_URL + "drink/get";
        this.getData(url, activity, callback);
    }

    // TODO singular and multi get drink/s
    public void getDrinks(Activity activity, DataInterface callback) {
        this.getDrink(activity, callback);
    }

    public void sendDrink(Activity activity, HashMap data, DataInterface callback) {
        String url = ROOT_URL + "drink/add/";
        this.makeRequest(url, data, activity, callback);
    }

    public interface DataInterface {
        void OnDataRecieved(List<Drink> drinks);
    }
}
