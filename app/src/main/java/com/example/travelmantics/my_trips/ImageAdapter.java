package com.example.travelmantics.my_trips;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelmantics.FirebaseUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Vector;

public class ImageAdapter extends BaseAdapter {
    private final AppCompatActivity context;
    private final  List<String> imageUri_s = new Vector<>();
    private static int nrInstances = 0;

    public ImageAdapter(AppCompatActivity context, String travelDealId) {
        this.context = context;
        Log.d("instances_nr", Integer.toString(++nrInstances));
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference()
                .child("users")
                .child(FirebaseUtil.getCurrentUserUid())
                .child(travelDealId);

        ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                listResult.getItems().forEach(item -> item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUri_s.add(uri.toString());
                        ImageAdapter.this
                                .context
                                .runOnUiThread(ImageAdapter.this::notifyDataSetChanged);
                    }
                }));
            }
        });
    }

    @Override
    public int getCount() {
        return imageUri_s.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUri_s.get(position);
    }

    @Override
    public long getItemId(int position) {
        return imageUri_s.get(position).hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView =  new ImageView(context);
        Picasso.with(context)
                .load(imageUri_s.get(position))
                .resize(300, 300)
                .centerCrop()
                .into(imageView);

        return imageView;
    }

    public  String getImageReference(int position) {
        return imageUri_s.get(position);
    }
}
