package com.example.travelmantics.utilities;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.travelmantics.ListActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class AuthUtil {
    private static String currentUserUid;
    private static final int RC_SIGN_IN = 123;
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static ListActivity caller;

    private AuthUtil() {
    }

    public static void openFbReference(final ListActivity _caller) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        caller = _caller;

        mAuthListener = AuthUtil::onAuthStateChanged;
    }

    private static void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            if (firebaseAuth.getCurrentUser() == null) {
                AuthUtil.signIn();
            }
            currentUserUid = firebaseAuth.getUid();
            Toast.makeText(caller.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();

    }

    private static void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN
        );
    }

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    public static String getCurrentUserUid() {
        return currentUserUid;
    }

}
