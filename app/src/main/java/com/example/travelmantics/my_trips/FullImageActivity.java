package com.example.travelmantics.my_trips;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.travelmantics.R;
import com.squareup.picasso.Picasso;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        Intent intent = getIntent();
        String uri = intent.getExtras().getString("uri");
        ImageView imageView = findViewById(R.id.imageView5);

        int  width = Resources.getSystem().getDisplayMetrics().widthPixels;
        Picasso.with(this)
                .load(uri)
                .resize(width, width)
                .centerCrop()
                .into(imageView);
    }
}