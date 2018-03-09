package com.nozagleh.ormur;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nozagleh.ormur.Models.Drink;

import java.io.ByteArrayOutputStream;

/**
 * Created by arnarfreyr on 03/03/2018.
 */

public class FirebaseData {
    private static String CLASS_TAG = "FirebaseData";

    private static FirebaseAuth firebaseAuth;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference("drinks");

    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static StorageReference storageReference = storage.getReference();

    private static Bitmap image;

    public static String setDrink(Drink drink, String id) {
        String key = null;
        if (id != null) {
            DatabaseReference childReference = reference.child(getUser().getUid()).child(id);
            key = childReference.getKey();
            childReference.setValue(drink);
        } else {
            final DatabaseReference childReference = reference.child(getUser().getUid());
            key = childReference.push().getKey();
            childReference.child(key).setValue(drink).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(CLASS_TAG, childReference.push().getKey());
                }
            });
        }

        return key;
    }

    public static void removeDrink(String id) {
        reference.child(getUser().getUid()).child(id).removeValue();
        removeImage(id);
    }

    public static void getDrinks(ValueEventListener listener) {
        DatabaseReference childReference = reference.child(getUser().getUid());
        childReference.addListenerForSingleValueEvent(listener);
    }

    public static void getImage(final String id, OnSuccessListener<byte[]> listener, OnFailureListener failListener) {
        StorageReference imageReference = storageReference.child("images/" + getUser().getUid() + "/" + id + ".jpg");
        imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(listener).addOnFailureListener(failListener);
    }

    public static void setImage(String id, Bitmap bitmap) {
        StorageReference imageReference = storageReference.child("images/" + getUser().getUid() + "/" + id + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    public static void setImage(String id, Uri imageUri) {
        StorageReference imageReference = storageReference.child("images/" + getUser().getUid() + "/" + id + ".jpg");

        UploadTask uploadTask = imageReference.putFile(imageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    /**
     * Remove a image from the Firabase storage. Based on the id of the object being removed.
     *
     * @param id ID of the object
     */
    public static void removeImage(String id) {
        StorageReference imageReference = storageReference.child("images/" + getUser().getUid() + "/" + id + ".jpg");
        imageReference.delete();
    }

    public static FirebaseUser getUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser();
    }
}
