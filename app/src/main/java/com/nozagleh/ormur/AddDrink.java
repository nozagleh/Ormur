package com.nozagleh.ormur;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.nozagleh.ormur.Models.Drink;

import org.w3c.dom.Text;

import java.io.File;
import java.net.URI;
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
public class AddDrink extends Fragment implements View.OnClickListener {
    private static final String FRAGMENT_TAG = "AddDrink";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DRINK_ID = "drinkId";
    private static final String DRINK_NAME = "drinkName";
    private static final String DRINK_DESC = "drinkDesc";
    private static final String DRINK_RATING = "drinkRating";
    private static final String DRINK_EDIT = "drinkEdit";

    private View view;

    private ViewFlipper viewFlipper;
    private Button btnPrev;
    private Button btnNext;
    private List<TextView> dots;
    private int current_dot = 0;

    private EditText txtName;
    private EditText txtDescription;

    private RatingBar seekBar;
    private ImageView cameraImage;
    private Button btnAddImage;

    private static Boolean isImageSet = false;

    // TODO: Rename and change types of parameters
    private String drinkId;
    private String drinkName;
    private String drinkDesc;
    private Double drinkRating;
    private Boolean isEdit = false;

    private OnFragmentInteractionListener mListener;

    private Data data;

    private static final int IMAGE_CAPTURE = 1;
    private File imageFile;
    private Bitmap scaledImage;
    private Uri imageURI;

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
        view = inflater.inflate(R.layout.fragment_add_drink, container, false);

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
        TextView dot3 = view.findViewWithTag(R.id.dot3);
        dots.add(dot3);

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
        cameraImage = view.findViewById(R.id.cameraImage);
        btnAddImage = view.findViewById(R.id.btnAddImage);
        btnAddImage.setOnClickListener(this);

        getImage();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Locator.startListening(getActivity());
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
                Log.d(FRAGMENT_TAG, String.valueOf(dots.size()));
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
                Location location = Locator.getLocation();

                String locationString = "";
                if (location != null) {
                    locationString = String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());
                }

                // Create a drink from the available information
                Drink drink = new Drink();
                drink.setId(drinkId);
                drink.setTitle(txtName.getText().toString());
                drink.setDescription(txtDescription.getText().toString());
                drink.setRating((double)seekBar.getRating());
                drink.setLocation(locationString);

                String key;
                if (drink.getId() != null) {
                    key = FirebaseData.setDrink(drink, drink.getId());
                } else {
                    key = FirebaseData.setDrink(drink, null);
                }

                //cameraImage.setDrawingCacheEnabled(true);
                //cameraImage.buildDrawingCache();
                if (isImageSet && imageFile != null && scaledImage != null) {
                    FirebaseData.setImage(key, scaledImage);
                    //Uri imageUri = FileProvider.getUriForFile(getContext(),getActivity().getPackageName() + ".fileprovider", imageFile);
                    //FirebaseData.setImage(key, imageUri);
                }

                isEdit = true;
                mListener.doneAddingDrink(getString(R.string.drink_added,drink.getTitle()));
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();

        Locator.stopListening();
    }

    public void removeDrink() {
        FirebaseData.removeDrink(drinkId);

        mListener.doneAddingDrink(getString(R.string.drink_removed,drinkName));
    }

    public void changeDot(int index) {
        TextView oldDot = dots.get(current_dot);
        TextView newDot = dots.get(index);

        Log.d(FRAGMENT_TAG, String.valueOf(current_dot));
        Log.d(FRAGMENT_TAG, String.valueOf(index));
        Log.d(FRAGMENT_TAG, String.valueOf(dots.get(2)));

        //oldDot.setTextColor(getResources().getColor(R.color.colorPrimary));
        //newDot.setTextColor(getResources().getColor(R.color.colorAccent));

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

    @Override
    public void onClick(View view) {
        takeImage();
    }

    private void getImage() {
        FirebaseData.getImage(drinkId, new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (bytes != null) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    cameraImage.setImageBitmap(image);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void takeImage() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if ( cameraIntent.resolveActivity(view.getContext().getPackageManager()) != null ) {
            try {
                imageFile = createTempImageFile("image",".jpg");
                imageFile.delete();

                imageURI = FileProvider.getUriForFile(getContext(),getActivity().getPackageName() + ".fileprovider",imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);

                startActivityForResult(cameraIntent, IMAGE_CAPTURE);
            } catch (Exception e) {
                Log.d(FRAGMENT_TAG, "Could not create image file");
                Permissions.askStorage(getActivity());
            }
        }
    }

    private File createTempImageFile(String prefix, String suffix) throws Exception {
        File tempDirectory = new File(getContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES),".temp"
        );

        if (!tempDirectory.mkdir() || tempDirectory.exists()) {
            Log.d(FRAGMENT_TAG, "File not created");
        }

        return File.createTempFile(prefix, suffix, tempDirectory);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == IMAGE_CAPTURE && resultCode == Activity.RESULT_OK ) {
            setImage();
        }
    }

    private void setImage() {
        this.getActivity().getContentResolver().notifyChange(imageURI, null);
        ContentResolver contentResolver = this.getActivity().getContentResolver();

        Bitmap image;

        try {
            // Get the image from URI
            image = android.provider.MediaStore.Images.Media.getBitmap(contentResolver, imageURI);
            // Get the correct size for the image
            image = Utils.getImageSize(image, Utils.ImageSizes.LARGE);

            if (image != null) {
                cameraImage.setImageBitmap(image);
                scaledImage = image;
                isImageSet = true;
            }

        } catch (Exception e) {
            Log.d(FRAGMENT_TAG, "Failed to load image");
        }
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
