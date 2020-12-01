package com.example.travelmantics.my_trips;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.travelmantics.R;
import com.example.travelmantics.utilities.TravelDeal;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class FullImageActivity extends AppCompatActivity {

    private String url;
    private String travelDealId;
    private String uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        Intent intent = getIntent();
        uri = intent.getExtras().getString("uri");
        url = intent.getExtras().getString("url");
        travelDealId  = intent.getExtras().getString("travelDealId");
        ImageView imageView = findViewById(R.id.imageView5);

        showImage(imageView);
    }

    private void showImage(ImageView imageView) {
        int  width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        Picasso.with(this)
                .load(uri)
                .resize(width, height*2/3)
                .centerCrop()
                .into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.full_image_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_picture) {

            FirebaseStorage.getInstance()
                    .getReference(url)
                    .delete()
                    .addOnSuccessListener(this::goToGallery);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToGallery(Void aVoid) {
        Intent intent = new Intent(this, GalleryActivity.class);
        FirebaseDatabase.getInstance()
                .getReference()
                .child("traveldeals")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot.getKey().equals(travelDealId)) {
                            TravelDeal td = snapshot.getValue(TravelDeal.class);
                            intent.putExtra("Deal", td);
                            startActivity(intent);
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
    }
}