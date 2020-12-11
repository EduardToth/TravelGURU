package com.example.travelmantics.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelmantics.R;
import com.example.travelmantics.utilities.TravelDeal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    private final List<Comment> comments = new LinkedList<>();

    public CommentAdapter(TravelDeal travelDeal) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("comments")
                .child(travelDeal.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren()
                        .forEach(this::addComment);
                    }

                    private void addComment(DataSnapshot child) {
                        Comment comment = child.getValue(Comment.class);
                        comments.add(comment);
                        notifyItemInserted(comments.size() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.comment_row, parent, false);

        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
