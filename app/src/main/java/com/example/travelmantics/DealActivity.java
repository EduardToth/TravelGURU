package com.example.travelmantics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelmantics.listeners.ChildEventListenerForDeals;
import com.example.travelmantics.utilities.AuthUtil;
import com.example.travelmantics.utilities.TravelDeal;
import com.example.travelmantics.utilities.UtilityClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

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
        btnImage.setText(R.string.buy);
        final TravelDeal finalDeal = deal;
        final AtomicInteger nr = new AtomicInteger(0);
        btnImage.setOnClickListener(view -> react(finalDeal, nr));
    }

    void react(TravelDeal finalDeal, AtomicInteger nr) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("bought_deals")
                .child(AuthUtil.getCurrentUserUid());

        ref.addChildEventListener(new ChildEventListenerForDeals(ref, finalDeal, nr));
        nr.incrementAndGet();
        ref.push().setValue(finalDeal.getId());
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


            ref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> storeImage(file, fileName));
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

    @SuppressLint("RestrictedApi")
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

        switch (item.getItemId()) {
            case R.id.save_menu:
                handleSaveDeal();
                return true;
            case R.id.delete_menu:
                handleDeleteDeal();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

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
                    .setValue(deal);
        } else {
            mDatabaseReference.child(deal.getId())
                    .setValue(deal);
        }
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
        Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();
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
}

