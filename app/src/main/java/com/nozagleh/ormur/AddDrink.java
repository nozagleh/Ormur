package com.nozagleh.ormur;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.tasks.Task;
import com.nozagleh.ormur.Models.Drink;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddDrink.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddDrink#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddDrink extends Fragment {
    private static final String FRAGMENT_TAG = "AddDrink";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DRINK_ID = "drinkId";
    private static final String DRINK_NAME = "drinkName";
    private static final String DRINK_DESC = "drinkDesc";
    private static final String DRINK_RATING = "drinkRating";
    private static final String DRINK_EDIT = "drinkEdit";

    private ViewFlipper viewFlipper;
    private Button btnPrev;
    private Button btnNext;
    private List<TextView> dots;
    private int current_dot = 0;

    private EditText txtName;
    private EditText txtDescription;

    private RatingBar seekBar;
    private TextView seekBarText;

    // TODO: Rename and change types of parameters
    private String drinkId;
    private String drinkName;
    private String drinkDesc;
    private Double drinkRating;
    private Boolean isEdit = false;

    private OnFragmentInteractionListener mListener;

    private Data data;

    public AddDrink() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param drink Parameter 1.
     * @return A new instance of fragment AddDrink.
     */
    // TODO: Rename and change types and number of parameters
    public static AddDrink newInstance(Drink drink) {
        AddDrink fragment = new AddDrink();
        Bundle args = new Bundle();
        args.putString(DRINK_ID, drink.getId());
        args.putString(DRINK_NAME, drink.getTitle());
        args.putString(DRINK_DESC, drink.getDescription());
        args.putDouble(DRINK_RATING, drink.getRating());
        args.putBoolean(DRINK_EDIT, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            drinkName = savedInstanceState.getString(DRINK_NAME);
            drinkDesc = savedInstanceState.getString(DRINK_DESC);
        }

        if (getArguments() != null) {
            drinkId = getArguments().getString(DRINK_ID,null);
            drinkName = getArguments().getString(DRINK_NAME);
            drinkDesc = getArguments().getString(DRINK_DESC);
            drinkRating = getArguments().getDouble(DRINK_RATING);
            isEdit = getArguments().getBoolean(DRINK_EDIT, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_drink, container, false);

        viewFlipper = view.findViewById(R.id.flipper_add);
        viewFlipper.setInAnimation(view.getContext(), R.anim.slide_in_right);
        viewFlipper.setOutAnimation(view.getContext(), R.anim.slide_out_left);

        if (isEdit) {
            mListener.addDrinkEditDrink();
        }

        txtName = view.findViewById(R.id.txtName);
        txtDescription = view.findViewById(R.id.txtDesc);
        txtName.setText("");
        txtDescription.setText("");

        if(isEdit) {
            txtName.setText(drinkName);
            txtDescription.setText(drinkDesc);
        }

        dots = new ArrayList<>();
        TextView dot1 = view.findViewById(R.id.dot1);
        dots.add(dot1);
        TextView dot2 = view.findViewById(R.id.dot2);
        dots.add(dot2);

        btnPrev = view.findViewById(R.id.btn_prev);
        btnNext = view.findViewById(R.id.btn_next);

        final TextView currentDot = dots.get(current_dot);
        currentDot.setTextColor(view.getResources().getColor(R.color.colorAccent));

        if (viewFlipper.getDisplayedChild() == 0) {
            btnPrev.setVisibility(View.INVISIBLE);
        }

        btnPrev.setOnClickListener(onPreviousClick());
        btnNext.setOnClickListener(onNextClick());

        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setRating(0);
        if (isEdit && drinkRating != null) {
            seekBar.setRating((float)drinkRating.floatValue());
        }
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!isEdit) {
            outState.putString(DRINK_NAME, txtName.getText().toString());
            outState.putString(DRINK_DESC, txtDescription.getText().toString());
        }

        super.onSaveInstanceState(outState);
    }

    private Button.OnClickListener onPreviousClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setInAnimation(view.getContext(), android.R.anim.slide_in_left);
                viewFlipper.setOutAnimation(view.getContext(), android.R.anim.slide_out_right);
                viewFlipper.showPrevious();

                changeDot(viewFlipper.getDisplayedChild());

                if ( current_dot > 0 ) {
                    btnPrev.setVisibility(View.VISIBLE);
                } else {
                    btnPrev.setVisibility(View.INVISIBLE);
                }

                if ( current_dot >= dots.size() - 1 ) {
                    btnNext.setVisibility(View.INVISIBLE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnNext.setText(getResources().getText(R.string.btn_next));
                    btnNext.setOnClickListener(onNextClick());
                }
            }
        };
    }

    private Button.OnClickListener onNextClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setInAnimation(view.getContext(), R.anim.slide_in_right);
                viewFlipper.setOutAnimation(view.getContext(), R.anim.slide_out_left);
                viewFlipper.showNext();
                changeDot(viewFlipper.getDisplayedChild());
                if ( current_dot >= dots.size() - 1 ) {
                    btnNext.setText(getResources().getText(R.string.txt_finish));
                    btnNext.setOnClickListener(onFinishListener());
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                }

                if ( current_dot > 0 ) {
                    btnPrev.setVisibility(View.VISIBLE);
                } else {
                    btnPrev.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    private Button.OnClickListener onFinishListener () {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtName.length() <= 0
                || txtDescription.length() <= 0) {
                    Snackbar snackbar = Snackbar.make(view,getString(R.string.add_error),Snackbar.LENGTH_LONG);
                    snackbar.show();

                    return;
                }
                // Establish a new data class
                Location location = Locator.getLocation(getActivity());

                String locationString = String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());

                // Create a drink from the available information
                Drink drink = new Drink();
                drink.setId(drinkId);
                drink.setTitle(txtName.getText().toString());
                drink.setDescription(txtDescription.getText().toString());
                drink.setRating((double)seekBar.getRating());

                drink.setLocation(locationString);

                if (drink.getId() != null) {
                    FirebaseData.setDrink(drink, drink.getId());
                } else {
                    FirebaseData.setDrink(drink, null);
                }

                mListener.doneAddingDrink(getString(R.string.drink_added,drink.getTitle()));

                // Send the drink to the backend
                /*data.addDrink(new Data.DataInterface() {
                    @Override
                    public void OnDataRecieved(List<Drink> drinks) {
                        // Not used
                    }

                    @Override
                    public void OnUserReceived(String userKey) {
                        // Not used
                    }

                    @Override
                    public void OnError(Exception exception) {
                        Log.d(FRAGMENT_TAG, exception.getMessage());
                    }

                    @Override
                    public void OnAdd(Boolean isSuccessful) {
                        if (isSuccessful) {
                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.appCoordinator), "asdas", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            isEdit = true;
                            mListener.doneAddingDrink();
                        } else {

                        }

                    }
                }, getActivity(), drink);*/
            }
        };
    }

    public void removeDrink() {
        FirebaseData.removeDrink(drinkId);
        mListener.doneAddingDrink(getString(R.string.drink_removed,drinkName));
    }

    public void changeDot(int index) {
        TextView oldDot = dots.get(current_dot);
        TextView newDot = dots.get(index);

        oldDot.setTextColor(getResources().getColor(R.color.colorPrimary));
        newDot.setTextColor(getResources().getColor(R.color.colorAccent));

        current_dot = index;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void addDrinkEditDrink();
        void doneAddingDrink(String message);
    }
}
