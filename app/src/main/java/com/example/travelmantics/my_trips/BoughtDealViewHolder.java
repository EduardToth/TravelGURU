package com.example.travelmantics.my_trips;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelmantics.R;
import com.example.travelmantics.utilities.DealViewHolder;
import com.example.travelmantics.utilities.TravelDeal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BoughtDealViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    TextView bought_deal_title;
    ImageView bought_deal_image;
    List<TravelDeal> bought_deals;

    public BoughtDealViewHolder(@NonNull View itemView, List<TravelDeal> bought_deals) {
        super(itemView);
        this.bought_deal_title = itemView.findViewById(R.id.bought_deal_title);
        this.bought_deal_image = itemView.findViewById(R.id.bought_deal_image);
        this.bought_deals = bought_deals;
        itemView.setOnClickListener(this::onClick);
    }

    public void bind(TravelDeal deal) {
        bought_deal_title.setText(deal.getTitle());
        // tvDescription.setText(deal.getDescription());
        showImage(deal.getImageUrl());
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            Picasso.with(bought_deal_image.getContext())
                    .load(url)
                    .resize(140, 140)
                    .centerCrop()
                    .into(bought_deal_image);
        }
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        TravelDeal selectedDeal = bought_deals.get(position);
        Intent intent = new Intent(view.getContext(), GalleryActivity.class);
        intent.putExtra("Deal", selectedDeal);
        view.getContext().startActivity(intent);
    }
}