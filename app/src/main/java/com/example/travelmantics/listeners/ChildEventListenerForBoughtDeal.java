package com.example.travelmantics.listeners;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.travelmantics.my_trips.*;
import com.example.travelmantics.utilities.*;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/*
This class is intended for implementing the ChildEventListener interface for readability
It breaks the demeter's law and additional implementations should not be added to it
 */
public final class ChildEventListenerForBoughtDeal implements ChildEventListener {

    private final List<TravelDeal> deals;
    private final BoughtDealAdapter boughtDealAdapter;


    public ChildEventListenerForBoughtDeal(List<TravelDeal> deals, BoughtDealAdapter boughtDealAdapter) {
        this.deals = deals;
        this.boughtDealAdapter = boughtDealAdapter;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        if (snapshot.getValue() != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("traveldeals");
            final String id = snapshot.getValue().toString();

            ChildEventListener listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    TravelDeal td = snapshot.getValue(TravelDeal.class);
                    assert td != null;
                    Log.d("id", td.getId() == null? "null" : td.getId());
                    if (id.equals(td.getId())) {
                        deals.add(td);
                        boughtDealAdapter.notifyItemInserted(deals.size() - 1);
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
            };
            ref.addChildEventListener(listener);
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
