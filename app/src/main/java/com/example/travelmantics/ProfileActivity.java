package com.example.travelmantics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelmantics.my_trips.BoughtDealAdapter;
import com.example.travelmantics.utilities.AuthUtil;
import com.example.travelmantics.utilities.TextGetterActivity;
import com.example.travelmantics.utilities.UtilityClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICTURE_RESULT = 142;
    private ImageView profilePicture;
    private Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_page);

        RecyclerView rvDeals = findViewById(R.id.rv_deals2);
        fillRecyclerView(rvDeals);

        //final Button show_offers_button = findViewById(R.id.go_to_offers);
        //final Button myTripsButton = findViewById(R.id.my_trips);
        profilePicture = findViewById(R.id.profile_picture);
        TextView showUsername = findViewById(R.id.show_username);
        editProfileButton = findViewById(R.id.edit_profile);
        editProfileButton.setOnClickListener(view -> changeUserName());

        sayHi(showUsername);
        showImageIfExists();

        profilePicture.setOnClickListener(view
                -> UtilityClass.startIntentForPicture(this, PICTURE_RESULT));

        //show_offers_button.setOnClickListener(view
        //        -> UtilityClass.changeActivity(this, ListActivity.class));

        //myTripsButton.setOnClickListener(view
        //        -> UtilityClass.changeActivity(this, MyTrips.class));
    }

    private void changeUserName() {
        Intent intent = new Intent(this, TextGetterActivity.class);
        startActivity(intent);
    }

    private void showImageIfExists() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .child(AuthUtil.getCurrentUserUid())
                .child("profile_picture_url")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null) {
                            showImage(Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sayHi(TextView sayHiView) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String greetings = "";

        if (bundle != null && bundle.getString(Intent.EXTRA_TEXT) != null) {
            String name = extractName(bundle);
            greetings += name;
            sayHiView.setText(greetings);
        } else {
            setDefaultGreeting(sayHiView);
            setGreetingsIfPossible(sayHiView);
        }
    }

    private void setGreetingsIfPossible(TextView sayHiView) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .child(AuthUtil.getCurrentUserUid())
                .child("user_name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null) {
                            String greetings = (String) snapshot.getValue();
                            sayHiView.setText(greetings);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setDefaultGreeting(TextView sayHiView) {
        String greetings;
        Optional<String> email = getEmail();
        email.ifPresent(mail -> editProfileButton.setText(R.string.add_username));
        greetings = email.map(this::getUserName)
                .orElse("");

        sayHiView.setText(greetings);
    }

    private String extractName(Bundle bundle) {
        String name = bundle.getString(Intent.EXTRA_TEXT);

        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .child(AuthUtil.getCurrentUserUid())
                .child("user_name")
                .setValue(name);
        return name;
    }

    private String getUserName(String email) {
        int index = email.indexOf("@");

        return email.substring(0, index);
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
                    .child("users")
                    .child(AuthUtil.getCurrentUserUid())
                    .child(fileName);

            ref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> storeUriInDatabase(file));
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

    private Optional<String> getEmail() {
        String email = Objects.requireNonNull(FirebaseAuth.getInstance()
                .getCurrentUser())
                .getEmail();

        return Optional.ofNullable(email);
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            int height =  profilePicture.getHeight();
            int width = profilePicture.getWidth();
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
                        public void onError() {
                        }
                    });
        }
    }

    private void fillRecyclerView(RecyclerView rvDeals) {
        final BoughtDealAdapter boughtDealAdapter = new BoughtDealAdapter();
        rvDeals.setAdapter(boughtDealAdapter);
        LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvDeals.setLayoutManager(dealsLayoutManager);
        AuthUtil.attachListener();
    }
}