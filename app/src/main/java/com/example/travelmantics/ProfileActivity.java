package com.example.travelmantics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelmantics.comments.User;
import com.example.travelmantics.my_trips.BoughtDealAdapter;
import com.example.travelmantics.utilities.AuthUtil;
import com.example.travelmantics.utilities.ProfileUpdateActivity;
import com.example.travelmantics.utilities.UtilityClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICTURE_RESULT = 142;
    private ImageView profilePicture;
    private TextView showUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_page);

        RecyclerView rvDeals = findViewById(R.id.rv_deals2);
        fillRecyclerView(rvDeals);

        profilePicture = findViewById(R.id.profile_picture);
        showUsername = findViewById(R.id.show_username);
        Button editProfileButton = findViewById(R.id.edit_profile);
        editProfileButton.setOnClickListener(this::editProfile);

        showImageIfExists();
    }

    private void editProfile(View view) {
        Intent intent = new Intent(this, ProfileUpdateActivity.class);
        startActivity(intent);
    }

    private void showImageIfExists() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Iterable<DataSnapshot> children = snapshot.getChildren();
                        setUserCharacteristics(children);
                    }

                    private void setUserCharacteristics(Iterable<DataSnapshot> children) {

                        for(DataSnapshot child : children) {
                            if(Objects.equals(child.getKey(), AuthUtil.getCurrentUserUid())) {
                                Optional<User> user = Optional.ofNullable(child.getValue(User.class));

                               user.map(User::getProfile_picture_url)
                                       .filter(pictureUrl -> !pictureUrl.isEmpty())
                                       .ifPresent(ProfileActivity.this::showImage);

                               user.map(User::getUser_name)
                                       .filter(name -> !name.isEmpty())
                                       .ifPresent(showUsername::setText);
                                break;
                            }

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            final String fileName = UtilityClass.getPictureFileName(this, data.getData());
            assert imageUri != null;
            final File file = new File(fileName);

            FirebaseStorage.getInstance()
                    .getReference()
                    .child("users")
                    .child(AuthUtil.getCurrentUserUid())
                    .child(fileName)
                    .putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storeUriInDatabase(file));
        }
    }

    private void storeUriInDatabase(File file) {
        FirebaseStorage.getInstance()
                .getReference()
                .child("users")
                .child(AuthUtil.getCurrentUserUid())
                .child(file.getName())
                .getDownloadUrl()
                .addOnSuccessListener(this::storeUriInDatabase);
    }

    private void storeUriInDatabase(Uri imageUri) {

        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .child(AuthUtil.getCurrentUserUid())
                .child("profile_picture_url")
                .setValue(imageUri.toString());
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            int height;
            int width;

            do {
                height = profilePicture.getHeight();
                width = profilePicture.getWidth();
            } while (height <= 0 && width <= 0);

            Picasso.with(this)
                    .load(url)
                    .resize(width, height)
                    .centerCrop()
                    .into(profilePicture, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            profilePicture.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError() {}
                    });
        }
    }

    private void fillRecyclerView(RecyclerView rvDeals) {
        final BoughtDealAdapter boughtDealAdapter = new BoughtDealAdapter();
        rvDeals.setAdapter(boughtDealAdapter);
        LinearLayoutManager dealsLayoutManager = new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false);
        rvDeals.setLayoutManager(dealsLayoutManager);
        AuthUtil.attachListener();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }
}