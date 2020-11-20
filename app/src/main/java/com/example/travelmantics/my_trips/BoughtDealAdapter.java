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
import com.example.travelmantics.utilities.DealViewHolder;
import com.example.travelmantics.utilities.TravelDeal;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BoughtDealAdapter extends RecyclerView.Adapter<DealViewHolder> {
    private final ArrayList<TravelDeal> deals = new ArrayList<>();

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
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false);
        return BoughtDealViewHolder.getInstance(itemView, deals);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal deal = deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }
}
