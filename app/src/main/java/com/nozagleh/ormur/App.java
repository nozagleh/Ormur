package com.nozagleh.ormur;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
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

    // The local list of drinks
    private List<Drink> listOfDrinks;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        listOfDrinks = new ArrayList<>();

        initRecycler();
        getImages();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void initRecycler() {
        mRecyclerView = findViewById(R.id.list);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (listOfDrinks.size() > 0) {
            mAdapter = setListAdapter();
            mRecyclerView.setAdapter(mAdapter);

            return;
        }

        FirebaseData.getDrinks(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop through all the drinks returned from the database
                for (DataSnapshot data:dataSnapshot.getChildren()) {
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

                    listOfDrinks.add(drink);
                }

                if(mAdapter == null) {
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

    private DrinkRecyclerViewAdapter setListAdapter() {
        return new DrinkRecyclerViewAdapter(listOfDrinks, new App.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteractionClick(Drink item) {
                openDrinkDetails(item);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    Intent drinkDetails = new Intent(getApplicationContext(), DrinkDetail.class);
                    drinkDetails.putExtra("isNew",true);

                    startActivity(drinkDetails);
                    return true;
                case R.id.navigation_notifications:
                    // Nothing here yet
                    return true;
            }
            return false;
        }

    };

    private void openDrinkDetails(Drink item) {
        Intent drinkDetails = new Intent(this, DrinkDetail.class);

        drinkDetails.putExtra("id", item.getId());
        drinkDetails.putExtra("title", item.getTitle());
        drinkDetails.putExtra("description", item.getDescription());
        drinkDetails.putExtra("location", item.getLocation());
        drinkDetails.putExtra("rating", item.getRating());

        drinkDetails.putExtra("imge", item.getImageBytes());

        startActivity(drinkDetails);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Get the list of drinks.
     */
    private void getList() {
        FirebaseData.getDrinks(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create a new list of drinks
                List<Drink> currentDrinkList = new ArrayList<>();

                // Loop through all the drinks returned from the database
                for (DataSnapshot data:dataSnapshot.getChildren()) {
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

                    currentDrinkList.add(drink);
                }
                listOfDrinks = currentDrinkList;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Utils.showSnackBar(findViewById(R.id.container),getString(R.string.list_error));
            }
        });
    }

    /**
     * Get the list item images.
     */
    private void getImages() {
        for(int i = 0; i < listOfDrinks.size(); i++) {
            final int nr = i;
            FirebaseData.getImage(listOfDrinks.get(i).getId(), new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    if (bytes != null) {
                        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        listOfDrinks.get(nr).setImage(Utils.getImageSize(image, Utils.ImageSizes.LARGE));

                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.showSnackBar(findViewById(R.id.container), getString(R.string.list_error));
                }
            });
        }
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
