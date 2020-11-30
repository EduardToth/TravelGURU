package com.example.travelmantics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.travelmantics.utilities.UtilityClass;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        LinearLayout layout = findViewById(R.id.linearLayout);
        ImageView backgroundImage = findViewById(R.id.imageView2);
        Button viewDealsButton = findViewById(R.id.view_deals);
        Button loginButton = findViewById(R.id.go_to_profile_button);

        layout.bringToFront();
        backgroundImage.invalidate();

        loginButton.setOnClickListener(view
                -> UtilityClass.changeActivity(this, ListActivity.class));

        viewDealsButton.setOnClickListener(view
                -> UtilityClass.changeActivity(this, ViewListActivity.class));
    }

}