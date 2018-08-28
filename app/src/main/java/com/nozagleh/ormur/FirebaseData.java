package com.nozagleh.ormur;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

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
 * A general class for holding all the functions that have
 * to do with the Firebase storage used for the application.
 *
 * All functions are static, as no class should be needed to instanced
 * for uploading, downloading, or removing drinks or media on the cloud.
 *
 * Created by arnarfreyr on 03/03/2018.
 */

public class FirebaseData {
    private static String CLASS_TAG = "FirebaseData";

    private static FirebaseAuth firebaseAuth;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference("drinks");

    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static StorageReference storageReference = storage.getReference();

    private static Boolean isListening = false;
    private static ValueEventListener activeListener;

    /**
     * Upload a drink to the firebase storage. Uses data from a drink object.
     *
     * @param drink The drink which to be uploaded
     * @param id The id of the drink
     * @return ID key
     */
    public static String setDrink(Drink drink, String id) {
        if (!NetworkChecker.hasNetwork(Statics.appContext)) {
            Toast toast = Toast.makeText(Statics.appContext, Statics.appContext.getText(R.string.no_internet), Toast.LENGTH_SHORT);
            toast.show();

            Log.d(CLASS_TAG, "No internet connection");

            drink.setOffline(true);
            drink.setSynced(false);

            // TODO cache drinks for syncing later, replace offline drink with just drink (include booleans for checking)
            Statics.localDb.offlineDrinkDao().insertSingle(drink);
        }

        String key;
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

    /**
     * Remove a single drink from the firebase storage.
     *
     * @param id Id of the drink to remove
     */
    public static void removeDrink(String id) {
        // Delete the drink via the default reference
        reference.child(getUser().getUid()).child(id).removeValue();
        // Call for a removal of the drink's image
        removeImage(id);
    }

    public static boolean listenForDrinkChanges(ValueEventListener listener) {
        activeListener = listener;
        isListening = true;
        DatabaseReference childReference = reference.child(getUser().getUid());
        childReference.addValueEventListener(listener);

        return isListening;
    }

    public static boolean stopListeningForDrinkChanges() {
        isListening = false;
        DatabaseReference child = reference.child(getUser().getUid());
        child.removeEventListener(activeListener);

        return isListening;
    }

    /**
     * Return if the application is listening to its storage.
     *
     * @return boolean is listening
     */
    public static Boolean isListening() {
        return isListening;
    }

    /**
     * Get an image for a drink from the firebase storage.
     *
     * @param id The id of the drink
     * @param listener The return listener, called when fetching is finished
     * @param failListener The return failure listener, called if fetch is failed
     */
    public static void getImage(final String id, final int pos, OnSuccessListener<byte[]> listener, OnFailureListener failListener) {
        StorageReference imageReference = storageReference.child("images/" + getUser().getUid() + "/" + id + ".jpg");
        imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(listener).addOnFailureListener(failListener);
    }

    /**
     * Set an image of a drink via its key id.
     * Calls the firebase storage and uploads an image for a drink.
     *
     * @param id The id of the drink
     * @param bitmap The image to be uploaded
     */
    public static void setImage(String id, Bitmap bitmap) {
        StorageReference imageReference = storageReference.child("images/" + getUser().getUid() + "/" + id + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
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
                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
            }
        });
    }

    /**
     * Remove a image from the Firabase storage. Based on the id of the object being removed.
     *
     * @param id ID of the object
     */
    private static void removeImage(String id) {
        StorageReference imageReference = storageReference.child("images/" + getUser().getUid() + "/" + id + ".jpg");
        imageReference.delete();
    }

    /**
     * Get the current Firebase user of the application.
     *
     * @return FirebaseUser The current firebase user
     */
    private static FirebaseUser getUser() {
        firebaseAuth = FirebaseAuth.getInstance();

        return firebaseAuth.getCurrentUser();
    }
}
