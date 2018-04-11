package com.nozagleh.ormur;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nozagleh.ormur.Models.Drink;
import com.nozagleh.ormur.Models.DrinkList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App extends AppCompatActivity {
    private static String ACTIVITY_TAG = "App";

    // Activity toolbar
    private Toolbar toolbar;

    // Floating action button
    private FloatingActionButton fabAddDrink;

    // Setup the recyclerview
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int COLUMN_SINGLE = 1;
    private static final int COLUMN_MULTI = 2;

    private DataSnapshot listSnapShot;
    private ValueEventListener eventListener;

    // The local list of drinks
    private DrinkList listOfDrinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        toolbar = findViewById(R.id.toolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setNavigationIcon(R.mipmap.ic_launcher_round);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        fabAddDrink = findViewById(R.id.quickAddItem);
        fabAddDrink.setOnClickListener(handleAddClick());


        if (!Permissions.hasStorage(this)) {
            Permissions.askStorage(this);
        }

        listOfDrinks = new DrinkList();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initRecycler();

        // fetch datasnapshot
        listenForDrinks();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        AlertDialog.Builder alertOnClose = new AlertDialog.Builder(this);
        alertOnClose.setTitle("Do you want to exit");
        alertOnClose.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertOnClose.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
    }

    /**
     * Return a click listener that launches the adding
     * of a new drink. This is returned as a clicklistener, since
     * it could be useful for adding to other elements as a listener
     * in the future.
     *
     * @return View.OnClickListener
     */
    private View.OnClickListener handleAddClick() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Run the adding of a new drink
                addNewDrink();
            }
        };
    }

    /**
     * Initiate the swipe to refresh for the SwipeRefreshLayout.
     */
    private void initSwipeToRefresh() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Repopulate the drink list on refresh
                reloadListData();
            }
        });
    }

    /**
     * Initiate the recycler view in the activity.
     */
    private void initRecycler() {
        // Locate the recycler
        mRecyclerView = findViewById(R.id.list);

        // Add a linear layout and set it as the layout manager for the recycler view
        if (Utils.isLandscape(this)) {
            mLayoutManager = new GridLayoutManager(this,COLUMN_MULTI);

        } else {
            mLayoutManager = new GridLayoutManager(this,COLUMN_SINGLE);

        }

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = setListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        // Init the swipe to refresh funcion
        initSwipeToRefresh();
    }

    private void listenForDrinks() {
        if(!FirebaseData.isListening()) {
            FirebaseData.listenForDrinkChanges(eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(ACTIVITY_TAG, "data changed");
                    if (!dataSnapshot.equals(listSnapShot)) {
                        listSnapShot = dataSnapshot;
                        reloadListData();
                    } else {
                        Log.d(ACTIVITY_TAG, "list is the same");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(ACTIVITY_TAG, "Database error", databaseError.toException());
                    Utils.showSnackBar(findViewById(R.id.list), getString(R.string.list_error));
                }
            });
        } else {
            stopListening();
            listenForDrinks();
        }
    }

    private void stopListening() {
        FirebaseData.stopListeningForDrinkChanges(eventListener);
    }

    private void reloadListData() {
        if (listSnapShot == null) {
            return;
        }

        for (DataSnapshot data : listSnapShot.getChildren()) {
            // Create a new drink object
            Drink drink = new Drink();

            // Get title first so we can check the value
            drink.setTitle((String) data.child("title").getValue());

            // Set the drink values
            drink.setId(data.getKey());
            drink.setDescription((String) data.child("description").getValue());
            drink.setLocation((String) data.child("location").getValue());

            // Check if rating comes as long or double
            if (data.child("rating").getValue() instanceof Long) {
                // Get the long value
                Long ratingLong = (long) data.child("rating").getValue();
                // Convert the rating to double
                drink.setRating(ratingLong.doubleValue());
            } else if (data.child("rating").getValue() instanceof Double) {
                // Cast the rating to double and set the drink rating
                drink.setRating((double) data.child("rating").getValue());
            }


            if (listOfDrinks.hasDrink(data.getKey())) {
                Drink oldDrink = listOfDrinks.getDrinkByKey(data.getKey());

                if (!oldDrink.equals(drink)) {
                    listOfDrinks.removeDrink(oldDrink);
                    listOfDrinks.addDrink(drink);
                }

            } else {
                // Add the drink to the list
                listOfDrinks.addDrink(drink);
            }

            mAdapter.notifyDataSetChanged();
        }

        // Check if is refreshing
        if(mSwipeRefreshLayout.isRefreshing()) {
            // Disable refreshing UI
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Set a list adapter to the recycler view.
     *
     * @return Instance of the drink recycler adapter, used for callbacks
     */
    private DrinkRecyclerViewAdapter setListAdapter() {
        return new DrinkRecyclerViewAdapter(listOfDrinks, new App.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteractionClick(Drink item) {
                // Call for opening of detailed view on item click
                openDrinkDetails(item);
            }
        });
    }

    /**
     * Handle bottom navigation clicks
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // Do nothing on list click, since the activity will always have the list
                    return true;
                case R.id.navigation_dashboard:
                    addNewDrink();

                    // Return that an object was found
                    return true;
                case R.id.navigation_notifications:
                    // Send toast about the function not being implemented yet
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.nothing_here), Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
            }

            // No item was found
            return false;
        }

    };

    private void addNewDrink() {
        // Call for an empty drink details view to be started
        // For adding a new drink
        Intent drinkDetails = new Intent(getApplicationContext(), DrinkDetail.class);

        // Notify the intent activity that a new drink is being added
        drinkDetails.putExtra("isNew",true);

        // Start the activity
        startActivity(drinkDetails);
    }

    /**
     * Open a detailed view of a drink item.
     *
     * Creates an intent for the DrinkDetails activity, with the needed
     * data added as extras to the intent.
     *
     * @param item The drink item
     */
    private void openDrinkDetails(Drink item) {
        // Create a new intent
        Intent drinkDetails = new Intent(this, DrinkDetail.class);

        // Put all the drink details into the intent extras
        drinkDetails.putExtra("id", item.getId());
        drinkDetails.putExtra("title", item.getTitle());
        drinkDetails.putExtra("description", item.getDescription());
        drinkDetails.putExtra("location", item.getLocation());
        drinkDetails.putExtra("rating", item.getRating());

        if (item.getImage() != null) {
            File file = Utils.cacheImage(this, item.getId() + ".jpeg", item.getImage());
            drinkDetails.putExtra("cachedImage", file.getName());
        }

        // Start the activity
        startActivity(drinkDetails);
    }

    /**
     * Override on request permission results.
     *
     * @param requestCode The request code
     * @param permissions The permission in question
     * @param grantResults The results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteractionClick(Drink item);
    }
}
