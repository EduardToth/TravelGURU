package com.example.travelmantics.my_trips;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelmantics.utilities.AuthUtil;
import com.example.travelmantics.R;
import com.example.travelmantics.listeners.ChildEventListenerForBoughtDeal;
import com.example.travelmantics.utilities.TravelDeal;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Vector;

public class BoughtDealAdapter extends RecyclerView.Adapter<BoughtDealViewHolder> {
    private final Vector<TravelDeal> deals = new Vector<>();

    public BoughtDealAdapter() {
        createChildEventListener();
    }

    private void createChildEventListener() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("bought_deals")
                .child(AuthUtil.getCurrentUserUid());

        ChildEventListener listener = new ChildEventListenerForBoughtDeal(deals, this);
        ref.addChildEventListener(listener);
    }

    @NonNull
    @Override
    public BoughtDealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.my_trips_row, parent, false);
        return new BoughtDealViewHolder(itemView, deals);
    }

    @Override
    public void onBindViewHolder(@NonNull BoughtDealViewHolder holder, int position) {
        TravelDeal deal = deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }
}
