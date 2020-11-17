package com.example.travelmantics.my_trips;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelmantics.FirebaseUtil;
import com.example.travelmantics.R;
import com.example.travelmantics.listeners.GalleryItemClickListener;
import com.example.travelmantics.utilities.TravelDeal;
import com.example.travelmantics.utilities.UtilityClass;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Optional;

public class GalleryActivity extends AppCompatActivity {

    private static final int PICTURE_RESULT = 5432;
    private TravelDeal travelDeal;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.grid_view);
        ImageView imageView = findViewById(R.id.imageView4);

        setupImageView(imageView);
    }

    private void setupGridView(GridView gridView) {
        ImageAdapter adapter = new ImageAdapter(this, travelDeal.getId());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new GalleryItemClickListener(getApplicationContext(), adapter));
    }

    private void setupImageView(ImageView imageView) {
        Intent intent = getIntent();
        Optional<TravelDeal> deal = getCurrentTravelDeal(intent);
        deal.ifPresent(myDeal -> setDefaults(imageView, myDeal));
    }

    private Optional<TravelDeal> getCurrentTravelDeal(Intent intent) {
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");

        return Optional.ofNullable(deal);
    }

    private void setDefaults(ImageView imageView, TravelDeal el) {
        travelDeal = el;
        showImage(el.getImageUrl(), imageView);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            assert data != null;
            Uri imageUri = data.getData();
            String fileName = UtilityClass.getPictureFileName(this, data.getData());
            assert imageUri != null;
            final File file = new File(fileName);
            StorageReference ref = FirebaseStorage.getInstance()
                    .getReference()
                    .child("users")
                    .child(FirebaseUtil.getCurrentUserUid())
                    .child(travelDeal.getId())
                    .child(file.getName());

            ref.putFile(imageUri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gallery_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_picture) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpg");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent,
                    "Insert Picture"), PICTURE_RESULT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        setupGridView(gridView);
        super.onResume();
    }
}