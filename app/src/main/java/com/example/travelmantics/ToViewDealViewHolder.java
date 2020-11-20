package com.example.travelmantics;

import android.view.View;

import com.example.travelmantics.utilities.DealViewHolder;
import com.example.travelmantics.utilities.TravelDeal;

import java.util.List;

public class ToViewDealViewHolder extends DealViewHolder {

    protected ToViewDealViewHolder(View itemView, List<TravelDeal> deals) {
        super(itemView, deals);
    }

    @Override
    public void onClick(View view) {
    }

    public static ToViewDealViewHolder getInstance(View itemView, List<TravelDeal> deals) {
        return new ToViewDealViewHolder(itemView, deals);
    }
}
