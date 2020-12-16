package com.example.travelmantics.comments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelmantics.R;
import com.example.travelmantics.utilities.AuthUtil;
import com.example.travelmantics.utilities.TravelDeal;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class CommentAdderActivity extends AppCompatActivity {

    private EditText editText;
    private TravelDeal deal;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_adder);
        ratingBar = findViewById(R.id.ratingBar);

        editText = findViewById(R.id.comment);
        deal = (TravelDeal) getIntent().getExtras().get("deal");
        ImageView imageView = findViewById(R.id.dealPicture);
        if (deal.getImageUrl() != null) {
            showImage(deal.getImageUrl(), imageView);
        }
    }

    private void showImage(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.comment_adder_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean itemSelectedSuccessfully = true;

        if (item.getItemId() == R.id.comment_save) {
            saveReview();
        } else {
            itemSelectedSuccessfully = super.onOptionsItemSelected(item);
        }

        return itemSelectedSuccessfully;
    }

    private void saveReview() {
        String review = editText.getText().toString();
        if (!review.isEmpty()) {
            saveReview(review);
        } else {
            Toast.makeText(this, "Cannot save an empty review", Toast.LENGTH_LONG).show();
        }
    }

    private void saveReview(String review) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (Objects.equals(snapshot.getKey(), AuthUtil.getCurrentUserUid())) {
                            Pair<DatabaseReference, Float> ratingLocalData = saveRating();
                            Pair<DatabaseReference, String> reviewLocalData = saveReview(review);

                            commitRating(ratingLocalData)
                                    .addOnSuccessListener(aVoid -> {
                                        commitComment(reviewLocalData)
                                                .addOnSuccessListener(CommentAdderActivity.this::finishSuccessfully)
                                                .addOnFailureListener(CommentAdderActivity.this::makeFailureToast);
                                    })
                                    .addOnFailureListener(CommentAdderActivity.this::makeFailureToast);
                        }
                    }

                    private Pair<DatabaseReference, String> saveReview(String review) {
                        DatabaseReference reference = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("comments")
                                .child(deal.getId())
                                .child(AuthUtil.getCurrentUserUid());

                        return Pair.create(reference, review);
                    }

                    private Task<Void> commitRating(Pair<DatabaseReference, Float> ratingLocalData) {
                        DatabaseReference ratingReference = ratingLocalData.first;
                        float rating = ratingLocalData.second;

                        return ratingReference.push().setValue(Float.toString(rating));
                    }

                    private Task<Void> commitComment(Pair<DatabaseReference, String> reviewLocalData) {
                        DatabaseReference reviewReference = reviewLocalData.first;
                        String review = reviewLocalData.second;

                        return reviewReference.push().setValue(review);
                    }

                    private Pair<DatabaseReference, Float> saveRating() {
                        float rating = ratingBar.getRating();

                        DatabaseReference reference = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("ratings")
                                .child(deal.getId())
                                .child(AuthUtil.getCurrentUserUid());

                        return Pair.create(reference, rating);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String
                            previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String
                            previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void makeFailureToast(Exception e) {
        Toast.makeText(this, "Couldn't save the comment", Toast.LENGTH_LONG).show();
    }

    private void finishSuccessfully(Void aVoid) {

        Toast.makeText(this, "Comment saved", Toast.LENGTH_LONG).show();
        finish();

    }
}