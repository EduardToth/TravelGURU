package com.example.travelmantics;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FirebaseUtil {
    private static String currentUserUid;
    private static final int RC_SIGN_IN = 123;
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static ListActivity caller;
    private static boolean isAdmin;

    private FirebaseUtil() {

    }

    public static void openFbReference(final ListActivity _caller) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        caller = _caller;

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    FirebaseUtil.signIn();
                } else {
                    currentUserUid = firebaseAuth.getUid();
                    String userId = firebaseAuth.getUid();
                    checkAdmin(userId);
                }
                Toast.makeText(caller.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
            }
        };
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

    private static void checkAdmin(String uid) {
        FirebaseUtil.isAdmin = false;
        AtomicBoolean enteredListener = new AtomicBoolean(false);
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("administrators")
                .child(uid);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
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

    public static boolean isCurrentUserAdmin() {
        return isAdmin;
    }
}
