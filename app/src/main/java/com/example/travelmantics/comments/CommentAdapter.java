package com.example.travelmantics.comments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelmantics.R;
import com.example.travelmantics.utilities.TravelDeal;
import com.example.travelmantics.utilities.UtilityClass;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    private final List<Comment> comments = new LinkedList<>();
    private final TravelDeal travelDeal;
    public CommentAdapter(TravelDeal travelDeal) {
        this.travelDeal = travelDeal;
        FirebaseDatabase.getInstance()
                .getReference()
                .child("comments")
                .child(travelDeal.getId())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("client_info")
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                                        if (Objects.equals(snapshot1.getKey(), snapshot.getKey())) {
                                            User user = snapshot1.getValue(User.class);
                                            assert user != null;
                                            user.setUserId(snapshot1.getKey());
                                            snapshot.getChildren()
                                                    .forEach(dataSnapshot -> {
                                                        String review = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                                        Comment comment = new Comment(review, user);
                                                        comments.add(comment);
                                                    });

                                            if (snapshot.getChildren().iterator().hasNext()) {
                                                notifyDataSetChanged();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
        Comment comment = comments.get(position);
        FirebaseDatabase.getInstance()
                .getReference()
                .child("ratings")
                .child(travelDeal.getId())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        if(snapshot.getKey().equals(comment.getUser().getUserId())) {
                            Optional<Float> ratingValue = UtilityClass.convertToList(snapshot.getChildren())
                                    .stream()
                                    .map(DataSnapshot::getValue)
                                    .map(Object::toString)
                                    .map(Float::parseFloat)
                                    .findAny();

                            holder.bind(comment, ratingValue.orElse((float)-1));
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
