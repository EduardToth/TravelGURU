package com.example.travelmantics.comments;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelmantics.R;
import com.squareup.picasso.Picasso;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private final ImageView profilePicture;
    private final TextView review;
    private final TextView name;
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        profilePicture = itemView.findViewById(R.id.client_picture);
        review = itemView.findViewById(R.id.review);
        name = itemView.findViewById(R.id.client_name);
    }

    public void bind(Comment comment) {
        showImage(comment.getUser().getProfile_picture_url());
        review.setText(comment.getReview());
        name.setText(comment.getUser().getUser_name());
    }

    private void showImage(String url) {
        if(url != null) {
            if (!url.isEmpty()) {
                Picasso.with(review.getContext())
                        .load(url)
                        .resize(200, 200)
                        .centerCrop()
                        .into(profilePicture);
            } else {
                Picasso.with(review.getContext())
                        .load(R.drawable.empty_profile)
                        .resize(200, 200)
                        .centerCrop()
                        .into(profilePicture);
            }
        }
    }
}
