package com.nozagleh.ormur;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.nozagleh.ormur.dummy.DummyContent;
import com.nozagleh.ormur.Models.Drink;

public class App extends AppCompatActivity implements DrinkFragment.OnListFragmentInteractionListener, AddDrink.OnFragmentInteractionListener {
    private static String ACTIVITY_TAG = "App";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    DrinkFragment drinkFragment = new DrinkFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, drinkFragment).commit();
                    return true;
                case R.id.navigation_dashboard:
                    AddDrink addDrink = new AddDrink();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, addDrink).commit();
                    return true;
                case R.id.navigation_notifications:
                    //getSupportFragmentManager().beginTransaction().add(R.id.content, addDrink).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.content) != null) {

            if (savedInstanceState != null) {
                return;
            }

            DrinkFragment drinkFragment = new DrinkFragment();

            getSupportFragmentManager().beginTransaction().add(R.id.content, drinkFragment).commit();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

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
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
