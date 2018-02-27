package com.nozagleh.ormur;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    private static String ROOT_URL = "http://192.168.1.21:8000/";

    private static String TYPE_DRINK = "drink";
    private static String TYPE_USER = "user";

    public void setupQueue(Activity activity) {
       requestQueue = Volley.newRequestQueue(activity);
    }

    public void getData(String url, final DataInterface callback, int method) {
        JsonObjectRequest request = new JsonObjectRequest(method, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String type = response.get("type").toString();

                    if (type.equals(TYPE_DRINK)) {
                        callback.OnDataRecieved(convertToDrinks(response));
                    } else if(type.equals(TYPE_USER)) {
                        callback.OnUserReceived(response.getString("key"));
                    }
                } catch (JSONException e) {
                    callback.OnError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.OnError(error);
            }
        });

        this.requestQueue.add(request);
    }

    private List<Drink> convertToDrinks(JSONObject json) {
        Gson gson = new Gson();
        List<Drink> drinks = new ArrayList<>();
        try {
            JSONArray jsonArray = json.getJSONArray("drinks");
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

        return drinks;
    }

    public void getDrink(Activity activity, DataInterface callback) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(App.STORAGE,0);

        String url = ROOT_URL + "drink/get/" + sharedPreferences.getString(App.USER_KEY, "") + "/";
        this.getData(url, callback, Request.Method.GET);
    }

    public void addUser(DataInterface callback) {
        String url = ROOT_URL + "user/add";
        this.getData(url, callback, Request.Method.POST);
    }

    public interface DataInterface {
        void OnDataRecieved(List<Drink> drinks);
        void OnUserReceived(String userKey);
        void OnError(Exception exception);
    }
}
