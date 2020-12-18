package com.example.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelmantics.comments.CommentAdderActivity;
import com.example.travelmantics.comments.CommentListActivity;
import com.example.travelmantics.listeners.ChildEventListenerForDeals;
import com.example.travelmantics.utilities.AuthUtil;
import com.example.travelmantics.utilities.TravelDeal;
import com.example.travelmantics.utilities.UtilityClass;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DealActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private EditText txtTitle;
    private EditText txtPrice;
    private EditText txtDescription;
    private TravelDeal deal;
    private ImageView imageView;
    private static final int PICTURE_RESULT = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);
        imageView = findViewById(R.id.imageView);
        Button btnImage = findViewById(R.id.btnImage);

        setupPage(btnImage);
    }

    private void setupPage(Button btnImage) {

        deal = getDeal();
        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("traveldeals");

        setupUI(deal);


        setupForUser(deal, btnImage);
        FirebaseDatabase.getInstance()
                .getReference()
                .child("administrators")
                .child(AuthUtil.getCurrentUserUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            setupForAdmin(btnImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setupUI(TravelDeal deal) {
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());

        showImage(deal.getImageUrl());
    }

    private TravelDeal getDeal() {
        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");

        if (deal == null) {
            deal = new TravelDeal();
        }

        return deal;
    }

    private void setupForAdmin(Button btnImage) {
        btnImage.setText(R.string.add_picture2);
        btnImage.setOnClickListener(view
                -> UtilityClass.startIntentForPicture(this, PICTURE_RESULT));
    }

    private void setupForUser(TravelDeal deal, Button btnImage) {
        AtomicBoolean buttonInvalidated = new AtomicBoolean(false);
        final Object monitor = new Object();
        FirebaseDatabase.getInstance()
                .getReference()
                .child("bought_deals")
                .child(AuthUtil.getCurrentUserUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (deal.getId().equals(snapshot.getValue())) {
                            synchronized (this) {
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("comments")
                                        .child(deal.getId())
                                        .addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                               synchronized (monitor) {
                                                   boolean userAddedComment = Objects.equals(snapshot.getKey(), AuthUtil.getCurrentUserUid());
                                                   buttonInvalidated.set(true);
                                                   btnImage.setText(R.string.add_review);

                                                   if (!userAddedComment) {
                                                       btnImage.setOnClickListener(DealActivity.this::goToCommentActivity);
                                                   } else {
                                                       btnImage.setOnClickListener(view -> Toast.makeText(DealActivity.this,
                                                               "You already added a review",
                                                               Toast.LENGTH_LONG).show());
                                                   }
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
                            synchronized (monitor) {
                                if (!buttonInvalidated.get()) {
                                    btnImage.setText(R.string.add_review);
                                    btnImage.setOnClickListener(DealActivity.this::goToCommentActivity);
                                }
                            }
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
            if (!buttonInvalidated.get()) {
                btnImage.setText(R.string.buy);
                final TravelDeal finalDeal = deal;
                final AtomicInteger nr = new AtomicInteger(0);

                btnImage.setOnClickListener(view -> react(finalDeal, nr));
            }
        }
    }

    private void react(TravelDeal finalDeal, AtomicInteger nr) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("bought_deals")
                .child(AuthUtil.getCurrentUserUid());

        ref.addChildEventListener(new ChildEventListenerForDeals(ref, finalDeal, nr));
        nr.incrementAndGet();
        ref.push().setValue(finalDeal.getId())
                .addOnSuccessListener(this::reactToOnSuccessfullyBoughDeal);
    }

    private void reactToOnSuccessfullyBoughDeal(Void aVoid) {
        backToList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            final String fileName = UtilityClass.getPictureFileName(this, data.getData());
            assert imageUri != null;
            final File file = new File(fileName);

            StorageReference ref = FirebaseStorage.getInstance()
                    .getReference()
                    .child("deals_pictures")
                    .child(file.getName());

            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storeImage(file, fileName));
        }
    }

    private void storeImage(File file, String fileName) {
        FirebaseStorage.getInstance()
                .getReference()
                .child("deals_pictures")
                .child(file.getName()).getDownloadUrl()
                .addOnSuccessListener(uri -> makeChanges(uri, fileName));
    }

    private void makeChanges(Uri uri, String fileName) {
        if (deal != null) {
            setImageDetails(deal, uri, fileName);
        }
        showImage(uri.toString());
    }

    private void setImageDetails(TravelDeal deal, Uri uri, String fileName) {
        deal.setImageUrl(uri.toString());
        deal.setImageName(fileName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("administrators")
                .child(AuthUtil.getCurrentUserUid());

        setupMenu(menu, false);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    setupMenu(menu, true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return true;
    }

    private void setupMenu(Menu menu, boolean isAdmin) {
        menu.findItem(R.id.delete_menu).setVisible(isAdmin);
        menu.findItem(R.id.save_menu).setVisible(isAdmin);
        enableEditTexts(isAdmin);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean itemSelectedSuccessfully = true;

        if (item.getItemId() == R.id.save_menu) {
            handleSaveDeal();
        } else if (item.getItemId() == R.id.delete_menu) {
            handleDeleteDeal();
        } else if (item.getItemId() == R.id.see_comments) {
            seeComments();
        } else {
            itemSelectedSuccessfully = super.onOptionsItemSelected(item);
        }

        return itemSelectedSuccessfully;
    }

    private void seeComments() {
        Intent intent = new Intent(this, CommentListActivity.class);
        intent.putExtra("deal", deal);
        startActivity(intent);
    }

    private void handleDeleteDeal() {
        deleteDeal();
        Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
        backToList();
    }

    private void handleSaveDeal() {
        saveDeal();
        Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
        clean();
        backToList();
    }

    private void clean() {
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.setText("");
        txtTitle.requestFocus();
    }

    private void saveDeal() {
        if (deal != null) {
            deal.setTitle(txtTitle.getText().toString());
            deal.setDescription(txtDescription.getText().toString());
            deal.setPrice(txtPrice.getText().toString());
            storeInDatabase();
        }
    }

    private void storeInDatabase() {
        if (deal.getId() == null) {
            mDatabaseReference.push()
                    .setValue(deal)
                    .addOnSuccessListener(this::setupAllIds);
        } else {
            mDatabaseReference.child(deal.getId())
                    .setValue(deal);
        }
    }

    private void setupAllIds(Void avoid) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("traveldeals")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        TravelDeal td = snapshot.getValue(TravelDeal.class);
                        if (td != null && td.getId() == null) {
                            td.setId(snapshot.getKey());
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("traveldeals")
                                    .child(td.getId())
                                    .setValue(td);
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

    private void deleteDeal() {
        if (deal != null) {
            deleteDeal(deal);
        } else {
            makeToast();
        }
    }

    private void deleteDeal(TravelDeal deal) {
        mDatabaseReference.child(deal.getId()).removeValue();
        Log.d("image name", deal.getImageName());

        if (deal.getImageName() != null && !deal.getImageName().isEmpty()) {
            StorageReference picRef = FirebaseStorage.getInstance()
                    .getReference()
                    .child(deal.getImageName());

            picRef.delete();
        }
    }

    private void makeToast() {
        Toast.makeText(this, "Please save the deal before deleting"
                , Toast.LENGTH_SHORT).show();
    }

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void enableEditTexts(boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(imageView);
        }
    }

    private void goToCommentActivity(View view) {
        Intent intent = new Intent(DealActivity.this, CommentAdderActivity.class);
        intent.putExtra("deal", deal);
        startActivity(intent);
    }
}

