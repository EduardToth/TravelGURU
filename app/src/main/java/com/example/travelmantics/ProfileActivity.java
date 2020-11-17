package com.example.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelmantics.my_trips.MyTrips;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_page);
        final Button show_offers_button = findViewById(R.id.go_to_offers);
        final Button myTripsButton = findViewById(R.id.my_trips);

        startOffersListener(show_offers_button);
        startMyTripsListener(myTripsButton);
    }

    private void startOffersListener(Button show_offers_button) {
        show_offers_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startMyTripsListener(Button myTripsButton) {
        myTripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MyTrips.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}