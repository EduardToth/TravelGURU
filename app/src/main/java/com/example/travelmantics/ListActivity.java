package com.example.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelmantics.listeners.UserStorageHandlerListener;
import com.example.travelmantics.utilities.AuthUtil;
import com.example.travelmantics.utilities.PersistenceSetter;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListActivity extends AppCompatActivity {

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        try {
            PersistenceSetter.setupPersistence();
        }catch (DatabaseException e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        this.menu = menu;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            setOptionVisibility();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean selectionSuccessful = true;
        if (item.getItemId() == R.id.insert_menu) {
            Intent intent = new Intent(this, DealActivity.class);
            intent.putExtra("is_new_travel_deal", true);
            startActivity(intent);
        } else if (item.getItemId() == R.id.logout_menu) {
            AuthUI.getInstance()
                    .signOut(this);
            Intent intent1 = new Intent(this, StartActivity.class);
            startActivity(intent1);
        } else if (item.getItemId() == R.id.go_to_profile) {
            Intent intent2 = new Intent(this, ProfileActivity.class);
            startActivity(intent2);
        } else {
            selectionSuccessful = super.onOptionsItemSelected(item);
        }

        return selectionSuccessful;
    }

    @Override
    protected void onPause() {
        super.onPause();
        AuthUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AuthUtil.openFbReference(this);
        RecyclerView rvDeals = findViewById(R.id.rvDeals);
        final DealAdapter adapter = new DealAdapter();
        rvDeals.setAdapter(adapter);
        LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvDeals.setLayoutManager(dealsLayoutManager);
        AuthUtil.attachListener();
    }

    @Override
    protected void onRestart() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
        }
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == -1) {
            checkUserInfo();
            setOptionVisibility();
        }
    }

    private void checkUserInfo() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("client_info")
                .addValueEventListener(new UserStorageHandlerListener());
    }

    private void setOptionVisibility() {
        MenuItem insertMenuItem = menu.findItem(R.id.insert_menu);
        MenuItem goToProfileMenuItem = menu.findItem(R.id.go_to_profile);
        insertMenuItem.setVisible(false);
        goToProfileMenuItem.setVisible(true);
        FirebaseDatabase.getInstance()
                .getReference()
                .child("administrators")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            insertMenuItem.setVisible(true);
                            goToProfileMenuItem.setVisible(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}