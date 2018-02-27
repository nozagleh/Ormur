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

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        drinkList = new ArrayList<>();

        data = new Data();
        data.setupQueue(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_drink_list, container, false);

        mListener.setAppBarSearch();

        bindSwipeRefresh();

        refreshList();

        return view;
    }

    public void bindSwipeRefresh() {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
                drinkRecyclerViewAdapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void refreshList() {
        data.getDrink(getActivity(), new Data.DataInterface() {
            @Override
            public void OnDataRecieved(List<Drink> drinks) {
                drinkList = drinks;
                Log.d(FRAGMENT_TAG,String.valueOf(drinks.size()));
                if (!adapterConnected) {
                    drinkRecyclerViewAdapter = new DrinkRecyclerViewAdapter(drinkList , mListener);

                    // Set the adapter
                    if (view instanceof SwipeRefreshLayout) {
                        Context context = view.getContext();
                        recyclerView = (RecyclerView) view.findViewById(R.id.list);
                        if (mColumnCount <= 1) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        } else {
                            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                        }
                        recyclerView.setAdapter(drinkRecyclerViewAdapter);
                    }
                }
                Log.d(FRAGMENT_TAG, String.valueOf(drinkList.size()));
                drinkRecyclerViewAdapter.notifyDataSetChanged();
                //drinkRecyclerViewAdapter.notifyItemChanged(0, drinkList.size());
            }

            @Override
            public void OnUserReceived(String userKey) {

            }

            @Override
            public void OnError(Exception exception) {

            }
        });
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
        // TODO: Update argument type and name
        void onListFragmentInteraction(Drink item);
        void setAppBarSearch();
    }
}
