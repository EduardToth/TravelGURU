package com.example.travelmantics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.travelmantics.utilities.TravelDeal;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ToViewDealAdapter extends RecyclerView.Adapter<ToViewDealViewHolder>{

    private final List<TravelDeal> deals = new ArrayList<>();
    private final ViewListActivity viewListActivity;

    public ToViewDealAdapter(ViewListActivity viewListActivity) {
        this.viewListActivity = viewListActivity;
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("traveldeals");

        ChildEventListener mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                TravelDeal td = snapshot.getValue(TravelDeal.class);
                assert td != null;
                td.setId(snapshot.getKey());
                deals.add(td);
                notifyItemInserted(deals.size() - 1);
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
        mDatabaseReference.addChildEventListener(mChildListener);
    }

    @NonNull
    @Override
    public ToViewDealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false);

        return ToViewDealViewHolder.getInstance(itemView, deals, viewListActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull ToViewDealViewHolder holder, int position) {
        TravelDeal deal = deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }
}
