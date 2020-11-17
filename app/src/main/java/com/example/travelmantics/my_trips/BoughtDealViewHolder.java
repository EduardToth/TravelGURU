package com.example.travelmantics.my_trips;

import android.content.Intent;
import android.view.View;

import com.example.travelmantics.utilities.DealViewHolder;
import com.example.travelmantics.utilities.TravelDeal;

import java.util.List;

public class BoughtDealViewHolder extends DealViewHolder {

    private BoughtDealViewHolder(View itemView, List<TravelDeal> deals) {
        super(itemView, deals);
    }

    @Override
    public void onClick(View view)  {
        int position = getAdapterPosition();
        TravelDeal selectedDeal = deals.get(position);
        Intent intent = new Intent(view.getContext(), GalleryActivity.class);
        intent.putExtra("Deal", selectedDeal);
        view.getContext().startActivity(intent);
    }

    public static DealViewHolder getInstance(View itemView, List<TravelDeal> deals) {
        DealViewHolder dealViewHolder = new BoughtDealViewHolder(itemView, deals);
        itemView.setOnClickListener(dealViewHolder);

        return dealViewHolder;
    }
}
