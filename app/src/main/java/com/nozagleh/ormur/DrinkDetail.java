package com.nozagleh.ormur;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class DrinkDetail extends AppCompatActivity {

    // Boolean to set if new object is being added
    Boolean isNew = false;

    // Display fields
    ImageView imageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_detail);

        // Set the toolbar
        Toolbar toolbar = findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Start by initializing edit fields
        initEditFields();

        // Hide edit fields by default
        setEditFieldsVisibility(View.GONE);

        // Init the image view
        imageView = findViewById(R.id.imgDrink);

        // Init the text views
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtRating = findViewById(R.id.txtRating);

        // Set the text details
        setDetails(savedInstanceState);

        // Set the floating button
        setupEditButton();
    }

    private void setDetails(Bundle bundle) {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getBooleanExtra("addNew",false)) {
                setEditFieldsVisibility(View.VISIBLE);
            } else {
                // Set field information from intent if object
                // is an existing object
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

    private void initTextFields() {

    }

    private void setEditFieldsVisibility(int visibility) {
        for (int i = 0; i < editFields.size(); i++) {
            editFields.get(i).setVisibility(visibility);
        }
    }

    private void setFieldInformation(Intent intent) {
        txtTitle.setText(intent.getStringExtra("title"));
        txtDescription.setText(intent.getStringExtra("description"));
        txtRating.setText(String.valueOf(intent.getDoubleExtra("rating",0)));

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

            }
        });
    }

    private void setupEditButton() {
        editButton = findViewById(R.id.editButton);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditFieldsVisibility(View.VISIBLE);
            }
        });
    }
}
