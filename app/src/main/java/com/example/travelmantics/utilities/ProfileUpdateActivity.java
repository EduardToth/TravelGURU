package com.example.travelmantics.utilities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.example.travelmantics.ProfileActivity;
import com.example.travelmantics.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ProfileUpdateActivity extends AppCompatActivity {

    private static final int PICTURE_RESULT = 142;

    private EditText editText;
    private ImageView profilePicture;
    private final AtomicReference<Uri> uri = new AtomicReference<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_getter);

        profilePicture = findViewById(R.id.profile_picture2);
        loadPicture();
        editText = findViewById(R.id.name_getter);
        loadUserName();
        Button button = findViewById(R.id.picture_changer_button);
        button.setOnClickListener(view
                -> UtilityClass.startIntentForPicture(this, PICTURE_RESULT));

    }

    private void loadUserName() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .child(AuthUtil.getCurrentUserUid())
                .child("user_name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            String userName = snapshot.getValue().toString();
                            editText.setText(userName);
                        } else {
                            editText.setText(R.string.choose_username);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadPicture() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .child(AuthUtil.getCurrentUserUid())
                .child("profile_picture_url")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            String imageUrl = snapshot.getValue().toString();
                            showImage(imageUrl, profilePicture);
                        } else {
                            FirebaseStorage.getInstance()
                                    .getReference()
                                    .child("users")
                                    .child("default_values")
                                    .child("empty_profile_picture.png")
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri
                                            -> showImage(uri.toString(), profilePicture));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showImage(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            RequestCreator requestCreator = Picasso.with(this).load(url);
            loadPicture(imageView, width, requestCreator);
        }
    }

    private void loadPicture(ImageView imageView, int width, RequestCreator load) {
        load.resize(width / 2, width / 2)
                .centerCrop()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadRoundedPicture(imageView);
                    }

                    @Override
                    public void onError() {
                        Log.d("Naspaaa", "Naspaaa");
                    }
                });
    }

    private void loadRoundedPicture(ImageView imageView) {
        Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
        imageDrawable.setCircular(true);
        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
        imageView.setImageDrawable(imageDrawable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                uri.set(data.getData());

                int width = Resources.getSystem()
                        .getDisplayMetrics()
                        .widthPixels;


                RequestCreator requestCreator = Picasso.with(this).load(uri.get());
                loadPicture(profilePicture, width, requestCreator);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean itemSelectedSuccessfully = true;

        if (item.getItemId() == R.id.save) {
            saveElements();
        } else {
            itemSelectedSuccessfully = super.onOptionsItemSelected(item);
        }

        return itemSelectedSuccessfully;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.change_profile_items_menu, menu);
        this.setVisible(true);

        return true;
    }

    private void goBackToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);

        startActivity(intent);
    }

    private void saveElements() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .child(AuthUtil.getCurrentUserUid());

        AtomicBoolean pictureSaveFinished = new AtomicBoolean(false);
        if (uri.get() != null) {
            saveInFirebaseStorage(pictureSaveFinished);
        } else {
            pictureSaveFinished.set(true);
        }

        AtomicBoolean saveUserNameFinished = new AtomicBoolean(false);
        if (!containsDefaultText()) {
            saveNewUserName(ref, editText.getText().toString(), saveUserNameFinished);
        } else {
            saveUserNameFinished.set(true);
        }

        if(!(pictureSaveFinished.get() && saveUserNameFinished.get())) {
            goBackToProfile();
        }


    }

    private void saveNewUserName(DatabaseReference ref, String s, AtomicBoolean saveUserNameFinished) {
        ref.child("user_name")
                .setValue(s)
                .addOnSuccessListener(runnable -> saveUserNameFinished.set(true))
                .addOnFailureListener(runnable -> saveUserNameFinished.set(true));
    }

    private boolean containsDefaultText() {
        return editText.getText()
                .toString()
                .equals("Enter username");
    }

    private void saveInFirebaseStorage( AtomicBoolean pictureSaveFinished ) {
        final String fileName = UtilityClass.getPictureFileName(this, uri.get());

        FirebaseStorage.getInstance()
                .getReference()
                .child("users")
                .child(AuthUtil.getCurrentUserUid())
                .child(fileName)
                .putFile(uri.get())
                .addOnSuccessListener(uri -> storeUriInDatabase(fileName, pictureSaveFinished));
    }

    private void storeUriInDatabase(String fileName,  AtomicBoolean pictureSaveFinished ) {
        FirebaseStorage.getInstance()
                .getReference()
                .child("users")
                .child(AuthUtil.getCurrentUserUid())
                .child(fileName)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> storeUriInDatabase(uri, pictureSaveFinished));
    }

    private void storeUriInDatabase(Uri imageUri,  AtomicBoolean pictureSaveFinished ) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .child(AuthUtil.getCurrentUserUid())
                .child("profile_picture_url")
                .setValue(imageUri.toString())
                .addOnSuccessListener(runnable -> pictureSaveFinished.set(true))
                .addOnFailureListener(runnable -> pictureSaveFinished.set(true));
    }
}