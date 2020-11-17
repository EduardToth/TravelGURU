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

import com.example.travelmantics.utilities.TravelDeal;
import com.example.travelmantics.utilities.UtilityClass;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


public class DealActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private EditText txtTitle;
    private EditText txtPrice;
    private EditText txtDescription;
    private Optional<TravelDeal> deal;
    private ImageView imageView;
    private static final int PICTURE_RESULT = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("traveldeals");

        txtTitle =  findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice =  findViewById(R.id.txtPrice);
        imageView =  findViewById(R.id.imageView);
        Intent intent = getIntent();

        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");

        if (deal == null) {
            deal = new TravelDeal();
        }

        this.deal = Optional.of(deal);
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());

        showImage(deal.getImageUrl());
        Button btnImage = findViewById(R.id.btnImage);
        if (!FirebaseUtil.isCurrentUserAdmin()) {
            btnImage.setText("Buy");
            final TravelDeal finalDeal = deal;
            final AtomicInteger nr = new AtomicInteger(0);
            btnImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference ref = database.getReference("bought_deals").child(FirebaseUtil.getCurrentUserUid());
                    ref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if (finalDeal.getId().equals(Objects.requireNonNull(snapshot.getValue()).toString())) {
                                if (nr.get() == 0) {
                                    ref.child(Objects.requireNonNull(snapshot.getKey())).removeValue();
                                } else {
                                    nr.set(0);
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
                    nr.incrementAndGet();
                    ref.push().setValue(finalDeal.getId());
                }
            });
        } else {
            btnImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpg");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intent,
                            "Insert Picture"), PICTURE_RESULT);
                }
            });
        }
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


            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FirebaseStorage.getInstance()
                            .getReference()
                            .child("deals_pictures")
                            .child(file.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            deal.ifPresent(myDeal -> setImageDetails(myDeal, uri, fileName));
                            showImage(uri.toString());
                        }
                    });
                }
            });
        }
    }

    private void setImageDetails(TravelDeal deal, Uri uri, String fileName) {
        deal.setImageUrl(uri.toString());
        deal.setImageName(fileName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isCurrentUserAdmin()) {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
        } else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void clean() {
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.setText("");
        txtTitle.requestFocus();
    }

    private void saveDeal() {
        if (deal.isPresent()) {
            deal.get().setTitle(txtTitle.getText().toString());
            deal.get().setDescription(txtDescription.getText().toString());
            deal.get().setPrice(txtPrice.getText().toString());
            if (deal.get().getId() == null) {
                mDatabaseReference.push().setValue(deal);
            } else {
                mDatabaseReference.child(deal.get().getId()).setValue(deal);
            }
        }
    }

    private void deleteDeal() {
        if (deal.isPresent()) {
            deleteDeal(deal.get());
        } else {
            makeToast("Please save the deal before deleting");
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

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

