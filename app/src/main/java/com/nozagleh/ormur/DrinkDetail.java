package com.nozagleh.ormur;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DrinkDetail extends AppCompatActivity {

    // Boolean to set if new object is being added or edited
    Boolean isNew = false, isEdit = false;

    // Actionbar menu
    Menu saveDeleteMenu;

    // Toolbar
    Toolbar toolbar;

    // Display fields
    ImageView imageView;

    // Text fields list
    List<TextView> textFields;

    // Text fields
    TextView txtTitle;
    TextView txtDescription;
    TextView txtRating;

    // Edit fields list
    List<EditText> editFields;

    // Edit fields
    EditText txtTitleEdit;
    EditText txtDescriptionEdit;
    EditText txtRatingEdit;

    // Floating edit action button
    FloatingActionButton editButton;

    /**
     * OnCreate activity.
     *
     * @param savedInstanceState Bundle saved bundle data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_detail);

        // Set the toolbar
        setupToolbar();

        // Init the image view
        imageView = findViewById(R.id.imgDrink);

        // Start by initializing edit fields
        initEditFields();

        // Init the text views
        initTextFields();

        // Set the fields visibility
        setFieldsVisibility();

        // Set the text details
        setDetails();

        // Set the floating button
        setupEditButton();
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
        showMenu(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_bar_save) {
            // TODO add save function
            return true;
        } else if (item.getItemId() == R.id.app_bar_delete) {
            // TODO add trash function
            return true;
        }

        return false;
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
            } else {
                // Set field information if not new
                setFieldInformation(intent);
            }
        }
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
        txtRatingEdit = findViewById(R.id.txtRatingEdit);

        // Add the fields to the list
        editFields.add(txtTitleEdit);
        editFields.add(txtDescriptionEdit);
        editFields.add(txtRatingEdit);
    }

    /**
     * Initiate the text fields.
     *
     * Add the text fields to a list for easy iteration access
     */
    private void initTextFields() {
        // Set the list
        textFields = new ArrayList<>();

        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtRating = findViewById(R.id.txtRating);

        textFields.add(txtTitle);
        textFields.add(txtDescription);
        textFields.add(txtRating);
    }

    /**
     * Set the visibility of the text and edit fields,
     * based on the status of isNew and isEdit.
     */
    private void setFieldsVisibility() {
        if (isNew || isEdit) {
            setTextFieldsVisibility(View.GONE);
            setEditFieldsVisibility(View.VISIBLE);

            if (isNew) {
                editButton.setVisibility(View.GONE);
            }
        } else {
            setEditFieldsVisibility(View.GONE);
            setTextFieldsVisibility(View.VISIBLE);
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

    /**
     * Set all field values based on the intent.
     *
     * @param intent Intent
     */
    private void setFieldValues(Intent intent) {
        // Set text fields
        txtTitle.setText(intent.getStringExtra("title"));
        txtDescription.setText(intent.getStringExtra("description"));
        txtRating.setText(String.valueOf(intent.getDoubleExtra("rating",0)));

        // Set edit fields
        txtTitleEdit.setText(intent.getStringExtra("title"));
        txtDescriptionEdit.setText(intent.getStringExtra("description"));
        txtRatingEdit.setText(String.valueOf(intent.getDoubleExtra("rating",0)));
    }

    /**
     * Set all field information. Text, editfields, and image.
     *
     * @param intent Intent
     */
    private void setFieldInformation(Intent intent) {
        // Set all fields values
        setFieldValues(intent);

        // Get the image from the firebase storage
        FirebaseData.getImage(intent.getStringExtra("id"), new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (bytes != null) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image = Utils.getImageSize(image, Utils.ImageSizes.LARGE);
                    imageView.setImageBitmap(image);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Show a snackbar on failure
                Snackbar snackbarFail = Snackbar.make(findViewById(R.id.drinkDetails),"Failed to fetch image",Snackbar.LENGTH_SHORT);
                snackbarFail.show();
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
                if (!isEdit) {
                    // Set visibility for fields
                    setTextFieldsVisibility(View.GONE);
                    setEditFieldsVisibility(View.VISIBLE);
                    // Show the actionbar menu
                    showMenu(true);

                    // Set is editing
                    isEdit = true;
                } else {
                    // Set visibility for fields
                    setTextFieldsVisibility(View.VISIBLE);
                    setEditFieldsVisibility(View.GONE);
                    // Hide the actionbar menu
                    showMenu(false);

                    // Set is editing
                    isEdit = false;
                }
            }
        });
    }
}
