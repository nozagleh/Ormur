package com.nozagleh.ormur;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nozagleh.ormur.Models.Drink;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DrinkFragment extends Fragment {
    private static String FRAGMENT_TAG = "DrinkFragment";

    private View view;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DrinkRecyclerViewAdapter drinkRecyclerViewAdapter;
    private Data data;
    private List<Drink> drinkList;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private boolean adapterConnected = false;

    private Bundle savedBundle;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DrinkFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DrinkFragment newInstance(int columnCount) {
        DrinkFragment fragment = new DrinkFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        savedBundle = savedInstanceState;

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        // Init a drink list
        drinkList = new ArrayList<>();

        // Do a initial refresh
        refreshList(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_drink_list, container, false);
        // Set the app bar to contain the search icon
        mListener.setAppBarSearch();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshList(true);

        // Bind the swipe
        bindSwipeRefresh();
    }

    /**
     * Bind the swipe to refresh call.
     */
    public void bindSwipeRefresh() {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call for a refresh
                refreshList(false);
                // Set refreshing to false
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setListener() {
        drinkRecyclerViewAdapter = new DrinkRecyclerViewAdapter(drinkList , mListener);

        if (view instanceof SwipeRefreshLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.list);
            if (mColumnCount < 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
            }
            recyclerView.setAdapter(drinkRecyclerViewAdapter);
        }

    }

    /**
     * Refresh the local list by getting the drinks from the database
     */
    public void refreshList(Boolean onlyIfEmpty) {
        if (onlyIfEmpty) {
            return;
        }

        FirebaseData.getDrinks(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Drink> list = processData(dataSnapshot);

                if (list.size() != drinkList.size()) {
                    drinkList = list;
                    setListener();
                }

                if (drinkRecyclerViewAdapter != null) {
                    drinkRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<Drink> processData(DataSnapshot dataSnapshot) {
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

        return currentDrinkList;
    }

    public void updateList(List<Drink> drinks) {
        drinkList = drinks;

        //drinkRecyclerViewAdapter = new DrinkRecyclerViewAdapter(drinkList , mListener, getActivity().getResources());
        //recyclerView.setAdapter(drinkRecyclerViewAdapter);

        drinkRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onListFragmentInteraction(Drink item);
        void onListFragmentInteractionClick(Drink item);
        void setAppBarSearch();
    }
}
