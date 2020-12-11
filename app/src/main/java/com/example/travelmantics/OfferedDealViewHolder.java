package com.example.travelmantics;

import android.content.Intent;
import android.view.View;

import com.example.travelmantics.utilities.DealViewHolder;
import com.example.travelmantics.utilities.TravelDeal;

import java.util.List;

public class OfferedDealViewHolder extends DealViewHolder {

    private OfferedDealViewHolder(View itemView, List<TravelDeal> deals) {
        super(itemView, deals);
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        TravelDeal selectedDeal = deals.get(position);
        Intent intent = new Intent(view.getContext(), DealActivity.class);
        intent.putExtra("Deal", selectedDeal);
        view.getContext().startActivity(intent);
    }

    public  static DealViewHolder getInstance(View itemView, List<TravelDeal> deals) {
        DealViewHolder dealViewHolder = new OfferedDealViewHolder(itemView, deals);
        itemView.setOnClickListener(dealViewHolder);

        return dealViewHolder;
    }
}
