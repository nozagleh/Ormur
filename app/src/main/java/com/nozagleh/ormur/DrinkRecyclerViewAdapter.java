package com.nozagleh.ormur;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nozagleh.ormur.App.OnListFragmentInteractionListener;
import com.nozagleh.ormur.Models.Drink;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Drink} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class DrinkRecyclerViewAdapter extends RecyclerView.Adapter<DrinkRecyclerViewAdapter.ViewHolder> {
    private final String CLASS_TAG = "DrinkRecyclerViewA";

    private final List<Drink> mDrinks;
    private final OnListFragmentInteractionListener mListener;

    public DrinkRecyclerViewAdapter(List<Drink> drinks, OnListFragmentInteractionListener listener) {
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
        holder.mItem = mDrinks.get(position);
        holder.mIdView.setText(mDrinks.get(position).getTitle());
        holder.mContentView.setText(mDrinks.get(position).getDescription());
        holder.mRating.setText(String.valueOf(mDrinks.get(position).getRating()));
        holder.mImage.setImageBitmap(holder.mItem.getImage());

        if (holder.mItem.getImage() == null) {
            FirebaseData.getImage(mDrinks.get(position).getId(), new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    if (image != null) {
                        holder.mImage.setDrawingCacheEnabled(true);
                        holder.mImage.buildDrawingCache();

                        holder.mImage.setImageBitmap(image);
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(CLASS_TAG, e.getMessage());
                    holder.mImage.setImageResource(R.mipmap.beer);
                }
            });
        } else {
            holder.mImage.setImageBitmap(holder.mItem.getImage());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.onListFragmentInteractionClick(holder.mItem);
                }
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
        public final ImageView mImage;

        public Drink mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mRating = (TextView) view.findViewById(R.id.rating);
            mImage = (ImageView) view.findViewById(R.id.image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
