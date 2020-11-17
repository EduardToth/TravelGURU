package com.example.travelmantics.my_trips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.travelmantics.FirebaseUtil;
import com.example.travelmantics.R;

public class MyTrips extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        RecyclerView rvDeals = findViewById(R.id.rv_deals2);
        fillRecyclerView(rvDeals);
    }

    private void fillRecyclerView(RecyclerView rvDeals) {
        final BoughtDealAdapter boughtDealAdapter = new BoughtDealAdapter();
        rvDeals.setAdapter(boughtDealAdapter);
        LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvDeals.setLayoutManager(dealsLayoutManager);
        FirebaseUtil.attachListener();
    }
}