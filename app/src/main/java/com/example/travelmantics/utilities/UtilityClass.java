package com.example.travelmantics.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilityClass {

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }
        return extension;
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String getPictureFileName(Context context, Uri uri) {
        String fileName = getFileName(context, uri);
        String typeName = getMimeType(context, uri);

        if (!fileName.endsWith(typeName)) {
            fileName += "." + typeName;
        }

        return fileName;
    }

    public static void changeActivity(AppCompatActivity currentActivity, Class<? extends AppCompatActivity> activityClass) {
        Intent intent = new Intent(currentActivity, activityClass);
        currentActivity.startActivity(intent);
    }

    public static void startIntentForPicture(AppCompatActivity activity, final int PICTURE_RESULT) {
        Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
        intent1.setType("image/jpg");
        intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        activity.startActivityForResult(Intent.createChooser(intent1,
                "Insert Picture"), PICTURE_RESULT);
    }

    public static List<DataSnapshot> convertToList(Iterable<DataSnapshot> children) {
        return StreamSupport.stream(children.spliterator(), false)
                .collect(Collectors.toList());
    }

    public static boolean userAlreadyAddedReview(TravelDeal travelDeal) {
        AtomicBoolean userAlreadyAddedComment = new AtomicBoolean(false);
        AtomicBoolean atomicFlagChanged = new AtomicBoolean(false);

        FirebaseDatabase.getInstance()
                .getReference()
                .child("comments")
                .child(travelDeal.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean userAddedComment = convertToList(snapshot.getChildren())
                                .stream()
                                .map(DataSnapshot::getKey)
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .anyMatch(AuthUtil.getCurrentUserUid()::equals);

                        userAlreadyAddedComment.set(userAddedComment);
                        atomicFlagChanged.set(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance()
                .getReference()
                .child("comments")
                .child(travelDeal.getId())
                .child(AuthUtil.getCurrentUserUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("snp", snapshot.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        while (!atomicFlagChanged.get()) {
        }

        return userAlreadyAddedComment.get();
    }
}
