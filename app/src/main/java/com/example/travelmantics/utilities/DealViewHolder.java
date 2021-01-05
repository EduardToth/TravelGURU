package com.example.travelmantics.utilities;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelmantics.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DealViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    private final TextView tvTitle;
    private final TextView tvDescription;
    private final TextView tvPrice;
    private final ImageView imageDeal;
    private final RatingBar ratingBar;
    protected List<TravelDeal> deals;

    protected DealViewHolder(View itemView, List<TravelDeal> deals) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        tvPrice = itemView.findViewById(R.id.tvPrice);
        imageDeal = itemView.findViewById(R.id.imageDeal);
        this.ratingBar = itemView.findViewById(R.id.row_rating_bar);
        this.deals = deals;
    }

    public void bind(TravelDeal deal) {
        tvTitle.setText(deal.getTitle());
        tvDescription.setText(deal.getDescription());
        tvPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());
        fillRatingBar(deal);
    }

    protected void fillRatingBar(TravelDeal deal) {

        AtomicBoolean thereAreRatings = new AtomicBoolean(false);
        FirebaseDatabase.getInstance()
                .getReference()
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        synchronized (this) {
                            if (snapshot.getKey().equals("ratings")) {
                                UtilityClass.convertToList(snapshot.getChildren())
                                        .parallelStream()
                                        .filter(dataSnapshot -> dataSnapshot.getKey().equals(deal.getId()))
                                        .findAny()
                                        .ifPresent(this::fillRating);

                            }
                        }
                    }

                    private void fillRating(DataSnapshot dataSnapshot) {
                        ratingBar.setVisibility(View.VISIBLE);
                        UtilityClass.convertToList(dataSnapshot.getChildren())
                                .parallelStream()
                                .map(DataSnapshot::getChildren)
                                .map(UtilityClass::convertToList)
                                .flatMap(List::stream)
                                .map(DataSnapshot::getValue)
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .filter(this::isNumber)
                                .map(Double::parseDouble)
                                .mapToDouble(d -> d)
                                .average()
                                .ifPresent(average -> ratingBar.setRating((float) average));
                    }

                    private List<DataSnapshot> getChildren(DataSnapshot dataSnapshot) {
                        return UtilityClass.convertToList(dataSnapshot.getChildren());
                    }

                    private boolean isNumber(String str) {
                        try {
                            Double.parseDouble(str);
                            return true;
                        } catch (NumberFormatException e) {
                            return false;
                        }
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
                });

        synchronized (this) {
            if (!thereAreRatings.get()) {
                ratingBar.setVisibility(View.INVISIBLE);
            }
        }
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