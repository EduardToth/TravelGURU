package com.example.travelmantics.comments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.travelmantics.R;
import com.example.travelmantics.utilities.AuthUtil;
import com.example.travelmantics.utilities.TravelDeal;

public class CommentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
    }

    @Override
    public void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.comment_list);
        TravelDeal deal = (TravelDeal) getIntent().getExtras().get("deal");
        CommentAdapter adapter = new CommentAdapter(deal);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(dealsLayoutManager);
        AuthUtil.attachListener();
    }
}