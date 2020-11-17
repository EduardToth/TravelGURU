package com.example.travelmantics.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import com.example.travelmantics.my_trips.FullImageActivity;
import com.example.travelmantics.my_trips.ImageAdapter;

public class GalleryItemClickListener implements AdapterView.OnItemClickListener {
    private final Context context;
    private ImageAdapter imageAdapter;

    public GalleryItemClickListener(Context context, ImageAdapter imageAdapter) {
        this.context = context;
        this.imageAdapter = imageAdapter;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent1 = new Intent(context, FullImageActivity.class);
        intent1.putExtra("uri", imageAdapter.getImageReference(position));
        context.startActivity(intent1);
    }
}
