package com.example.travelmantics.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import com.example.travelmantics.my_trips.FullImageActivity;
import com.example.travelmantics.my_trips.ImageAdapter;
import com.example.travelmantics.utilities.TravelDeal;
import com.google.firebase.storage.StorageReference;

public class GalleryItemClickListener implements AdapterView.OnItemClickListener {
    private final Context context;
    private final ImageAdapter imageAdapter;
    private final TravelDeal travelDeal;

    public GalleryItemClickListener(Context context, ImageAdapter imageAdapter, TravelDeal travelDeal) {
        this.context = context;
        this.imageAdapter = imageAdapter;
        this.travelDeal = travelDeal;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(context, FullImageActivity.class);
        String imageUri =  imageAdapter.getImageReference(position);
        StorageReference storageReference = imageAdapter.getDownloadUrl(imageUri);


        intent.putExtra("url", storageReference.getPath());
        intent.putExtra("uri", imageUri);
        intent.putExtra("travelDealId", travelDeal.getId());
        context.startActivity(intent);
    }
}
