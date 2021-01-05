package com.example.travelmantics.listeners;

import androidx.annotation.NonNull;

import com.example.travelmantics.comments.User;
import com.example.travelmantics.utilities.AuthUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Optional;

public class UserStorageHandlerListener implements ValueEventListener {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        boolean userIsPresentInDatabase = isUserPresent(snapshot);

        if (!userIsPresentInDatabase) {
            Optional<String> email = getEmail();
            email.map(this::getUserName)
                    .ifPresent(this::saveUserInDatabase);

        }
    }

    private void saveUserInDatabase(String userName) {
        User user = new User("", userName);
        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .child(AuthUtil.getCurrentUserUid())
                .setValue(user);
    }

    private boolean isUserPresent(@NonNull DataSnapshot snapshot) {
        boolean isUserPresent = false;
        for (DataSnapshot child : snapshot.getChildren()) {
            if (Objects.equals(child.getKey(), AuthUtil.getCurrentUserUid())) {
                isUserPresent = true;
                break;
            }
        }

        return isUserPresent;
    }

    private String getUserName(String email) {
        int index = email.indexOf("@");

        return email.substring(0, index);
    }

    private Optional<String> getEmail() {
        String email = Objects.requireNonNull(FirebaseAuth.getInstance()
                .getCurrentUser())
                .getEmail();

        return Optional.ofNullable(email);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }


}
