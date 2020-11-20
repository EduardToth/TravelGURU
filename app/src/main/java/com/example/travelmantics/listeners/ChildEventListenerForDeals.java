package com.example.travelmantics.listeners;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.travelmantics.utilities.TravelDeal;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ChildEventListenerForDeals implements ChildEventListener {

    private final DatabaseReference ref;
    private final TravelDeal finalDeal;
    private final AtomicInteger nr;

    public ChildEventListenerForDeals(DatabaseReference ref, TravelDeal deal, AtomicInteger nr) {
        this.ref = ref;
        this.finalDeal = deal;
        this.nr = nr;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        if (finalDeal.getId().equals(Objects.requireNonNull(snapshot.getValue()).toString())) {
            if (nr.get() == 0) {
                ref.child(Objects.requireNonNull(snapshot.getKey()))
                        .removeValue();
            } else {
                nr.set(0);
            }
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}

