package com.nozagleh.ormur;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nozagleh.ormur.Models.Drink;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends AppCompatActivity implements DrinkFragment.OnListFragmentInteractionListener, AddDrink.OnFragmentInteractionListener {
    private static String ACTIVITY_TAG = "App";

    public static String STORAGE = "AppStorage";
    public static String USER_KEY = "userKey";

    private AddDrink addDrinkFragment;
    private DrinkFragment drinkListFragment;

    private SharedPreferences sharedPreferences;

    private Toolbar toolbar;

    private FragmentTransaction fragmentTransaction;

    private RelativeLayout searchBlock;
    private EditText searchText;

    private List<Drink> listOfDrinks;

    private FirebaseAuth firebaseAuth;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        addDrinkFragment = new AddDrink();
        drinkListFragment = new DrinkFragment();

        //fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.toString()) {
                    case "Search":
                        if (searchBlock.getVisibility() == View.GONE) {
                            if (searchText.length() > 0) {
                                searchTextChanged(searchText.getText().toString());
                            }
                            item.setIcon(R.drawable.ic_close_black_24px);
                            searchBlock.setVisibility(View.VISIBLE);

                        } else {
                            resetList();
                            item.setIcon(R.drawable.ic_search_black_24dp);
                            searchBlock.setVisibility(View.GONE);

                        }
                        return true;
                    case "Delete":
                        AddDrink addDrink = (AddDrink) getSupportFragmentManager().findFragmentById(R.id.content);
                        addDrink.removeDrink();
                        return true;
                }

                return false;
            }
        });

        sharedPreferences = getSharedPreferences(STORAGE, 0);

        searchBlock = findViewById(R.id.searchBlock);
        searchBlock.setVisibility(View.GONE);

        searchText = findViewById(R.id.txtSearch);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() <= 0) {
                    resetList();
                } else {
                    searchTextChanged(editable.toString());
                }
            }
        });

        getDrinks(false, null);

        if (findViewById(R.id.content) != null) {
            if (savedInstanceState != null) {
                return;
            }

            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            fragmentTransaction.add(R.id.content, drinkListFragment).commit();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Hide the search block
            searchBlock.setVisibility(View.GONE);

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().popBackStack();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    fragmentTransaction.replace(R.id.content, drinkListFragment).addToBackStack(null).commit();
                    return true;
                case R.id.navigation_dashboard:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    fragmentTransaction.replace(R.id.content, addDrinkFragment).addToBackStack(null).commit();
                    return true;
                case R.id.navigation_notifications:
                    //getSupportFragmentManager().beginTransaction().add(R.id.content, addDrink).commit();
                    return true;
            }
            return false;
        }

    };

    /**
     * On long click fragment interaction, a link between the activity -> fragment -> list.
     *
     * When a list item is clicked, the action is sent to the fragment holding the list,
     * then passed on to the parent activity(this) for further development.
     *
     * @param item List item being long clicked
     */
    @Override
    public void onListFragmentInteraction(Drink item) {
        AddDrink addDrink = AddDrink.newInstance(item);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.content,addDrink).addToBackStack(null).commit();
    }

    /**
     * On click fragment interaction. A link between the activity -> fragment -> list.
     *
     * When a list item is clicked, the action is sent to the fragment holding the list,
     * then passed on to the parent activity(this) for further development.
     *
     * @param item Current list item being clicked
     */
    @Override
    public void onListFragmentInteractionClick(Drink item) {
        Intent drinkDetails = new Intent(this, DrinkDetail.class);

        drinkDetails.putExtra("id", item.getId());
        drinkDetails.putExtra("title", item.getTitle());
        drinkDetails.putExtra("description", item.getDescription());
        drinkDetails.putExtra("location", item.getLocation());
        drinkDetails.putExtra("rating", item.getRating());

        startActivity(drinkDetails);
    }

    @Override
    public void setAppBarSearch() {
        toolbar.getMenu().clear();
        getMenuInflater().inflate(R.menu.bar, toolbar.getMenu());
    }

    @Override
    public void addDrinkEditDrink() {
        toolbar.getMenu().clear();
        getMenuInflater().inflate(R.menu.bar_delete, toolbar.getMenu());
    }

    @Override
    public void doneAddingDrink(String message) {
        getSupportFragmentManager().popBackStack();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.content,drinkListFragment).addToBackStack(null).commit();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.content),message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void resetList() {
        DrinkFragment drinkFragment = (DrinkFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        drinkFragment.refreshList(true);
    }

    private List<Drink> getDrinks(final Boolean isSearch, final String searchText) {
        FirebaseData.getDrinks(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create a new list of drinks
                List<Drink> drinkList = new ArrayList<>();

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

                    // Add the drink to the drink list
                    if (isSearch) {
                        Pattern searchPattern = Pattern.compile("([\\w\\s])*" + searchText + "([\\w\\s])*");
                        Matcher matcher = searchPattern.matcher(drink.getTitle().toLowerCase());

                        if (matcher.find()) {
                            drinkList.add(drink);
                        }
                    } else {
                        drinkList.add(drink);
                    }
                }
                listOfDrinks = drinkList;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return listOfDrinks;
    }

    private void searchTextChanged(final String searchText) {
        DrinkFragment drinkFragment = (DrinkFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        drinkFragment.updateList(getDrinks(true, searchText));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
