package com.nozagleh.ormur;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageException;
import com.nozagleh.ormur.Models.Drink;

import java.util.ArrayList;
import java.util.List;

public class App extends AppCompatActivity {
    private static String ACTIVITY_TAG = "App";

    // Activity toolbar
    private Toolbar toolbar;

    // Setup the recyclerview
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int COLUMN_SINGLE = 1;
    private static final int COLUMN_MULTI = 2;

    // The local list of drinks
    private List<Drink> listOfDrinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        toolbar = findViewById(R.id.toolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setNavigationIcon(R.mipmap.ic_launcher_round);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        if (!Permissions.hasStorage(this)) {
            Permissions.askStorage(this);
        }

        listOfDrinks = new ArrayList<>();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        listOfDrinks = new ArrayList<>();

        mAdapter = null;

        initRecycler();

        getDrinks(false);
    }

    private void initSwipeToRefresh() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Repopulate the drink list on refresh
                getDrinks(true);
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

        // Only bind the adapter if the list is not empty
        if (listOfDrinks.size() > 0) {
            // Set the list adapter
            mAdapter = setListAdapter();
            mRecyclerView.setAdapter(mAdapter);

            // Return before fetching list since it has been populated
            return;
        }

        // Init the swipe to refresh funcion
        initSwipeToRefresh();
    }

    private void getDrinksPersistently() {
        FirebaseData.getDrinks(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDrinks(final Boolean isRefreshing) {
        final List<Drink> tempList = new ArrayList<>();
        FirebaseData.getDrinks(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               // Loop through all the drinks returned from the database
               for (DataSnapshot data : dataSnapshot.getChildren()) {
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
                    // Add the drink to the list
                   tempList.add(drink);
               }

               listOfDrinks = tempList;

               if(isRefreshing) {
                   mSwipeRefreshLayout.setRefreshing(false);
               }

               if (mAdapter == null) {
                   mAdapter = setListAdapter();
                   mRecyclerView.setAdapter(mAdapter);
               } else {
                   mAdapter.notifyDataSetChanged();
               }
           }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Utils.showSnackBar(findViewById(R.id.container),getString(R.string.list_error));
            }
        });
    }

    /**
     * Get image for a single drink item.
     *
     * After fetching the image, the image is added to the respective
     * item that it belongs to.
     *
     * @param location The location of the item in the list of items
     */
    private void getImage(final int location) {
        FirebaseData.getImage(listOfDrinks.get(location).getId(), new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (bytes != null) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    listOfDrinks.get(location).setImageBytes(bytes);
                    listOfDrinks.get(location).setImage(Utils.getImageSize(image, Utils.ImageSizes.LARGE));

                    mAdapter.notifyItemChanged(location);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.mipmap.beer);
                listOfDrinks.get(location).setImage(defaultImage);

                mAdapter.notifyDataSetChanged();

                // Get the HTTP response code
                int httpResponseCode = ((StorageException) e).getHttpResultCode();

                // Show a snackbar on failure, not 404
                if (httpResponseCode != 404) {
                    Snackbar snackbarFail = Snackbar.make(findViewById(R.id.drinkDetails),getString(R.string.image_error),Snackbar.LENGTH_SHORT);
                    snackbarFail.show();
                }
            }
        });
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
                    // Call for an empty drink details view to be started
                    // For adding a new drink
                    Intent drinkDetails = new Intent(getApplicationContext(), DrinkDetail.class);

                    // Notify the intent activity that a new drink is being added
                    drinkDetails.putExtra("isNew",true);

                    // Start the activity
                    startActivity(drinkDetails);

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

        // Start the activity
        startActivity(drinkDetails);
    }

    /**
     * Override on request permission results.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
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
