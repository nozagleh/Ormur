package com.nozagleh.ormur;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.nozagleh.ormur.Models.Drink;

import java.util.List;

public class App extends AppCompatActivity implements DrinkFragment.OnListFragmentInteractionListener, AddDrink.OnFragmentInteractionListener {
    private static String ACTIVITY_TAG = "App";

    public static String STORAGE = "AppStorage";
    public static String USER_KEY = "userKey";

    private AddDrink addDrinkFragment;
    private DrinkFragment drinkListFragment;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        addDrinkFragment = new AddDrink();
        drinkListFragment = new DrinkFragment();

        sharedPreferences = getSharedPreferences(STORAGE, 0);

        Data data = new Data();
        data.setupQueue(this);

        if (sharedPreferences.getString(USER_KEY, "").length() <= 0) {
            data.addUser(new Data.DataInterface() {
                @Override
                public void OnDataRecieved(List<Drink> drinks) {
                    // Nothing here
                }

                @Override
                public void OnUserReceived(String userKey) {
                    Log.d(ACTIVITY_TAG, userKey);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USER_KEY, userKey);
                    editor.apply();
                }

                @Override
                public void OnError(Exception exception) {
                    // TODO error
                }
            });
        }

        Log.d(ACTIVITY_TAG, sharedPreferences.getString(USER_KEY,""));

        if (findViewById(R.id.content) != null) {

            if (savedInstanceState != null) {
                return;
            }

            getSupportFragmentManager().beginTransaction().add(R.id.content, drinkListFragment).commit();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, drinkListFragment).addToBackStack(null).commit();
                    return true;
                case R.id.navigation_dashboard:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, addDrinkFragment).addToBackStack(null).commit();
                    return true;
                case R.id.navigation_notifications:
                    //getSupportFragmentManager().beginTransaction().add(R.id.content, addDrink).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:
                // TODO add search
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListFragmentInteraction(Drink item) {
        AddDrink addDrink = AddDrink.newInstance(item.getTitle(), item.getDescription());
        getSupportFragmentManager().beginTransaction().replace(R.id.content, addDrink).addToBackStack(null).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
