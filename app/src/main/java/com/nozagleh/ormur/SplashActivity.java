package com.nozagleh.ormur;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nozagleh.ormur.Models.Drink;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get the Firebase auth instance
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // Get if there is a user logged in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Check if any user is signed in
        isSignedIn(currentUser);
    }

    private void isSignedIn(FirebaseUser user) {
        if ( user != null ) {
            Intent appIntent = new Intent(this, App.class);
            startActivity(appIntent);
        } else {
            Intent signInIntent = new Intent(this, LoginActivity.class);
            startActivity(signInIntent);
        }

        finish();
    }
}
