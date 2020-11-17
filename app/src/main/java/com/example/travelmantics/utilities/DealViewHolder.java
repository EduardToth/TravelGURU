package com.example.travelmantics.utilities;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.travelmantics.R;
import com.example.travelmantics.utilities.TravelDeal;
import com.squareup.picasso.Picasso;

import java.util.List;

public abstract  class DealViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    private final TextView tvTitle;
    private final TextView tvDescription;
    private final TextView tvPrice;
    private final ImageView imageDeal;
    protected List<TravelDeal> deals;

    protected DealViewHolder(View itemView, List<TravelDeal> deals) {
        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
        tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        imageDeal = (ImageView) itemView.findViewById(R.id.imageDeal);
        this.deals = deals;
    }

    public void bind(TravelDeal deal) {
        tvTitle.setText(deal.getTitle());
        tvDescription.setText(deal.getDescription());
        tvPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());
    }

    @Override
    public abstract void onClick(View view);

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            Picasso.with(imageDeal.getContext())
                    .load(url)
                    .resize(160, 160)
                    .centerCrop()
                    .into(imageDeal);
        }
    }
}