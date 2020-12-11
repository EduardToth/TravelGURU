package com.example.travelmantics.comments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.travelmantics.R;
import com.example.travelmantics.utilities.AuthUtil;
import com.example.travelmantics.utilities.TravelDeal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CommentAdderActivity extends AppCompatActivity {

    private EditText editText;
    private TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_adder);

        editText = findViewById(R.id.comment);
        deal =  (TravelDeal) getIntent().getExtras().get("deal");
        ImageView imageView = findViewById(R.id.dealPicture);
        if(deal.getImageUrl() != null) {
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
        if(!review.isEmpty()) {
            saveReview(review);
        } else {
            Toast.makeText(this, "Cannot save an empty review", Toast.LENGTH_LONG).show();
        }
    }

    private void saveReview(String review) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .child(AuthUtil.getCurrentUserUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Comment comment = new Comment(review, user);
                        saveComment(comment);

                    }

                    private void saveComment(Comment comment) {
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("comments")
                                .child(deal.getId())
                                .push()
                                .setValue(comment)
                                .addOnSuccessListener(CommentAdderActivity.this::finishSuccessfully)
                                .addOnFailureListener(CommentAdderActivity.this::makeFailureToast);
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