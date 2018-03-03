package com.nozagleh.ormur;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nozagleh.ormur.Models.Drink;

import org.json.JSONObject;

/**
 * Created by arnarfreyr on 03/03/2018.
 */

public class FirebaseData {
    private static String CLASS_TAG = "FirebaseData";

    private static FirebaseAuth firebaseAuth;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference("drinks");

    public static void setDrink(Drink drink, String id) {
        if (id != null) {
            reference.child(getUser().getUid()).child(id).setValue(drink);
        } else {
            DatabaseReference childReference = reference.child(getUser().getUid());
            childReference.push().setValue(drink);
        }
    }

    public static void removeDrink(String id) {
        reference.child(getUser().getUid()).child(id).removeValue();
    }

    public static void getDrinks(ValueEventListener listener) {
        DatabaseReference childReference = reference.child(getUser().getUid());
        childReference.addValueEventListener(listener);
    }

    public static FirebaseUser getUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser();
    }
}
