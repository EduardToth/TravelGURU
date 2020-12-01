package com.example.travelmantics.my_trips;

import android.content.res.Resources;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelmantics.utilities.AuthUtil;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ImageAdapter extends BaseAdapter {
    private final AppCompatActivity context;
    private final List<String> imageUri_s = new Vector<>();
    private final Map<String, StorageReference> urlMappins = new HashMap<>();

    public ImageAdapter(AppCompatActivity context, String travelDealId) {
        this.context = context;
        addPicturesToList(travelDealId);

    }

    private void addPicturesToList(String travelDealId) {

        FirebaseStorage.getInstance()
                .getReference()
                .child("users")
                .child(AuthUtil.getCurrentUserUid())
                .child(travelDealId)
                .listAll()
                .addOnSuccessListener(this::addPicturesToList);
    }

    public void addPicturesToList(ListResult listResult) {
        listResult.getItems().forEach(this::addPictureToList);
    }


    private void addPictureToList(StorageReference item) {
        item.getDownloadUrl().addOnSuccessListener(uri -> addPictureToList(uri, item));
    }

    public void addPictureToList(Uri uri, StorageReference storageReference) {
        imageUri_s.add(uri.toString());
        notifyDataSetChanged();
        urlMappins.put(uri.toString(), storageReference);

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
        ImageView imageView = new ImageView(context);

        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        Picasso.with(context)
                .load(imageUri_s.get(position))
                .resize(width / 2, height / 3)
                .centerCrop()
                .into(imageView);

        return imageView;
    }

    public String getImageReference(int position) {

        return imageUri_s.get(position);
    }

    public StorageReference getDownloadUrl(String uri) {
        return urlMappins.get(uri);
    }
}
