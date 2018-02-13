package com.nozagleh.ormur;

import android.app.Activity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by arnarfreyr on 12.2.2018.
 */

public class Data {
    public RequestQueue requestQueue;
    private static String ROOT_URL = "";

    public void setupQueue(Activity activity) {
       requestQueue = Volley.newRequestQueue(activity);
    }

    public void makeRequest() {
        
    }
}
