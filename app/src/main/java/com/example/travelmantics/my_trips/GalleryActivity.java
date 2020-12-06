package com.example.travelmantics.my_trips;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelmantics.R;
import com.example.travelmantics.listeners.GalleryItemClickListener;
import com.example.travelmantics.utilities.AuthUtil;
import com.example.travelmantics.utilities.TravelDeal;
import com.example.travelmantics.utilities.UtilityClass;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Optional;

public class GalleryActivity extends AppCompatActivity {

    private static final int PICTURE_RESULT = 5432;
    private TravelDeal travelDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        GridView gridView = findViewById(R.id.grid_view);

        setupImageView();
        setupGridView(gridView);
    }

    private void setupGridView(GridView gridView) {
        ImageAdapter adapter = new ImageAdapter(this, travelDeal.getId());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new GalleryItemClickListener(getApplicationContext()
                , adapter, travelDeal));
    }

    private void setupImageView() {
        Intent intent = getIntent();
        Optional<TravelDeal> deal = getCurrentTravelDeal(intent);
        deal.ifPresent(this::setDefaults);
    }

    private Optional<TravelDeal> getCurrentTravelDeal(Intent intent) {
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");

        return Optional.ofNullable(deal);
    }

    private void setDefaults(TravelDeal el) {
        travelDeal = el;
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

            FirebaseStorage.getInstance()
                    .getReference()
                    .child("users")
                    .child(AuthUtil.getCurrentUserUid())
                    .child(travelDeal.getId())
                    .child(file.getName())
                    .putFile(imageUri)
                    .addOnSuccessListener(this::reloadPage);

        }
    }

    private void reloadPage(UploadTask.TaskSnapshot runnable) {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
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
}