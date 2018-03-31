package com.nozagleh.ormur;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = LoginActivity.class.getName();

    // Create a static number callback for the Google sign in
    private static int RC_SIGN_IN = 12;

    // Define a Google sign in client
    private GoogleSignInClient gsc;
    // Define a Google sign in button
    private SignInButton googleSignIn;
    // Define the Firebase authenticator
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get a Fiebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Add the Google sign in button to the view
        googleSignIn = findViewById(R.id.sign_in_button);
        // Set the size of the Google sign in button
        googleSignIn.setSize(SignInButton.SIZE_WIDE);
        // Set an on click listener for the Google sign in button
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = gsc.getSignInIntent();
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });

        // Get the google sign in option
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Get a Google sign in client
        gsc = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get the current user firebase user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Check if there is a user signed in or not
        isSignedIn(currentUser, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthentication(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    /**
     * Authenticate the Google user on the Firebase backend.
     *
     * @param account Google account
     */
    private void firebaseAuthentication(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()) {
                            // Get the current user
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            // Check if there is any user signed in
                            isSignedIn(user, false);
                        } else {
                            // Send null to the sign in checker on failure
                            isSignedIn(null, false);
                        }
                    }
                });
    }

    /**
     * Check if a user is signed in and authenicated.
     *
     * @param user Firebase user
     */
    private void isSignedIn(FirebaseUser user, boolean initialTry) {
        // Only start the main activity if the user was authenticated
        if (user != null) {
            // Create a new main app class intent
            Intent intent = new Intent(this, App.class);
            // Start the activity from the intent
            startActivity(intent);
        } else {
            if (!initialTry) {
                // Show a snackbar on failure
                Utils.showSnackBar(findViewById(R.id.loginContent), getString(R.string.login_error));
            }
        }
    }
}
