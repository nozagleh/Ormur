package com.nozagleh.ormur;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nozagleh.ormur.DrinkFragment.OnListFragmentInteractionListener;
import com.nozagleh.ormur.Models.Drink;
import com.nozagleh.ormur.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DrinkRecyclerViewAdapter extends RecyclerView.Adapter<DrinkRecyclerViewAdapter.ViewHolder> {
    private final String CLASS_TAG = "DrinkRecyclerViewA";

    private final List<Drink> mDrinks;
    private final OnListFragmentInteractionListener mListener;
    private GoogleMap map;
    private Bundle savedBundle;

    public DrinkRecyclerViewAdapter(List<Drink> drinks, OnListFragmentInteractionListener listener, Bundle bundle) {
        mDrinks = drinks;
        mListener = listener;
        savedBundle = bundle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_drink, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mDrinks.get(position);
        holder.mIdView.setText(mDrinks.get(position).getTitle());
        holder.mContentView.setText(mDrinks.get(position).getDescription());
        holder.mRating.setText(mDrinks.get(position).getRating().toString());

        final int pos = position;
        holder.mMap.onCreate(savedBundle);
        holder.mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                String[] locStrings = mDrinks.get(pos).getLocation().split(",");
                LatLng latLng =  new LatLng(Double.valueOf(locStrings[0]), Double.valueOf(locStrings[1]));
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 10);

                map.addMarker(new MarkerOptions().position(latLng)).setVisible(true);
                //map.animateCamera(location);
                map.moveCamera(location);
                holder.mMap.onResume();
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDrinks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mRating;
        public final MapView mMap;

        public Drink mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mRating = (TextView) view.findViewById(R.id.rating);
            mMap = (MapView) view.findViewById(R.id.map);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    private void ma() {

    }
}
