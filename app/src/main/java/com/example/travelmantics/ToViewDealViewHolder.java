package com.example.travelmantics;

import android.view.View;
import android.widget.Toast;

import com.example.travelmantics.utilities.DealViewHolder;
import com.example.travelmantics.utilities.TravelDeal;

import java.util.List;

public class ToViewDealViewHolder extends DealViewHolder {

    private final ViewListActivity viewListActivity;

    protected ToViewDealViewHolder(View itemView, List<TravelDeal> deals,
                                   ViewListActivity viewListActivity) {
        super(itemView, deals);
        this.viewListActivity = viewListActivity;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(viewListActivity, "Please login first", Toast.LENGTH_LONG)
                .show();
    }

    public static ToViewDealViewHolder getInstance(View itemView, List<TravelDeal> deals,
                                                   ViewListActivity viewListActivity) {
        return new ToViewDealViewHolder(itemView, deals, viewListActivity);
    }
}
