package com.nozagleh.ormur;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nozagleh.ormur.App.OnListFragmentInteractionListener;
import com.nozagleh.ormur.Models.Drink;
import com.nozagleh.ormur.Models.DrinkList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Drink} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class DrinkRecyclerViewAdapter extends RecyclerView.Adapter<DrinkRecyclerViewAdapter.ViewHolder> {
    private final String CLASS_TAG = "DrinkRecyclerViewA";

    private final DrinkList mDrinks;
    private final OnListFragmentInteractionListener mListener;

    public DrinkRecyclerViewAdapter(DrinkList drinks, OnListFragmentInteractionListener listener) {
        mDrinks = drinks;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_drink, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mDrinks.getDrink(position);
        holder.mIdView.setText(mDrinks.getDrink(position).getTitle());
        if (holder.mItem.getDescription().equals("")) {
            holder.mContentView.setVisibility(View.GONE);
        } else {
            holder.mContentView.setText(mDrinks.getDrink(position).getDescription());
        }
        holder.mRating.setText(String.valueOf(mDrinks.getDrink(position).getRating()));

        if (holder.mItem.getImage() == null) {
            Bitmap cache = Utils.getCachedImage(holder.mImage.getContext(), holder.mItem.getId() + ".jpeg");
            if (cache == null) {
                holder.mImage.setVisibility(View.GONE);
            } else {
                holder.mBitmap = cache;
                holder.mImage.setDrawingCacheEnabled(true);
                holder.mImage.buildDrawingCache();
                holder.mImage.setImageBitmap(cache);
            }
        } else {
            holder.mImage.setImageBitmap(holder.mItem.getImage());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    Drink clickedDrink = holder.mItem;
                    clickedDrink.setImage(holder.mBitmap);

                    mListener.onListFragmentInteractionClick(clickedDrink);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDrinks.listSize();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mIdView;
        private final TextView mContentView;
        private final TextView mRating;
        public final ImageView mImage;
        public Bitmap mBitmap;

        public Drink mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.content);
            mRating = view.findViewById(R.id.rating);
            mImage = view.findViewById(R.id.image);
            mBitmap = null;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
