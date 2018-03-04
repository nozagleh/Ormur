package com.nozagleh.ormur;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class DrinkDetail extends AppCompatActivity {

    ImageView imageView;
    TextView txtTitle;
    TextView txtDescription;
    TextView txtRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_detail);

        // Init the image view
        imageView = findViewById(R.id.imgDrink);

        // Init the text views
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtRating = findViewById(R.id.txtRating);

        // Set the text details
        setDetails(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setDetails(Bundle bundle) {
        Intent intent = getIntent();
        if (intent != null) {
            txtTitle.setText(intent.getStringExtra("title"));
            txtDescription.setText(intent.getStringExtra("description"));
            txtRating.setText(String.valueOf(intent.getDoubleExtra("rating",0)));

            FirebaseData.getImage(intent.getStringExtra("id"), new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    if (bytes != null) {
                        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(image);
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
}
