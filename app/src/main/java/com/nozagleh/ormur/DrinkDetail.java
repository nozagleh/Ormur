package com.nozagleh.ormur;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageException;
import com.nozagleh.ormur.Models.Drink;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DrinkDetail extends AppCompatActivity {
    private static final String TAG = "DrinkDetails";

    // Boolean to set if new object is being added or edited
    Boolean isNew = false, isEdit = false, hasChanged = false, hasImageChanged = false;

    // Actionbar menu
    Menu saveDeleteMenu;

    // Toolbar
    Toolbar toolbar;

    // Current drink
    Drink currentDrink;
    // Drink image
    Bitmap image;
    // Drink image URI
    Uri imageURI;

    // Display fields
    ImageView imageView;
    // Image view change hint
    TextView txtImageHint;

    // Text fields list
    List<TextView> textFields;

    // Text fields
    TextView txtTitle;
    TextView txtDescription;

    // Edit fields list
    List<EditText> editFields;

    // Edit fields
    EditText txtTitleEdit;
    EditText txtDescriptionEdit;

    RatingBar ratingBar;
    RatingBar ratingBarEdit;

    // Floating edit action button
    FloatingActionButton editButton;


    SharedPreferences sharedPreferences;

    String tempTitle, tempDescription;
    Float tempRating;

    /**
     * OnCreate activity.
     *
     * @param savedInstanceState Bundle saved bundle data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_detail);

        // Check if the intent is for a new item
        getIsNew();

        // Set the toolbar
        setupToolbar();

        // Init the image view
        imageView = findViewById(R.id.imgDrink);

        // Start by initializing edit fields
        initEditFields();

        // Init the text views
        initTextFields();

        // Init the rating bars
        initRatingBar();

        // Set the floating button
        setupEditButton();

        // Set the fields visibility
        setFieldsVisibility();

        // Prepare the image changer based on is new boolean
        prepareImageChange(isNew);

        // Set the text details
        setDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!Permissions.hasGPS(this)) {
            Permissions.askGPS(this);
        }

        Locator.startListening(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Locator.stopListening();
    }

    /**
     * Inflate the options menu.
     *
     * @param menu Menu being inflated
     * @return Boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Set the menu to the local menu
        saveDeleteMenu = menu;

        // Inflate the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bar_save, menu);

        // Hide menu by default
        showMenu(isNew);

        return true;
    }

    /**
     * On app bar menu item selected.
     *
     * @param item MenuItem
     * @return Boolean if item clicked was found
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_bar_save) {
            // Save the current drink
            saveDrink();
            return true;
        } else if (item.getItemId() == R.id.app_bar_delete) {
            // Remove the current drink
            if (!isNew) {
                FirebaseData.removeDrink(currentDrink.getId());
            }
            // Finish the activity
            this.finish();
            return true;
        }

        return false;
    }

    /**
     * Return a TextWatcher to watch for changes in various text fields.
     *
     * @return TextWatcher
     */
    private TextWatcher onTextChange() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Set the state of change to has changed
                hasChanged = true;

                // Set the drink information from the edit fields
                combineFields();
            }
        };
    }

    /**
     * Creates a new on ratingbar change listener.
     *
     * Listens for changes to a ratingbar to see if it has changed.
     *
     * @return RatingBar.OnRatingBarChangeListener ratingbar change listener
     */
    private RatingBar.OnRatingBarChangeListener onRatingBarChange() {
        return new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                hasChanged = true;
            }
        };
    }


    /**
     * Return a new onClick listener for when the image is going to be changed.
     *
     * @return OnClickListener Listens for clicks for items bound to the listener
     */
    private View.OnClickListener onImageCLick() {
        return new View.OnClickListener() {
            @Override   
            public void onClick(View view) {
                initiateCamera();
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Get the boolean to check if a new item is being added with the current intent.
     */
    private void getIsNew() {
        Intent intent = getIntent();

        isNew = intent.getBooleanExtra("isNew",false);

        if (isNew) {
            sharedPreferences = getSharedPreferences(Utils.SP_ADD_DRINK, Context.MODE_PRIVATE);
            tempTitle = sharedPreferences.getString("title","");
            tempDescription = sharedPreferences.getString("description", "");
            tempRating = sharedPreferences.getFloat("rating", 0);
        }
    }

    /**
     * Show or hide the actionbar save/delete menu.
     *
     * @param showMenu Boolean show or hide the menu
     */
    private void showMenu(Boolean showMenu) {
        if (saveDeleteMenu != null) {
            saveDeleteMenu.setGroupVisible(R.id.saveDeleteGroup, showMenu);
        }
    }

    /**
     * Setup the toolbar and the actionbar menu.
     */
    private void setupToolbar() {
        toolbar = findViewById(R.id.detailsToolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24px);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Get the intent details to see if a new drink is being added
     */
    private void setDetails() {
        // Get the intent
        Intent intent = getIntent();

        // Check if the intent is not empty
        if (intent != null) {
            if (intent.getBooleanExtra("addNew",false)) {
                isNew = true;
                // Show the edit fields if adding ned
                setEditFieldsVisibility(View.VISIBLE);

                getInfoFromStore();
            } else {
                // Set current drink
                setDrinkFromIntent(intent);
                // Set field information if not new
                setFieldInformation();
            }
        }
    }

    private void getInfoFromStore() {
        currentDrink = new Drink();
        currentDrink.setTitle(tempTitle);
        currentDrink.setDescription(tempDescription);
        currentDrink.setRating(tempRating.doubleValue());

        loadInfoToFields();
    }

    private void loadInfoToFields() {
        txtTitleEdit.setText(currentDrink.getTitle());
        txtDescriptionEdit.setText(currentDrink.getDescription());
        ratingBarEdit.setRating(currentDrink.getRating().floatValue());
        imageView.setImageBitmap(currentDrink.getImage());
    }

    /**
     * Initiate edit fields.
     */
    private void initEditFields() {
        // Set the list
        editFields = new ArrayList<>();

        // Bind the fields
        txtTitleEdit = findViewById(R.id.txtTitleEdit);
        txtDescriptionEdit = findViewById(R.id.txtDescriptionEdit);

        // Add on text change listeners
        txtTitleEdit.addTextChangedListener(onTextChange());
        txtDescriptionEdit.addTextChangedListener(onTextChange());

        // Add the fields to the list
        editFields.add(txtTitleEdit);
        editFields.add(txtDescriptionEdit);
    }

    /**
     * Initiate the text fields.
     *
     * Add the text fields to a list for easy iteration access
     */
    private void initTextFields() {
        // Set the list
        textFields = new ArrayList<>();

        // Bind content fields
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);

        // Bind image hint field
        txtImageHint = findViewById(R.id.txtImageAddHint);

        // Add the content fields to a list
        textFields.add(txtTitle);
        textFields.add(txtDescription);
    }

    private void initRatingBar() {
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setEnabled(false);

        ratingBarEdit = findViewById(R.id.ratingBarEdit);
        ratingBarEdit.setOnRatingBarChangeListener(onRatingBarChange());
    }

    /**
     * Set the visibility of the text and edit fields,
     * based on the status of isNew and isEdit.
     */
    private void setFieldsVisibility() {
        if (isNew || isEdit) {
            // Set the visibility of the text and edit fields
            setTextFieldsVisibility(View.GONE);
            setEditFieldsVisibility(View.VISIBLE);
            setRatingBarVisibility(true);

            prepareImageChange(true);

            // Hide the edit button if the item is new
            if (isNew) {
                editButton.setVisibility(View.GONE);
            }
        } else {
            // Set the visibility of the text and edit fields
            setEditFieldsVisibility(View.GONE);
            setTextFieldsVisibility(View.VISIBLE);
            setRatingBarVisibility(false);

            prepareImageChange(false);
        }
    }

    /**
     * Set the visibility for the text fields.
     *
     * @param visibility View.INT Which visibility option should be set
     */
    private void setTextFieldsVisibility(int visibility) {
        for (int i = 0; i < textFields.size(); i++) {
            textFields.get(i).setVisibility(visibility);
        }
    }

    /**
     * Set the visibility for the edit fields.
     *
     * @param visibility View.INT Which visibility option should be set
     */
    private void setEditFieldsVisibility(int visibility) {
        for (int i = 0; i < editFields.size(); i++) {
            editFields.get(i).setVisibility(visibility);
        }
    }

    private void setRatingBarVisibility(Boolean isEditing) {
        Log.d("DrinkDetail", isEditing.toString());
        ratingBar.setVisibility(View.GONE);
        ratingBarEdit.setVisibility(View.GONE);
        if (isEditing) {
            ratingBarEdit.setVisibility(View.VISIBLE);
        } else {
            ratingBar.setVisibility(View.VISIBLE);
        }
    }

    private void setDrinkFromIntent(Intent intent) {
        currentDrink = new Drink();
        currentDrink.setId(intent.getStringExtra("id"));
        currentDrink.setTitle(intent.getStringExtra("title"));
        currentDrink.setDescription(intent.getStringExtra("description"));
        currentDrink.setLocation(intent.getStringExtra("location"));
        currentDrink.setRating(intent.getDoubleExtra("rating",0));
    }

    /**
     * Set all field values based on the intent.
     */
    private void setFieldValues() {
        // Set text fields
        txtTitle.setText(currentDrink.getTitle());
        txtDescription.setText(currentDrink.getDescription());

        // Set edit fields
        txtTitleEdit.setText(currentDrink.getTitle());
        txtDescriptionEdit.setText(currentDrink.getDescription());

        ratingBar.setRating(currentDrink.getRating().floatValue());
        ratingBarEdit.setRating(currentDrink.getRating().floatValue());
    }

    /**
     * Set all field information. Text, editfields, and image.
     */
    private void setFieldInformation() {
        // Set all fields values
        setFieldValues();

        // Get the image from the firebase storage
        FirebaseData.getImage(currentDrink.getId(), new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (bytes != null) {
                    image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image = Utils.getImageSize(image, Utils.ImageSizes.LARGE);
                    imageView.setImageBitmap(image);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Get the HTTP response code
                int httpResponseCode = ((StorageException) e).getHttpResultCode();

                // Show a snackbar on failure, not 404
                if (httpResponseCode != 404) {
                    Snackbar snackbarFail = Snackbar.make(findViewById(R.id.drinkDetails),"Failed to fetch image",Snackbar.LENGTH_SHORT);
                    snackbarFail.show();
                }
            }
        });
    }

    /**
     * Setup the edit button and add an onclick listener.
     */
    private void setupEditButton() {
        // Set the button
        editButton = findViewById(R.id.editButton);

        // Add the click listener
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIsEditing();
            }
        });
    }

    private void setIsEditing() {
        if (!isEdit) {
            // Set visibility for fields
            setTextFieldsVisibility(View.GONE);
            setEditFieldsVisibility(View.VISIBLE);

            setRatingBarVisibility(true);

            // Show the actionbar menu
            showMenu(true);

            // Prepare image view for change of image
            prepareImageChange(true);

            // Set is editing
            isEdit = true;
        } else {
            // Change the text fields to the new text
            combineFields();

            // Set visibility for fields
            setTextFieldsVisibility(View.VISIBLE);
            setEditFieldsVisibility(View.GONE);

            setRatingBarVisibility(false);

            // Hide the actionbar menu
            showMenu(false);

            // Disable changing of image in the imageview
            prepareImageChange(false);

            // Set is editing
            isEdit = false;
        }
    }

    /**
     * Initiate a camera intent. Setting a URI to watch for changes, which will allow
     * for fetching of the image from a temporary local file.
     */
    private void initiateCamera() {
        // Create a new camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if the activity resolves
        if ( cameraIntent.resolveActivity(getPackageManager()) != null ) {
            try {
                // Create a temporary file
                File imageFile;

                // Create the actual file
                imageFile = Utils.createTempImageFile("image",".jpg", this);
                // Run a delete on the file
                imageFile.delete();

                // Set the URI for the file
                imageURI = FileProvider.getUriForFile(this,getPackageName() + ".fileprovider",imageFile);

                // Add the URI to the extras for the camera intent
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);

                // Start the camera activity
                startActivityForResult(cameraIntent, Utils.IMAGE_CAPTURE);
            } catch (Exception e) {
                // Log a creation error
                Log.d(TAG, "Could not create image file");
                // Ask for storage permissions
                Permissions.askStorage(this);
            }
        }
    }

    /**
     * Set on activity results for different results returned from an activity.
     *
     * @param requestCode Request code used
     * @param resultCode Result code sent back
     * @param data The data sent back
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == Utils.IMAGE_CAPTURE && resultCode == Activity.RESULT_OK ) {
            hasImageChanged = true;
            setImage();
        }
    }

    private void setImage() {
        // Set a content resolver notifier
        this.getContentResolver().notifyChange(imageURI, null);
        ContentResolver contentResolver = this.getContentResolver();

        // Create a temporary image
        Bitmap tempImage;

        try {
            // Get the image from URI
            tempImage = android.provider.MediaStore.Images.Media.getBitmap(contentResolver, imageURI);
            // Get the correct size for the image
            tempImage = Utils.getImageSize(tempImage, Utils.ImageSizes.SMALL);

            if (tempImage != null) {
                // Set the image to the local Bitmap
                image = tempImage;
                // Change the image in the imageView
                imageView.setImageBitmap(image);
                // Set the scale type for the imageview
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

        } catch (Exception e) {
            // Log if an error occurred
            Log.d(TAG, "Failed to load image");
        }
    }

    /**
     * Prepare for the image change.
     *
     * Shows or hides the image hint text and sets a on image, or textview click listener
     * for changing the current image.
     *
     * @param imageChanging
     */
    private void prepareImageChange(Boolean imageChanging) {
        if (imageChanging) {
            txtImageHint.setVisibility(View.VISIBLE);
            txtImageHint.setOnClickListener(onImageCLick());
            imageView.setOnClickListener(onImageCLick());

        } else {
            txtImageHint.setVisibility(View.GONE);
            txtImageHint.setOnClickListener(null);
            imageView.setOnClickListener(null);
        }
    }

    /**
     * Combine the edit fields with the text fields
     */
    private void combineFields() {
        txtTitle.setText(txtTitleEdit.getText().toString());
        txtDescription.setText(txtDescriptionEdit.getText().toString());
        ratingBar.setRating(ratingBarEdit.getRating());
    }

    /**
     * Save the drink being edited or added.
     */
    private void saveDrink() {
        if(!hasChanged && !hasImageChanged) {
            return;
        }

        // Establish a new data class
        Location location = Locator.getLocation();

        String locationString = "";
        if (location != null) {
            locationString = String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());
        }

        // Set the location from the fetched location
        currentDrink.setLocation(locationString);

        // Set the drink information from the edit fields
        combineFields();

        // Set the drink information based on the text views
        currentDrink.setTitle(txtTitle.getText().toString());
        currentDrink.setDescription(txtDescription.getText().toString());
        currentDrink.setRating(Double.valueOf(ratingBar.getRating()));

        // Set the drink
        String key;
        if (currentDrink.getId() != null) {
            // Set drink being edited
            key = FirebaseData.setDrink(currentDrink, currentDrink.getId());
        } else {
            // Set drink being added
            key = FirebaseData.setDrink(currentDrink, null);
        }

        // If image is not empty, set the image
        if (image != null) {
            FirebaseData.setImage(key, image);
            //FirebaseData.setImage(key, imageURI);
        }

        if (isNew) {
            // Fake that the drink is now being edited
            isEdit = true;
            // Setup the edit button for the drink
            setupEditButton();
            // Show the edit button
            editButton.setVisibility(View.VISIBLE);
        }

        // Set to non editing mode
        setIsEditing();
    }
}
